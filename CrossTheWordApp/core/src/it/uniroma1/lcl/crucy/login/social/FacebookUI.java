package it.uniroma1.lcl.crucy.login.social;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import it.uniroma1.lcl.crucy.cache.ConnectionManager;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.MainThreadCallbackVoid;
import it.uniroma1.lcl.crucy.cache.exceptions.InvalidEmailException;
import it.uniroma1.lcl.crucy.cache.exceptions.InvalidPasswordException;
import it.uniroma1.lcl.crucy.cache.exceptions.SynchronizedExeption;
import it.uniroma1.lcl.crucy.cache.exceptions.UnexpectedServerResponseException;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.menu.login.ChoiceScreen;
import it.uniroma1.lcl.crucy.menu.login.ChoiceScreenController;
import it.uniroma1.lcl.crucy.login.ProfilePicture;
import it.uniroma1.lcl.crucy.menu.login.SupporterClass;
import it.uniroma1.lcl.crucy.utils.Dialoghi;
import it.uniroma1.lcl.crucy.utils.Loader;

import static it.uniroma1.lcl.crucy.login.social.utils.Assets.DEFAULT_PIC;
import static it.uniroma1.lcl.crucy.login.social.utils.Assets.FB_SIGNIN_CLIECKED;
import static it.uniroma1.lcl.crucy.login.social.utils.Assets.FB_SIGNOUT;
import static it.uniroma1.lcl.crucy.login.social.utils.Assets.FB_SIGNOUT_CLICKED;
import static it.uniroma1.lcl.crucy.login.social.utils.Assets.FB_SINGIN;

/**
 *  Classe contenente gli elementi leagti al login con Facebook
 *  dell' interfaccia grafica di {@link ChoiceScreen}.
 */
public class FacebookUI {

    private Stage stage;
    private MainCrucy main;
    private ChoiceScreen cs;

    private FacebookService fbService;
    private SocialButton fbLoginButton;
    private SocialButton fbLogoutButton;

    private boolean loggingIn;

    public FacebookUI(ChoiceScreen cs, Stage stage,final MainCrucy main,
                      final ProfilePicture profilePicture) {

        this.cs = cs;
        this.stage = stage;
        this.main = main;

        fbService = FacebookService.getInstance();
        fbService.setProfilePicture(profilePicture);
        fbService.setStage(stage);
        fbService.setChoiceScreen(cs);
        fbService.addObserver(cs);
        fbService.setIsLoggedIn(true);

        // Inizializzazione dei bottoni

        fbLoginButton = new SocialButton(new SpriteDrawable(new Sprite(
                Loader.getInstance().get(FB_SINGIN,Texture.class))),
                new SpriteDrawable(new Sprite(Loader.getInstance().get(FB_SIGNIN_CLIECKED,Texture.class))), stage, ChoiceScreenController.Buttons.FACEBOOK);

        fbLogoutButton = new SocialButton(new SpriteDrawable(new Sprite(
                Loader.getInstance().get(FB_SIGNOUT,Texture.class))),
                new SpriteDrawable(new Sprite(Loader.getInstance().get(FB_SIGNOUT_CLICKED,Texture.class))), stage, ChoiceScreenController.Buttons.FACEBOOK);


        fbLogoutButton.setVisible(false);

        fbLoginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.playSound(SoundFx.SELECT);
                fbService.login();
            }
        });
        fbLoginButton.setVisible(true);

        fbLogoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.playSound(SoundFx.SELECT);
                fbService.fbLogout();
                //profilePicture.setPicture(Loader.getInstance().get(DEFAULT_PIC,Texture.class));
            }
        });
    }

    /**
     *  Metodo che permette l'effettivo login al server
     */
    public void fbLogin() {
        loggingIn = true;
        System.out.println(ConnectionManager.getInstance().getSessionId());
        ConnectionManager.getInstance().login(ConnectionManager.getInstance().getSessionId(), new MainThreadCallbackVoid() {
            @Override
            public void onSuccessInMainThread() {
                System.out.println("fbUI.fbLogin ok");
                loggingIn = false;
                main.setFileLog(true);
                main.setConnLog(true);
                FileManager.getInstance().saveLogin(FileManager.getInstance().getEmail(), FileManager.getInstance().getUsername(), ConnectionManager.getInstance().getSessionId());
                SupporterClass.createDialog(main.getString(Dialoghi.LOGINCOMPLETED.name()), stage, main, SupporterClass.DialogDestination.TOMAIN);
            }

            @Override
            public void onFailureInMainThread(Throwable cause) {
                cause.printStackTrace();
                loggingIn = false;
                if(cause instanceof InvalidEmailException)
                    SupporterClass.createDialog(
                            main.getString(Dialoghi.INVALIDEMAIL.name()) +"\"" +cause.getMessage()+"\"",
                            stage, main, SupporterClass.DialogDestination.STAYHERE);
                else if(cause instanceof InvalidPasswordException)
                    SupporterClass.createDialog(
                            main.getString(Dialoghi.INVALIDPSWD.name()),
                            stage, main, SupporterClass.DialogDestination.STAYHERE);
                else if(cause instanceof UnexpectedServerResponseException)
                    SupporterClass.createDialog(
                            main.getString(Dialoghi.LOGINERROR.name()),
                            stage, main, SupporterClass.DialogDestination.STAYHERE);
                else if(cause instanceof SynchronizedExeption) {
                    main.setConnLog(true);
                    main.setFileLog(true);
                    FileManager.getInstance().saveLogin(FileManager.getInstance().getEmail(),
                            FileManager.getInstance().getUsername(),
                            ConnectionManager.getInstance().getSessionId());
                    SupporterClass.createDialog(
                            main.getString(Dialoghi.SYNCERROR.name()),
                            stage,main, SupporterClass.DialogDestination.DIALOCHOICESMAIN);
                } else
                    SupporterClass.createDialog(
                            main.getString(Dialoghi.CONNECTIONERROR.name()+"facebookui"),
                            stage,main, SupporterClass.DialogDestination.STAYHERE);
            }
        });
    }

    // -------------------------------------- GETTER -----------------------------------------------

    public FacebookService getFbService() {
        return fbService;
    }

    public SocialButton getFbLoginButton() {
        return fbLoginButton;
    }

    public SocialButton getFbLogoutButton() {
        return fbLogoutButton;
    }

}
