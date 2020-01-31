package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import it.uniroma1.lcl.crucy.utils.Loader;

import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.font4;

public class SenseButton extends TextButton {

    private Sense sense;

    public SenseButton()
    {
        super("",createStyle());
        this.sense = null;
    }

    public SenseButton(Sense sense)
    {
        super(sense.getLemma(), createStyle());
        this.sense = sense;
    }

    @Override
    public void setText(String text) { super.setText(text); }

    public void setSense(Sense sense) { this.sense = sense;}

    public Sense getSense() {return sense;}

    private static TextButtonStyle createStyle() {
        TextButton.TextButtonStyle sense_tbs = new TextButton.TextButtonStyle();
        sense_tbs.font = font4;
        sense_tbs.fontColor = sense_tbs.font.getColor();
        sense_tbs.checked = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Popup/button_down.png", Texture.class)));
        sense_tbs.up = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Popup/button_up.png", Texture.class)));
        sense_tbs.down = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Popup/button_down.png", Texture.class)));
        return sense_tbs;
    }
}
