package it.uniroma1.lcl.crucy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.utils.Language;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.GameSelectionMode;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.utils.Loader;
import it.uniroma1.lcl.crucy.utils.Parole;
import it.uniroma1.lcl.crucy.utils.TextureConverter;

/**
 * Classe che gestisce lo Screen delle impostazioni(musica,sfx,lingua e modalita)
 * Created by Camilla
 */
public class SettingsScreen implements Screen
{
    private MainCrucy game;
    private Stage stage;

    private CheckBox checkboxAudio;
    private CheckBox checkboxSfx;
    private CheckBox checkboxVibration;

    private BitmapFont font;
    private Texture whiteBlock;
    private Texture checkedBlock;

    //Tutte le righe commentate sotto sono per la scelta dell'input, per ora è selezionato di default il Tap
    //private TextButton tapButton;
    //private TextButton swipeButton;
    //private TextButton pressButton;

    private Table screenTable;

    private Table table1;
    //private Table table2;
    private Table table3;
    private UserTag uTag;

    private ArrayList<Actor> toAnimate; 
    private final float animationDuration = 0.5f;
    private  boolean duringAnimation = true;

    public SettingsScreen(MainCrucy main) {
        this.game = main;
        this.stage = new Stage(main.getViewport(),main.getBatch());
        Gdx.input.setInputProcessor(stage);
        creaMenu();
    }

    private void creaMenu() {
        toAnimate = new ArrayList<Actor>();
        font = Loader.getInstance().get("Font/regularFont/regular.fnt",BitmapFont.class);
        uTag = new UserTag(stage);

        //crea la checkbox per l'audio
        CheckBox.CheckBoxStyle sfxStyle = new CheckBox.CheckBoxStyle();
        sfxStyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(createSprite("Boxes/White_Block_Small.png","sfxOn.png")));
        sfxStyle.checkboxOff = new TextureRegionDrawable(new TextureRegion(createSprite("Boxes/White_Block_Small.png","sfxOff.png")));
        sfxStyle.font= font;
        checkboxSfx= new CheckBox("",sfxStyle);

        //setto il bottone per la musica con la giusta icona all'avvio
        if (game.isMusicOn()) checkboxSfx.setChecked(true);
        else checkboxSfx.setChecked(false);


        checkboxSfx.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(checkboxAudio.isChecked()) game.playSound(SoundFx.SELECT);
                game.setMusic(checkboxSfx.isChecked());
            }
        });


        //crea la checkbox per l'audio
        CheckBox.CheckBoxStyle cbs = new CheckBox.CheckBoxStyle();
        cbs.checkboxOn = new TextureRegionDrawable(new TextureRegion(new TextureRegion(createSprite("Boxes/White_Block_Small.png","audioOn.png"))));
        cbs.checkboxOff = new TextureRegionDrawable(new TextureRegion(new TextureRegion(createSprite("Boxes/White_Block_Small.png","audioOff.png"))));
        cbs.font= font;
        checkboxAudio = new CheckBox("",cbs);

        if (game.isSoundOn()) checkboxAudio.setChecked(true);
        else checkboxAudio.setChecked(false);

        checkboxAudio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setSound(checkboxAudio.isChecked());
                if(checkboxAudio.isChecked()) game.playSound(SoundFx.SELECT);
            }
        });

        //crea la checkbox per l'audio
        CheckBox.CheckBoxStyle cbv = new CheckBox.CheckBoxStyle();
        cbv.checkboxOn = new TextureRegionDrawable(new TextureRegion(new TextureRegion(createSprite("Boxes/White_Block_Small.png","vibrationOn.png"))));
        cbv.checkboxOff = new TextureRegionDrawable(new TextureRegion(new TextureRegion(createSprite("Boxes/White_Block_Small.png","vibrationOff.png"))));
        cbv.font= font;
        checkboxVibration = new CheckBox("",cbv);

        if (game.isVibrationOn()) checkboxVibration.setChecked(true);
        else checkboxVibration.setChecked(false);

        checkboxVibration.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
              game.switchVibration();
            }
        });


        //creo la checkbox per la lingua dei menu
        whiteBlock = Loader.getInstance().get("Boxes/White_Block.png",Texture.class);
        checkedBlock= Loader.getInstance().get("Boxes/Checked_Block_yellow.png",Texture.class);

        TextButton.TextButtonStyle checkStyle= new TextButton.TextButtonStyle();
        checkStyle.up=new TextureRegionDrawable(new TextureRegion(whiteBlock));
        checkStyle.over=new TextureRegionDrawable(new TextureRegion(checkedBlock));
        checkStyle.checked=new TextureRegionDrawable(new TextureRegion(checkedBlock));
        checkStyle.font=font;

        Label.LabelStyle label1Style = new Label.LabelStyle();
        label1Style.font = font;

        Label audioLabel = new Label("Audio",label1Style);
        audioLabel.setFontScale(0.8f);
        audioLabel.setPosition(stage.getWidth()/2-audioLabel.getWidth()/2, uTag.getLabel().getY()-audioLabel.getHeight()-150);
        audioLabel.setAlignment(Align.center);
        toAnimate.add(audioLabel);

        table1 = new Table();
        table1.row().colspan(3).padRight(20f).center();
        table1.add(checkboxAudio);
        table1.add(checkboxSfx).padLeft(20f);
        table1.add(checkboxVibration).padLeft(40f);

        /*final Label inputLabel = new Label("Input",label1Style);
        inputLabel.setFontScale(0.8f);
        inputLabel.setPosition(stage.getWidth()/2-inputLabel.getWidth()/2, table1.getY()-inputLabel.getHeight()-100);
        inputLabel.setAlignment(Align.center);
        toAnimate.add(inputLabel);*/

        TextButton.TextButtonStyle buttonStyle1 = new TextButton.TextButtonStyle();
        buttonStyle1.up=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/new_crossword.png", Texture.class)));
        buttonStyle1.over=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/new_crossword_pressed.png", Texture.class)));
        font.setColor(Color.WHITE);
        buttonStyle1.font = font;
        final TextButton buttonNew = new TextButton(Parole.getInstance().getString("Credits"), buttonStyle1);
        buttonNew.getLabel().setFontScale(0.8f);
        buttonNew.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    stage.addAction(Actions.delay(0.1f, Actions.run(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            game.setScreen(new CreditsScreen(game));
                            dispose();
                        }


                    })));
            }
        });
        toAnimate.add(buttonNew);


        /*tapButton= new TextButton("Tap",checkStyle);
        tapButton.getLabel().setFontScale(0.6f);
        tapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setGameMode(GameSelectionMode.TAP_AND_SWIPE_MOD);
            }
        });

        pressButton = new TextButton("Press",checkStyle);
        pressButton.getLabel().setFontScale(0.6f);
        pressButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!duringAnimation) game.playSound(SoundFx.SELECT);
                game.setGameMode(GameSelectionMode.LONG_PRESS_SELECTED_MOD);
            }
        });

        swipeButton= new TextButton("Swipe",checkStyle);
        swipeButton.getLabel().setFontScale(0.6f);
        swipeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setGameMode(GameSelectionMode.LONG_PRESS_MOVE_MOD);
            }
        });


        if(game.getGameMode()!= null) {
            if(game.getGameMode().equals(GameSelectionMode.LONG_PRESS_MOVE_MOD))
                swipeButton.setChecked(true);
            else if(game.getGameMode().equals(GameSelectionMode.LONG_PRESS_SELECTED_MOD))
                pressButton.setChecked(true);
            else
                tapButton.setChecked(true);
        }*/

        checkboxAudio.setTransform(true);
        checkboxSfx.setTransform(true);

        /*tapButton.setTransform(true);
        pressButton.setTransform(true);
        swipeButton.setTransform(true);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(tapButton,pressButton,swipeButton);
        modeGroup.setMaxCheckCount(1);
        modeGroup.setMinCheckCount(1);

        table2 = new Table();
        table2.row().colspan(3).padRight(20f);
        table2.add(tapButton).width(250).height(250);
        table2.add(pressButton).width(250).height(250).padLeft(20f);
        table2.add(swipeButton).width(250).height(250).padLeft(40f);
        toAnimate.add(table2);*/

        final Label langLabel = new Label(game.getString("Lingua"), label1Style);
        langLabel.setFontScale(0.8f);
        langLabel.setAlignment(Align.center);

        TextButton langIta= new TextButton("IT",checkStyle);
        langIta.getLabel().setFontScale(0.7f);
        langIta.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound(SoundFx.SELECT);
                game.setLingua(Language.ITALIAN);
                uTag.updateLabel();
                langLabel.setText(game.getString("Lingua"));
            }
        });

        TextButton langEng= new TextButton("EN",checkStyle);
        langEng.getLabel().setFontScale(0.7f);
        langEng.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound(SoundFx.SELECT);
                game.setLingua(Language.ENGLISH);
                uTag.updateLabel();
                langLabel.setText(game.getString("Lingua"));
            }
        });

        ButtonGroup languageGroup = new ButtonGroup();
        languageGroup.add(langIta, langEng);
        languageGroup.setMinCheckCount(1);
        languageGroup.setMaxCheckCount(1);

        if(game.getLingua().equals(null)) languageGroup.uncheckAll();
        else if (game.getLingua().equals(Language.ITALIAN)) langIta.setChecked(true);
        else if (game.getLingua().equals(Language.ENGLISH)) langEng.setChecked(true);

        table3 = new Table();
        table3.row().colspan(2).padRight(20f);
        table3.add(langIta).height(250f).width(250f);
        table3.add(langEng).height(250f).width(250f).padLeft(20f);
        toAnimate.add(table3);

        screenTable = new Table();
        screenTable.row();
        screenTable.add(audioLabel).pad(5f);
        screenTable.row();
        screenTable.add(table1).pad(20f).padBottom(20f);
        screenTable.row();
        /*screenTable.add(inputLabel).pad(5f);
        screenTable.row();
        screenTable.add(table2).pad(20f).padBottom(20f);
        screenTable.row();*/
        screenTable.add(langLabel).pad(5f);
        screenTable.row();
        screenTable.add(table3).pad(20f).padBottom(20f);
        screenTable.row();
        screenTable.add(buttonNew).width(520f).pad(50f);
        screenTable.setOrigin(0);
        screenTable.setPosition(stage.getWidth()/2-screenTable.getWidth()/2 , stage.getHeight()/2-screenTable.getHeight()/2-90f);
        stage.addActor(screenTable);

    }


    private void animateOut() {
        for(Actor act:toAnimate) {
            act.addAction(Actions.moveTo(act.getX(), act.getY()+530, 0.15f));
        }

    }

    private void animateIn(){
        for(Actor act:toAnimate) {
            act.addAction(Actions.moveTo(act.getX(), act.getY()-530,0.15f));
        }

    }


    @Override
    public void show() {
        animationIn();
        duringAnimation = false;
    }

    @Override
    public void render(float delta) {

        // Se premo il tasto indietro del telefono
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            game.playSound(SoundFx.SELECT);
            this.dispose();
            game.setScreen(new Menu(game));
        }
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose()
    {
        stage.dispose();
    }

    private void animationIn()
    {
        animationInTable(table1);
        //animationInTable(table2);
        animationInTable(table3);

        for(Actor ac: stage.getActors())
        {
            if(ac.getName()!=null && ac.getName().equals(uTag.getBackgroundName())) continue;
            ac.addAction(getAnimationIn());
        }
     }

    private void animationOut()
    {
        animationOutTable(table1);
        //animationOutTable(table2);
        animationInTable(table3);

        for(Actor ac: stage.getActors())
        {
            if(ac.getName()!=null && ac.getName().equals(uTag.getBackgroundName())) continue;
            ac.addAction(getAnimationOut());
        }
        checkboxVibration.addAction(Actions.fadeOut(0.15f));
        uTag.getLabel().addAction(Actions.fadeOut(0.3f));
    }

    private void animationInTable(Table t) {
        for(Cell cell : t.getCells())
        {
            Actor actor = cell.getActor();
            actor.setOrigin(actor.getOriginX() + actor.getWidth()/2, actor.getOriginY() + actor.getHeight()/2);
            actor.getColor().a = 0;
            actor.addAction(getAnimationIn());
        }

    }

    private void animationOutTable(Table t) {
        for(Cell cell : t.getCells())
        {
            Actor actor = cell.getActor();
            actor.setTouchable(Touchable.disabled);
            actor.addAction(getAnimationOut());
        }
    }

    private ParallelAction getAnimationIn() {
        float duration = ((float)Math.random()+0.1f)*animationDuration;
        return Actions.parallel(
                Actions.fadeIn(duration),
                Actions.sequence(
                        Actions.scaleTo(0, 0),
                        Actions.scaleTo(1, 1, duration, Interpolation.bounceIn)
                ));
    }

    private Action getAnimationOut() {
        float duration = ((float)Math.random()+0.5f)*animationDuration;
        return Actions.scaleTo(0, 0, duration, Interpolation.bounceOut);
    }

    private Sprite createSprite(String block, String sprite){
        Pixmap tile2 = TextureConverter.toPixmap(Loader.getInstance().get(block, Texture.class));
        tile2.setFilter(Pixmap.Filter.BiLinear);
        Pixmap sfxOn = TextureConverter.toPixmap(Loader.getInstance().get(sprite, Texture.class));
        sfxOn.setFilter(Pixmap.Filter.BiLinear);
        tile2.drawPixmap(sfxOn, (tile2.getWidth() -sfxOn.getWidth()) / 2, (tile2.getHeight() - sfxOn.getHeight()) / 2);
        return new Sprite(new Texture(tile2));
    }

}
