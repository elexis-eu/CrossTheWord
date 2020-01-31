package it.uniroma1.lcl.crucy.menu;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.utils.GameType;
import it.uniroma1.lcl.crucy.utils.LevelStructure;
import it.uniroma1.lcl.crucy.utils.Loader;
import it.uniroma1.lcl.crucy.utils.TextureConverter;


public class GameModeScreen implements Screen
{
    //Integrare con Classe Level
    private MainCrucy game;
    private Stage stage;

    private ImageButton ranked;
    private ImageButton freeplay;
    private Table t;

    private float animationDuration= 0.3f;

    private Label mode;
    private UserTag uTag;


    public GameModeScreen(MainCrucy game, boolean from) {
        this.game = game;
        stage = new Stage(game.getViewport(),game.getBatch());
        Gdx.input.setInputProcessor(stage);
        LevelStructure.getInstance();//
        createMenu(from);
    }

    public void createMenu(boolean from)
    {
        uTag = new UserTag(stage);

        BitmapFont font= Loader.getInstance().get("Font/regularFont/regular.fnt",BitmapFont.class);
        Label.LabelStyle label1Style = new Label.LabelStyle();
        label1Style.font = font;

        mode = new Label(game.getString("Mode"),label1Style);
        mode.setAlignment(Align.center);

        ranked = createPressableButton("Boxes/btile.png", "Boxes/btile_pressed.png" , "MainMenu/rank.png");
        ranked.setName(game.getString("ranked"));
        ranked.addListener( new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button){
                game.playSound(SoundFx.SELECT);
                FileManager.getInstance().setGameType(GameType.RANKED);
                game.setScreen(new StartedScreen(game));
            }
            @Override
            public boolean longPress(Actor a, float x, float y) {
                //this.createText(playI);
                return true;
            }
        });


        freeplay= createPressableButton("Boxes/btile.png", "Boxes/btile_pressed.png" , "MainMenu/freep.png");
        freeplay.setName(game.getString("freeplay"));
        freeplay.addListener( new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button){
                game.playSound(SoundFx.SELECT);
                FileManager.getInstance().setGameType(GameType.FREEPLAY);
                FileManager.getInstance().setCrosswordLevel("-1");
                //activeAnimationOut(new CrosswordChoiceScreen(game));
                animationOutScreen(0.1f);

            }
            @Override
            public boolean longPress(Actor a, float x, float y) {
                //this.createText(playI);
                return true;
            }
        });


        t=new Table();
        t.row().colspan(1).padTop(30f).padRight(50f);
        t.add(mode).top().padLeft(50f);
        t.row().colspan(1).padTop(150f).padRight(50f);
        t.add(ranked).top().padLeft(50f);
        t.row().colspan(1).padTop(10f).padRight(50f);
        t.add(freeplay).top().padLeft(50f);
        t.pack();

        t.setY(stage.getHeight()/2-t.getHeight()/2);
        stage.addActor(t);


        if(from) resumeAnimation(0.1f);
        else animationIn(1f);
    }

    @Override
    public void render(float delta)
    {
        // Se premo il tasto indietro del telefono
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            game.playSound(SoundFx.SELECT);
            this.dispose();
            game.setScreen(new Menu(game));
        }
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    public void dispose(){}
    public void show(){}
    public void hide(){}
    public void pause(){}
    public void resume(){}
    public void resize(int a, int b)
    {
        stage.getViewport().update(a, b);
    }


    public void animationOutBtnBack() {
        stage.addAction(Actions.fadeOut(animationDuration));
    }

    public void animationOutScreen(float f)
    {
        //imgInstruction.addAction(Actions.moveTo(uTag.getLabel().getX(), stage.getHeight(),f));
        //freeplay.addAction(Actions.moveTo(freeplay.getX(), stage.getHeight(),f));
        //ranked.addAction(Actions.moveTo(ranked.getX(), stage.getHeight(),f));

        //uTag.getBackground().addAction(Actions.moveTo(uTag.getBackground().getX(), stage.getHeight(),f));
        //user.addAction(Actions.moveTo(user.getX(), stage.getHeight(),f));

        mode.addAction(Actions.moveTo(-mode.getWidth(),mode.getY(),f));
        freeplay.addAction(Actions.moveTo(-freeplay.getWidth(),freeplay.getY(),f));
        ranked.addAction(Actions.moveTo(-ranked.getWidth(), ranked.getY(),f));

        stage.addAction(Actions.delay(f, Actions.run(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new StartedScreen(game));
            }
        })));
    }

    //animation-in from pickcrosswordscreen

    public void resumeAnimation(float f)
    {
        mode.addAction(Actions.sequence(Actions.moveTo(-mode.getWidth(),mode.getY()),Actions.moveTo(mode.getX(),mode.getY(), f)));
        freeplay.addAction(Actions.sequence(Actions.moveTo(-freeplay.getWidth(),freeplay.getY()),Actions.moveTo(freeplay.getX(),freeplay.getY(), f)));
        ranked.addAction(Actions.sequence(Actions.moveTo(-ranked.getWidth(),ranked.getY()),Actions.moveTo(ranked.getX(),ranked.getY(), f)));
    }

    //animation-in from menu
    public void animationIn(float f)
    {
        stage.addAction(Actions.fadeIn(f));
    }

    public ImageButton createPressableButton(String backgroundNotPressed, String backgroundPressed, String Legends)
    {
        Pixmap tile3 = TextureConverter.toPixmap(Loader.getInstance().get(backgroundNotPressed, Texture.class));
        tile3.setFilter(Pixmap.Filter.BiLinear);
        Pixmap play = TextureConverter.toPixmap(Loader.getInstance().get(Legends,  Texture.class));
        play.setFilter(Pixmap.Filter.BiLinear);
        tile3.drawPixmap(play, (tile3.getWidth() -play.getWidth()) / 2,
                (tile3.getHeight() - play.getHeight()) / 2);
        Sprite PlaySpriteUp = new Sprite(new Texture(tile3));
        Pixmap tile3c = TextureConverter.toPixmap(Loader.getInstance().get(backgroundPressed, Texture.class));
        tile3c.setFilter(Pixmap.Filter.BiLinear);
        tile3c.drawPixmap(play, (tile3c.getWidth() -play.getWidth()) / 2,
                (tile3c.getHeight() - play.getHeight()) / 2);
        Sprite PlaySpriteC = new Sprite(new Texture(tile3c));


        tile3.dispose();
        tile3c.dispose();
        play.dispose();

        ImageButton.ImageButtonStyle playStyle= new ImageButton.ImageButtonStyle();
        playStyle.imageUp=new TextureRegionDrawable(new TextureRegion(PlaySpriteUp));
        playStyle.imageOver=new TextureRegionDrawable(new TextureRegion(PlaySpriteC));
        ImageButton ret = new ImageButton(playStyle);
        ret.setTransform(true);
        return ret;

    }
}
