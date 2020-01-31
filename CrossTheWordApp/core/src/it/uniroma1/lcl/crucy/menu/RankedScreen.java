package it.uniroma1.lcl.crucy.menu;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.io.File;
import java.util.Map;

import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.utils.CrosswordSize;
import it.uniroma1.lcl.crucy.cache.utils.Difficult;
import it.uniroma1.lcl.crucy.cache.utils.Language;
import it.uniroma1.lcl.crucy.cache.utils.Parameters;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.gameplay.loading.LoadingScreen;
import it.uniroma1.lcl.crucy.utils.GameType;
import it.uniroma1.lcl.crucy.utils.LevelStructure;
import it.uniroma1.lcl.crucy.utils.LimitedDomains;
import it.uniroma1.lcl.crucy.utils.Loader;
import it.uniroma1.lcl.crucy.utils.Parole;

public class RankedScreen implements Screen {

    //Integrare con Classe Level
    private MainCrucy game;
    private Stage stage;

    private Label imgInstruction;

    private UserTag uTag;
    private Gear gear;

    private float animationDuration= 0.1f;
    private Map<String,String> cruciverba;
    private int previousLvl;
    private ExperienceBar xpModule;
    private Label levelImg;

    private TextButton i;
    private BitmapFont font;
    private Texture balloon;
    private boolean visBal;


    public RankedScreen(MainCrucy game) {
        this.game = game;
        stage = new Stage(game.getViewport(),game.getBatch());
        Gdx.input.setInputProcessor(stage);

        createMenu();
    }

    public void createMenu()
    {

        balloon=Loader.getInstance().get("bg4.png",Texture.class); ///
        font = Loader.getInstance().get("Font/font.fnt",BitmapFont.class); ///

        gear=new Gear(stage);
        previousLvl = gear.getLevel();

        uTag = new UserTag(stage);

        Label.LabelStyle label1Style = new Label.LabelStyle();
        label1Style.font = font;

        imgInstruction = new Label(game.getString("Level"),label1Style);
        imgInstruction.setPosition(stage.getWidth()/2-imgInstruction.getWidth()/2, gear.getY()+gear.getWidth()-imgInstruction.getHeight()+170);
        imgInstruction.setAlignment(Align.center);
        imgInstruction.setTouchable(Touchable.disabled);

        TextButton.TextButtonStyle buttonStyle1 = new TextButton.TextButtonStyle();
        buttonStyle1.up=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/new_crossword.png", Texture.class)));
        buttonStyle1.over=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/new_crossword.png", Texture.class)));
        font.setColor(Color.WHITE);
        buttonStyle1.font = font;
        TextButton buttonNew = new TextButton(Parole.getInstance().getString("Next"), buttonStyle1);
        buttonNew.setPosition(stage.getWidth()/9,stage.getHeight()/9);

        buttonNew.addListener(new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.playSound(SoundFx.SELECT);
                FileManager.getInstance().setCrosswordLevel(gear.getLevel()+"");

                stage.addAction(Actions.fadeOut(0.3f));
                dispose();
                game.setScreen(new PickScreen(game));
            }
        });

        xpModule = new ExperienceBar(stage, FileManager.getInstance().getLevel(), uTag.getBackground().getY(),gear);
        xpModule.addActors();

        xpModule.addListeners( new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button){
                game.playSound(SoundFx.SELECT);
                if(!visBal)  xpModule.touched();
                else xpModule.touched();
            }
            @Override
            public boolean longPress(Actor a, float x, float y) {
                return true;
            }
        });

        gear.setBar(xpModule);

        Label.LabelStyle titleStyle=new Label.LabelStyle();
        titleStyle.font=font;
        levelImg= new Label(FileManager.getInstance().getLevel()+"",titleStyle);
        levelImg.setPosition(stage.getWidth()-levelImg.getWidth()-20, uTag.getLabel().getY());

        stage.addActor(imgInstruction);
        stage.addActor(levelImg);
        stage.addActor(buttonNew);
    }


    public void animationOutBtnBack()
    {
        //btnBack.setTouchable(Touchable.disabled);
        //btnBack.addAction(Actions.moveTo(btnBack.getX(), stage.getHeight(), animationDuration, Interpolation.swing));
        for(Actor a: stage.getActors())
        {
            if(!a.equals(uTag.getBackground()) && !a.equals(uTag.getLabel()))
                a.addAction(Actions.moveTo(stage.getWidth(), a.getY(), animationDuration));
        }
    }

    public void render(float f)
    {
        // Se premo il tasto indietro del telefono
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            game.playSound(SoundFx.SELECT);
            this.dispose();
            game.setScreen(new GameModeScreen(game,true));
        }

        if (!(gear.getLevel()== previousLvl && gear.levelAllowed())) {
            previousLvl=gear.getLevel();
            resetAnimationButtons();
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
        xpModule.render(gear.getLevel());

        Tmp.getInstance().setLVL(gear.getLevel());
    }

    public void dispose(){}

    @Override
    public void show()
    {
        //gear.addAction(Actions.sequence(Actions.moveTo(stage.getWidth(), gear.getY()), Actions.moveTo(gear.getX(), gear.getY(), 0.1f)));
       // imgInstruction.addAction(Actions.sequence(Actions.moveTo(stage.getWidth(), imgInstruction.getY()), Actions.moveTo(imgInstruction.getX(), imgInstruction.getY(), 0.1f)));
       // easy.addAction(Actions.sequence(Actions.moveTo(stage.getWidth(), easy.getY()), Actions.moveTo(easy.getX(), easy.getY(), 0.1f)));
        //medium.addAction(Actions.sequence(Actions.moveTo(stage.getWidth(), medium.getY()), Actions.moveTo(medium.getX(), medium.getY(), 0.1f)));
       // hard.addAction(Actions.sequence(Actions.moveTo(stage.getWidth(), hard.getY()), Actions.moveTo(hard.getX(), hard.getY(), 0.1f)));
        for(Actor a: stage.getActors())
        {
            if(!a.equals(uTag.getBackground()) && !a.equals(uTag.getLabel()))
                a.addAction(Actions.sequence(Actions.moveTo(stage.getWidth(), a.getY()), Actions.moveTo(a.getX(), a.getY(), animationDuration)));
        }
    }

    public void hide(){}
    public void pause(){}
    public void resume(){}

    public void resize(int a, int b)
    {
        stage.getViewport().update(a, b);
    }

    public void resetAnimationButtons()
    {
        xpModule.update(gear.getLevel());
    }

    public void pref(String s, int n)
    {
        Preferences p = Gdx.app.getPreferences("STARTED_C");
        p.putString(s,n+":"+1);
        p.flush();
    }


}
