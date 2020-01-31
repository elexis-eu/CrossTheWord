package it.uniroma1.lcl.crucy.menu;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.BackButton;
import it.uniroma1.lcl.crucy.utils.Loader;
import it.uniroma1.lcl.crucy.utils.Parole;

public class UserTag
{
    private Image imgBluer; // Banda nera trasparente sopra ai menu
    private Label hintLabel; // Etichetta con il nome dell'utenten
    private Stage stage;

    public UserTag(Stage stage, String toWrite) {
        this.stage = stage;
        create(toWrite);
    }

    public UserTag(Stage stage) {
        this(stage, FileManager.getInstance().isLoggedIn()? FileManager.getInstance().getUsername(): Parole.getInstance().getString("Ospite"));
    }

    private void create(String toWrite)
    {
        imgBluer= new Image(new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("MainMenu/bluer.png", Texture.class))));
        imgBluer.setPosition(0, stage.getHeight() - imgBluer.getHeight());

        BitmapFont font= Loader.getInstance().get("Font/whiteFont/regular.fnt",BitmapFont.class);

        Label.LabelStyle titleStyle=new Label.LabelStyle();
        titleStyle.font=font;
        hintLabel= new Label(toWrite,titleStyle);
        hintLabel.setFontScale(0.8f);
        hintLabel.setAlignment(Align.center);
        hintLabel.setPosition(stage.getWidth()/2 - hintLabel.getWidth()/2, stage.getHeight() - 1.2f * hintLabel.getHeight());

        imgBluer.setName("uTagBluer");
        hintLabel.setName("uTagLabel");

        stage.addActor(imgBluer);
        stage.addActor(hintLabel);

    }

    public Label getLabel() { return hintLabel; }

    public void  updateLabel(){
        hintLabel.setText(FileManager.getInstance().isLoggedIn()?
                FileManager.getInstance().getUsername(): Parole.getInstance().getString("Ospite"));
    }

    public Image getBackground() { return imgBluer; }

    public String getLabelName() { return hintLabel.getName(); }

    public String getBackgroundName() { return imgBluer.getName(); }

    public boolean isEqual(Actor a)
    {
        return imgBluer.equals(a) || hintLabel.equals(a);
    }




}
