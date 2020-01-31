package it.uniroma1.lcl.crucy.menu.login;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Map;

import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.utils.Parameters;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.BackButton;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.gameplay.tools.GifDecoder;
import it.uniroma1.lcl.crucy.login.social.SocialButton;
import it.uniroma1.lcl.crucy.login.social.utils.TextureDownload;
import it.uniroma1.lcl.crucy.menu.Menu;
import it.uniroma1.lcl.crucy.login.ProfilePicture;
import it.uniroma1.lcl.crucy.login.social.FacebookUI;
import it.uniroma1.lcl.crucy.login.social.utils.LoginObs;
import it.uniroma1.lcl.crucy.menu.UserTag;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class ChoiceScreen implements Screen, LoginObs
{
    private static final String TAG = ChoiceScreen.class.getSimpleName();

    private Stage stage;
    private MainCrucy main;
    private ProfilePicture profilePicture;

    private Menu menu;
    private ChoiceScreenController choiceScreenController;

    private FileManager fileManager;
    private TextureDownload tImg;

    private UserTag uTag;

    private boolean loggingIn = false;
    private float stateTime;
    //private Animation animation;

    // Facebook Fields
    private FacebookUI fbUI;

    public ChoiceScreen(final MainCrucy main, Menu menu) {
        this.menu = menu;
        this.main = main;

        Textures.loadTextures();
        //animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("Gifs/8.gif").readBytes());
        fileManager = FileManager.getInstance();

        stage = new Stage(main.getViewport(),main.getBatch());
        Gdx.input.setInputProcessor(stage);
        choiceScreenController = new ChoiceScreenController(main,stage,this);

        profilePicture = new ProfilePicture(stage);

        // Facebook
        fbUI = new FacebookUI(this, stage, main, profilePicture);

        //DA INTEGRARE E NO DOWNLOAD
        //tImg = new TextureDownload(stage.getBatch(), FileManager.getInstance().getPic(),profilePicture);

        uTag = new UserTag(stage);


    }

    public void fadeOut()
    {
        //fbUI.getFbLoginButton().addAction(Actions.fadeOut(0.3f));
    }

    @Override
    public void show() {
        // Fa comparire la profilePicture (Fade)
        profilePicture.getColor().a=0.0f;
        profilePicture.addAction(fadeIn(.2f));

        // Aggiunge l'effetto ai bottoni di login in base a quale è attivato
        choiceScreenController.getButtonText(ChoiceScreenController.Buttons.LOGIN).addAction(sequence(scaleTo(0f,0f),delay(.2f,scaleTo(1f,1f,.4f,Interpolation.swingOut))));

        // Aggiunge l'effetto al RegisterButton
        choiceScreenController.getButtonText(ChoiceScreenController.Buttons.REGISTER).addAction(sequence(scaleTo(0f,0f),delay(.3f,scaleTo(1f,1f,.4f,Interpolation.swingOut))));

        // Aggiunge l'effetto ai bottoni di login/logout di Facebook
        fbUI.getFbLoginButton().addAction(sequence(scaleTo(0f,0f),delay(0.2f,scaleTo(1f,1f,.4f,Interpolation.swingOut))));
        fbUI.getFbLogoutButton().addAction(sequence(scaleTo(0f,0f),delay(0.2f,scaleTo(1f,1f,.4f,Interpolation.swingOut))));
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }


    public void animationOut(float duration)
    {
        for(Actor a: stage.getActors())
            if(!a.equals(profilePicture) && !(a.equals(uTag.getBackground())))
                a.addAction(Actions.fadeOut(duration));

        profilePicture.addAction(Actions.moveTo(profilePicture.getX(), stage.getHeight(), duration, Interpolation.swing));
    }

    @Override
    public void render(float delta)
    {
        // Se premo il tasto indietro del telefono
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            main.playSound(SoundFx.SELECT);
            this.dispose();
            main.setScreen(new Menu(main));
        }

        if (fileManager.getSocial().equals(Parameters.FACEBOOK)) fbUI.getFbService().update();

        if(profilePicture.toAnimate())
        {
            uTag.getLabel().addAction(sequence(Actions.fadeOut(0.3f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    uTag.getLabel().setText(fileManager.getUsername());
                }
            }),
            Actions.fadeIn(0.3f)));
        }

        stage.act(delta);
        stage.draw();

    }

    public SocialButton getFacebookLoginButton () {
        return fbUI.getFbLoginButton();
    }

    public SocialButton getFacebookLogoutButton () {
        return fbUI.getFbLogoutButton();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose()
    {
        stage.dispose();
    }

    public void setIsLoading(Boolean bool)
    {
        this.loggingIn = bool;
        if(bool) {
            for(Actor a : stage.getActors())
                a.setTouchable(Touchable.disabled);
        } else {
            for(Actor a : stage.getActors())
                a.setTouchable(Touchable.enabled);
        }
    }

    public Menu getMainMenu()
    {
        return menu;
    }

    @Override
    public void updateObs(boolean isLogged) {
        //this.isLogged = isLogged;
        setLoginButtonStatus(isLogged);
    }

    @Override
    public void authFbLogin() {
        fbUI.fbLogin();
    }



    public UserTag getTag()
    {
        return uTag;
    }

    /**
     * Setta la visibilità dei bottoni di login
     * @param loggedIn  true se loggato, false altrimenti
     */
    private void setLoginButtonStatus(boolean loggedIn) {
        if (loggedIn) {
            fbUI.getFbLoginButton().setVisible(false);
            fbUI.getFbLogoutButton().setVisible(true);
            choiceScreenController.getButtonText(ChoiceScreenController.Buttons.LOGIN).setVisible(false);
            choiceScreenController.getButtonText(ChoiceScreenController.Buttons.REGISTER).setVisible(false);
        } else {
            fbUI.getFbLoginButton().setVisible(true);
            fbUI.getFbLogoutButton().setVisible(false);
            choiceScreenController.getButtonText(ChoiceScreenController.Buttons.LOGIN).setVisible(true);
            choiceScreenController.getButtonText(ChoiceScreenController.Buttons.REGISTER).setVisible(true);
            choiceScreenController.logoutClicked(ChoiceScreenController.Buttons.LOGIN);
        }
    }

}
