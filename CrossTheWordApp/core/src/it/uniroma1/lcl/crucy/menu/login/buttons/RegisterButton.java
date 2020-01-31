package it.uniroma1.lcl.crucy.menu.login.buttons;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.menu.login.ChoiceScreenController;
import it.uniroma1.lcl.crucy.utils.Parole;

/**
 * Created by bejgu on 11/09/2016.
 */
public class RegisterButton extends TextButton implements ChoiceButtonInterface
{
    private Stage stage;
    private MainCrucy main;
    private ChoiceScreenController.Buttons button;

    public RegisterButton(TextButtonStyle tbs, Stage stage, MainCrucy main, ChoiceScreenController.Buttons button)
    {
        super(Parole.getInstance().getString("Registra"),tbs);
        this.main = main;
        this.stage = stage;
        this.button = button;
        setPosition(stage.getViewport().getWorldWidth()/2+10,
                stage.getViewport().getWorldHeight()/2-this.getHeight()/2);
        setOrigin(getOriginX()+getWidth()/2,getOriginY()+getHeight()/2);
        setTransform(true);
        setVisible(true);
        setSize(stage.getWidth()/2.5f, 150f);
        getLabel().setFontScale(.7f);
        stage.addActor(this);
    }

    @Override
    public ChoiceScreenController.Buttons getButton() {
        return button;
    }

    @Override
    public Object getType() {
        return this;
    }
}
