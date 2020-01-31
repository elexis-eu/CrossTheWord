package it.uniroma1.lcl.crucy.menu.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import it.uniroma1.lcl.crucy.utils.Loader;

/**
 * Created by bejgu on 11/09/2016.
 */
public class Textures {

    public static Texture buttonUpTex ;
    public static Texture buttonOverTex ;
    public static Texture buttonDownTex ;
    public static Texture  textureCursor ;
    public static Texture  textureSelection ;
    public static Texture   buttonBack;
    public static Texture buttonBackDown;
    public static Texture  labelTexture ;
    public static Texture up ;
    public static Texture  over ;
    public static Texture  down ;
    public static BitmapFont font;
    public static Texture textureBG;
    public static TextureRegion background;

    public static void loadTextures()
    {
        buttonUpTex = Loader.getInstance().get("Buttons/menu_button_pop.png", Texture.class);
        buttonUpTex.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        buttonOverTex = Loader.getInstance().get("Buttons/menu_button_pop_hover.png", Texture.class);
        buttonDownTex = Loader.getInstance().get("Buttons/menu_button_pop_active.png", Texture.class);
        textureCursor = Loader.getInstance().get("textfield/cursorT.png", Texture.class);
        buttonBack= Loader.getInstance().get("keyboard_backDown.png", Texture.class);
        textureSelection = Loader.getInstance().get("textfield/selection.png", Texture.class);
        textureBG = Loader.getInstance().get("BGTextures/loading.png", Texture.class);

        background = new TextureRegion(textureBG, 0, 0, textureBG.getWidth(), textureBG.getHeight());
        buttonBackDown =Loader.getInstance().get("keyboard_back.png", Texture.class);
        labelTexture = Loader.getInstance().get("definition_label.jpg", Texture.class);
        up = Loader.getInstance().get("Buttons/dialog_button_pop.png", Texture.class);
        over = Loader.getInstance().get("Buttons/dialog_button_pop_hover.png", Texture.class);
        down = Loader.getInstance().get("Buttons/dialog_button_pop_active.png", Texture.class);
        font =  Loader.getInstance().get("Font/regularFont/regular.fnt", BitmapFont.class);

    }
}
