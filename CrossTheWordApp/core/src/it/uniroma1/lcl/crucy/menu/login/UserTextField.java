package it.uniroma1.lcl.crucy.menu.login;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created by bejgu on 11/09/2016.
 */
public class UserTextField extends TextField {
    private Stage stage;
    private Sprite sprite = new Sprite(Textures.labelTexture);

    public UserTextField(TextFieldStyle style, Stage stage) {
        super("", style);
        this.stage = stage;

        float width = 520;
        this.setWidth(width);
        this.setPosition(stage.getViewport().getWorldWidth()/2 - (width/2), stage.getViewport().getWorldHeight()/2 + 400f);
        this.setMessageText("Username");

        stage.addActor(this);
        sprite.setBounds(this.getX()-20f,this.getY(),this.getWidth()+40f,this.getHeight());
        this.selectAll();

    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        sprite.draw(batch, parentAlpha);
        super.draw(batch, parentAlpha);
    }
}
