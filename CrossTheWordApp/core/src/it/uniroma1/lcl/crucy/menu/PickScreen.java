package it.uniroma1.lcl.crucy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;

import java.util.List;
import java.util.Map;

import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.Callback;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.RequestManager;
import it.uniroma1.lcl.crucy.cache.utils.Difficult;
import it.uniroma1.lcl.crucy.cache.utils.Language;
import it.uniroma1.lcl.crucy.cache.utils.Parameters;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.gameplay.loading.LoadingScreen;
import it.uniroma1.lcl.crucy.menu.login.SupporterClass;
import it.uniroma1.lcl.crucy.utils.GameType;
import it.uniroma1.lcl.crucy.utils.LimitedDomains;
import it.uniroma1.lcl.crucy.utils.Loader;
import it.uniroma1.lcl.crucy.utils.Parole;

import static it.uniroma1.lcl.crucy.cache.RequestManager.MIN_CROSSWORDS;

public class PickScreen implements Screen {

    private MainCrucy game;
    private Stage stage;

    private Difficult difficulty;
    private LimitedDomains domain;
    private Language language;
    private GameType gameType;
    private String level;

    private Map<String,String> cruciverba;


    public PickScreen(MainCrucy game) {
        this.game = game;
        this.stage = new Stage(game.getViewport(), game.getBatch());
        this.difficulty = Difficult.EASY;
        this.language = Language.ENGLISH;
        this.domain = LimitedDomains.MIXED;
        this.gameType = FileManager.getInstance().getGameType();
        this.level = FileManager.getInstance().getCrosswordLevel();
        Gdx.input.setInputProcessor(stage);
        createMenu();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Se premo il tasto indietro del telefono
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            game.playSound(SoundFx.SELECT);
            this.dispose();
            game.setScreen(new StartedScreen(game));
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
    public void dispose() {

    }

    public void createMenu() {
        UserTag uTag = new UserTag(stage);

        BitmapFont font = Loader.getInstance().get("Font/regularFont/regular.fnt", BitmapFont.class);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;

        Table table = new Table();

        if (gameType.equals(GameType.FREEPLAY)) {
            Label diff = new Label(game.getString("Difficolta"), style);
            diff.setAlignment(Align.center);
            diff.setFontScale(0.8f);


            ImageButton.ImageButtonStyle easyStyle = new ImageButton.ImageButtonStyle();
            easyStyle.imageUp = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Difficulty/easy_60.png", Texture.class)));
            easyStyle.imageOver = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Difficulty/easy.png", Texture.class)));
            easyStyle.imageChecked = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Difficulty/easy.png", Texture.class)));

            ImageButton easy = new ImageButton(easyStyle);
            easy.setName(Difficult.EASY.name());

            easy.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.playSound(SoundFx.SELECT);
                    FileManager.getInstance().setCrosswordDifficult(Difficult.EASY);
                    difficulty = Difficult.EASY;
                }
            });


            ImageButton.ImageButtonStyle mediumStyle = new ImageButton.ImageButtonStyle();
            mediumStyle.imageUp = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Difficulty/medium_60.png", Texture.class)));
            mediumStyle.imageOver = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Difficulty/medium.png", Texture.class)));
            mediumStyle.imageChecked = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Difficulty/medium.png", Texture.class)));

            ImageButton medium = new ImageButton(mediumStyle);
            medium.setName(Difficult.MEDIUM.name());

            medium.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.playSound(SoundFx.SELECT);
                    FileManager.getInstance().setCrosswordDifficult(Difficult.MEDIUM);
                    difficulty = Difficult.MEDIUM;
                }
            });


            ImageButton.ImageButtonStyle hardStyle = new ImageButton.ImageButtonStyle();
            hardStyle.imageUp = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Difficulty/hard_60.png", Texture.class)));
            hardStyle.imageOver = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Difficulty/hard.png", Texture.class)));
            hardStyle.imageChecked = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Difficulty/hard.png", Texture.class)));

            ImageButton hard = new ImageButton(hardStyle);
            hard.setName(Difficult.HARD.name());

            hard.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.playSound(SoundFx.SELECT);
                    FileManager.getInstance().setCrosswordDifficult(Difficult.HARD);
                    difficulty = Difficult.HARD;
                }
            });

            easy.setTransform(true);
            medium.setTransform(true);
            hard.setTransform(true);

            ButtonGroup difficultyGroup = new ButtonGroup();
            difficultyGroup.add(easy, medium, hard);
            difficultyGroup.setMinCheckCount(1);
            difficultyGroup.setMaxCheckCount(1);

            if (difficulty == null) difficultyGroup.uncheckAll();
            else if (difficulty == Difficult.EASY) easy.setChecked(true);
            else if (difficulty == Difficult.MEDIUM) medium.setChecked(true);
            else hard.setChecked(true);

            Table difficulty = new Table();
            difficulty.row().colspan(3);
            difficulty.add(diff).pad(8.0f);
            difficulty.row();
            difficulty.add(easy).pad(8.0f);
            difficulty.add(medium).pad(8.0f);
            difficulty.add(hard).pad(8.0f);

            table.row().expandY();
            table.add(difficulty);
        }

        final Label lang = new Label(game.getString("Lingua"), style);
        lang.setAlignment(Align.center);
        lang.setFontScale(0.8f);

        TextButton.TextButtonStyle checkStyle= new TextButton.TextButtonStyle();
        checkStyle.up=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/White_Block.png",Texture.class)));
        checkStyle.over=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/Checked_Block_yellow.png",Texture.class)));
        checkStyle.checked=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/Checked_Block_yellow.png",Texture.class)));
        checkStyle.font=font;


        /*TextButton langIta= new TextButton("IT", checkStyle);
        langIta.getLabel().setFontScale(0.7f);
        langIta.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound(SoundFx.SELECT);
                game.setLingua(Language.ITALIAN);
                FileManager.getInstance().setCrosswordLanguage(Language.ITALIAN);
            }
        });*/

        TextButton langEng= new TextButton("EN",checkStyle);
        langEng.getLabel().setFontScale(0.7f);
        langEng.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound(SoundFx.SELECT);
                game.setLingua(Language.ENGLISH);
                FileManager.getInstance().setCrosswordLanguage(Language.ENGLISH);
            }
        });
        ButtonGroup languageGroup = new ButtonGroup();
        //languageGroup.add(langIta, langEng);
        languageGroup.add(langEng);
        languageGroup.setMinCheckCount(1);
        languageGroup.setMaxCheckCount(1);

        if(language==null) languageGroup.uncheckAll();
        //else if (language == Language.ITALIAN) langIta.setChecked(true);
        else if (language == Language.ENGLISH) langEng.setChecked(true);


        TextButton.TextButtonStyle buttonStyle1 = new TextButton.TextButtonStyle();
        buttonStyle1.up=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/new_crossword.png", Texture.class)));
        buttonStyle1.over=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/new_crossword_pressed.png", Texture.class)));
        buttonStyle1.font = font;
        TextButton buttonNew = new TextButton(Parole.getInstance().getString("Play"), buttonStyle1);
        buttonNew.setPosition(stage.getWidth()/9,stage.getHeight()/9);
        buttonNew.getLabel().setFontScale(0.8f);

        buttonNew.addListener(new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.playSound(SoundFx.SELECT);
                game.stopMusic();

                if (FileManager.getInstance().availableCrosswords(language, domain, gameType, level+"", difficulty) < MIN_CROSSWORDS) {
                    RequestManager.getInstance().getAndSaveCrosswords(language, domain, gameType, level, difficulty);
                }

                cruciverba = FileManager.getInstance().loadNewCrossword(language, domain, gameType, level, difficulty);
                if (cruciverba == null) {
                    SupporterClass.createDialog(game.getString("NoCrosswords"),stage, game, SupporterClass.DialogDestination.STAYHERE);
                } else {
                    stage.addAction(Actions.fadeOut(0.3f));
                    stage.addAction(Actions.delay(0.3f, Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(new LoadingScreen(cruciverba.get(Parameters.CROSSWORD), cruciverba.get(Parameters.CROSSWORD_ID), game, gameType));
                        }
                    })));
                }
            }
        });


        Table language = new Table();
        language.row().colspan(2);
        language.add(lang).pad(8.0f);
        language.row();
        //language.add(langIta).pad(10.0f);
        language.add(langEng).pad(10.0f);

        table.row().expandY();
        table.add(language);
        table.row().expandY();
        table.add(buttonNew).width(520f).height(200f).pad(8.0f);

        table.setOrigin(0,0);
        table.setSize(stage.getWidth(), stage.getHeight()*6/8f);
        table.setPosition(stage.getWidth()/2-table.getWidth()/2, stage.getHeight()/2-table.getHeight()/2-100f);

        stage.addActor(table);
    }

}