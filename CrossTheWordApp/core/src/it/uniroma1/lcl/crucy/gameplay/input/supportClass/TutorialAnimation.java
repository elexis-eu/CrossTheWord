package it.uniroma1.lcl.crucy.gameplay.input.supportClass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import it.uniroma1.lcl.crucy.utils.Loader;

/**
 * Created by alessandro on 10/09/2016.
 */
public class TutorialAnimation extends Image{

    private boolean active;
    private Texture texture = Loader.getInstance().get("tap2.png",Texture.class);

    public TutorialAnimation() { super(Loader.getInstance().get("tap2.png",Texture.class)); }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public void draw(Batch batch)
    {
        batch.draw(texture, this.getX(), this.getY(), 71, 132);
    }
}
