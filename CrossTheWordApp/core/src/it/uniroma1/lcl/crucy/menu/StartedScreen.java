package it.uniroma1.lcl.crucy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.utils.Difficult;
import it.uniroma1.lcl.crucy.cache.utils.Parameters;
import it.uniroma1.lcl.crucy.gameplay.audio.MusicAmbient;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.gameplay.crosswordmain.WordGenerator;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.ScoreManager;
import it.uniroma1.lcl.crucy.gameplay.loading.LoadingScreen;
import it.uniroma1.lcl.crucy.menu.login.SupporterClass;
import it.uniroma1.lcl.crucy.menu.login.Textures;
import it.uniroma1.lcl.crucy.utils.GameType;
import it.uniroma1.lcl.crucy.utils.Loader;
import it.uniroma1.lcl.crucy.utils.Parole;
import it.uniroma1.lcl.crucy.utils.TextureConverter;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.definitionLabelTexture;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.dialogDownTexture;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.dialogOverTexture;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.dialogUpTexture;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.font2;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.font4;
import static it.uniroma1.lcl.crucy.menu.login.SupporterClass.getTextButtonStyleDialog;
import static it.uniroma1.lcl.crucy.menu.login.SupporterClass.splitString;

public class StartedScreen implements Screen {

    private MainCrucy game;
    private Stage stage;
    private List<Map<String, String>> startedCrosswords;

    private GameType gametype;
    private UserTag uTag;
    private BitmapFont font;
    private Label gametypeLabel;
    private Table startedTable;
    private ScrollPane scroll;
    private Table table;

    private Pixmap baseButton;
    private Pixmap baseButtonPressed;
    private Pixmap crosswordIcon;

    private final float animationDuration = 0.5f;
    private final int MAX_STARTED_CROSSWORDS = 10;


    public StartedScreen(MainCrucy game){
        this.game = game;
        this.gametype = FileManager.getInstance().getGameType();
        this.stage = new Stage(game.getViewport(),game.getBatch());
        this.startedCrosswords = FileManager.getInstance().getStartedCrosswords(gametype);
        Gdx.input.setInputProcessor(stage);

        loadStartedTextures();
        createMenu();
    }



    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Se premo il tasto indietro del telefono
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            dispose();
            game.playSound(SoundFx.SELECT);
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
    public void dispose() {

    }

    public void createMenu(){
        uTag = new UserTag(stage);
        font = Loader.getInstance().get("Font/regularFont/regular.fnt", BitmapFont.class);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;

        //gametypeLabel = new Label(gametype.name(), style); //TODO: Se viene introdotta nuovamente la modalità Ranked riattivate
        gametypeLabel = new Label(game.getString("Crosswords").toUpperCase(), style);
        gametypeLabel.setPosition(stage.getWidth()/2 - gametypeLabel.getWidth()/2,stage.getHeight() - 400f);
        gametypeLabel.setAlignment(Align.center);
        gametypeLabel.setFontScale(0.8f);

        table = new Table();
        table.setWidth(stage.getWidth()-200f);
        for (final Map<String, String> started : startedCrosswords) {
            String description = "";
            if (gametype.equals(GameType.RANKED))
                description += game.getString("CrosswordLevel") + ": " + started.get(Parameters.LEVEL) + "\n";
            description += game.getString(started.get(Parameters.LANGUAGE)).toUpperCase();

            final TextButton tb = new TextButton(description, getButtonStyle(started.get(Parameters.DIFFICULT).toLowerCase()));
            tb.setTransform(true);
            System.out.println(stage.getWidth()-200f);
            tb.setHeight(200f);
            tb.getLabel().setFontScale(0.6f);
            tb.getLabel().setAlignment(Align.center);
            tb.setName(started.get(Parameters.CROSSWORD_ID));
            tb.addListener(new ActorGestureListener(20, 0.4f, 0.3f, 0.15f) {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    game.playSound(SoundFx.SELECT);
                    game.stopMusic();
                    String crossword = FileManager.getInstance().loadStartedCrossword(started);
                    game.setScreen(new LoadingScreen(crossword, tb.getName(), game, gametype));
                }

                @Override
                public boolean longPress(Actor actor, float x, float y){
                    deleteDialog(tb.getName());
                    return true;
                }

            });

            table.row().pad(6.0f).width(table.getWidth());
            table.add(tb);
        }

        scroll = addScroll(table);

        TextButton.TextButtonStyle buttonStyle1 = new TextButton.TextButtonStyle();
        buttonStyle1.up=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/new_crossword.png", Texture.class)));
        buttonStyle1.over=new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/new_crossword_pressed.png", Texture.class)));
        buttonStyle1.font = font;

        TextButton buttonNew = new TextButton(Parole.getInstance().getString("Nuovo"), buttonStyle1);

        buttonNew.getLabel().setFontScale(0.7f);
        buttonNew.addListener(new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.playSound(SoundFx.SELECT);

                if (!game.isLoggedInFile()) {
                    SupporterClass.createDialog(game.getString("NotLogged"), stage, game, SupporterClass.DialogDestination.STAYHERE);
                } else if (startedCrosswords.size() >= MAX_STARTED_CROSSWORDS) {
                        SupporterClass.createDialog(game.getString("MaxCrosswords"), stage, game, SupporterClass.DialogDestination.STAYHERE);
                } else {
                    stage.addAction(Actions.fadeOut(0.3f));
                    dispose();
                    if (gametype == GameType.RANKED) game.setScreen(new RankedScreen(game));
                    else if (gametype == GameType.FREEPLAY) game.setScreen(new PickScreen(game));
                }
            }
        });

        startedTable = new Table();
        startedTable.row().pad(10.0f);
        startedTable.add(gametypeLabel);
        startedTable.row().pad(10.0f);
        startedTable.add(scroll);
        startedTable.row().pad(10.0f);
        startedTable.add(buttonNew).width(520f).height(200f);
        startedTable.row().pad(10.0f);
        startedTable.setOrigin(0,0);
        startedTable.setSize(stage.getWidth(), stage.getHeight()*6/8f);
        startedTable.setPosition(stage.getWidth()/2-startedTable.getWidth()/2, stage.getHeight()/2-startedTable.getHeight()/2-100f);
        stage.addActor(startedTable);
    }

    private ScrollPane addScroll(Table table) {
        Texture scroll_horizontal  = Loader.getInstance().get("scroll_horizontal2.png", Texture.class);
        Texture knob_scroll =  Loader.getInstance().get("knob_scroll_little.png", Texture.class);

        ScrollPane.ScrollPaneStyle sps = new ScrollPane.ScrollPaneStyle();
        sps.vScroll = new TextureRegionDrawable(new TextureRegion(scroll_horizontal));
        sps.vScrollKnob = new TextureRegionDrawable(new TextureRegion(knob_scroll));

        ScrollPane scroll = new ScrollPane(table, sps);
        scroll.setSize(stage.getWidth(), 260f);
        return scroll;
    }

    private void loadStartedTextures() {
        this.baseButton = TextureConverter.toPixmap(Loader.getInstance().get("Started/started_small.png", Texture.class));
        baseButton.setFilter(Pixmap.Filter.BiLinear);

        this.baseButtonPressed = TextureConverter.toPixmap(Loader.getInstance().get("Started/started_small_down.png", Texture.class));
        baseButtonPressed.setFilter(Pixmap.Filter.BiLinear);

        this.crosswordIcon = TextureConverter.toPixmap(Loader.getInstance().get("Started/mini_cross3.png", Texture.class));
        crosswordIcon.setFilter(Pixmap.Filter.BiLinear);

        baseButton.drawPixmap(crosswordIcon, crosswordIcon.getWidth()/5,
                (baseButton.getHeight() - crosswordIcon.getHeight()) / 2);

        baseButtonPressed.drawPixmap(crosswordIcon, crosswordIcon.getWidth()/5,
                (baseButtonPressed.getHeight() - crosswordIcon.getHeight()) / 2);

    }

    private TextButton.TextButtonStyle getButtonStyle(String difficult){
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

        Pixmap difficulty = TextureConverter.toPixmap(Loader.getInstance().get(String.format("Started/started_%s_icon2.png", difficult), Texture.class));
        difficulty.setFilter(Pixmap.Filter.BiLinear);

        baseButton.drawPixmap(difficulty, baseButton.getWidth() - (difficulty.getWidth() + difficulty.getWidth()/5),
                (baseButton.getHeight() - difficulty.getHeight()) / 2);

        baseButtonPressed.drawPixmap(difficulty, baseButton.getWidth() - (difficulty.getWidth() + difficulty.getWidth()/5),
                (baseButtonPressed.getHeight() - difficulty.getHeight()) / 2);

        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(baseButton)));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture(baseButtonPressed)));
        buttonStyle.font = font;
        return buttonStyle;
    }

    public void deleteDialog(final String crosswordID) {
        ArrayList<String> set = splitString(game.getString("Delete?"));
        ArrayList<Label> setLabel = new ArrayList<Label>();

        Textures.loadTextures();
        //Prese in input le varie righe crea un label per ogni riga
        for (String strin : set) {
            Label label = new Label(strin, new Label.LabelStyle(Textures.font, Textures.font.getColor()));
            label.setFontScale(0.6f);
            setLabel.add(label);
        }

        TextButton.TextButtonStyle dialogBS = getTextButtonStyleDialog();
        final TextButton bottoneSi = new TextButton(game.getString("Si"), dialogBS);
        //bottoneSi.setColor(174f / 255f, 211f / 255f, 157f / 255f, 1f);
        bottoneSi.setTransform(true);
        bottoneSi.setOrigin(bottoneSi.getOriginX() + bottoneSi.getWidth() / 2, bottoneSi.getOriginY() + bottoneSi.getHeight() / 2);
        bottoneSi.setScale(0, 0);
        bottoneSi.getLabel().setFontScale(0.5f);
        bottoneSi.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                FileManager.getInstance().deleteStartedCrossword(crosswordID);
                game.setScreen(new StartedScreen(game));
            }
        });

        final TextButton bottoneNo = new TextButton(game.getString("No"), dialogBS);
        //bottoneNo.setColor(229f / 255f, 135f / 255f, 152f / 255f, 1f);
        bottoneNo.setTransform(true);
        bottoneNo.setOrigin(bottoneNo.getOriginX() + bottoneNo.getWidth() / 2, bottoneNo.getOriginY() + bottoneNo.getHeight() / 2);
        bottoneNo.setScale(0, 0);
        bottoneNo.getLabel().setFontScale(0.5f);
        bottoneNo.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        Window.WindowStyle ws = new Window.WindowStyle();
        ws.titleFont = Textures.font;

        Dialog dialog = new Dialog("", ws);
        dialog.setModal(true);
        dialog.setBackground(new TextureRegionDrawable(new TextureRegion(Textures.labelTexture)));

        float maxWidth = 0;
        float maxHeight = 0;
        int colspan = 1;
        for (Label label : setLabel) {
            dialog.getContentTable().row().colspan(colspan).center();
            dialog.getContentTable().add(label);
            maxHeight += label.getPrefHeight();
            if (maxWidth < label.getPrefWidth())
                maxWidth = label.getPrefWidth();
            colspan++;
        }

        dialog.getContentTable().row().colspan(colspan).center();
        dialog.button(bottoneSi);
        dialog.button(bottoneNo);
        dialog.padBottom(25f);
        dialog.pack();

        if (maxWidth + 50f < stage.getViewport().getWorldWidth() - 10f)
            dialog.setSize(maxWidth + 50f, maxHeight + 230);
        else
            dialog.setSize(stage.getViewport().getWorldWidth() - 10f, maxHeight + 230);

        dialog.setPosition(stage.getCamera().viewportWidth / 2 - dialog.getWidth() / 2, stage.getCamera().viewportHeight / 3 * 2);
        dialog.addAction(moveTo(stage.getCamera().viewportWidth / 2 - dialog.getWidth() / 2, stage.getCamera().viewportHeight / 2 - dialog.getHeight() / 2, .5f, Interpolation.swingOut));

        if (bottoneSi != null) {
            bottoneSi.setTransform(true);
            bottoneSi.setOrigin(bottoneSi.getOriginX() + bottoneSi.getWidth() / 2, bottoneSi.getOriginY() + bottoneSi.getHeight() / 2);
            bottoneSi.addAction(sequence(scaleTo(0, 0), delay(.2f, scaleTo(1, 1, .3f, Interpolation.swingOut))));
        }

        if (bottoneNo != null) {
            bottoneNo.setTransform(true);
            bottoneNo.setOrigin(bottoneNo.getOriginX() + bottoneNo.getWidth() / 2, bottoneNo.getOriginY() + bottoneNo.getHeight() / 2);
            bottoneNo.addAction(sequence(scaleTo(0, 0), delay(.2f, scaleTo(1, 1, .3f, Interpolation.swingOut))));
        }

        stage.addActor(dialog);
    }
}
