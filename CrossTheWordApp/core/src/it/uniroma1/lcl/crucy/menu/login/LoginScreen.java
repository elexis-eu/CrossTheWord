package it.uniroma1.lcl.crucy.menu.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.sun.org.apache.bcel.internal.generic.FLOAD;

import java.util.Map;
import java.util.Set;

import it.uniroma1.lcl.crucy.cache.Callback;
import it.uniroma1.lcl.crucy.cache.CallbackVoid;
import it.uniroma1.lcl.crucy.cache.RequestManager;
import it.uniroma1.lcl.crucy.cache.exceptions.UserDoesntExists;
import it.uniroma1.lcl.crucy.cache.exceptions.UserWrongPassword;
import it.uniroma1.lcl.crucy.menu.Menu;
import it.uniroma1.lcl.crucy.menu.UserTag;
import it.uniroma1.lcl.crucy.menu.login.buttons.LoginButton;
import it.uniroma1.lcl.crucy.utils.Dialoghi;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.BackButton;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.gameplay.tools.GifDecoder;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.ConnectionManager;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.MainThreadCallbackVoid;
import it.uniroma1.lcl.crucy.cache.exceptions.InvalidEmailException;
import it.uniroma1.lcl.crucy.cache.exceptions.InvalidPasswordException;
import it.uniroma1.lcl.crucy.cache.exceptions.SynchronizedExeption;
import it.uniroma1.lcl.crucy.cache.exceptions.UnexpectedServerResponseException;

/**
 * Created by bejgu on 11/09/2016.
 */
public class LoginScreen implements Screen
{
    private ChoiceScreen choiceScreen;
    private Stage stage;
    private static MainCrucy main;
    private LoginButton loginButton;

    private EmailTextField emailTextField;
    private PassTextField passTextField;
    private TextField.TextFieldStyle textFieldStyle;
    private UserTag uTag;

    private Animation animation;
    private boolean loadingState = false;
    private float elapsed;

    private GestureDetector gd;


    public LoginScreen(MainCrucy main, ChoiceScreen choiceScreen)  {
        this.main = main;
        this.choiceScreen = choiceScreen;
        stage=new Stage(main.getViewport(),main.getBatch());

        textFieldStyle = SupporterClass.getTextStyle();

        TextButton.TextButtonStyle tbs = ChoiceScreenController.getTextButtonStyleButtons();
        loginButton = new LoginButton(tbs,stage,main, ChoiceScreenController.Buttons.LOGIN);
        loginButton.setPosition(stage.getViewport().getWorldWidth()/2 - loginButton.getWidth()/2 , loginButton.getY() - 40f);
        loginButton.addListener(new LoginListener());
        animation =  GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("loading300.gif").readBytes());

        stage.addListener(new RemoveKeyboardListener(stage));
        emailTextField = new EmailTextField(textFieldStyle, stage);
        passTextField = new PassTextField(textFieldStyle, stage);
        uTag = new UserTag(stage);

        Gdx.input.setInputProcessor(stage);

    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta)
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            main.playSound(SoundFx.SELECT);
            this.dispose();
            main.setScreen(new ChoiceScreen(main, choiceScreen.getMainMenu()));
        }

        stage.act(delta);
        stage.draw();

        if (loadingState) {
            Gdx.input.setOnscreenKeyboardVisible(false);
            elapsed += Gdx.graphics.getDeltaTime();
            TextureRegion frame = animation.getKeyFrame(elapsed);
            main.getBatch().begin();
            main.getBatch().draw(Textures.background, 0, 0, stage.getWidth(), stage.getHeight());
            main.getBatch().draw(frame, stage.getViewport().getWorldWidth() / 2 - frame.getRegionWidth() / 2, stage.getViewport().getWorldHeight() / 2 - frame.getRegionHeight() / 2);
            main.getBatch().end();
        }

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


    public class LoginListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            main.playSound(SoundFx.SELECT);

            loadingState = true;
            Gdx.input.setOnscreenKeyboardVisible(false);
            ConnectionManager.getInstance().login(emailTextField.getText(), passTextField.getText(), new MainThreadCallbackVoid() {
                    @Override
                    public void onSuccessInMainThread() {
                        main.setFileLog(true);
                        main.setConnLog(true);
                        Gdx.input.setOnscreenKeyboardVisible(false);
                        FileManager.getInstance().saveLogin(emailTextField.getText(),ConnectionManager.getInstance().getUsername(), ConnectionManager.getInstance().getSessionId());
                        RequestManager.getInstance().getInitialCrosswords();
                        RequestManager.getInstance().recoverCrosswordState();
                        RequestManager.getInstance().getUserScore(new Callback<Map<String, Float>>() {
                            @Override
                            public void onSuccess(Map<String, Float> result) {
                                FileManager.getInstance().setUserScore(result);
                            }

                            @Override
                            public void onFailure(Throwable cause) {

                            }
                        });
                        loadingState = false;
                        main.setScreen(new Menu(main));
                    }

                    @Override
                    public void onFailureInMainThread(Throwable cause) {
                        cause.printStackTrace();
                        if(cause instanceof InvalidEmailException) {
                            System.out.println("EMAIL NON VALIDA");
                            SupporterClass.createDialog(main.getString(Dialoghi.INVALIDEMAIL.name()), stage, main, SupporterClass.DialogDestination.STAYHERE);
                        } else if(cause instanceof InvalidPasswordException) {
                            System.out.println("PASSWORD NON VALIDA");
                            System.out.println(cause.toString());
                            SupporterClass.createDialog(main.getString(Dialoghi.INVALIDPSWD.name()), stage, main, SupporterClass.DialogDestination.STAYHERE);
                        } else if (cause instanceof UserDoesntExists){
                            System.out.println("L'UTENTE RICHIESTO NON ESISTE");
                            SupporterClass.createDialog(main.getString(Dialoghi.USERDOESNTEXIST.name()), stage, main, SupporterClass.DialogDestination.STAYHERE);
                        } else if (cause instanceof UserWrongPassword) {
                            System.out.println("LA PASSWORD INSERITA NON E' VALIDA");
                            SupporterClass.createDialog(main.getString(Dialoghi.USERWRONGPASSWORD.name()), stage, main, SupporterClass.DialogDestination.STAYHERE);
                        } else if(cause instanceof UnexpectedServerResponseException) {
                            System.out.println("RISPOSTA SERVER NON VALIDA");
                            SupporterClass.createDialog(main.getString(Dialoghi.LOGINERROR.name()), stage, main, SupporterClass.DialogDestination.STAYHERE);
                        } else if(cause instanceof SynchronizedExeption) {
                            System.out.println("SINCRONIZZAZIONE FALLITA ");
                            main.setConnLog(true);
                            main.setFileLog(true);
                            FileManager.getInstance().saveLogin(emailTextField.getText(),ConnectionManager.getInstance().getUsername(), ConnectionManager.getInstance().getSessionId());
                            SupporterClass.createDialog(main.getString(Dialoghi.SYNCERROR.name()) ,stage,main, SupporterClass.DialogDestination.DIALOCHOICESMAIN);
                        } else
                            SupporterClass.createDialog(main.getString(Dialoghi.CONNECTIONERROR.name()) ,stage,main, SupporterClass.DialogDestination.STAYHERE);
                        loadingState = false;
                    }
                });
        }
    }


    public void animationout(float duration)
    {
        for(Actor a: stage.getActors()) {
            if (!choiceScreen.getTag().isEqual(a))
                a.addAction(Actions.fadeOut(duration));
        }
        LoginScreen.this.dispose();
        main.setScreen(new ChoiceScreen(main,LoginScreen.this.choiceScreen.getMainMenu()));
    }
}
