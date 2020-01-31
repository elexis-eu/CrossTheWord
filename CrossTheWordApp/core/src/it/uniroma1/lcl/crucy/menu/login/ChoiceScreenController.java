package it.uniroma1.lcl.crucy.menu.login;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;

import it.uniroma1.lcl.crucy.login.social.SocialButton;
import it.uniroma1.lcl.crucy.utils.Dialoghi;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.menu.login.buttons.LoginButton;
import it.uniroma1.lcl.crucy.menu.login.buttons.RegisterButton;
import it.uniroma1.lcl.crucy.cache.ConnectionManager;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.MainThreadCallbackVoid;
import it.uniroma1.lcl.crucy.utils.Loader;

/**
 * Created by bejgu on 01/10/2016.
 */

public class ChoiceScreenController
{
    public enum Buttons {
        LOGIN,REGISTER,FACEBOOK
    }


    private HashMap<Buttons,TextButton> mappaPulsantiText = new HashMap<Buttons, TextButton>();
    private HashMap<SocialButton, ImageButton> mappaPulsantiImage = new HashMap<SocialButton, ImageButton>();

    private ChoiceScreen choiceScreen;
    private MainCrucy main;
    private Stage stage;
    private ChoiceScreenController choiceScreenController;
    private TextButton.TextButtonStyle tbs;

    public ChoiceScreenController(MainCrucy main, Stage stage,ChoiceScreen choiceScreen) {
        this.main = main;
        this.stage = stage;
        this.choiceScreen = choiceScreen;
        choiceScreenController = this;
        tbs = getTextButtonStyleButtons();

        mappaPulsantiText.put(Buttons.LOGIN,
                new LoginButton(tbs, stage, main, ChoiceScreenController.Buttons.LOGIN));
        mappaPulsantiText.put( Buttons.REGISTER,
                new RegisterButton(tbs,stage, main, ChoiceScreenController.Buttons.REGISTER));

        if(main.isLoggedInFile()) {
            mappaPulsantiText.get(Buttons.LOGIN).setText("Logout");
            mappaPulsantiText.get(Buttons.REGISTER).setVisible(false);

            ((LoginButton)choiceScreenController.getButtonText(ChoiceScreenController.Buttons.LOGIN)).centerButton();
            mappaPulsantiText.get(Buttons.LOGIN).addListener(new LogoutListenner());

        } else {
            mappaPulsantiText.get(Buttons.REGISTER).setVisible(true);
            mappaPulsantiText.get(Buttons.LOGIN).addListener(new LoginListenner());
        }

        mappaPulsantiText.get(Buttons.REGISTER).addListener(new RegisterListenner());
    }

    public void loginClicked(Buttons button)
    {
        switch (button)
        {
            case LOGIN: {
                mappaPulsantiText.get(Buttons.LOGIN).setText("Logout");
                mappaPulsantiText.get(Buttons.LOGIN).clearListeners();
                mappaPulsantiText.get(Buttons.LOGIN).addListener(new LogoutListenner());
                //TODO disabilitare Pulsanti facebook e google per il momento.. poi bisogna fare l'integrazione

            }
            case FACEBOOK:
            {
                mappaPulsantiText.get(Buttons.LOGIN).setTouchable(Touchable.disabled);
                mappaPulsantiText.get(Buttons.REGISTER).setTouchable(Touchable.disabled);

                //TODO gestire pulsante logout facebook
            }
        }
    }

    public void logoutClicked(Buttons button)
    {
        switch (button) {
            case LOGIN: {
                TextButton login = mappaPulsantiText.get(Buttons.LOGIN);
                login.setVisible(false);
                mappaPulsantiText.get(Buttons.REGISTER).setVisible(true);

                login = new LoginButton(tbs, stage, main, ChoiceScreenController.Buttons.LOGIN);
                login.addListener(new LoginListenner());

            }
            case FACEBOOK: {
                mappaPulsantiText.get(Buttons.LOGIN).setTouchable(Touchable.enabled);
                mappaPulsantiText.get(Buttons.REGISTER).setTouchable(Touchable.enabled);

            }
        }
    }

    public TextButton getButtonText(Buttons button)
    {
        return mappaPulsantiText.get(button);
    }
    public ImageButton getButtonImage(Buttons button)
    {
        return mappaPulsantiImage.get(button);
    }





    /**
     * Metodo che crea lo Style del TextButton
     */
    protected static TextButton.TextButtonStyle getTextButtonStyleButtons()
    {
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = Textures.font;
        tbs.fontColor= Textures.font.getColor();
        tbs.up = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Buttons/menu_button_pop_2d.png", Texture.class)));
        tbs.down = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Buttons/menu_button_pop_active.png", Texture.class)));
        return tbs;
    }

    // ---------------------------- FB ---------------------------------




    ///////////////////////////////////LISTENERS///////////////////////////////////////////////////
    /**
     * Listener usato per quando si clicca sul logout
     */
    private class LogoutListenner extends ClickListener
    {
        @Override
        public void clicked(InputEvent event, float x, float y)
        {

            main.playSound(SoundFx.SELECT);
            main.setFileLog(false);
            FileManager.getInstance().logout();
            choiceScreen.setIsLoading(true);
            ConnectionManager.getInstance().logout(new MainThreadCallbackVoid() {
                @Override
                public void onSuccessInMainThread()
                {
                    choiceScreenController.logoutClicked(Buttons.LOGIN);
                    SupporterClass.createDialog(main.getString(Dialoghi.LOGOUTCOMPLETED.name()),stage,main, SupporterClass.DialogDestination.TOMAIN);
                    choiceScreen.setIsLoading(false);
                }

                @Override
                public void onFailureInMainThread(Throwable cause) {
                    //SupporterClass.createDialog("Logout failed",stage,main, SupporterClass.DialogDestination.STAYHERE);
                    choiceScreenController.logoutClicked(Buttons.LOGIN);
                    SupporterClass.createDialog(main.getString(Dialoghi.LOGOUTCOMPLETED.name()),stage,main, SupporterClass.DialogDestination.TOMAIN);
                    choiceScreen.setIsLoading(false);
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
    }

    /**
     * Listener usato per quando si clicca sul login
     */
    private class LoginListenner extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            main.playSound(SoundFx.SELECT);
            animationout(0.3f);
            stage.addAction(Actions.delay(0.3f,
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            main.setScreen(new LoginScreen(main,choiceScreen));
                        }
                    })));
        }
    }



    /**
     * Listener usato per quando si clicca sul Register
     */

    private class RegisterListenner extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            main.playSound(SoundFx.SELECT);
            if(main.isLoggedInFile()) {
                SupporterClass.createDialog(main.getString(Dialoghi.ALREADYLOGGEDIN.name()),stage,main, SupporterClass.DialogDestination.STAYHERE);
            } else {
                animationout(0.3f);
                stage.addAction(Actions.delay(0.3f,
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                main.setScreen(new RegisterScreen(main,choiceScreen));
                            }
                        })));
            }
        }
    }
}
