package it.uniroma1.lcl.crucy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Map;

import it.uniroma1.lcl.crucy.cache.Callback;
import it.uniroma1.lcl.crucy.cache.CallbackVoid;
import it.uniroma1.lcl.crucy.cache.MainThreadCallback;
import it.uniroma1.lcl.crucy.cache.exceptions.SessionExpiredException;
import it.uniroma1.lcl.crucy.gameplay.audio.MusicAmbient;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.menu.login.SupporterClass;
import it.uniroma1.lcl.crucy.utils.AudioManager;
import it.uniroma1.lcl.crucy.utils.Dialoghi;
import it.uniroma1.lcl.crucy.utils.Parole;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.GameSelectionMode;
import it.uniroma1.lcl.crucy.gameplay.textures.BackGround;
import it.uniroma1.lcl.crucy.menu.Menu;
import it.uniroma1.lcl.crucy.cache.ConnectionManager;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.RequestManager;
import it.uniroma1.lcl.crucy.cache.utils.Difficult;
import it.uniroma1.lcl.crucy.cache.utils.Language;
import it.uniroma1.lcl.crucy.login.social.FacebookService;
import it.uniroma1.lcl.crucy.utils.InitialLoading;

import static it.uniroma1.lcl.crucy.mainconstants.MainConstants.BACKGROUND_ALPHA;
import static it.uniroma1.lcl.crucy.mainconstants.MainConstants.BACKGROUND_BLUE;
import static it.uniroma1.lcl.crucy.mainconstants.MainConstants.BACKGROUND_GREEN;
import static it.uniroma1.lcl.crucy.mainconstants.MainConstants.BACKGROUND_RED;
import it.uniroma1.lcl.crucy.utils.Loader;

public class MainCrucy extends Game
{
	private static MainCrucy main;

	private Boolean fileLog = false;
	private Boolean connLog = false;
	private Boolean synchron = false;

	private static final int VIRTUAL_WIDTH = 1000;
	private static final int VIRTUAL_HEIGHT = 800;

	private Viewport viewport;
	private SpriteBatch batch;

	private AudioManager audio;

	private InitialLoading initLoad;

	private Difficult difficolta;
	private GameSelectionMode gameMode;

	private BackGround backGround;

	private ConnectionManager connectioManager;
	private RequestManager requestManager;
    private FileManager fileManager;

	private FacebookService fbService;

	private String username= null;

	public MainCrucy(){	}

	public static MainCrucy getMain()
	{
		return main;
	}

	public void playMusic(MusicAmbient newM) { audio.playMusic(newM, true); }

	public void stopMusic() { audio.stopMusic(); }

	public String getString(String s) {
		return Parole.getInstance().getString(getLingua(),s);
	}
	
	public void setLingua(Language l) { fileManager.setLanguage(l); }

	public Language getLingua() { return fileManager.getLanguage(); }

	public void setDifficolta(Difficult d){ difficolta=d;}

	public Difficult getDifficolta(){return difficolta;}

	//Aggiunti per effettuare lo Switch tra le modalità
	public void setGameMode(GameSelectionMode gameMode) {
		this.gameMode=gameMode;
		fileManager.setGameMode(gameMode);
	}

	public GameSelectionMode getGameMode() {return fileManager.getGameMode();}

	@Override
	public void create() {
		main = this;
		batch = new SpriteBatch();
		backGround = new BackGround(new Texture(Gdx.files.internal("background/white.png")),batch);
		fileManager = FileManager.getInstance();
		viewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		gameMode = GameSelectionMode.TAP_AND_SWIPE_MOD;

		//Controllo per vedere se l'utente si è già loggato
		connectioManager = ConnectionManager.getInstance();
		requestManager = RequestManager.getInstance();
		fileLog = FileManager.getInstance().isLoggedIn();
		checkConnection();

		//Se l'utente si è loggato allora viene effettuato il login sul server
		fbService = FacebookService.getInstance();
		Gdx.input.setCatchBackKey(true);

		initLoad = new InitialLoading(this);
		setScreen(initLoad);

		Map<String, String> infos = fileManager.recoverLogin();
	}

	public void checkConnection() {
		ConnectionManager.getInstance().login(FileManager.getInstance().getSessionID(), new CallbackVoid() {
			@Override
			public void onSuccess() {
				main.setConnLog(true);
			}

			@Override
			public void onFailure(Throwable cause) {
				if (cause instanceof SessionExpiredException) {
					System.out.println("Session Expired");
					main.setFileLog(false);
					main.setConnLog(false);
					FileManager.getInstance().logout();
					ConnectionManager.getInstance().logout(new CallbackVoid() {
						@Override
						public void onSuccess() {

						}

						@Override
						public void onFailure(Throwable cause) {

						}
					});
				}
			}
		});
	}

	public void afterLoading() {
		audio = new AudioManager(fileManager.getMusicOn(),fileManager.getSoundFxOn(),true);
		playMusic(MusicAmbient.MENU);
		setScreen(new Menu(this));
	}



	public Viewport getViewport()
	{
		return viewport;
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(BACKGROUND_RED, BACKGROUND_GREEN, BACKGROUND_BLUE, BACKGROUND_ALPHA);
        backGround.render();
		super.render();
	}

	public SpriteBatch getBatch()
	{
		return batch;
	}


    @Override
    public void dispose() {
		audio.disposeAll();
		Loader.getInstance().dispose();
	}

	public void setFileLog(boolean b)
	{
		this.fileLog = b;
	}
	public Boolean isLoggedInFile() {
		return fileLog;
	}

	public Boolean isLoggedInConn(){return connLog;}
	public void setConnLog(boolean b){this.connLog = b;}
	public void setSynchron(boolean b){this.synchron = b;}

	public void playSound(SoundFx toPlay) {  audio.playSound(toPlay); }

	public boolean isSoundOn() { return audio.isSoundOn(); }
	public boolean isMusicOn() { return audio.isMusicOn(); }

	public void setMusic(boolean b) {
		audio.setMusicOn(b);
		fileManager.setMusic(b);
	}

	public void setSound(boolean b) {
		audio.setSoundOn(b);
		fileManager.setSoundFx(b);
	}

	public String getUsername(){ return username; }
	public void setUsername(String s) { username=s; }

	public void vibrate() { audio.vibrate(); }

	public boolean isVibrationOn() { return audio.isVibrationOn(); }

	public void switchVibration(){ audio.switchVibration(); }

}
