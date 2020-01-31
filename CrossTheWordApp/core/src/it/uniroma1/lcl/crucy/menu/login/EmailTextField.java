package it.uniroma1.lcl.crucy.menu.login;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created by bejgu on 26/09/2016.
 */

public class EmailTextField extends TextField
{
    private Sprite sprite = new Sprite(Textures.labelTexture);

    public EmailTextField(TextField.TextFieldStyle style, Stage stage) {
        super("", style);

        float width = 520;

        this.setWidth(width);
        this.setPosition(stage.getViewport().getWorldWidth()/2 - (width/2f), stage.getViewport().getWorldHeight()/2 + 230f);

        this.setMessageText("Email");
        stage.addActor(this);
        sprite.setBounds(this.getX()-20f,this.getY(),this.getWidth()+40f,this.getHeight());
        this.selectAll();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch, parentAlpha);
        super.draw(batch, parentAlpha);
    }
}