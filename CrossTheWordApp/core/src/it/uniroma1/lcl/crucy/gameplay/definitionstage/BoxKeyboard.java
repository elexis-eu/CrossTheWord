package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import it.uniroma1.lcl.crucy.utils.Loader;
import it.uniroma1.lcl.crucy.utils.TextureConverter;


/**
 * Created by bejgu on 22/08/2016.
 * casella della tastiera del gioco
 */
public class BoxKeyboard extends Image {
    public static final float DISTANCE_BOX = 1.09f; //Costante usata per distanziare le caselle tra loro
    public static  float WIDTH_BOX;
    public static  float HEIGTH_BOX;
    private char character;

    public BoxKeyboard(float x , float y, char point) {
        super(getSprite(point));
        this.character = point;
        setSize(point == ' ' ? WIDTH_BOX * 2 : WIDTH_BOX, HEIGTH_BOX);
        setPosition(x, y);
        setTouchable(Touchable.disabled);
        setVisible(false);
    }

    private static Sprite getSprite(Character point) {
        Sprite sprite;
        if (point != ' ') {
            Pixmap tile = TextureConverter.toPixmap(Loader.getInstance().get("keyboard_button.png", Texture.class));
            BitmapFont.BitmapFontData data = it.uniroma1.lcl.crucy.gameplay.textures.Textures.fontKeyboard.getData();
            Pixmap fontPixmap = new Pixmap(Gdx.files.internal(data.imagePaths[0]));
            BitmapFont.Glyph glyph = data.getGlyph(point);
            tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2, glyph.srcX, glyph.srcY, glyph.width, glyph.height);

            sprite = new Sprite(new Texture(tile));
            tile.dispose();
            fontPixmap.dispose();
        }
        else {
            sprite = new Sprite(it.uniroma1.lcl.crucy.gameplay.textures.Textures.cancel);
        }
        return sprite;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x * DISTANCE_BOX, y * DISTANCE_BOX);
    }

    public char getCharacter() { return character; }

}
