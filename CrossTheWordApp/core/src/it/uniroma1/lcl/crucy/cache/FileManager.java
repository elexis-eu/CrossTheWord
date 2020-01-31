package it.uniroma1.lcl.crucy.cache;

import java.io.File;
import java.io.FileFilter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

import it.uniroma1.lcl.crucy.cache.utils.Difficult;
import it.uniroma1.lcl.crucy.mainconstants.RankedXP;
import it.uniroma1.lcl.crucy.menu.Level;
import it.uniroma1.lcl.crucy.menu.SimpleImageButton;
import it.uniroma1.lcl.crucy.utils.GameType;
import it.uniroma1.lcl.crucy.utils.LevelStructure;
import it.uniroma1.lcl.crucy.utils.LimitedDomains;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.GameSelectionMode;
import it.uniroma1.lcl.crucy.cache.utils.CrosswordSize;
import it.uniroma1.lcl.crucy.cache.utils.Language;
import it.uniroma1.lcl.crucy.cache.utils.Parameters;
import sun.security.krb5.internal.PAData;

// come è fatto il path delle cartelle cartelle
// CROSSWORDS_FOLDER/<LimitedsDomains>/<CrosswordSize>/<Difficult>/<Language>/cruciverba
// 


/**
 * Classe che si occupa del caricamento delle informazioni cachate del gioco. Sia per quanto riguarda il giocatore (punteggio, 
 * password etc.) sia per quanto riguarda le impostazioni di gioco.
 * @author Ele1955
 */
public class FileManager 
{
	
	/**
	 * Nome del file di Preferences che contiene le informazioni relative alle impostazioni del gioco.
	 */
	private final String SETTINGS_FILE = "it.uniroma1.lcl.crucy.cache.settings";

	/**
	 * Nome del file di Preferences che contiene le informazioni dell'utente necessarie a recuperare l'ultimo login eseguito e
	 * le informazioni di gioco dell'utente.
	 */
	private final String RECOVER_LOGIN_FILE = "it.uniroma1.lcl.crucy.cache.recoverLogin";
	
	/**
	 * Nome del file di Preferences che contiene le informazioni necessarie a recuperare lo stato iniziale dell'utente.
	 */
	private final String RESET_LOGIN_FILE = "it.uniroma1.lcl.crucy.cache.resetLogin";

	/**
	 * E' il separatore che viene usato per comporre il percorso di cartella in cartella per poter accedere ai file. Variabile
	 * in quanto dipendente dal sistema operativo.
	 */
	private final String SEPARATOR;
	
	/**
	 * Nome della cartella che contiene tutte le informazioni relative ai cruciverba presenti sul dispositivo.
	 */
	 private final String CROSSWORDS_FOLDER = "crossword";

	/**
	 * Nome della cartella che contiene i cruciverba iniziati
	 */
	private final String STARTED_CROSSWORDS_FOLDER = "startedCrosswords";

	/**
	* Nome della cartella che contiene le informazioni relative allo stato dei cruciverba iniziati
 	*/
	private final String STATE_CROSSWORD_FOLDER = "stateCrosswords";
	
	/**
	 * Nome del file che contiene gli indici dei cruciverba terminati sul dispositivo, la cui fine non è ancora stata
	 * notificata al server. 
	 */
	private final String ENDED_CROSSWORDS = "endedCrosswords";

	/**
	 * Esperienza iniziale utente
	 */
	private final String START_EXPERIENCE = "0";

	/**
	 * Esperienza iniziale utente
	 */
	private final String START_LEVEL = "1";

	/**
	 * Diamanti iniziali utente
	 */
	private final String START_DIAMONDS = "200";

	/**
	 * Numero di cruciverba (e quindi file) necessari per ogni tipologia.
	 */
	private final int NEEDED_FILE = 5;

	/**
	 * Quantità di denaro iniziale dell'utente.
	 */
	private final String START_MONEY = "200";

	/**
	 * Array delle informazioni principali che vengono memorizzate per ciascun utente.
	 */
	private final String[] USER_INFO_TAG = {
			Parameters.EMAIL,
			Parameters.USERNAME,
			Parameters.SOCIAL,
			Parameters.SESSION_ID,
			Parameters.PASSWORD,
			Parameters.MONEY,
			Parameters.EXPERIENCE,
			Parameters.DIAMONDS,
			Parameters.LEVEL
	};
	
	/**
	 * Array delle impostazioni del gioco sul dispositivo.
	 */
	private final String[] GAME_SETTINGS_TAG = {
			Parameters.SOUND_FX,
			Parameters.MUSIC,
			Parameters.LANGUAGE,
			Parameters.GAME_MODE,
			Parameters.CROSSWORD_LANGUAGE,
			Parameters.DIFFICULT,
	};
	
	/**
	 * Parametro che tiene traccia dell'id dell'ultimo cruciverba richiesto al dispositivo.
	 */
	private String lastCrosswordID;
	
	/**
	 * Parametro che tiene traccia del dominio dell'ultimo cruciverba richiesto al dispositivo.
	 */
	private LimitedDomains lastDomain;

	/**
	 * Parametro che tiene traccia della difficolta' dell'ultimo cruciverba richiesto al dispositivo.
	 */
	private Difficult lastDifficult;
	
	/**
	 * Parametro che tiene traccia della lingua dell'ultimo cruciverba richiesto al dispositivo.
	 */
	private Language lastLanguage;

	/**
	 * Parametro che tiene traccia della lingua dell'ultimo gametype utilizzato.
	 */
	private GameType lastGameType;

	/**
	 * Parametro che tiene traccia della lingua dell'ultimo livello richiesto.
	 */
	private String lastLevel;


	private final String STATE = "state";


	//################################################################################################################
	//############################-----------COSTRUTTORE E SINGLETON-----------#######################################
	//################################################################################################################

	/**
	 * Unica istanza del FileManager. Segue il singleton pattern.
	 */
	private static FileManager instance;
	
	/**
	 * Costruttore privato del FileManager. Segue il singleton pattern.
	 */
	private FileManager()
	{
		Preferences pref = Gdx.app.getPreferences(RESET_LOGIN_FILE);
		if(pref.get().size() == 0) initLogin();
		Preferences setting = Gdx.app.getPreferences(SETTINGS_FILE);
		if(setting.get().size()==0) initSettings();
		SEPARATOR = "/";
	}
	
	/**
	 * Secondo il singleton pattern, unico metodo che ritorna un'istanza di FileManager.
	 * @return l'unica istanza dell'ogetto.
	 */
	public static FileManager getInstance()
	{
		if(instance==null)instance = new FileManager();
		return instance;
	}

	
	//################################################################################################################
	//###########################----------INIZIALIZZAZIONE PREFERENCES---------######################################
	//################################################################################################################
	
	
	/**
	 * Metodo che viene chiamato solo la prima volta che l'aapplicazione viene eseguita sul dispositivo. Crea i file  
	 * di Preferences relativi al login dell'utente e li inizializza ai valori di default.
	 */
	private void initLogin()
	{
		Map<String,String> data = new HashMap<String,String>();
		data.put(Parameters.USERNAME, Parameters.NO_USER);
		data.put(Parameters.PASSWORD, "");
		data.put(Parameters.SESSION_ID, "");
		data.put(Parameters.SOCIAL, "");

		data.put(Parameters.MONEY, START_MONEY);
		data.put(Parameters.EXPERIENCE, START_EXPERIENCE);
		data.put(Parameters.DIAMONDS, START_DIAMONDS);
		data.put(Parameters.LEVEL, START_LEVEL);
		writePrefFile(RECOVER_LOGIN_FILE,data);
		writePrefFile(RESET_LOGIN_FILE,data); 		//NECESSARIO DOPO LOGOUT
	}
	
	/**
	 * Metodo che viene chiamato solo la prima volta che l'aapplicazione viene eseguita sul dispositivo. Crea i file  
	 * di Preferences relativi alle impostazioni del gioco (es. suono e lingua) e li inizializza ai valori di default.
	 */
	private void initSettings() 
	{
		Map<String,String> data = new HashMap<String,String>();
		data.put(Parameters.GAME_MODE, GameSelectionMode.TAP_AND_SWIPE_MOD.name());
		data.put(Parameters.MUSIC, Boolean.toString(true));
		data.put(Parameters.SOUND_FX, Boolean.toString(true));
		data.put(Parameters.LANGUAGE, Locale.getDefault().toString().equals("it_IT") ? Language.ITALIAN.name() : Language.ENGLISH.name());
		//AGGIUNTA SUCCESSIVA TIENE TRACCIA DELL'ULTIMA SCELTA NEL MENU DEI CRUCIVERBA
		data.put(Parameters.DIFFICULT, Difficult.EASY.name());
		data.put(Parameters.CROSSWORD_LANGUAGE, Locale.getDefault().toString().equals("it_IT") ? Language.ITALIAN.name() : Language.ENGLISH.name());
		writePrefFile(SETTINGS_FILE,data);
	}
	
	
	
	
	//################################################################################################################
	//#############################----------- PREFERENCES DEL LOGIN -----------######################################
	//################################################################################################################
	
	
	/**
	 * Metodo che permette di salvare le informazioni di login di un utente: sia quelle necessarie al recupero 
	 * dell'ultimo login effettuato sia quelle ottenute sull'utente dal login sul server.
	 * @param username dell'utente
	 * @param sessionID identificativo dell'ultimo accesso dell'utente con questo dispositivo
	 * @param money : quantità di denaro posseduta dall'utente
	 * @param diamonds : punteggio accumulato dall'utente
	 * @param level : punteggio accumulato dall'utente
	 */
	public void saveLogin(String email, String username, String sessionID, String money, String xp, String diamonds, String level)
	{
		Map<String,String> data = new HashMap<String,String>();
		data.put(Parameters.EMAIL, email);
		data.put(Parameters.USERNAME, username);
		data.put(Parameters.SESSION_ID, sessionID);
		data.put(Parameters.MONEY, money);
		data.put(Parameters.EXPERIENCE, xp);
		data.put(Parameters.DIAMONDS, diamonds);
		data.put(Parameters.LEVEL, level);

		writePrefFile(RECOVER_LOGIN_FILE,data);
	}

	/**
	 * Metodo che permette di salvare le informazioni necessarie al recupero dell'ultimo login di un utente.
	 * @param username dell'utente
	 * @param sessionID identificativo dell'ultimo accesso dell'utente con questo dispositivo
	 */
	public void saveLogin(String email,String username,String sessionID) {
		saveLogin(email, username, sessionID, START_MONEY, START_EXPERIENCE, START_DIAMONDS, START_LEVEL);
	}


	public void saveLogin(Map<String, String> map) {
		writePrefFile(RECOVER_LOGIN_FILE, map);
	}
	
	/**
	 * Metodo che ritorna le informazioni relative all'ultimo login effettuato sul dispositivo. Le informazioni vengono
	 * codificate atraverso una mappa le cui chiavi sono:
	 * Parameters.USERNAME -> username dell'utente
	 * Parameters.PASSWORD -> password dell'utente
	 * Parameters.SESSION_ID -> ultimo session ID con cui ci si è loggati da questo dispositivo
	 * Parameters.SCORE -> punteggio aggiornato all'ultima volta che si è giocato su un determinato dispositivo
	 * Paramters.MONEY -> denaro/bonus aggiornato all'ultima volta che si è giocato su un determinato dispositivo
	 * @return una mappa avente come chiavi i valori di Parameters a ognuno dei quali corrisponde un'informazione dell'utente.
	 */
	public Map<String,String> recoverLogin() {
		return readPrefFile(RECOVER_LOGIN_FILE, USER_INFO_TAG);
	}
	
	/**
	 * Metodo che elimina le informazioni dell'ultimo login dalla cache, sia quelle relative all'utente sia quelle di 
	 * gioco che non sono ancora state salvate sul server.
	 */
	public void logout() {
		writePrefFile(RECOVER_LOGIN_FILE, readPrefFile(RESET_LOGIN_FILE, USER_INFO_TAG));
		deleteAllStartedCrosswords();
		removeAllEndedCrosswords();
	}


	/**
	 * Metodo che controlla se l'utente che sta giocando si è registrato o loggato sul dispositivo.
	 * @return true se l'utente è loggato, false altrimenti
	 */
	public boolean isLoggedIn() {
		return !getUsername().equals(Parameters.NO_USER);
	}
	
	/**
	 * Metodo che controlla se l'utente sta giocando senza essersi registrato o loggato.
	 * @return true se l'utente non si è loggato, false altrimenti
	 */
	public boolean isNoUser() {
		return !isLoggedIn();
	}
	
	
	
	/**
	 * Metodo che permette di settare l'username dell'utente.
	 * @param username è il nuovo nome dell'utente
	 */
	public void setUsername(String username) {
		setPrefParameter(RECOVER_LOGIN_FILE,Parameters.USERNAME,username);
	}
	
	/**
	 * Metodo che permette di reimpostare la password dell'utente.
	 * @param password 
	 */
	public void setPassword(String password) {
		setPrefParameter(RECOVER_LOGIN_FILE, Parameters.PASSWORD, password);
	}
	
	/**
	 * Metodo che permette di settare la sessione dell'utente.
	 * @param sessionID
	 */
	public void setSessionID(String sessionID) {
		setPrefParameter(RECOVER_LOGIN_FILE, Parameters.SESSION_ID, sessionID);
	}
	
	/**
	 * Metodo che permette di settare esperienza guadagnata dall'utente.
	 * @param experience
	 */
	public void setXP(int experience) {
		setPrefParameter(RECOVER_LOGIN_FILE,Parameters.EXPERIENCE, experience+"");
	}

	public void increaseXP(int experience) {
		Integer level = Integer.parseInt(readPrefFile(RECOVER_LOGIN_FILE, Parameters.LEVEL));
		Integer gameXP = Integer.parseInt(readPrefFile(RECOVER_LOGIN_FILE, Parameters.EXPERIENCE));

		if(Level.levelUp(level,gameXP + experience)) {
			setLevel(level+1);
            setXP(0);
            setPrefParameter(RECOVER_LOGIN_FILE,Parameters.EXPERIENCE, gameXP + experience - RankedXP.valueOf("L"+level).getXP() + "");
		} else {
			setPrefParameter(RECOVER_LOGIN_FILE,Parameters.EXPERIENCE, gameXP + experience + "");
		}
	}

	/**
	 * Metodo che permette di accedere al esperienza dell'utente nell' attuale livello.
	 * @return il denaro sotto forma di stringa
	 */
	public int getXP() {
		return Integer.parseInt(readPrefFile(RECOVER_LOGIN_FILE,Parameters.EXPERIENCE));
	}

	/**
	 * Metodo che permette di settare l'email dell'utente.
	 * @param email : nuova email che si vuole impostare come email dell'utente
	 */
	public void setEmail(String email) {
		setPrefParameter(RECOVER_LOGIN_FILE,Parameters.EMAIL,email);
	}
	


	/**
	 * Metodo che permette di settare il link della foto profilo di facebook per il recupero dalla
	 * cache
	 * @param pic
	 */
	public void setPic(String pic) {
		setPrefParameter(RECOVER_LOGIN_FILE, Parameters.PICTURE,pic);
	}

	/**
	 * Metodo che permette di settare il link della foto profilo di facebook
	 * @param pic_url
	 */
	public void setPicUrl(String pic_url) {
		setPrefParameter(RECOVER_LOGIN_FILE, Parameters.PIC_URL,pic_url);
	}

	/**
	 * Metodo che permette di settare il social network con cui si è effettuato il login
	 * @param social
	 */
	public void setSocial(String social) {
		setPrefParameter(RECOVER_LOGIN_FILE, Parameters.SOCIAL,social);
	}


	/**
	 * Metodo che permette di accedere all'username dell'utente.
	 * @return il nome dell'utente
	 */
	public String getUsername() {
		return readPrefFile(RECOVER_LOGIN_FILE,Parameters.USERNAME);
	}

	/**
	 * Metodo che permette di accedere alla password dell'utente.
	 * @return la password impostata dall'utente
	 */
	public String getPassword() {
		return readPrefFile(RECOVER_LOGIN_FILE,Parameters.PASSWORD);
	}

	/**
	 * Metodo che permette di accedere al sessionID dell'utente.
	 * @return la stringa che indica l'ultima sessione attiva dell'utente sul dispositivo
	 */
	public String getSessionID() {
		return readPrefFile(RECOVER_LOGIN_FILE,Parameters.SESSION_ID);
	}
	

	/**
	 * Metodo che ritorna l'email dell'utente che è loggato.
	 * @return email : una stringa che rappresenta l'email dell'utente loggato
	 */
	public String getEmail() {
		return readPrefFile(RECOVER_LOGIN_FILE, Parameters.EMAIL);
	}

	public String getSocial() { return readPrefFile(RECOVER_LOGIN_FILE, Parameters.SOCIAL); }

	public String getPic() { return readPrefFile(RECOVER_LOGIN_FILE, Parameters.PICTURE); }

	public String getPicUrl() { return readPrefFile(RECOVER_LOGIN_FILE, Parameters.PIC_URL); }


	/**
	 * Metodo che permette di accedere al punteggio dell'utente.
	 * @return il valore intero come long del punteggio
	 */
	/*public long getScoreAsLong() {
		return Long.parseLong(getScore());
	}*/

	/*
	GESTIONE DELLE MONETE DI GIOCO PER LA PARTE FREEPLAY
	 */
	public void setMoney(int money) {
		System.out.println("SETTING MONEY: " +  money);
		setPrefParameter(RECOVER_LOGIN_FILE, Parameters.MONEY, money+"");
	}

	public void increaseMoney(int money) {
		int moneyGame = Integer.parseInt(readPrefFile(RECOVER_LOGIN_FILE, Parameters.MONEY));
		setPrefParameter(RECOVER_LOGIN_FILE, Parameters.MONEY, money + moneyGame + "");
	}

	public int getMoney() {
		return Integer.parseInt(readPrefFile(RECOVER_LOGIN_FILE,Parameters.MONEY));
	}


	/*
	GESTIONE DEI DIAMANTI DI GIOCO PER LA PARTE RANKED
	*/
	public void setDiamonds(int diamonds) {
		setPrefParameter(RECOVER_LOGIN_FILE, Parameters.DIAMONDS, diamonds+"");
	}

	public void increaseDiamonds(int diamonds) {
		int diamondsGame = Integer.parseInt(readPrefFile(RECOVER_LOGIN_FILE, Parameters.DIAMONDS));
		setPrefParameter(RECOVER_LOGIN_FILE, Parameters.DIAMONDS, diamonds + diamondsGame + "");
	}

	public int getDiamonds() {
		return Integer.parseInt(readPrefFile(RECOVER_LOGIN_FILE,Parameters.DIAMONDS));
	}



	//LEVEL
	public void setLevel(int level) {
		setPrefParameter(RECOVER_LOGIN_FILE,Parameters.LEVEL, level + "");
	}

	public void increaseLevel() {
		int levelGame = Integer.parseInt(readPrefFile(RECOVER_LOGIN_FILE, Parameters.LEVEL));
		setPrefParameter(RECOVER_LOGIN_FILE, Parameters.LEVEL, levelGame + 1 + "");
	}

	public int getLevel() {
		return Integer.parseInt(readPrefFile(RECOVER_LOGIN_FILE, Parameters.LEVEL));
	}


	public Map<String, String> getUserScore() {
	    Map<String, String> userScore = new HashMap<String, String>();
	    userScore.put(Parameters.MONEY, getMoney() + "");
        userScore.put(Parameters.DIAMONDS, getDiamonds()+ "");
        userScore.put(Parameters.EXPERIENCE, getXP() + "");
        userScore.put(Parameters.LEVEL, getLevel() + "");
        System.out.println("MONEY: " + getMoney());
		System.out.println("DIAMONDS: " + getDiamonds());
		System.out.println("XP: " + getXP());
		System.out.println("LEVEL: " + getLevel());
		return userScore;
    }

	public void setUserScore(Map<String, Float> userScore) {
		setMoney(Math.round(userScore.get(Parameters.MONEY)));
		setDiamonds(Math.round(userScore.get(Parameters.DIAMONDS)));
		setXP(Math.round(userScore.get(Parameters.EXPERIENCE)));
		setLevel(Math.round(userScore.get(Parameters.LEVEL)));
	}


	//################################################################################################################
	//###########################----------IMPOSTAZIONI DI GIOCO---------#############################################
	//################################################################################################################
	
	
	/**
	 * Metodo che permette di settare tutte le impostazioni del gioco.
	 * @param musicOn booleano che se asserito corrisponde alla musica attiva
	 * @param soundFxOn booleano che se asserito corrisponde agli effetti sonori del gioco attivati
	 * @param language lingua del gioco : menu, finestre per l'interazione, etc.
	 * @param mode modalità con cui si vuole giocare sul cruciverba(comandi con cui ci si muove, si fa lo zoom etc.)
	 * @param crosswordDifficult ultima difficoltà scelta per il cruciverba su cui si stava giocando
	 * @param crosswordLanguage ultima lingua scelta per il cruciverba su cui si stava giocando
	 */
	public void setSettings(boolean musicOn, boolean soundFxOn,Language language,GameSelectionMode mode,
			Difficult crosswordDifficult,Language crosswordLanguage) {
		Map<String,String> data = new HashMap<String,String>();
		data.put(Parameters.MUSIC, Boolean.toString(musicOn));
		data.put(Parameters.SOUND_FX, Boolean.toString(soundFxOn)); 
		data.put(Parameters.LANGUAGE, language.name());
		data.put(Parameters.GAME_MODE, mode.name());
		data.put(Parameters.DIFFICULT, crosswordDifficult.name());
		data.put(Parameters.CROSSWORD_LANGUAGE, crosswordLanguage.name());
		data.put(Parameters.EXPERIENCE, "100");
		writePrefFile(SETTINGS_FILE,data);
	}
	
	/**
	 * Metodo che ritorna una mappa contenente tutte le informazioni sulle impostazioni di gioco.
	 * Parameters.MUSIC --> musica attiva o meno
	 * Parameters.SOUND_FX --> suono attivo o meno
	 * Parameters.GAME_MODE --> modalità di gioco scelta
	 * Parameters.LANGUAGE --> lingua scelta per i menu di gioco
	 * Parameters.CROSSWORD_LANGUAGE --> lingua scelta per il cruciverba per l'ultimo nuovo cruciverba aperto
	 * Parameters.DIFFICULT --> difficolta' scelta per l'ultimo nuovo cruciverba aperto
	 * @return mappa contenente le impostazioni del gioco
	 */
	public Map<String,String> getSettings() {
		return readPrefFile(SETTINGS_FILE,GAME_SETTINGS_TAG);
	}

	
	/**
	 * Metodo che permette di ritorna l'ultimo valore impostato per l'accensione/spegnimento della musica.
	 * @return true se la musica era attiva, false altrimenti
	 */
	public boolean getMusicOn() {
		return Boolean.valueOf(readPrefFile(SETTINGS_FILE,Parameters.MUSIC));
	}
	
	/**
	 * Metodo che permette di ritorna l'ultimo valore impostato per l'accensione/spegnimento degli effetti sonori.
	 * @return true se il suono era attivo, false altrimenti
	 */
	public boolean getSoundFxOn() {
		return Boolean.valueOf(readPrefFile(SETTINGS_FILE,Parameters.SOUND_FX));
	}
	
	/**
	 * Metodo che ritorna la lingua scelta nei menu di gioco.
	 * @return lingua scelta l'ultima voltache si è giocato
	 */
	public Language getLanguage() {
		return Language.valueOf(readPrefFile(SETTINGS_FILE,Parameters.LANGUAGE));
	}
	
	/**
	 * Metodo che ritorna quale corrispondenza comando<->azione e' stata scelta
	 * @return modalità di gioco
	 */
	public GameSelectionMode getGameMode() {
		return GameSelectionMode.valueOf(readPrefFile(SETTINGS_FILE,Parameters.GAME_MODE));
	}
	
	/**
	 * Metodo che ritorna la difficoltà dell'ultimo cruciverba iniziato.
	 * @return difficolta' del cruciverba
	 */
	public Difficult getCrosswordDifficult() {
		return Difficult.valueOf(readPrefFile(SETTINGS_FILE,Parameters.DIFFICULT));
	}
	
	/**
	 * Metodo che ritorna la lingua scelta nell'ultimo cruciverba iniziato.
	 * @return lingua del cruciverba
	 */
	public Language getCrosswordLanguage() {
		return Language.valueOf(readPrefFile(SETTINGS_FILE,Parameters.CROSSWORD_LANGUAGE));
	}

	/**
	 * Metodo che ritorna il livello scelto nell'ultimo cruciverba iniziato.
	 * @return ranked level
	 */
	public String getCrosswordLevel() {
		return readPrefFile(SETTINGS_FILE,Parameters.GAME_LEVEL);
	}

	/**
	 * Metodo che ritorna il livello scelto nell'ultimo cruciverba iniziato.
	 * @return ranked level
	 */
	public GameType getGameType() {
		return GameType.valueOf(readPrefFile(SETTINGS_FILE,Parameters.GAMETYPE));
	}
	
	
	/**
	 * Permette di modificare l'impostazione relativa all'attivazione della musica.
	 * @param musicOn assserito se la musica deve essere attivata,false altrimenti
	 */
	public void setMusic(boolean musicOn) {
		setPrefParameter(SETTINGS_FILE,Parameters.MUSIC,Boolean.toString(musicOn));
	}
	
	/**
	 * Permette di modificare l'impostazione relativa all'attivazione degli effetti sonori.
	 * @param soundFxOn asserito se i suoni devono essere attivati,false altrimenti
	 */
	public void setSoundFx(boolean soundFxOn) {
		setPrefParameter(SETTINGS_FILE,Parameters.SOUND_FX,Boolean.toString(soundFxOn));
	}
	
	/**
	 * Permette di modificare l'impostazione relativa alla lingua dei menu.
	 * @param language lingua dei menu di gioco
	 */
	public void setLanguage(Language language) {
		setPrefParameter(SETTINGS_FILE,Parameters.LANGUAGE,language.name());
	}
	
	/**
	 * Permette di modificare l'impostazione relativa alla modalita' di gioco.
	 * @param gameMode modalita' di gioco
	 */
	public void setGameMode(GameSelectionMode gameMode) {
		setPrefParameter(SETTINGS_FILE,Parameters.GAME_MODE,gameMode.name());
	}
	
	/**
	 * Permette di modificare l'impostazione relativa alla difficolta' dell'ultimo cruciverba iniziato.
	 * @param crosswordDifficult difficolta' del cruciverba
	 */
	public void setCrosswordDifficult(Difficult crosswordDifficult) {
		setPrefParameter(SETTINGS_FILE,Parameters.DIFFICULT,crosswordDifficult.name());
	}
	
	/**
	 * Permette di modificare l'impostazione relativa alla lingua dell'ultimo cruciverba iniziato.
	 * @param crosswordLanguage lingua del cruciverba
	 */
	public void setCrosswordLanguage(Language crosswordLanguage) {
		setPrefParameter(SETTINGS_FILE,Parameters.CROSSWORD_LANGUAGE, crosswordLanguage.name());
	}

	/**
	 * Permette di modificare l'impostazione relativa al livello dell'ultimo cruciverba iniziato.
	 * @param level ranked level
	 */
	public void setCrosswordLevel(String level) {
		setPrefParameter(SETTINGS_FILE, Parameters.GAME_LEVEL, level);
	}

	/**
	 * Permette di modificare l'impostazione relativa all' ultimo GameType utilizzato
	 * @param gametype ranked level
	 */
	public void setGameType(GameType gametype) {
		setPrefParameter(SETTINGS_FILE,Parameters.GAMETYPE, gametype.name());
	}
	
	
	
	//################################################################################################################
	//##############################--------------PREFERENCES UTILS------------#######################################
	//################################################################################################################
	
	/**
	 * Metodo privato che consente di modificare un solo valore all'interno della mappa di una data Preferences.
	 * @param nameFile : stringa corrispondente al nome del file di Preferences che si vuole modificare
	 * @param key :
	 * @param value : nuovo valore da inserire nella mappa
	 */
	private void setPrefParameter(String nameFile, String key, String value) {
		Map<String,String> data = new HashMap<String,String>();
		data.put(key, value);
		writePrefFile(nameFile, data);
	}
	
	/**
	 * Metodo privato che consente di modificare  tutti i valori impostati nella mappa passata all'interno del file di 
	 * Preferences del login.
	 * @param nameFile nome del file di preferences su cui vengono modificati i dati
	 * @param data mappa contenente le chiavi e i valori che vanno aggiunti/modificati al file
	 */
	private void writePrefFile(String nameFile, Map<String,String> data)
	{
		Preferences pref=Gdx.app.getPreferences(nameFile);
		Map<String, String> chipheredMap = new HashMap<String, String>();
		// Copy the map
		for (String key : data.keySet()) chipheredMap.put(key, Base64Coder.encodeString(data.get(key)));
		pref.put(chipheredMap);
		pref.flush();
	}
	
	/**
	 *  Metodo privato che consente di leggere piu' parametri della preferences richiesta attraverso la chiavi mappate
	 *  dalla classe Parameters.
	 * @param nameFile nome del file che si vuole leggere
	 * @param keys chiavi a cui corrispondono i valori richiesti
	 * @return valore connesso alla chiave sul file di preferences indicato
	 */
	private Map<String,String> readPrefFile(String nameFile,final String[] keys) 
	{
		Map<String,String> data = new HashMap<String,String>();
		Preferences pref=Gdx.app.getPreferences(nameFile);
		for(String s: keys) data.put(s, Base64Coder.decodeString(pref.getString(s)));
		return data;
	}
	
	/**
	 *  Metodo privato che consente di leggere un singolo parametro della preferences richiesta attraverso la chiave mappata
	 *  dalla classe Parameters.
	 * @param nameFile nome del file che si vuole leggere
	 * @return valore connesso alla chiave sul file di preferences indicato
	 */
	private String readPrefFile(String nameFile, String parameter) 
	{
		return Base64Coder.decodeString(Gdx.app.getPreferences(nameFile).getString(parameter));
	}
	
	  
	
	
	//################################################################################################################
	//##############################---------- API GESTIONE CRUCIVERBA ----------#####################################
	//################################################################################################################

	public Map<String,String> loadNewCrossword(Language language, LimitedDomains domain, GameType gametype, String level, Difficult difficult) {
		FileHandle dir = Gdx.files.local(generateDirectoryPath(CROSSWORDS_FOLDER, language.name(), domain.name(), gametype.name(), level, difficult.name()));
		if (dir.list().length != 0) {
			Map<String, String> crosswordInfo = new HashMap<String, String>();
			FileHandle crosswordFile = dir.list()[0];
			String cName = crosswordFile.name();

			crosswordFile.copyTo(Gdx.files.local(intoDirectory(STARTED_CROSSWORDS_FOLDER,
					createStartedCrosswordName(cName, language, domain, gametype, level, difficult))));
			changeLastCrossword(cName, language, domain, difficult, gametype, level);
			saveStateCrossword(cName, new HashSet<String>(), false);

			crosswordInfo.put(Parameters.CROSSWORD_ID, cName);
			crosswordInfo.put(Parameters.CROSSWORD, Base64Coder.decodeString(crosswordFile.readString()));

			System.out.println("DELETING FILE: " + crosswordFile.name());
			crosswordFile.delete();
			return crosswordInfo;
		}
		else return null;
	}

	/**
	 * Metodo che salva tra i cruciverba disponibili nel gioco un cruciverba con tutte le sue informazioni di base : id,
	 * dominio,lingua e difficolta'.
	 * N.B. SOLO PER QUELLI SCARICATI DAL SERVER
	 * @param crosswordID id che identifica univocamente il cruciverba
	 * @param crossword toString intelligente del cruciverba
	 * @param domain dominio del cruciverba
	 * @param difficult difficolta' del cruciverba
	 * @param language lingua del cruciverba caricato
	 */
	public void saveNewCrossword(String crosswordID, String crossword, String language, String domain, String gametype, String level, String difficult) {
	    Gdx.files.local(generateLocalPath(crosswordID, CROSSWORDS_FOLDER, language, domain, gametype, level, difficult)).writeString(Base64Coder.encodeString(crossword), false);
	}
	
	/**
	 * Metodo che consente di salvare l'inserimento di una parola in un dato cruciverba.
	 * @param crosswordID identificativo del cruciverba in cui viene inserita la parola
	 * @param infoWord stringa che rappresenta le informazioni della parola da inserire nel cruciverba
	 */
	public void saveStateCrossword(String crosswordID, String infoWord, boolean append) {
		saveStateCrossword(crosswordID, new HashSet<String>(Arrays.asList(infoWord)),append);
	}
	

	/**
	 * Metodo di utilita' che permette di salvare l'intero stato di avanzamento del cruciverba.
	 * N.B. Solo per gli stati caricati dal server. Sovrascrive il vecchio stato.
	 * @param crosswordID id del cruciverba
	 * @param state nuovo stato di avanzamento del cruciverba
	 * @param append se true aggiunge state allo statocorrente altrimenti sovrascrive il file con lo stato passato
	 */

	public void saveStateCrossword(String crosswordID, Set<String> state, boolean append) {
		changeLastCrossword(crosswordID);
		FileHandle file = Gdx.files.local(intoDirectory(STATE_CROSSWORD_FOLDER, createStateName(crosswordID)));

		if (append) state.addAll(loadStateCrossword(crosswordID));
		file.writeString(Base64Coder.encodeString(new Json().toJson(state)),false);
	}


	/**
	 * Metodo che permette di salvare tutte le informazioni relative a un cruciverba iniziato.
	 * @param crosswordID id del cruciverba
	 * @param domain dominio delcruciverba
	 * @param difficult difficolta' del cruciverba
	 * @param language lingua delcruciverba
	 * @param crosswordAsString toString intelligente del cruciverba
	 * @param state stato di avanzamento del cruciverba
	 */
	public void saveStartedCrossword(String crosswordID, Language language, LimitedDomains domain, GameType gameType, String level, Difficult difficult, String crosswordAsString,Set<String> state) {//SOLO SERVER (salvataggio diretto delle info del server){
		FileHandle startedCrosswordFile = Gdx.files.local(intoDirectory(STARTED_CROSSWORDS_FOLDER, createStartedCrosswordName(crosswordID, language, domain, gameType, level, difficult)));
		startedCrosswordFile.writeString(Base64Coder.encodeString(crosswordAsString),false);

		FileHandle stateFile = Gdx.files.local(intoDirectory(STATE_CROSSWORD_FOLDER, createStateName(crosswordID)));
		stateFile.writeString(Base64Coder.encodeString(new Json().toJson(state)),false);
	}

	/**
	 * Metodo che permette di caricare da file lo stato di un cruciverba.
	 * @param crosswordID identificativo del cruciverba
	 * @return insieme di stringhe ognuna delle quali contiene le informazioni relative all'inserimento della singola parola
	 */
	public Set<String> loadStateCrossword(String crosswordID)
	{
		changeLastCrossword(crosswordID);
		FileHandle stateFile = Gdx.files.local(intoDirectory(STATE_CROSSWORD_FOLDER, createStateName(crosswordID)));
		if(stateFile.exists())
			return new Json().fromJson(new HashSet<String>().getClass(), Base64Coder.decodeString(stateFile.readString()));
		return new HashSet<String>();	
	}
	
	/**
	 * Metodo che permette di caricare il cruciverba del file prendendone il toString intelligente.
	 * @param crosswordID identificativo del cruciverba
	 * @return stringa che permette di ricostruire il cruciverba
	 */
	public String loadStartedCrossword(String crosswordID, GameType gt)
	{
		FileHandle startedCrosswordFile = Gdx.files.local(intoDirectory(STARTED_CROSSWORDS_FOLDER, createStartedCrosswordName(crosswordID, lastLanguage, lastDomain, lastGameType, lastLevel, lastDifficult)));
		if(startedCrosswordFile.exists())
			return Base64Coder.decodeString(startedCrosswordFile.readString());
		return new String();
	}

	public String loadStartedCrossword(Map<String,String> startedCrossword) {
		FileHandle startedCrosswordFile = Gdx.files.local(intoDirectory(STARTED_CROSSWORDS_FOLDER, createStartedCrosswordName(startedCrossword)));
		if(startedCrosswordFile.exists())
			return Base64Coder.decodeString(startedCrosswordFile.readString());
		return new String();
	}
	
	/**
	 * Metodo che ritorna la lista dei cruciverba iniziati e non terminati.
	 * Ogni cruciverba viene rappresentato da una mappa aventi come associazioni chiave-valore:
	 * Parameters.CROSSWORD_ID --> l'id del crucvierba
	 * Parameters.DOMAIN --> dominio del cruciverba
	 * Parameters.LANGUAGE --> lingua del cruciverba
	 * Parameters.DIFFICULT --> difficolta' del cruciverba
	 * @return la lista delle informazioni dei cruciverba iniziati
	 */
	public List<Map<String,String>> getStartedCrosswords()
	{
		FileHandle dir = Gdx.files.local(intoDirectory(STARTED_CROSSWORDS_FOLDER,""));
		List<Map<String,String>> startedCrosswords= new ArrayList<Map<String,String>>();
		for(FileHandle f : dir.list()) {
			if (!f.name().startsWith("."))
				startedCrosswords.add(parseStartedCrosswordName(f.name()));
		}
		return startedCrosswords;
	}

	/**
	 * Metodo che ritorna la lista dei cruciverba iniziati in base alla tipologia di gioco
	 * Ogni cruciverba viene rappresentato da una mappa aventi come associazioni chiave-valore:
	 * Parameters.CROSSWORD_ID --> l'id del crucvierba
	 * Parameters.DOMAIN --> dominio del cruciverba
	 * Parameters.LANGUAGE --> lingua del cruciverba
	 * Parameters.DIFFICULT --> difficolta' del cruciverba
	 * @return la lista delle informazioni dei cruciverba iniziati
	 */
	public List<Map<String,String>> getStartedCrosswords(GameType gameType)
	{
		FileHandle dir = Gdx.files.local(intoDirectory(STARTED_CROSSWORDS_FOLDER,""));
		List<Map<String,String>> startedCrosswords= new ArrayList<Map<String,String>>();
		for(FileHandle f : dir.list()) {
			if (!f.name().startsWith(".") && f.name().contains(gameType.name()))
				startedCrosswords.add(parseStartedCrosswordName(f.name()));
		}
		return startedCrosswords;
	}

	public int availableCrosswords(Language language, LimitedDomains domain, GameType gametype, String level, Difficult difficult){
		FileHandle dir = Gdx.files.local(generateDirectoryPath(CROSSWORDS_FOLDER, language.name(), domain.name(), gametype.name(), level, difficult.name()));
        return dir.list().length;
    }


	/**
	 * Metodo che indica che il cruciverba è terminato e permette di cancellarne le informazioni.
	 * @param crosswordID identificativo del cruciverba
	 */
	public void addEndedCrossword(String crosswordID)
	{
		deleteStartedCrossword(crosswordID);
		//Scrivo l'ID del cruciverba tra quelli dei cruciverba terminati 
		FileHandle endedCrosswordsFile = Gdx.files.local(intoDirectory(ENDED_CROSSWORDS,""));
		Set<String> ec;
		if(endedCrosswordsFile.exists())
			ec = new Json().fromJson(new HashSet<String>().getClass(), endedCrosswordsFile);
		else ec = new HashSet<String>();
		ec.add(crosswordID);
		new Json().toJson(ec, endedCrosswordsFile);
	}
	
	/**
	 * Metodo che carica gli id dei cruciverba terminati la cui fine non è ancora stata notificata la server
	 * @return insieme di identificativi dei cruciverba
	 */
	public Set<String> loadEndedCrosswords()
	{
		FileHandle endedCrosswords = Gdx.files.local(intoDirectory(ENDED_CROSSWORDS,""));
		if(endedCrosswords.exists())
			return new Json().fromJson(new HashSet<String>().getClass(),endedCrosswords);
		return new HashSet<String>();
	}
	
	/**
	 * Metodo che cancella tutti i cruciverba terminati dall'utente.
	 */
	public void removeAllEndedCrosswords()
	{
		Gdx.files.local(intoDirectory(ENDED_CROSSWORDS,"")).delete();
	}
	
	/**
	 * Metodo che permette di rimuovere un singolo cruciverba, attaverso il suo id, dalla lista dei cruciverba terminati.
	 * @param crosswordID identificativo del cruciverba
	 */
	public void removeEndedCrossword(String crosswordID)
	{
		FileHandle endedCrosswordsFile = Gdx.files.local(intoDirectory(ENDED_CROSSWORDS,""));
		Set<String> ec;
		if(endedCrosswordsFile.exists())
			ec = new Json().fromJson(new HashSet<String>().getClass(), endedCrosswordsFile);
		else ec = new HashSet<String>();
		ec.remove(crosswordID);
		new Json().toJson(ec, endedCrosswordsFile);
	}
	
	/**
	 * Metodo che cancella tutti i crucivarba iniziati : sia le informazioni per poterli ricostruire che i loro stati.
	 * N.B. I cruciverba così cancellati NON vengono rimessi tra quelli che l'utente potrà scegliere come nuovi cruciverba!
	 */
	public void deleteAllStartedCrosswords() 
	{
		lastCrosswordID = null;
		for(FileHandle f : Gdx.files.local(intoDirectory(STATE_CROSSWORD_FOLDER,"")).list() )
			if(!f.name().startsWith(".")) f.delete();

		for(FileHandle f : Gdx.files.local(intoDirectory(STARTED_CROSSWORDS_FOLDER,"")).list() )
			if(!f.name().startsWith(".")) f.delete();
	}
	
	/**
	 * Metodo che permette di cancellare le informazioni di un singolo cruciverba iniziato ma non terminato.
	 * @param crosswordID identificativo del cruciverba
	 */
	public void deleteStartedCrossword(String crosswordID) 
	{
		// Cancello il file che contiene il cruciverba come stringa e lo stato di avanzamento (terminano entrambi con il crosswordID)
		if(crosswordID.equals(lastCrosswordID)) lastCrosswordID = null;
		for(FileHandle f : Gdx.files.local(intoDirectory(STATE_CROSSWORD_FOLDER,"")).list())
			if(f.name().startsWith(crosswordID)) f.delete();

		for(FileHandle f : Gdx.files.local(intoDirectory(STARTED_CROSSWORDS_FOLDER,"")).list())
			if(f.name().startsWith(crosswordID)) f.delete();
	}
	
	/**
	 * Metodo che permette di caricare lo screen di un cruciverba attraverso il suo id.
	 * @param crosswordID id del cruciverba
	 * @return pixmap rappresentante lo screen del cruciverba l'ultima volta che si è giocato su quel cruciverba.
	 */
	@Deprecated
	public Pixmap loadCrosswordScreen(String crosswordID) {
		FileHandle file = Gdx.files.local(generateLocalPath(crosswordID+"img",CROSSWORDS_FOLDER, STARTED_CROSSWORDS_FOLDER));
		return PixmapIO.readCIM(file);
	}
	
	/**
	 * Metodo che permette di salvare lo screen di un cruciverba.
	 * @param crosswordID id del cruciverba
	 * @param m pixmap contenente lo screen
	 */
	@Deprecated
	public void saveCrosswordScreen(String crosswordID,Pixmap m) {
		FileHandle file = Gdx.files.local(generateLocalPath(crosswordID+"img",CROSSWORDS_FOLDER, STARTED_CROSSWORDS_FOLDER));
		PixmapIO.writeCIM(file, m);
	}
	
	/**
	 * Metodo che aggiorna le informazioni dell'ultimo cruciverba con cui si e' giocato.
	 * @param crosswordID id del cruciverba
	 * @param domain dominio del cruciverba
	 * @param difficult difficolta' del cruciverba
	 * @param language lingua del cruciverba
	 */
	private void changeLastCrossword(String crosswordID, Language language, LimitedDomains domain, Difficult difficult, GameType gameType, String level) {
		lastCrosswordID = crosswordID;
		lastLanguage = language;
		lastDomain = domain;
		lastGameType = gameType;
		lastLevel = level;
		lastDifficult = difficult;

		Preferences prefs = Gdx.app.getPreferences("My Preferences");
		prefs.putString("test", lastCrosswordID);
		prefs.putLong(crosswordID, new Timestamp (System.currentTimeMillis()).getTime());
		prefs.flush();

	}
	
	/**
	 * Metodo che aggiorna le informazioni dell'ultimo cruciverba con cui si e' giocato.
	 * @param crosswordInfo mappa contenente le informazioni dell'ultimo cruciverba
	 */
	private void changeLastCrossword(Map<String,String> crosswordInfo) {
		lastCrosswordID = crosswordInfo.get(Parameters.CROSSWORD_ID);
		lastLanguage = Language.valueOf(crosswordInfo.get(Parameters.LANGUAGE));
		lastDomain = LimitedDomains.valueOf(crosswordInfo.get(Parameters.DOMAIN));
		lastGameType = GameType.valueOf(crosswordInfo.get(Parameters.GAMETYPE));
		lastLevel = crosswordInfo.get(Parameters.LEVEL);
		lastDifficult = Difficult.valueOf(crosswordInfo.get(Parameters.DIFFICULT));

		Preferences prefs = Gdx.app.getPreferences("My Preferences"); //TOGLIERE
		prefs.putString("test",lastCrosswordID);//TOGLIERE
		prefs.putLong(lastCrosswordID,new Timestamp (System.currentTimeMillis()).getTime());
		prefs.flush();
	}
	
	/**
	 * Metodo che aggiorna le informazioni dell'ultimo cruciverba con cui si e' giocato.
	 * @param crosswordID id del cruciverba
	 */
	public void changeLastCrossword(String crosswordID) {
		Preferences prefs = Gdx.app.getPreferences("My Preferences"); //TOGLIERE
		prefs.putLong(crosswordID,new Timestamp (System.currentTimeMillis()).getTime());
		prefs.flush();

		if(!crosswordID.equals(lastCrosswordID))
			for(FileHandle f : Gdx.files.local(intoDirectory(STARTED_CROSSWORDS_FOLDER,"")).list() )
				if(isStartedName(f.name(),crosswordID)) {
					changeLastCrossword(parseStartedCrosswordName(f.name()));
					break;
				}	
	}
	
	
	//################################################################################################################
	//#############################------------GESTIONE PATH CRUCIVERBA-----------####################################
	//################################################################################################################
		
	
	/**
	 * Metodo che genera il percorso ad un file locale
	 * @param nameFile nome del file
	 * @param sequence sequenza dei nomi delle cartelle per arrivare al file
	 * @return stringa contenente il percorso al file
	 */
	private String generateLocalPath(String nameFile, String... sequence) {
		return generateDirectoryPath(sequence).concat(nameFile);
	}
	
	/**
	 * Metodo che genera il percorso ad una cartella.
	 * @param sequence sequenza di cartelle entro le quali ci si muove
	 * @return stringa contenente il percorso
	 */
	private String generateDirectoryPath(String...sequence) {
		StringBuffer path = new StringBuffer();
		for(String s: sequence) path.append(s).append(SEPARATOR);
		return path.toString().toLowerCase();
	}
	
	/**
	 * Metodo che genera il percorso ad un file contenuto nella cartella dei cruciverba iniziati
	 * @paran directory local directory path
	 * @param filename nome del file
	 * @return stringa contenente il percorso al file
	 */
	private String intoDirectory(String directoryString, String filename) {
		return generateLocalPath(filename, CROSSWORDS_FOLDER, directoryString);
	}


	/**
	 * Metodo che crea il nome significativo del file contenente il toString intelligente di un cruciverba gia' iniziato
	 * @param crosswordID id del cruciverba
	 * @param domain dominio
	 * @param difficult difficolta'
	 * @param language lingua
	 * @return una stringa contenente il nome del file
	 */
	private String createStartedCrosswordName(String crosswordID, Language language, LimitedDomains domain, GameType gameType, String level, Difficult difficult) {
		return crosswordID + "_" + language.name() + "_" + domain.name() + "_" + gameType.name() + "_" + level + "_" + difficult.name();
	}

	private String createStartedCrosswordName(Map<String, String> crossword){
		return crossword.get(Parameters.CROSSWORD_ID) + "_" + crossword.get(Parameters.LANGUAGE) + "_" + crossword.get(Parameters.DOMAIN) + "_" +
				crossword.get(Parameters.GAMETYPE) + "_" + crossword.get(Parameters.LEVEL) + "_" + crossword.get(Parameters.DIFFICULT);

	}
	
	/**
	 * Metodo che permette di risalire dal nome di un file contenente il toString intelligente di un cruciverba gia' iniziato alle informazioni 
	 * del cruciverba
	 * Parameters.CROSSWORD_ID --> id del cruciverba
	 * Parameters.LANGUAGE --> lingua
	 * Parameters.DOMAIN --> dominio
	 * Parameters.DIFFICULT --> difficolta'
	 * @param name nome del file
	 * @return una mappa avente come chiavi i campi di parameters a ognuno dei quali corrispondo le info del cruciverba
	 */
	private Map<String, String> parseStartedCrosswordName(String name)
	{
		String[] tokens = name.split("_");
		Map<String,String> crosswordInfo = new HashMap<String,String>();
		crosswordInfo.put(Parameters.CROSSWORD_ID, tokens[0]);
		crosswordInfo.put(Parameters.LANGUAGE, tokens[1]);
		crosswordInfo.put(Parameters.DOMAIN, tokens[2]);
		crosswordInfo.put(Parameters.GAMETYPE, tokens[3]);
		crosswordInfo.put(Parameters.LEVEL, tokens[4]);
		crosswordInfo.put(Parameters.DIFFICULT, tokens[5]);

		System.out.println("Crossword Info: " + crosswordInfo.toString());
		return crosswordInfo;
	}
	
	/**
	 * Metodo che permette di costruire il nome del file contenente lo stato di avanzamento di un cruciverba.
	 * @param crosswordID id del cruciverba
	 * @return stringa che rappresenta il nome del file contenente lo stato cruciverba
	 */
	private String createStateName(String crosswordID) {
		return crosswordID + "_" + STATE;
	}
	
	/**
	 * Metodo che riconosce se un file nella cartella STARTED_CROSSWORDS e' un file contenente il toString intelligente o meno
	 * @param fileName nome del file
	 * @param crosswordID id del cruciverba
	 * @return true se il file contiene il toString del cruciverba di cui viene passato l'id, false altrimenti
	 */
	private boolean isStartedName(String fileName,String crosswordID) {
		return fileName.startsWith(crosswordID);
	}

}