package it.uniroma1.lcl.crucy.menu.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import it.uniroma1.lcl.crucy.cache.RequestManager;
import it.uniroma1.lcl.crucy.cache.exceptions.UnexpectedServerResponseException;
import it.uniroma1.lcl.crucy.menu.Menu;
import it.uniroma1.lcl.crucy.menu.UserTag;
import it.uniroma1.lcl.crucy.menu.login.buttons.RegisterButton;
import it.uniroma1.lcl.crucy.utils.Dialoghi;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.BackButton;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.gameplay.tools.GifDecoder;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.CallbackVoid;
import it.uniroma1.lcl.crucy.cache.ConnectionManager;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.exceptions.EmailAlreadyUsedException;
import it.uniroma1.lcl.crucy.cache.exceptions.InvalidEmailException;
import it.uniroma1.lcl.crucy.cache.exceptions.InvalidPasswordException;
import it.uniroma1.lcl.crucy.cache.exceptions.InvalidUsernameException;
import it.uniroma1.lcl.crucy.cache.exceptions.SynchronizedExeption;
import it.uniroma1.lcl.crucy.cache.exceptions.UsernameAlreadyUsedException;

/**
 * Created by bejgu on 11/09/2016.
 */
public class RegisterScreen implements Screen {

    private ChoiceScreen choiceScreen;
    private Stage stage;
    private MainCrucy main;
    private RegisterButton registerButton; // Bottone per la registrazione
    private UserTextField userTextField; // campo per l'username
    private PassTextField passTextField; // campo per la password
    private EmailTextField emailTextField; // campo per l'email
    private BackButton btnBack; // Bottone per tornare indietro
    private UserTag uTag; // Etichetta con il nome dell'utente


    // E' una varibile booleana che serve per settare lo screen da dentro la seconda callback, a quanto pare senza questa variabile boolean
    // quando provi a settare lo screen da dentro la seconda callback ovvero quella di login da errore di OpenGL.
    private boolean loggingIn = false;
    private Animation animation;
    private Throwable errorCause = null;

    private float stateTime;
    private TextField textField;



    public RegisterScreen(MainCrucy main, ChoiceScreen choiceScreen)
    {
        this.main = main;
        this.choiceScreen = choiceScreen;
        stage=new Stage(main.getViewport(),main.getBatch());

        //Creazione del pulsante di Registrazione
        TextButton.TextButtonStyle tbs = ChoiceScreenController.getTextButtonStyleButtons();
        registerButton = new RegisterButton(tbs,stage,main, ChoiceScreenController.Buttons.REGISTER);
        registerButton.setPosition(stage.getViewport().getWorldWidth()/2-registerButton.getWidth()/2 , registerButton.getY()-50f);
        registerButton.addListener(new RegisterListener());

        stage.addListener(new RemoveKeyboardListener(stage));
        //Creazione dei campi per l'inserimento dei dati
        TextField.TextFieldStyle style = SupporterClass.getTextStyle();

        emailTextField = new EmailTextField(style, stage);
        passTextField = new PassTextField(style, stage);
        userTextField = new UserTextField(style, stage);

        uTag = new UserTag(stage);
        animation =  GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("loading300.gif").readBytes());
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void show() {

    }


    @Override
    public void render(float delta)
    {
        // Se premo il tasto indietro del telefono
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            RegisterScreen.this.main.playSound(SoundFx.SELECT);
            RegisterScreen.this.dispose();
            RegisterScreen.this.main.setScreen(new ChoiceScreen(RegisterScreen.this.main, RegisterScreen.this.choiceScreen.getMainMenu()));
        }

        stage.act(delta);
        stage.draw();

        if (loggingIn) {
            stateTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            main.getBatch().begin();
            main.getBatch().draw(Textures.background, 0, 0,stage.getWidth(), stage.getHeight());
            main.getBatch().draw(currentFrame, stage.getViewport().getWorldWidth()/2 - currentFrame.getRegionWidth()/2, stage.getViewport().getWorldHeight()/2 - currentFrame.getRegionHeight()/2);
            main.getBatch().end();
        } else if (errorCause != null) {
            if(errorCause instanceof InvalidEmailException)
                SupporterClass.createDialog(main.getString(Dialoghi.INVALIDEMAIL.name()), stage, main, SupporterClass.DialogDestination.STAYHERE);
            else if( errorCause instanceof InvalidUsernameException)
                SupporterClass.createDialog(main.getString(Dialoghi.INVALIDUSER.name()),stage,main, SupporterClass.DialogDestination.STAYHERE);
            else if(errorCause instanceof InvalidPasswordException)
                SupporterClass.createDialog(main.getString(Dialoghi.INVALIDPSWD.name()) ,stage,main, SupporterClass.DialogDestination.STAYHERE);
            else if (errorCause instanceof UsernameAlreadyUsedException)
                SupporterClass.createDialog(main.getString(Dialoghi.USERUSED.name()),stage,main, SupporterClass.DialogDestination.STAYHERE);
            else if(errorCause instanceof EmailAlreadyUsedException)
                SupporterClass.createDialog(main.getString(Dialoghi.EMAILUSED.name()),stage,main, SupporterClass.DialogDestination.STAYHERE);
            else
                SupporterClass.createDialog(main.getString(Dialoghi.CONNECTIONERROR.name()) ,stage,main, SupporterClass.DialogDestination.STAYHERE);
            errorCause = null;
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
    public void dispose() {
        stage.dispose();
    }

    private class RegisterListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            main.playSound(SoundFx.SELECT);
            loggingIn = true;
            Gdx.input.setOnscreenKeyboardVisible(false);

            ConnectionManager.getInstance().register(userTextField.getText(), passTextField.getText(),emailTextField.getText(), new CallbackVoid() {
                @Override
                public void onSuccess() {
                    ConnectionManager.getInstance().login(emailTextField.getText(), passTextField.getText(), new CallbackVoid() {
                        @Override
                        public void onSuccess() {
                            main.setFileLog(true);
                            FileManager.getInstance().saveLogin(emailTextField.getText(),userTextField.getText(), ConnectionManager.getInstance().getSessionId());
                            Gdx.input.setOnscreenKeyboardVisible(false);
                            RequestManager.getInstance().getInitialCrosswords();

                            RunnableAction act = new RunnableAction() {
                                @Override
                                public void run() {
                                    loggingIn = false;
                                    main.setScreen(new Menu(main));
                                }
                            };

                            stage.addAction(Actions.delay(0.5f, act));
                        }

                        @Override
                        public void onFailure(Throwable cause) {
                            cause.printStackTrace();
                            if(cause instanceof SynchronizedExeption) {
                                main.setFileLog(true);
                                FileManager.getInstance().saveLogin(emailTextField.getText(),ConnectionManager.getInstance().getUsername(), ConnectionManager.getInstance().getSessionId());
                            } else if (cause instanceof UnexpectedServerResponseException){
                                SupporterClass.createDialog(main.getString(Dialoghi.ACCOUNTCREATEDLOGINFAILED.name()), stage, main, SupporterClass.DialogDestination.STAYHERE);
                            }
                            Gdx.input.setOnscreenKeyboardVisible(false);
                            loggingIn = false;
                        }
                    });
                }

                @Override
                public void onFailure(Throwable cause) {
                    cause.printStackTrace();
                    loggingIn = false;
                    errorCause = cause;
                    Gdx.input.setOnscreenKeyboardVisible(false);
                }
            });

        }
    }
    public void animationOut(float duration) {
        for(Actor a: stage.getActors()) {
            if (!choiceScreen.getTag().isEqual(a))
                a.addAction(Actions.fadeOut(duration));
        }
        dispose();
        main.setScreen(new Menu(main));
    }
}
