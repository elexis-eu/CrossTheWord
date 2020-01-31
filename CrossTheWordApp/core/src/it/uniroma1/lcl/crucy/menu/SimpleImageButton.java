package it.uniroma1.lcl.crucy.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by Daniel on 13/05/2017.
 */

public class SimpleImageButton extends ImageButton {



    public SimpleImageButton(ClickListener clickListener,  Texture img)
    {
        super(new TextureRegionDrawable(new TextureRegion(img)));
        this.addListener(clickListener);
    }

}
