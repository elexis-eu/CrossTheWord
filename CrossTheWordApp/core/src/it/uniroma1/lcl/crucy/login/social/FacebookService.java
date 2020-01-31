package it.uniroma1.lcl.crucy.login.social;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.StreamUtils;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.tomgrill.gdxfacebook.core.GDXFacebook;
import de.tomgrill.gdxfacebook.core.GDXFacebookCallback;
import de.tomgrill.gdxfacebook.core.GDXFacebookConfig;
import de.tomgrill.gdxfacebook.core.GDXFacebookError;
import de.tomgrill.gdxfacebook.core.GDXFacebookGraphRequest;
import de.tomgrill.gdxfacebook.core.GDXFacebookSystem;
import de.tomgrill.gdxfacebook.core.JsonResult;
import de.tomgrill.gdxfacebook.core.SignInMode;
import de.tomgrill.gdxfacebook.core.SignInResult;
import de.tomgrill.gdxfacebook.core.SignOutMode;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.CallbackVoid;
import it.uniroma1.lcl.crucy.cache.ConnectionManager;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.menu.login.ChoiceScreen;
import it.uniroma1.lcl.crucy.cache.RequestManager;
import it.uniroma1.lcl.crucy.cache.utils.Parameters;
import it.uniroma1.lcl.crucy.login.ProfilePicture;
import it.uniroma1.lcl.crucy.login.social.utils.LoginObs;
import it.uniroma1.lcl.crucy.login.social.utils.LoginSub;


/**
 * Classe che permette la gestione del login con Facebook. Invia a {@link ChoiceScreen}
 * gli aggiornamenti sullo stato del login.
 */

public class FacebookService implements LoginSub {

    private static final String TAG = FacebookService.class.getSimpleName();

    private Stage stage;
    private MainCrucy main;

    private FileHandle file;
    private RequestManager requestManager;
    private FileManager fileManager;
    private ProfilePicture profilePicture;
    private LoginObs choiceScreen;

    private GDXFacebook gdxFacebook;
    private Array<String> permissionsRead = new Array<String>();
    private Array<String> permissionsPublish = new Array<String>();

    private boolean bAutoLoginFinished = false;
    private boolean loginFB = false;
    private boolean newPic;
    private boolean reset;


    private static final int picDimension = 400;

    private static FacebookService instance;

    public static FacebookService getInstance() {
        if (instance == null) instance = new FacebookService();
        return instance;
    }

    private FacebookService() {

        // Inizializzazione Facebook SDK
        GDXFacebookConfig config = new GDXFacebookConfig();
        config.APP_ID = "2424972070929191";//"1953867464839887";
        config.PREF_FILENAME = ".facebookSessionData";
        config.GRAPH_API_VERSION = "v2.7"; // 2.7 è l'ultima release. Default v2.6
        gdxFacebook = GDXFacebookSystem.install(config);

        //pref = Pref.getInstance();
        requestManager = RequestManager.getInstance();
        fileManager = FileManager.getInstance();
        file = Gdx.files.local("social/dl_profile_pic.png");

        permissionsRead.add("email");
        permissionsRead.add("public_profile");
        permissionsRead.add("user_friends");

        permissionsPublish.add("publish_actions");

    }

    /**
     * Permette l'update della UI in {@link ChoiceScreen} se
     * loggati con Facebook
     */
    public void update() {
        //System.out.println("newpic "+ newPic);
        if (loginFB) {
            //System.out.println("logiFB true");
            profilePicture.fbLoggedIn();
            newPic = false;

            notifyObs(true);
        }
        else
        {
            //System.out.println("logiFB false");

            profilePicture.reset();
            reset = false;
        }
    }

    public void login() {
        loginWithReadPermissions();
    }

    /**
     * Permette il logout da Facebook
     */
    public void fbLogout() {
        gdxFacebook.signOut(SignOutMode.DELETE_SESSION_DATA);
        notifyObs(false);
        cleanPref();
        reset=true;
        loginFB=false;
    }

    /**
     * Metodo per l' autologin.
     * <p>
     * GWT HTML5 is single threaded and the Facebook Javascript SDK is loaded
     * asynchronously which takes a few seconds. So we have to wait till this is finished.
     * <p>
     * In a productive environment I recommend to do this while you show your loading screen.
     */
    public void autologin() {
        if (!bAutoLoginFinished && fileManager.getSocial().equals("facebook")
                && gdxFacebook.isLoaded()) {
            System.out.println("Autologin FB andato");
            bAutoLoginFinished = true;
            loginWithReadPermissionsAuto();
        }
    }

    // -------------------------- METODI PRIVATI ---------------------------------------------------

    /**
     * Login con permessi di lettura
     */
    private void loginWithReadPermissions() {
        gdxFacebook.signIn(SignInMode.READ, permissionsRead,
                new GDXFacebookCallback<SignInResult>() {

                    @Override
                    public void onSuccess(SignInResult result) {
                        Gdx.app.debug(TAG, "LOGIN (read permissions): Login avvenuto con successo");
                        String token = result.getAccessToken().getToken();
                        System.out.println("Access_token: " + token);
                        getUserInfo();

                        ConnectionManager.getInstance().sendFacebookToken(token, new CallbackVoid() {
                            @Override
                            public void onSuccess() {
                                System.out.println("sendFbToken chiamato");
                                choiceScreen.updateObs(true);
                                choiceScreen.authFbLogin();

                                newPic =true;
                                loginFB =true;
                            }

                            @Override
                            public void onFailure(Throwable cause) {

                                System.out.println("causa fallimento "+ cause);
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        Gdx.app.debug(TAG, "LOGIN (read permissions): Login cancellato dall'utente");

                    }

                    @Override
                    public void onFail(Throwable t) {
                        Gdx.app.error(TAG, "LOGIN (read permissions): Errore, " + t);
                        fbLogout();
                    }

                    @Override
                    public void onError(GDXFacebookError error) {
                        Gdx.app.error(TAG,
                                "LOGIN (read permissions): Errore: " + error.getErrorMessage());
                        fbLogout();
                    }
                });
    }

    /**
     * Login con permessi di lettura automatico
     */
    private void loginWithReadPermissionsAuto() {
        gdxFacebook.signIn(SignInMode.READ, permissionsRead,
                new GDXFacebookCallback<SignInResult>() {

                    @Override
                    public void onSuccess(SignInResult result) {
                        Gdx.app.debug(TAG, "LOGIN (read permissions): Login avvenuto con successo");

                        getUserInfo();
                        fileManager.setSocial("facebook");
                    }

                    @Override
                    public void onCancel() {
                        Gdx.app.debug(TAG, "LOGIN (read permissions): Login cancellato dall'utente");

                    }

                    @Override
                    public void onFail(Throwable t) {
                        Gdx.app.error(TAG, "LOGIN (read permissions): Errore, " + t);
                        fbLogout();
                    }

                    @Override
                    public void onError(GDXFacebookError error) {
                        Gdx.app.error(TAG,
                                "LOGIN (read permissions): Errore: " + error.getErrorMessage());
                        fbLogout();
                    }
                });
    }

    /**
     * Login con permessi di publicazione
     */
    private void loginWithPublishPermissions() {
        gdxFacebook.signIn(SignInMode.PUBLISH, permissionsPublish,
                new GDXFacebookCallback<SignInResult>() {

                    @Override
                    public void onSuccess(SignInResult result) {
                        Gdx.app.debug(TAG, "LOGIN (publish permissions): Login avvenuto con successo");

                        getUserInfo();

                    }

                    @Override
                    public void onCancel() {
                        Gdx.app.debug(TAG, "LOGIN (publish permissions): Login cancellato dall'utente");

                    }

                    @Override
                    public void onFail(Throwable t) {
                        Gdx.app.error(TAG, "LOGIN (publish permissions): Errore, " + t);
                        fbLogout();
                    }

                    @Override
                    public void onError(GDXFacebookError error) {
                        Gdx.app.error(TAG,
                                "LOGIN (publish permissions): Errore: " + error.getErrorMessage());
                        fbLogout();
                    }
                });
    }

    /**
     * Permette di effettuare delle richieste tramite le API Graph e avere accesso alle
     * informazioni dell'utente Facebook loggato.
     */
    private void getUserInfo() {

        GDXFacebookGraphRequest request = new GDXFacebookGraphRequest()
                .setNode("me")
                .putField("fields", "email,name,picture,id")
                .useCurrentAccessToken();

        gdxFacebook.graph(request, new GDXFacebookCallback<JsonResult>() {

            @Override
            public void onSuccess(JsonResult result) {
                fileManager = FileManager.getInstance();
                JsonValue userData = result.getJsonValue();

                Map<String, String> map = new HashMap<String, String>();
                String pic;

                map.put(Parameters.USERNAME, userData.getString("name").split(" ")[0]);
                map.put(Parameters.EMAIL, userData.getString("email"));
                map.put("id", userData.getString("id"));

                pic = "https://graph.facebook.com/" + userData.getString("id")
                        + "/picture?width="+ picDimension+"&height="+picDimension+"400";

                map.put(Parameters.PICTURE, pic);
                fileManager.saveLogin(map);

                if (fileManager.getPicUrl().equals("") ||
                        !fileManager.getPicUrl().equals(userData.get("picture").get("data").getString("url"))) {
                    newPic = true;
                    fileManager.setPicUrl(userData.get("picture").get("data").getString("url"));
                }

                if (!fileManager.getPic().equals("")) {
                    loginFB = true;
                }

                fileManager.setSocial(Parameters.FACEBOOK);
            }

            @Override
            public void onCancel() {
                fbLogout();
                Gdx.app.debug(TAG, "Graph Reqest: nada.");

            }

            @Override
            public void onFail(Throwable t) {
                Gdx.app.error(TAG, "Graph Reqest: fallito con eccezione: " + t);
                fbLogout();
            }

            @Override
            public void onError(GDXFacebookError error) {
                Gdx.app.error(TAG, "Graph Reqest: Errore con access token.");
                fbLogout();
            }
        });
    }



    /**
     * Elimina le informazioni di Facebook al logout
     */
    private void cleanPref() {
        ConnectionManager.getInstance().logout(new CallbackVoid() {
            @Override
            public void onSuccess() {
                FileManager.getInstance().logout();
            }

            @Override
            public void onFailure(Throwable cause) {
                FileManager.getInstance().logout();
            }
        });
    }


    @Override
    public void addObserver(LoginObs obs) {
        choiceScreen = obs;
    }

    /**
     * Aggiorna {@link ChoiceScreen} sullo stato del login
     * @param isLogged
     */
    @Override
    public void notifyObs(boolean isLogged) {
        choiceScreen.updateObs(isLogged);
    }

    /*
    Non so se è il metodo migliore per creare texture da url, l'ho trovato nella documentazione
    di LibGDX.
    Nella documentazione viene usato dentro ad un nuovo Thread.
    */
    private int downloadProfilePic(byte[] out, String url) {
        InputStream in = null;
        try {
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(true);
            conn.connect();
            in = conn.getInputStream();
            int readBytes = 0;
            while (true) {
                int length = in.read(out, readBytes, out.length - readBytes);
                if (length == -1) break;
                readBytes += length;
            }
            return readBytes;
        } catch (Exception ex) {
            return 0;
        } finally {
            StreamUtils.closeQuietly(in);
        }
    }

    private void makePic(String url) {
        byte[] bytes = new byte[200 * 1024]; // assuming the content is not bigger than 200kb.
        int numBytes = downloadProfilePic(bytes, url);
        if (numBytes != 0) {
            // load the pixmap, make it a power of two if necessary (not needed for GL ES 2.0!)
            Pixmap pixmap = new Pixmap(bytes, 0, numBytes);
            final int originalWidth = pixmap.getWidth();
            final int originalHeight = pixmap.getHeight();
            System.out.println("maked pic "+originalHeight+ " "+originalWidth);
            final Pixmap potPixmap = new Pixmap(originalWidth, originalHeight, pixmap.getFormat());
            potPixmap.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
            pixmap.dispose();
            PixmapIO.writePNG(file, potPixmap);
            potPixmap.dispose();
        }
    }

    // ----------------------------- SETTER --------------------------------------------------------

    public void setIsLoggedIn(boolean b) {
        loginFB = b;
    }

    public void setChoiceScreen(ChoiceScreen choiceScreen) {
        this.choiceScreen = choiceScreen;
    }

    public void setMain(final MainCrucy main) {
        this.main = main;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }
}
