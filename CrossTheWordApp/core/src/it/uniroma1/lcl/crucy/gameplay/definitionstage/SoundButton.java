package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.buttonTexture;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.buttonTextureDown;


/**
 * Created by Marzia Riso on 01/09/2016.
 * Classe che gestisce i bottone dei suoni e degli effetti speciali
 */
public class SoundButton extends CheckBox
{
    /**
     * Dimesione del bottone
     */
    private static final float SIZE = 110;

    /**
     * Posizione rispetto all'altezza dello stage
     */
    private static final float POSITION = 160;

    /**
     * Stage su cui il bottone deve essere aggiunto
     */
    private static Stage stage;


    /**
     * Costruttore
     * @param event ClickListener da aggiungere al bottone
     * @param stage Stage su cui deve essere aggiunto il bottone
     * @param font font utilizzato per creare il CheckBoxStyle
     * @param on Texture che viene visualizzata quando il checkbox è attivato
     * @param off Texture che viene visualizzata quando il checkbox è disattivato
     * @param state boolean che indica lo stato del suono/effetti sonori (utilizzato per determinare la texture On e Off)
     */
    public SoundButton(ClickListener event, Stage stage, BitmapFont font, Texture on, Texture off, boolean state)
    {
        super("",create(font, on, off, state));
        this.stage = stage;
        this.addListener(event);
        setButton();
        setVisible(true);
    }

    /**
     * Metodo che setta la posizione e le dimensioni del button.
     * Aggiunte il button allo stage
     */
    private void setButton()
    {
        this.setPosition(stage.getCamera().viewportWidth-POSITION,stage.getCamera().viewportHeight-POSITION);
        this.setSize(SIZE,SIZE);
        stage.addActor(this);
    }

    /**
     * Metodo che crea il CheckBoxStyle del SoundButton
     * @param font font che viene utilizzato nella creazione del CheckBox
     * @param on Texture che viene visualizzata quando il checkbox è attivato
     * @param off Texture che viene visualizzata quando il checkbox è disattivato
     * @param state boolean che indica lo stato del suono/effetti sonori (utilizzato per determinare la texture On e Off)
     * @return Ritorna il CheckBoxStyle usato nel costruttore del SoundButton
     */
    private static CheckBoxStyle create(BitmapFont font, Texture on, Texture off, boolean state)
    {
        CheckBox.CheckBoxStyle cbs = new CheckBox.CheckBoxStyle();
        cbs.up= new TextureRegionDrawable(new TextureRegion(buttonTexture));
        if (state)
        {
            cbs.checkboxOn = new TextureRegionDrawable(new TextureRegion(off));
            cbs.checkboxOff = new TextureRegionDrawable(new TextureRegion(on));

        }
        else
        {
            cbs.checkboxOn = new TextureRegionDrawable(new TextureRegion(on));
            cbs.checkboxOff = new TextureRegionDrawable(new TextureRegion(off));
        }
        cbs.down = new TextureRegionDrawable(new TextureRegion(buttonTextureDown));
        cbs.over = new TextureRegionDrawable(new TextureRegion(buttonTextureDown));
        cbs.font=font;
        return cbs;
    }
}
