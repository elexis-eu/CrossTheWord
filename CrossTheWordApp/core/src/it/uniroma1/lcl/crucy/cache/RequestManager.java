package it.uniroma1.lcl.crucy.cache;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.utils.NeededCrossword;
import it.uniroma1.lcl.crucy.gameplay.boxes.Word;
import it.uniroma1.lcl.crucy.gameplay.builderclasses.Point;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.Sense;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.SenseInfo;
import it.uniroma1.lcl.crucy.utils.GameType;
import it.uniroma1.lcl.crucy.utils.LimitedDomains;
import it.uniroma1.lcl.crucy.cache.exceptions.ActionCancelledException;
import it.uniroma1.lcl.crucy.cache.exceptions.AuthenticationRequiredException;
import it.uniroma1.lcl.crucy.cache.exceptions.UnexpectedServerResponseException;
import it.uniroma1.lcl.crucy.cache.utils.Actions;
import it.uniroma1.lcl.crucy.cache.utils.Difficult;
import it.uniroma1.lcl.crucy.cache.utils.Language;
import it.uniroma1.lcl.crucy.cache.utils.Parameters;

import static it.uniroma1.lcl.crucy.cache.utils.Parameters.LEMMA;
import static it.uniroma1.lcl.crucy.cache.utils.Parameters.SENSEID;
import static it.uniroma1.lcl.crucy.cache.utils.Parameters.SENSES;
import static it.uniroma1.lcl.crucy.cache.utils.Parameters.SOURCE;
import static it.uniroma1.lcl.crucy.cache.utils.Parameters.SYNSET_ID;

/**
 * Classe con il quale l'applicazione effettua le richieste al server.
 * @author dario
 *
 */
public class RequestManager
{
	private static final int TIMEOUT = 5000;
	public static final int MIN_CROSSWORDS = 3;
	private ConnectionManager connectionManager;
	
	// --------------------- COSTRUTTORE E SINGLETON ----------------------------
	
	private static RequestManager instance;
	
	private RequestManager()
	{
		connectionManager = ConnectionManager.getInstance();
	}
	
	public static RequestManager getInstance() {
		if(instance == null) instance = new RequestManager();
		return instance;
	}
	
	// ---------------------- METODI CRUCIVERBA ------------------------------------------------
	
	/**
	 * Metodo con il quale si effettua la richiesta di un cruciverba tramite dominio, difficolta' e lingua.
	 * @param domain dominio del cruciverba che si vuole richiedere.
	 * @param difficult	difficolta'del cruciverba che si vuole richiedere.
	 * @param cb la callback da utilizzare per ricevere il cruciverba.
	 */
	public void getCrossword(Language language, LimitedDomains domain, GameType gameType, String level, Difficult difficult, final Callback<List<JsonValue>> cb) {
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Parameters.DOMAIN, domain.name());
		parameters.put(Parameters.DIFFICULT, difficult.name());
		parameters.put(Parameters.LANGUAGE, language.name());
		parameters.put(Parameters.LEVEL, level);

		HttpRequest request;
		try {
			request = buildAuthHttpRequestGet(parameters,Actions.CROSSWORD);
		} catch(AuthenticationRequiredException e) {
			e.printStackTrace();
			request = buildHttpRequestGet(parameters,Actions.CROSSWORD);
		}

		getInformationAsList(request, cb);
	}
	
	/**
	 * Metodo con il si effettua la richiesta di un cruciverba tramite id.
	 * @param crosswordId id del cruciverba da richiedere.
	 * @param cb la callback da utilizzare per ricevere il cruciverba.
	 */
	public void getCrossword(String crosswordId, final Callback<List<JsonValue>> cb)
	{
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Parameters.ID_CROSSWORD, crosswordId);
		HttpRequest request;
		try {
			request = buildAuthHttpRequestGet(parameters, Actions.CROSSWORD);
		} catch (AuthenticationRequiredException e) {
			cb.onFailure(new AuthenticationRequiredException());
			return;
		}
		getInformationAsList(request, cb);
	}
	
	/**
	 * Metodo con il quale si effettua la richiesta dello stato del cruciverba.
	 * @param crosswordId id del cruciverba di cui si desidera lo stato.
	 * @param cb la callback da utilizzare per ricevere lo stato del cruciverba.
	 */
	public void getStatusCrossword(String crosswordId, final Callback<Set<String>> cb)
	{
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Parameters.ID_CROSSWORD, crosswordId);
		HttpRequest request;
		try {
			request = buildAuthHttpRequestGet(parameters, Actions.STATUS_CROSSWORD);
		} catch (AuthenticationRequiredException e) {
			cb.onFailure(e);
			return;
		}

		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse response) {
				String value = response.getResultAsString();
				JsonValue base = new JsonReader().parse(value);
				if (base.has(Parameters.DEFINITIONS)) {
					Set<String> status = new HashSet<String>();
					for (JsonValue senses : base.get(Parameters.DEFINITIONS)) {
						Map<String, String> wordInfo = new Json().fromJson(new HashMap<String,String>().getClass(), senses.toString());
						status.add(wordInfo.get(Parameters.WORD));
					}
					cb.onSuccess(status);
					return;
				} else { cb.onFailure(new UnexpectedServerResponseException()); }
			}

			@Override
			public void failed(Throwable cause) {
				cb.onFailure(cause);
			}

			@Override
			public void cancelled() {
				cb.onFailure(new ActionCancelledException());
			}
		});
	}
	

	public void sendCompletedWords(String crosswordID, List<Word> completed, final CallbackVoid cb){
		Json jsonObject = new Json();
		StringWriter jsonText = new StringWriter();
		JsonWriter writer = new JsonWriter(jsonText);
		jsonObject.setOutputType(JsonWriter.OutputType.json);
		jsonObject.setWriter(writer);
		jsonObject.writeObjectStart();
		jsonObject.writeValue(Parameters.ID_CROSSWORD, crosswordID);
		jsonObject.writeArrayStart(Parameters.DEFINITIONS);{
			for (Word word : completed) {
				Json wordInfo = new Json();
				wordInfo.setOutputType(JsonWriter.OutputType.json);
				wordInfo.setWriter(writer);
				wordInfo.writeObjectStart();
				wordInfo.writeValue(Parameters.BABELNETID, word.getBabelnetID());
				wordInfo.writeValue(Parameters.WORD, word.getParola());
				Point coordinates = word.getFirst().getBox().getCoordinates();
				wordInfo.writeValue(Parameters.ROW, coordinates.getY() + "");
				wordInfo.writeValue(Parameters.COLUMN, coordinates.getX() + "");
				if (word.getOrientamento().equals(Word.Orientamento.ORIZZONTALE))
					wordInfo.writeValue(Parameters.HORIZONTAL, true + "");
				else wordInfo.writeValue(Parameters.HORIZONTAL, false + "");
				wordInfo.writeObjectEnd();
			}
		}
        jsonObject.writeArrayEnd();
        jsonObject.writeObjectEnd();

		HttpRequest request = null;
		try {
			request = buildAuthHttpRequest(new HashMap<String,String>(), Actions.STATUS_CROSSWORD);
		} catch (AuthenticationRequiredException e) {
			e.printStackTrace();
			cb.onFailure(e);
		}

		request.setContent(jsonObject.getWriter().getWriter().toString());
		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse response) {
				String value = response.getResultAsString();
				if(response.getStatus().getStatusCode() == HttpStatus.SC_OK) cb.onSuccess();
				else cb.onFailure(new UnexpectedServerResponseException());
			}

			@Override
			public void failed(Throwable cause)
			{
				cb.onFailure(cause);
			}

			@Override
			public void cancelled()
			{
				cb.onFailure(new ActionCancelledException());
			}

		});
	}

	/**
	 * Metodo da utilizzare per ricevere gli id dei cruciverba iniziati dall'utente.
	 * @param cb la callback de utilizzare per ricevere gli id.
	 */
	public void getStartedCrosswords(final Callback<Set<String>> cb)
	{
		Map<String, String> parameters = new HashMap<String,String>();
		HttpRequest request;
		try{
			request = buildAuthHttpRequestGet(parameters, Actions.STATUS_CROSSWORD);
		} catch (AuthenticationRequiredException e){
			cb.onFailure(e);
			return;
		}
		getInformationAsSet(request, cb);
	}

	public void getAndSaveCrosswords(Language language, final LimitedDomains domain, final GameType gameType, String level, Difficult difficulty){
		RequestManager.getInstance().getCrossword(language, domain, gameType, level+"", difficulty, new Callback<List<JsonValue>>() {
			@Override
			public void onSuccess(List<JsonValue> result) {
				System.out.println(result);
				System.out.println("NUMBERO DI CRUCIVERBA RETRIEVED: " + result.size());

				for (JsonValue js : result){
					FileManager.getInstance().saveNewCrossword(js.getString(Parameters.ID_CROSSWORD),js.getString(Parameters.VALUE),
							js.getString(Parameters.CROSSWORD_LANGUAGE), domain.name(), gameType.name(),
							js.getString(Parameters.LEVEL), js.getString(Parameters.DIFFICULT));
				}
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
	}

	private List<NeededCrossword> neededInitialCrosswords() {
		LimitedDomains domain = LimitedDomains.MIXED; //TODO: Se poi viene implementata nuovamente la divisione in domini eliminare questo e aggiungere un for

		List<NeededCrossword> needed = new ArrayList<NeededCrossword>();
		for (Language language : Language.values())
			for (Difficult difficulty : Difficult.values())
				for (GameType gt : GameType.values())
					if (gt.equals(GameType.FREEPLAY)) {
						if (FileManager.getInstance().availableCrosswords(language, domain, gt, -1+"", difficulty) < MIN_CROSSWORDS)
							needed.add(new NeededCrossword(language, domain, gt, -1, difficulty));
					} else {
						for (int level = 1 ; level <= FileManager.getInstance().getLevel(); level++) {
							if (FileManager.getInstance().availableCrosswords(language, domain, gt, level + "", difficulty) < MIN_CROSSWORDS)
								needed.add(new NeededCrossword(language, domain, gt, level, difficulty));
						}
					}
		return needed;
	}

	public void getInitialCrosswords(){
		List<NeededCrossword> needed = neededInitialCrosswords();
		for (final NeededCrossword nc : needed)
			getAndSaveCrosswords(nc.getLanguage(), nc.getDomain(), nc.getGametype(), nc.getLevel() + "", nc.getDifficulty());
	}
	
	/**
	 * Metodo con il quale si rimuove lo stato di un cruciverba iniziato.
	 * @param crosswordId id del cruciverba.
	 * @param cb la callback da utilizzare per verificare l'esito dell'operazione.
	 */
	public void removeStatusCrossword(String crosswordId, final CallbackVoid cb)
	{
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(Parameters.CROSSWORD_ID, crosswordId);
		setUserInformation(parameters,Actions.REMOVE_STATUS,cb);
	}
	
	/**
	 * Metodo con il quale si salva un cruciverba finito.
	 * @param crosswordId id del cruciverba finito.
	 * @param cb la callback da utilizzare per verificare l'esito dell'operazione.
	 */
	public void setFinishedCrossword(String crosswordId, final CallbackVoid cb)
	{
		Map<String, String> parameters = new HashMap<String,String>();
		parameters.put(Parameters.CROSSWORD_ID, crosswordId);
		setUserInformation(parameters,Actions.FINISHED_CROSSWORD,cb);
	}
	
	/**
	 * Metodo da utilizzare per salvare gli id dei crossword finiti.
	 * @param crosswordsId id dei crossword
	 * @param cb la callback da utilizzare per verificare l'esito dell'operazione.
	 */
	public void setFinishedCrosswords(Set<String> crosswordsId, final CallbackVoid cb)
	{
		Map<String,String> parameters = new HashMap<String,String>();
		String set = new Json().toJson(crosswordsId);
		parameters.put(Parameters.CROSSWORDS_ID, set);
		setUserInformation(parameters,Actions.FINISHED_CROSSWORD,cb);
	}
	
	/**
	 * Metodo da utilizzare per ricevere gli id dei cruciverba finiti.
	 * @param cb la callback da utilizzare per ricevere gli id.
	 */
	public void getFinishedCrosswords(final Callback<Set<String>> cb) {
		Map<String, String> parameters = new HashMap<String,String>();
		HttpRequest request;
		try {
			request = buildAuthHttpRequest(parameters, Actions.FINISHED_CROSSWORD);
		} catch (AuthenticationRequiredException e) {
			cb.onFailure(e);
			return;
		}
		getInformationAsSet(request, cb);
	}

	// ------------------------- METODI POPUP ---------------------------------

	public void getSenses(final String lemma, String synsetID, final Callback cb) {
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(SYNSET_ID, synsetID);

		final HttpRequest request = buildHttpRequestGet(parameters, Actions.POPUP_SENSES);
		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse response) {
				String value = response.getResultAsString();
				System.out.println(value);

				JsonValue base = new JsonReader().parse(value);
				System.out.println(base);
				if (!base.isNull()) {
					ArrayList<Sense> retrieved = new ArrayList<Sense>();
					for (JsonValue senses : base) {
						System.out.println(senses.toString());
						String synsetID = senses.getString(SYNSET_ID);
						String lemma = senses.getString(LEMMA);

						JsonValue sources_json = senses.get(SENSES);
						System.out.println(sources_json);
						ArrayList<SenseInfo> senseInfos = new ArrayList<SenseInfo>();
						for (JsonValue sourceInfo : sources_json) {
							String senseID = sourceInfo.getString(SENSEID);
							String source = sourceInfo.getString(SOURCE);
							senseInfos.add(new SenseInfo(senseID, source));
						}

                        Sense sense = new Sense(lemma, synsetID, senseInfos);
						retrieved.add(sense);
					}

					List<Sense> correlated = new ArrayList<Sense>();
					for (Sense s : retrieved)
						if (!s.getLemma().equals(lemma))
							correlated.add(s);

					cb.onSuccess(correlated);
					return;
				} else cb.onFailure(new UnexpectedServerResponseException());
			}


			@Override
			public void failed(Throwable cause) {
				cb.onFailure(cause);
			}

			@Override
			public void cancelled() {
				cb.onFailure(new ActionCancelledException());
			}
		});
	}



	public void sendStatistics(String lemma, String synsetID, Boolean right, final CallbackVoid cb){
		Map<String,String> parameters = new HashMap<String,String>();
		System.out.println("Sending lemma: " + lemma);
		System.out.println("Sending sysnet ID:" + synsetID);
		parameters.put(LEMMA, lemma);
		parameters.put(SYNSET_ID, synsetID);
		parameters.put(Parameters.RIGHT, right.toString());
		final HttpRequest request = buildHttpRequest(parameters, Actions.POPUP_STATISTICS);


		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				cb.onSuccess();
			}

			@Override
			public void failed(Throwable cause) {
				cb.onFailure(cause);
			}

			@Override
			public void cancelled() {
				cb.onFailure(new ActionCancelledException());
			}
		});
	}

	public void sendUserStatistics(int wordnetCorrect, int wordnetTotal, final CallbackVoid cb)  {
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Parameters.WORDNET_CORRECT, wordnetCorrect+"");
		parameters.put(Parameters.WORDNET_TOTAL, wordnetTotal+"");

		System.out.println("Wordnet Correct" + wordnetCorrect);
		System.out.println("Wordnet Total" + wordnetTotal);
		HttpRequest request = null;
		try {
			request = buildAuthHttpRequest(parameters, Actions.POPUP_USER_STATISTICS);
			Gdx.net.sendHttpRequest(request, new HttpResponseListener() {

				@Override
				public void handleHttpResponse(HttpResponse httpResponse) {
					System.out.println("Sent users statistics");
					cb.onSuccess();
				}

				@Override
				public void failed(Throwable cause) {
					cb.onFailure(cause);
				}

				@Override
				public void cancelled() {
					cb.onFailure(new ActionCancelledException());
				}
			});
		} catch (AuthenticationRequiredException e) {
			cb.onFailure(e);
			return;
		}

	}

	
	// ------------------------------------ METODO FACEBOOK ------------------------------
	
	public void  sendFacebookToken(String token, final CallbackVoid cb)
	{
	    HttpRequest request = new HttpRequest(HttpMethods.POST);
	    Map<String,String> parameters = new HashMap<String,String>();
	    parameters.put("token",token); // token va messo nella classe Parameter
		System.out.println("Token in class: "+ token);
	    request.setUrl(connectionManager.getFullUrl("facebook")); // facebookService va messp nella classe Action
		System.out.println("Query "+ HttpParametersUtils.convertHttpParameters(parameters));
		request.setContent(HttpParametersUtils.convertHttpParameters(parameters));
	    request.setTimeOut(TIMEOUT);
	    Gdx.net.sendHttpRequest(request, new HttpResponseListener()
			{
				@Override
				public void handleHttpResponse(HttpResponse response)
				{
					String session = (String) response.getHeaders().get("JSESSIONID").get(0);


					String value = response.getResultAsString();
					if(response.getStatus().getStatusCode() == HttpStatus.SC_OK) cb.onSuccess();
					else cb.onFailure(new UnexpectedServerResponseException());
				}

				@Override
				public void failed(Throwable cause)
				{
					cb.onFailure(cause);
				}

				@Override
				public void cancelled()
				{
					cb.onFailure(new ActionCancelledException()); 
				}
				
			});
	    
	}
	
	// ----------------------------------- METODO IMAGE ----------------------------------------
	
	
	public void getImage(int pixel, String url, final Callback<Pixmap> cb)
	{
		// metodo da implementare
	}
	
	public void getImage(String url, final Callback<Pixmap> cb)
	{
		getImage(null,url,cb);
	}
	
	/**
	 * Metodo con il quale si effettua la richiesta di un immagine.
	 * @param url url dell'immagine.
	 * @param cb la callback da utilizzare per ricevere l'immagine.
	 */
	public void getImage(Map<String,String> parameters, String url, final Callback<Pixmap> cb)
	{
		HttpRequest request = new HttpRequest(HttpMethods.GET);
		if(parameters != null)
			request.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		request.setUrl(url);
		//request.setTimeOut(TIMEOUT);
		Gdx.net.sendHttpRequest(request, new HttpResponseListener()
				{
					@Override
					public void handleHttpResponse(HttpResponse response)
					{
						try
						{
							final byte[] rawImageByte = response.getResult();
							Pixmap pixmap = new Pixmap(rawImageByte,0,rawImageByte.length);
							cb.onSuccess(pixmap);
						}
						catch(Exception e)
						{
							cb.onFailure(e);
						}
					}

					@Override
					public void failed(Throwable cause)
					{
						cb.onFailure(cause);
					}

					@Override
					public void cancelled()
					{
						cb.onFailure(new ActionCancelledException());
					}
			
				});
	}
	
	// -------------------------------------- METODO X LE STATISTICHE ----------------------------
	
	/**
	 * Metodo da utilizzare per inviare informazioni sulla risposta dell'utente.
	 * @param definitionId id della definizione
	 * @param word parola inserita dall'utente.
	 * @param cb la callback da utilizzare per verificare l'esito dell'operazione
	 */
	public void sendAnnotation(String definitionId, String word, final CallbackVoid cb)
	{
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Parameters.DEFINITION_ID, definitionId);
		parameters.put(Parameters.WORD, word);
		Gdx.net.sendHttpRequest(buildHttpRequest(parameters,Actions.ANNOTATION), new HttpResponseListener()
				{
					@Override
					public void handleHttpResponse(HttpResponse response)
					{
						String value = response.getResultAsString();
						if(value.equals("true")) cb.onSuccess();
						else cb.onFailure(new UnexpectedServerResponseException());
					}

					@Override
					public void failed(Throwable cause)
					{
						cb.onFailure(cause);
					}

					@Override
					public void cancelled()
					{
						cb.onFailure(new ActionCancelledException()); 
					}
					
				});
	}
	
	// ------------------------------------ METODI SCORE E MONEY ----------------------------------------------

	/**
	 * Metodo con il quale si effettua la richiesta della classifica degli utenti.
	 * @param numUsers numero di utenti della classifica.
	 * @param cb la callback da utilizzare per ricevere la map che rappresenta la classifica.
	 */
	public void getHighScores(String numUsers, final MainThreadCallback<ArrayList<String>> cb)
	{
		Map<String, String> parameters = new HashMap<String,String>();
		parameters.put(Parameters.NUM_USERS,numUsers);
		HttpRequest request;
		try{
			request = buildAuthHttpRequestGet(parameters, Actions.HIGHSCORE);
		} catch (AuthenticationRequiredException e){
			request = buildHttpRequestGet(parameters, Actions.HIGHSCORE);
		}
		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse response) {
				String value = response.getResultAsString();
				JsonValue jsonValue = new JsonReader().parse(value);
				ArrayList<String> scores = new ArrayList<String>();
				for (int i = 0; i < jsonValue.size; i++) {
					System.out.println("Dentro Request Manager" + jsonValue.getString(i));
					scores.add(jsonValue.getString(i));
				}
				cb.onSuccess(scores);
			}

			@Override
			public void failed(Throwable cause) {
				cb.onFailure(cause);
			}

			@Override
			public void cancelled() {
				cb.onFailure(new ActionCancelledException());
			}
		});
	}


	public void setUserScore(Map<String, String> userScore, final CallbackVoid cb){
		try {
			Gdx.net.sendHttpRequest(buildAuthHttpRequest(userScore, Actions.SCORE), new HttpResponseListener() {
				@Override
				public void handleHttpResponse(HttpResponse response) {
					int  statusCode = response.getStatus().getStatusCode();
					if (statusCode == HttpStatus.SC_OK) cb.onSuccess();
					else cb.onFailure(new UnexpectedServerResponseException());
				}

				@Override
				public void failed(Throwable cause)
				{
					cb.onFailure(cause);
				}

				@Override
				public void cancelled()
				{
					cb.onFailure(new ActionCancelledException());
				}

			});
		} catch (AuthenticationRequiredException e) {
			cb.onFailure(e);
		}
	}

	public void getUserScore(final Callback<Map<String, Float>> cb){

        Map<String, String> parameters = new HashMap<String,String>();
        HttpRequest request;
        try{
            request = buildAuthHttpRequestGet(parameters, Actions.SCORE);
        } catch (AuthenticationRequiredException e){
            cb.onFailure(e);
            return;
        }

		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse response)
			{
				String value = response.getResultAsString();
				Map<String, Float> result = new Json().fromJson(new HashMap<String, Float>().getClass(), value);
				if(result.isEmpty()) cb.onFailure(new UnexpectedServerResponseException());
				else cb.onSuccess(result);
			}

			@Override
			public void failed(Throwable cause)
			{
				cb.onFailure(cause);
			}

			@Override
			public void cancelled()
			{
				cb.onFailure(new ActionCancelledException());
			}

		});
	}

	// --------------------- METODO RECOVER STATO DEI CROSSWORDS ---------------
	public void recoverCrosswordState(){
		RequestManager.getInstance().getStartedCrosswords(new Callback<Set<String>>() {
			@Override
			public void onSuccess(Set<String> result) {
                for (final String crosswordId : result) {
					RequestManager.getInstance().getCrossword(crosswordId, new Callback<List<JsonValue>>(){
						@Override
						public void onSuccess(final List<JsonValue> crosswords) {
							RequestManager.getInstance().getStatusCrossword(crosswordId, new Callback<Set<String>>() {
								@Override
								public void onSuccess(Set<String> status) {
									JsonValue crossInfo = crosswords.get(0);
									GameType gametype = crossInfo.getString(Parameters.LEVEL).equals("-1") ? GameType.FREEPLAY : GameType.RANKED;
									FileManager.getInstance().saveStartedCrossword(crosswordId,
											Language.valueOf(crossInfo.getString(Parameters.CROSSWORD_LANGUAGE)),
											LimitedDomains.MIXED, //TODO: Se poi dovessero essere riaggiunti i domini va cambiato
											gametype,
											crossInfo.getString(Parameters.LEVEL),
											Difficult.valueOf(crossInfo.getString(Parameters.DIFFICULT)),
											crossInfo.getString(Parameters.VALUE),
											status);
									MainCrucy.getMain().setSynchron(true);
																										}
								@Override
								public void onFailure(Throwable cause) {
									MainCrucy.getMain().setSynchron(false);
									cause.printStackTrace(); }
							});
						}
						@Override
						public void onFailure(Throwable cause) {
							MainCrucy.getMain().setSynchron(false);
							cause.printStackTrace(); }
					});
				}
			}

			@Override
			public void onFailure(Throwable cause) {
				MainCrucy.getMain().setSynchron(false);
				cause.printStackTrace();
			}
		});
	}


	// --------------------- METODI UTILITA ------------------------------------
	
	/**
	 * Metodo di utilita' privato che effettua effettivamente la richiesta per ricevere informazioni.
	 * @param request la richiesta da effettuare.
	 * @param cb la callback da utilizzare per ricevera la map delle informazioni.
	 */
	private void getInformationAsMap(HttpRequest request, final Callback<Map<String, String>> cb) {
		Gdx.net.sendHttpRequest(request, new HttpResponseListener()
			{
			@Override
			public void handleHttpResponse(HttpResponse response)
			{
				String value = response.getResultAsString();
				Map<String, String> result = new Json().fromJson(new HashMap<String, String>().getClass(), value);
				if(result.isEmpty()) cb.onFailure(new UnexpectedServerResponseException());
				else cb.onSuccess(result);
			}

			@Override
			public void failed(Throwable cause)
			{
				cb.onFailure(cause);
			}

			@Override
			public void cancelled()
			{
				cb.onFailure(new ActionCancelledException()); 
			}
				
		});
	}
	
	/**
	 * Metodo di utilita' privato che effettua effettivamente la richiesta per ricevere informazioni.
	 * @param request la richiesta da effettuare.
	 * @param cb la callback da utilizzare per ricevere il set delle informazioni.
	 */
	private void getInformationAsSet(HttpRequest request, final Callback<Set<String>> cb)
	{
		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			
			@Override
			public void handleHttpResponse(HttpResponse response)
			{
				String value = response.getResultAsString();
				Set<String> result = new Json().fromJson(new HashSet<String>().getClass(), value);
				cb.onSuccess(result);
			}
			
			@Override
			public void failed(Throwable cause)
			{
				cb.onFailure(cause);				
			}
			
			@Override
			public void cancelled()
			{
				cb.onFailure(new ActionCancelledException());
			}
		});
	}

    private void getInformationAsList(HttpRequest request, final Callback<List<JsonValue>> cb)
    {
        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {

            @Override
            public void handleHttpResponse(HttpResponse response) {
                String value = response.getResultAsString();
                ArrayList<JsonValue> result = new Json().fromJson(new ArrayList<JsonValue>().getClass(), value);
                cb.onSuccess(result);
            }

            @Override
            public void failed(Throwable cause)
            {
                cb.onFailure(cause);
            }

            @Override
            public void cancelled()
            {
                cb.onFailure(new ActionCancelledException());
            }
        });
    }



    /**
	 * Metodo di utilita' privato che effettua effettivamente la richiesta per salvare informazioni.
	 * @param parameters mappa dei parametri della richiesta.
	 * @param action il tipo di richiesta da effetturare.
	 * @param cb la callback da utilizzare per verificare l'esito dell'operazione.
	 */
	private void setUserInformation(Map<String,String> parameters, String action, final CallbackVoid cb) {
		try {
			Gdx.net.sendHttpRequest(buildAuthHttpRequest(parameters,action), new HttpResponseListener() {
				@Override
				public void handleHttpResponse(HttpResponse response) {
					String value = response.getResultAsString();
					System.out.println(value);
					if(value.equals("true")) cb.onSuccess();
					else cb.onFailure(new UnexpectedServerResponseException());
				}

				@Override
				public void failed(Throwable cause) {
					cb.onFailure(cause);
				}

				@Override
				public void cancelled()
				{
					cb.onFailure(new ActionCancelledException()); 
				}
				
			});
		} catch (AuthenticationRequiredException e) {
			cb.onFailure(e);
		}
	}
	
	/**
	 * Metodo di utilita' con il quale si costruisce le richieste.
	 * @param parameters map dei parametri da inserire nella richiesta.
	 * @param action tipo di richiesta.
	 * @return un oggetto di tipo HttpRequest.
	 */
	private HttpRequest buildHttpRequest(Map<String,String> parameters, String action)
	{
		HttpRequest request = new HttpRequest(HttpMethods.POST);
		request.setUrl(connectionManager.getFullUrl(action));
		request.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		request.setHeader("Cookie", "JSESSIONID="+connectionManager.getExsternalSession());
		request.setTimeOut(TIMEOUT);
		return request;
	}

	private HttpRequest buildHttpRequestGet(Map<String,String> parameters, String action)
	{
		HttpRequest request = new HttpRequest(HttpMethods.GET);
		request.setUrl(connectionManager.getFullUrl(action));
		request.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		request.setHeader("Cookie", "JSESSIONID="+connectionManager.getExsternalSession());
		request.setTimeOut(TIMEOUT);
		return request;
	}
	
	/**
	 * Metodo di utilita' con il quale si costruisce una richiesta autenticata.
	 * @param parameters map dei parametri da inserire nella richiesta.
	 * @param action tipo di richiesta.
	 * @return un oggetto di tipo HttpRequest.
	 */
	private HttpRequest buildAuthHttpRequest(Map<String,String> parameters, String action) throws AuthenticationRequiredException
	{
		if(!connectionManager.isLogged()) throw new AuthenticationRequiredException();
		HttpRequest request = buildHttpRequest(parameters, action);
		System.out.println("SESSION ID: " + connectionManager.getSessionId());
        System.out.println("SESSION EMAIL: " + connectionManager.getEmail());
        request.setHeader("Cookie", "JSESSIONID="+connectionManager.getSessionId());
		return request;
	}

	private HttpRequest buildAuthHttpRequestGet(Map<String,String> parameters, String action) throws AuthenticationRequiredException
	{
		if(!connectionManager.isLogged()) throw new AuthenticationRequiredException();
		HttpRequest request = buildHttpRequestGet(parameters, action);
		System.out.println("SESSION ID: " + connectionManager.getSessionId());
		System.out.println("SESSION EMAIL: " + connectionManager.getEmail());
		request.setHeader("Cookie", "JSESSIONID="+connectionManager.getSessionId());
		return request;
	}
	
	
}
