package it.uniroma1.lcl.crucy.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import it.uniroma1.lcl.crucy.cache.exceptions.ActionCancelledException;
import it.uniroma1.lcl.crucy.cache.exceptions.AuthenticationRequiredException;
import it.uniroma1.lcl.crucy.cache.exceptions.EmailAlreadyUsedException;
import it.uniroma1.lcl.crucy.cache.exceptions.InvalidEmailException;
import it.uniroma1.lcl.crucy.cache.exceptions.InvalidPasswordException;
import it.uniroma1.lcl.crucy.cache.exceptions.InvalidUsernameException;
import it.uniroma1.lcl.crucy.cache.exceptions.SessionExpiredException;
import it.uniroma1.lcl.crucy.cache.exceptions.SynchronizedExeption;
import it.uniroma1.lcl.crucy.cache.exceptions.UnexpectedServerResponseException;
import it.uniroma1.lcl.crucy.cache.exceptions.UserDoesntExists;
import it.uniroma1.lcl.crucy.cache.exceptions.UserWrongPassword;
import it.uniroma1.lcl.crucy.cache.retrievers.InformationRetriever;
import it.uniroma1.lcl.crucy.cache.retrievers.InformationRetrieverFactory;
import it.uniroma1.lcl.crucy.cache.utils.Actions;
import it.uniroma1.lcl.crucy.cache.utils.Difficult;
import it.uniroma1.lcl.crucy.cache.utils.Language;
import it.uniroma1.lcl.crucy.cache.utils.Parameters;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.Sense;
import it.uniroma1.lcl.crucy.utils.GameType;
import it.uniroma1.lcl.crucy.utils.LimitedDomains;


/**
 * Classe con il quale si effettua la connessione del l'utente al server.
 * @author dario
 *
 */
public class ConnectionManager
{
	private final static String USERNAME_REGEX = "^[a-zA-Z0-9\\-\\_]{4,16}$";
	private final static String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*-_])(?=\\S+$).{8,}$";
	private final static String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final int TIMEOUT = 5000;
	
	private String email;
	private String username;
	private String sessionId;
	
	private String serverUrl;
	private String connLanguage;

	private InformationRetriever informationRetriever;
	// -------------------------- 	COSTRUTTORE E SINGLETON ---------------------------
    
	private static ConnectionManager instance;
    
    private ConnectionManager() {
    	informationRetriever = InformationRetrieverFactory.create();
    	informationRetriever.init();
    	connLanguage = informationRetriever.getConnLanguage();
		serverUrl = informationRetriever.getServerUrl();
	}
   
    public static ConnectionManager getInstance() {
       if(instance == null) instance = new ConnectionManager();
       return instance;
    }
    
    // ------------------------- METODI UTENTE ----------------------------------
    
    /**
     * Metodo con il quale si registra il nuovo utente.
     * @param username username dell'utente.
     * @param password password dell'utente.
     * @param email email dell'utente.
     * @param cb la callback da utilizzare per verificare l'esito dell'operazione.
     */
    public void register(final String username,final String password,final String email,final CallbackVoid cb) {
    	try{
    		if(!checkValidString(email, EMAIL_REGEX)) throw new InvalidEmailException(email);
    		if(!checkValidString(username, USERNAME_REGEX)) throw new InvalidUsernameException(username);
    		if(!checkValidString(password, PASSWORD_REGEX)) throw new InvalidPasswordException();
    	} catch(IllegalArgumentException e) {
    		cb.onFailure(e);
			return;
    	}
    	
    	Map<String,String> parameters = new HashMap<String,String>();
    	parameters.put(Parameters.USERNAME, username);
    	parameters.put(Parameters.PASSWORD, password);
    	parameters.put(Parameters.EMAIL, email);
    	
    	HttpRequest request = buildHttpRequest(parameters,Actions.REGISTER);
    	Gdx.net.sendHttpRequest(request, new HttpResponseListener()
    			{
					@Override
					public void handleHttpResponse(HttpResponse response)
					{
						System.out.println(response.getStatus().getStatusCode());
						if(response.getStatus().getStatusCode() == 200) cb.onSuccess();
						else cb.onFailure(new EmailAlreadyUsedException(email));
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
     * Metodo con il quale viene effettuata la richiesta di login dell'utente.
     * @param email email dell'utente.
     * @param password password dell'utente.
     * @param cb la callback da utilizzare per verificare l'esito della richiesta.
     */
    public void login(String email, String password, final CallbackVoid cb) {
    	try {
    		login(buildParameters(email,password), cb);
    	} catch(IllegalArgumentException e) {
    		cb.onFailure(e);
			return;
    	}
    }

	public void sendFacebookToken(String token, final CallbackVoid cb) {
		HttpRequest request = new HttpRequest(HttpMethods.POST);
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("token",token); // token va messo nella classe Parameter
		request.setUrl(this.getFullUrl("facebook")); // facebookService va messp nella classe Action
		request.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		request.setTimeOut(TIMEOUT);
		Gdx.net.sendHttpRequest(request, new HttpResponseListener()
		{
			@Override
			public void handleHttpResponse(HttpResponse response)
			{
				ConnectionManager.this.sessionId = (String) response.getHeaders().get("Set-Cookie").get(0)
						.replaceFirst("JSESSIONID=", "");


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
     * Metodo con il quale viene effettuata la richiesta di login dell'utente che possiede gia' sessionId.
     * @param sessionId sessionId dell'utente.
     * @param cb la callback da utilizzare per verificare l'esito della richiesta.
     */
    public void login(String sessionId, final CallbackVoid cb) {
    	try {
    		final HttpRequest request = buildHttpRequest(new HashMap<String, String>(), Actions.LOGIN);
    		request.setHeader("Cookie", "JSESSIONID="+sessionId);

			Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
				@Override
				public void handleHttpResponse(HttpResponse response)
				{
					System.out.println("Status code: " + response.getStatus().getStatusCode());
					String value = response.getResultAsString();
					JsonValue jsonValue = new JsonReader().parse(value);
					System.out.println(jsonValue);

					if (response.getStatus().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
						cb.onFailure(new SessionExpiredException());

						return;
					}

					boolean success = false;
					System.out.println(jsonValue);

					if(jsonValue.has(Parameters.SESSION_ID))
					{
						String temp = jsonValue.getString(Parameters.SESSION_ID);
						if(!temp.equals("null"))
						{

							ConnectionManager.this.sessionId = temp;
							ConnectionManager.this.username = jsonValue.getString(Parameters.USERNAME);
							ConnectionManager.this.email = jsonValue.getString(Parameters.EMAIL);
							System.out.println(jsonValue.getString(Parameters.USERNAME));


							success = true;
						}
					}

					if(success) cb.onSuccess();
					else {
						cb.onFailure(new UnexpectedServerResponseException());
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
    	catch(IllegalArgumentException e)
    	{
    		cb.onFailure(e);
			return;
    	}
    }
    
    /**
     * Metodo di utilita' che crea la map dei parametri da inserire nella richiesta.
     * @param password password dell'utente.
     * @return un oggetto di tipo Map<String,String>.
     * @throws IllegalArgumentException se username o password non rispettano il formato prestabilito.
     */
    private Map<String,String> buildParameters(String email, String password) throws IllegalArgumentException {

    	Map<String,String> parameters = new HashMap<String,String>();
    	parameters.put(Parameters.EMAIL, email);
    	parameters.put(Parameters.PASSWORD, password);
    	return parameters;
    }
    
    /**
     * Metodo con il quale viene effettuata "effettivamente" la richiesta di login dell'utente.
     * @param parameters map dei parametri da inserire nella richiesta.
     * @param cb la callback da utilizzare per verificare l'esito della richiesta.
     */
    private void login(Map<String,String> parameters, final CallbackVoid cb) {
    	HttpRequest request = buildHttpRequest(parameters, Actions.LOGIN);
		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse response) {
				if (response.getStatus().getStatusCode() == HttpStatus.SC_NOT_FOUND) cb.onFailure(new UserDoesntExists());
				else if (response.getStatus().getStatusCode() == HttpStatus.SC_FORBIDDEN) cb.onFailure(new UserWrongPassword());
				else if (response.getStatus().getStatusCode() == HttpStatus.SC_BAD_REQUEST) cb.onFailure(new UnexpectedServerResponseException());
				else {
					String value = response.getResultAsString();
					JsonValue jsonValue = new JsonReader().parse(value);

					boolean success = false;
					System.out.println(jsonValue);

					if(jsonValue.has(Parameters.SESSION_ID)) {
						String temp = jsonValue.getString(Parameters.SESSION_ID);
						if(!temp.equals("null")) {
							ConnectionManager.this.sessionId = temp;
							ConnectionManager.this.username = jsonValue.getString(Parameters.USERNAME);
							ConnectionManager.this.email = jsonValue.getString(Parameters.EMAIL);
							success = true;
						}
					}

					if(success) cb.onSuccess();
					else cb.onFailure(new UnexpectedServerResponseException());
				}
			}

				@Override
				public void failed(Throwable cause) { cb.onFailure(cause); }

				@Override
				public void cancelled() { cb.onFailure(new ActionCancelledException()); }
			});
    }
    
    /**
     * Metodo con il quale si effettua la richiesta di logout e si resetta il ConnectionManager.
     * @param cb la callback da utilizzare per verificare l'esito dell'operazione.
     */
    public void logout(final CallbackVoid cb) {
    	HttpRequest request = buildHttpRequestGet(new HashMap<String,String>(), Actions.LOGOUT);
    	request.setHeader("Cookie", "JSESSIONID="+getSessionId());
    	Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse response) {
				cb.onSuccess();
				reset();
			}

			@Override
			public void failed(Throwable cause) {
				cb.onFailure(cause);
				reset();
			}

			@Override
			public void cancelled() {
				cb.onFailure(new ActionCancelledException());
				reset();
			}

		});

    }


	// ------------------------- METODI FB --------------------------------------

	/**
	 * Metodo con il quale viene effettuata la richiesta di login attraverso
	 * facebook dell'utente.
	 * @param email email dell'utente.
	 * @param username Nome e cognome dell'utente.
	 * @param cb la callback da utilizzare per verificare l'esito della richiesta.
	 */
	public void loginFb(String email, String username,final CallbackVoid cb) {
		try { login(buildParametersFb(email,username), cb);
		} catch(IllegalArgumentException e) {
			cb.onFailure(e);
			return;
		}
	}

	/**
	 * Metodo con il quale viene effettuata la richiesta di login attraverso
	 * facebook dell'utente.
	 * @param email email dell'utente.
	 * @param username Nome e cognome dell'utente.
	 * @param sessionId sessionId dell'utente.
	 * @param cb la callback da utilizzare per verificare l'esito della richiesta.
	 */
	public void loginFb(String email, String username, String sessionId, final CallbackVoid cb) {
		try {
			Map<String,String> parameters = buildParametersFb(email,username);
			parameters.put(Parameters.SESSION_ID, sessionId);
			login(parameters, cb);
		} catch(IllegalArgumentException e) {
			cb.onFailure(e);
			return;
		}
	}

	/**
	 * Metodo di utilita' che crea la map dei parametri da inserire nella richiesta.
	 * @param email email dell'utente.
	 * @param username Nome e cognome dell'utente.
	 * @return un oggetto di tipo Map<String,String>.
	 */
	private Map<String,String> buildParametersFb(String email, String username) throws IllegalArgumentException {
		if(!checkValidString(email, EMAIL_REGEX)) throw new InvalidEmailException(email);

		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Parameters.EMAIL, email);
		parameters.put(Parameters.USERNAME, username);
		return parameters;
	}


	// ------------------------- METODI UTILITA ---------------------------------
    
    /**
     * Metodo di utilita' che resetta il ConnectionManager.
     */
    private void reset() {
    	username = null;
    	sessionId = null;  
    	email = null;
    }
    
    /**
     * Metodo di utilita' che restituisce la lingua di connessione al server.
     * @return la lingua di connessione.
     */
    public String getLanguage() {
    	return connLanguage;
    }
    
    /**
     * Metodo di utilita' che restituisce la email dell'utente.
     * @return l'email dell'utente.
     */
    public String getEmail() {
    	return email;
    }
    
    /**
     * Metodo di utilita' che restituisce l'username dell'utente.
     * @return l'username.
     */
    public String getUsername() {
    	return username;
    }
    
    /**
     * Metodo di utilita' che restituisce l'id della sessione dell'utente.
     * @return id della sessione.
     */
    public String getSessionId() {
    	return sessionId;
    }
    
    /**
     * Metodo di utilita' che restituisce una sessione esterna.
     * @return la sessione esterna.
     */
    public String getExsternalSession() {
    	return informationRetriever.getExsternalSession();
    }
    
    /**
     * Metodo di utilita' che restituisce l'url completo per la richiesta.
     * @param action 
     * @return l'url completo.
     */
    public String getFullUrl(String action) {
    	return serverUrl + "/" + action;
    }
    
    /**
     * Metodo di utilita'�che restituisce true se l'utente � loggato, altrimenti false.
     * @return true se loggato, altrimenti false.
     */
   public boolean isLogged() {return sessionId!=null; }
   
   /**
    * Metodo di utilita' che restituisce true se la string s contiene solo caratteri contenuti in characters.
    * @param s stringa da controllare.
    * @return true se la stringa e' valida, altrimenti false.
    */
   private boolean checkValidString(String s, String regex) {
	   return s != null && Pattern.matches(regex, s);
   }
   
   /**
    * Metodo di utilita'
    * @param parameters
    * @param action
    * @return
    */
   private HttpRequest buildHttpRequest(Map<String,String> parameters, String action) {
	   HttpRequest request = new HttpRequest(HttpMethods.POST);
	   request.setUrl(getFullUrl(action));
	   System.out.println(getFullUrl(action));
	   request.setTimeOut(TIMEOUT);
	   request.setContent(HttpParametersUtils.convertHttpParameters(parameters));
	   return request;
   }

	/**
	 * Metodo di utilita'
	 * @param parameters
	 * @param action
	 * @return
	 */
	private HttpRequest buildHttpRequestGet(Map<String,String> parameters, String action) {
		HttpRequest request = new HttpRequest(HttpMethods.GET);
		request.setUrl(getFullUrl(action));
		request.setTimeOut(TIMEOUT);
		request.setContent(HttpParametersUtils.convertHttpParameters(parameters));
        System.out.println(getFullUrl(action));
        System.out.println(request.getContent());
        return request;
	}
     
}
