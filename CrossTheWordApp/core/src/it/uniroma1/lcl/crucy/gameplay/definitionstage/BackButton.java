package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import it.uniroma1.lcl.crucy.utils.Loader;

/**
 * Created by antho on 25/08/2016.
 */
public class BackButton extends MenuButton
{
    /**
     * Distanza dall'altezza dello stage
     * */
    private static final float PIXEL_DISTANCE = 160;

    /**
     * Costruttore
     * @param event Clicklistener legato al bottone
     * @param stage stage su cui deve essere inserito il bottone
     */
    public BackButton(ClickListener event, Stage stage)
    {
        super(event,stage, Loader.getInstance().get("arrow.png",Texture.class));
        setButton();
        setVisible(true);
        this.setName("btnBack");
    }

    @Override
    /**
     * Override del metodo setButton di MenuButton.
     * Determina la posizione del bottone, la dimensione e lo aggiunge allo stage
     */
    public void setButton()
    {
        this.setPosition(50,stage.getHeight() - PIXEL_DISTANCE);
        this.setSize(SIZE,SIZE); //quando cambierà il menù lo toglierò
        stage.addActor(this);
    }

    /**
     * Setta un ClickListerer al bottone
     * @param event ClickListerer con l'operazione da compiere
     */
    public void setEvent(ClickListener event)
    {
        this.addCaptureListener(event);
    }

}
