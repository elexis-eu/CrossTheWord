package it.uniroma1.lcl.crucy.login.social;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import it.uniroma1.lcl.crucy.menu.login.ChoiceScreenController;
import it.uniroma1.lcl.crucy.menu.login.buttons.ChoiceButtonInterface;

public class SocialButton extends ImageButton implements ChoiceButtonInterface{

    private Stage stage;
    private Drawable imageUp;
    private ChoiceScreenController.Buttons button;

    public SocialButton(Drawable imageUp, Drawable imageDown, Stage stage,
                        ChoiceScreenController.Buttons button) {
        super(imageUp, imageDown);
        this.stage = stage;
        this.imageUp = imageUp;
        this.button = button;

        setPosition(stage.getViewport().getWorldWidth()/2 - this.getMinWidth()/2,
                stage.getViewport().getWorldHeight()/2 - this.getHeight()*2 + 50);
        setOrigin(getOriginX()+getWidth()/2,getOriginY()+getHeight()/2);
        setTransform(true);

        stage.addActor(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        this.setSize(imageUp.getMinWidth(), imageUp.getMinHeight());

        super.draw(batch, parentAlpha);
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

