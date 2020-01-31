package it.uniroma1.lcl.crucy.menu.login;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.menu.Menu;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.menu.StartedScreen;
import it.uniroma1.lcl.crucy.utils.Loader;

/**
 * Created by bejgu on 11/09/2016.
 */

public class SupporterClass
{
    private static MainCrucy main;
    //private static SupporterClass instance;
    /**
     * Enum usato in fase di creazione del dialogo che dice cosa farà il pulsante ok del dialogo
     *
     * TOMAIN : il pulsante ok riporterà al main
     *
     * STAYHERE : il pulsante farà rimanere l'utente sulla stessa schermata
     */
    public enum DialogDestination { TOMAIN, STAYHERE, TOSTARTED, DIALOCHOICES, DIALOCHOICESMAIN}
/*
    public static SupporterClass getInstance()
    {
        if (instance == null) { instance = new SupporterClass(main); }
        return instance;
    }*/
    public SupporterClass(MainCrucy main) {
        SupporterClass.main = main;
    }


    /**
     * Metodo che serve a creare il dialogo con l'utente e centra bene le stringhe di qualsiasi dimensione
     * e può essere usato per impostare il pulsante l'"ok" a tornare al main o a rimanere sullo stesso screen
     * prende in input:
     * @param s : Stringa con la quale si deve creare il dialogo
     * @param stage : stage dal quale si sta creando il dialogo
     * @param main : il main del gioco
     * @param dialogDestination : destinazione premendo il tasto "OK" vedi enum
     * @param old : se old non è null, il dialogo puo chiamare metodi su old
     * @param screen : screen sul quale sara visualizzato il dialogo
     */

    public static void createDialog(String s, Stage stage, final MainCrucy main, DialogDestination dialogDestination, final ImageButton old, final Screen screen) {
        ArrayList<String> set  = splitString(s);
        ArrayList<Label> setLabel = new ArrayList<Label>();

        Textures.loadTextures();
        //Prese in input le varie righe crea un label per ogni riga
        for(String strin : set) {
            Label label = new Label(strin,new Label.LabelStyle(Textures.font, Textures.font.getColor()));
            label.setFontScale(0.6f);
            setLabel.add(label);
        }

        TextButton bottoneSi= null;
        TextButton bottoneNo= null;
        TextButton.TextButtonStyle dialogBS = getTextButtonStyleDialog();

        if(!dialogDestination.equals(DialogDestination.DIALOCHOICES) && !dialogDestination.equals(DialogDestination.DIALOCHOICESMAIN)) {
            bottoneSi = new TextButton("OK", dialogBS);
            bottoneSi.getLabel().setFontScale(0.6f);
        }else
        {
            bottoneSi= new TextButton("OK",dialogBS);
            bottoneNo= new TextButton("NO",dialogBS);
            bottoneSi.getLabel().setFontScale(0.6f);
            bottoneNo.getLabel().setFontScale(0.6f);

        }

        Gdx.input.setOnscreenKeyboardVisible(false);
        Window.WindowStyle ws = new Window.WindowStyle();
        ws.titleFont = Textures.font;

        Dialog dialog = new Dialog("",ws);
        dialog.setModal(true);
        dialog.setBackground(new TextureRegionDrawable(new TextureRegion(Textures.labelTexture)));
        if(dialogDestination.equals(DialogDestination.TOMAIN))
            bottoneSi.addListener(mainMenu(main));
        else if(dialogDestination.equals(DialogDestination.TOSTARTED))
            bottoneSi.addListener(startedCrosswords(main));
        else if(dialogDestination.equals(DialogDestination.STAYHERE))
            bottoneSi.addListener(stayHere(dialog));
        else if(dialogDestination.equals(DialogDestination.DIALOCHOICESMAIN)){
            bottoneSi.addListener(mainMenu(main));
            bottoneNo.addListener(mainMenu(main));
        } else {

            if(old!=null)
            bottoneSi.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            FileManager.getInstance().deleteStartedCrossword(old.getName());
                            screen.dispose();
                            main.setScreen(new Menu(main));
                        }
                    }
            );
            else
            bottoneSi.addListener(stayHere(dialog));
            bottoneNo.addListener(stayHere(dialog));
        }

        float maxWidth = 0;
        float maxHeight = 0;
        int colspan = 1;
        for(Label label : setLabel) {
            dialog.getContentTable().row().colspan(colspan).center();
            dialog.getContentTable().add(label);
            maxHeight += label.getPrefHeight();
            if(maxWidth < label.getPrefWidth())
                maxWidth = label.getPrefWidth();
            colspan++;
        }

        dialog.getContentTable().row().colspan(colspan).center();
        dialog.button(bottoneSi);
        if(dialogDestination.equals(DialogDestination.DIALOCHOICES) || dialogDestination.equals(DialogDestination.DIALOCHOICESMAIN) )
            dialog.button(bottoneNo);
        dialog.padBottom(25f);
        dialog.pack();

        if(maxWidth+50f< stage.getViewport().getWorldWidth()-10f)
            dialog.setSize(maxWidth+50f,maxHeight+230);
        else
            dialog.setSize(stage.getViewport().getWorldWidth()-10f,maxHeight+230);

        dialog.setPosition(stage.getCamera().viewportWidth/2-dialog.getWidth()/2,stage.getCamera().viewportHeight/3*2);
        dialog.addAction(moveTo(stage.getCamera().viewportWidth/2-dialog.getWidth()/2,stage.getCamera().viewportHeight/2-dialog.getHeight()/2,.5f, Interpolation.swingOut));

        if (bottoneSi!=null) {
            bottoneSi.setTransform(true);
            bottoneSi.setOrigin(bottoneSi.getOriginX()+bottoneSi.getWidth()/2,bottoneSi.getOriginY()+bottoneSi.getHeight()/2);
            bottoneSi.addAction(sequence(scaleTo(0,0),delay(.2f,scaleTo(1,1,.3f,Interpolation.swingOut))));}

        if (bottoneNo!=null) {
            bottoneNo.setTransform(true);
            bottoneNo.setOrigin(bottoneNo.getOriginX()+bottoneNo.getWidth()/2,bottoneNo.getOriginY()+bottoneNo.getHeight()/2);
            bottoneNo.addAction(sequence(scaleTo(0,0),delay(.2f,scaleTo(1,1,.3f,Interpolation.swingOut))));}

        stage.addActor(dialog);
    }


    public static void createDialog(String s, Stage stage, MainCrucy main, DialogDestination dialogDestination) {
        createDialog(s, stage, main, dialogDestination, null, null);
    }


    public static TextButton.TextButtonStyle getTextButtonStyleDialog() {
        TextButton.TextButtonStyle dialogBS = new TextButton.TextButtonStyle();
        dialogBS.up = new TextureRegionDrawable(new TextureRegion(Textures.up));
        dialogBS.over = new TextureRegionDrawable(new TextureRegion(Textures.over));
        dialogBS.down = new TextureRegionDrawable(new TextureRegion(Textures.down));
        dialogBS.font = Textures.font;
        return dialogBS;
    }

    private static ClickListener startedCrosswords(MainCrucy main) {
        SupporterClass.main = main;
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                SupporterClass.main.playSound(SoundFx.SELECT);
                SupporterClass.main.setScreen(new StartedScreen(SupporterClass.main));
            }
        };
    }


    private static ClickListener mainMenu(MainCrucy main) {
        SupporterClass.main = main;
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                SupporterClass.main.playSound(SoundFx.SELECT);
                SupporterClass.main.setScreen(new Menu(SupporterClass.main));
            }
        };
    }


    private static ClickListener stayHere(final Dialog dialog) {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                dialog.hide();
            }
        };
    }


    public static TextField.TextFieldStyle getTextStyle(){
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = Loader.getInstance().get("Font/regularFont/regular.fnt", BitmapFont.class);
        style.font.getData().setScale(0.7f);
        style.fontColor = Color.LIGHT_GRAY;
        style.messageFont = Loader.getInstance().get("Font/silverFont/regular.fnt", BitmapFont.class);
        style.messageFont.getData().setScale(0.7f);

        Sprite spriteCursor = new Sprite(Textures.textureCursor);
        Sprite spriteSelection = new Sprite(Textures.textureSelection);
        style.selection = new TextureRegionDrawable(new TextureRegion(spriteSelection));
        style.cursor = new SpriteDrawable(spriteCursor);
        return style;
    }

    /**
     * Metodo che preso in input
     * @param s : stringa da dividere per creare il dialogo
     * @return un ArrayList<String> che contiene la lista delle stringhe con le dimensioni massime contenute per riga .. per ottimizzare
     * le dimensioni del dialogo
     */
    public static ArrayList<String> splitString(String s) {
        int count = 1;
        boolean split = false;
        ArrayList<String> set = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        if(s.length() > 17)
            for(int k = 0; k< s.length();k++) {
                if(k==17*count && !split)
                {
                    split = true;
                    count++;
                }
                if(split && (s.charAt(k)+"").equals(" "))
                {
                    set.add(sb.toString());
                    sb = new StringBuffer();
                    split=false;
                    continue;
                }
                sb.append(s.charAt(k)+"");
            }
        else {
            int index = s.indexOf(":");
            if (index<0) set.add(s);
            else {
                set.add(s.substring(0,index+1));
                set.add(s.substring(index+1));
            }
            return set;
        }

        if(sb.length()>23) {
            sb.delete(23,sb.length());
            sb.append("...");
        }

        if(sb.toString().length()!=0)
            set.add(sb.toString());

        return set;
    }
 
}
