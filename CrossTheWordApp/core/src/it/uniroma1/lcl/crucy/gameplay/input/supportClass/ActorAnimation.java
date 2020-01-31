package it.uniroma1.lcl.crucy.gameplay.input.supportClass;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import it.uniroma1.lcl.crucy.cache.utils.Difficult;
import it.uniroma1.lcl.crucy.utils.Loader;

/**
 * Created by bejgu on 28/08/2016.
 */
public class ActorAnimation extends Image {

    private Sprite sprite = new Sprite(Loader.getInstance().get("WordTexture/word_highlight_green.png", Texture.class));

    public ActorAnimation(int x, int y, it.uniroma1.lcl.crucy.gameplay.boxes.Word word)
    {
        super(new SpriteDrawable(new Sprite(chooseTexture(word))));
        this.setBounds(x,y,sprite.getWidth(),sprite.getHeight());
    }

    public static Texture chooseTexture(it.uniroma1.lcl.crucy.gameplay.boxes.Word word)
    {
        if (word.getDefinizioneClass().getDifficulty() .equals(Difficult.EASY))
            return Loader.getInstance().get("WordTexture/word_highlight_green.png",Texture.class);
        else if (word.getDefinizioneClass().getDifficulty().equals(Difficult.MEDIUM))
            return Loader.getInstance().get("WordTexture/word_highlight_yellow.png",Texture.class);
        else
            return Loader.getInstance().get("WordTexture/word_highlight_red.png",Texture.class);
    }

}
