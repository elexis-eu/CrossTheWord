package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import it.uniroma1.lcl.crucy.utils.Loader;


public class InputButton extends ImageButton
{
    protected static final float POSITION = 160;
    protected static final float SIZE = 110;
    protected Stage stage;
    protected float oldZoom;

    /**
     * Costruttore
     * @param event ClickListener che determina l'azione compiuta cliccando sul bottone
     * @param stage stage su cui deve essere inserito il bottone
     * @param imageUp texutre che viene visualizzata sopra al bottone
     */
    public InputButton(ClickListener event, Stage stage, Texture imageUp)
    {
        super(create(imageUp));
        this.stage = stage;
        this.addListener(event);
        setButton();
        setVisible(true);
    }

    /**
     * Costruttore che imposta un clickListener inizialmente vuoto
     * @param stage stage su cui deve essere aggiunto il bottone
     * @param imageUp texture che viene visualizzata sopra al bottone
     */
    public InputButton(Stage stage,Texture imageUp)
    {
        this(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {};
        },stage,imageUp);
    }


    /**
     * Metodo che setta la posizione e la dimenzione del bottone e che lo aggiunge allo stage
     */
    public void setButton()
    {
        this.setPosition(stage.getCamera().viewportWidth-POSITION,stage.getCamera().viewportHeight-POSITION);
        this.setSize(SIZE,SIZE);
        stage.addActor(this);
    }

    /**
     * Metodo che crea l'ImageButtonStyle che viene usato nei costruttori
     * @param imageUp texture che viene visualizzata sopra al bottone
     * @return ritorna l'ImageButtonStyle che viene usato nei costruttori
     */
    public static ImageButtonStyle create(Texture imageUp)
    {
        ImageButton.ImageButtonStyle ib = new ImageButton.ImageButtonStyle();
        ib.up = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("keyboard_200.png",Texture.class)));
        ib.down = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("keyboard_200_down.png",Texture.class)));
        ib.over = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("keyboard_200_down.png",Texture.class)));
        ib.imageUp=new TextureRegionDrawable(new TextureRegion(imageUp));
        return ib;
    }

    public void setZoom(float z)
    {
        oldZoom=z;
    }

    public float getZoom(){return oldZoom; }

}
