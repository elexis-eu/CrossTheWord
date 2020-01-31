package it.uniroma1.lcl.crucy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Map;

import it.uniroma1.lcl.crucy.cache.Callback;
import it.uniroma1.lcl.crucy.cache.CallbackVoid;
import it.uniroma1.lcl.crucy.cache.RequestManager;
import it.uniroma1.lcl.crucy.cache.exceptions.SessionExpiredException;
import it.uniroma1.lcl.crucy.cache.exceptions.SynchronizedExeption;
import it.uniroma1.lcl.crucy.menu.login.ChoiceScreen;
import it.uniroma1.lcl.crucy.menu.login.SupporterClass;
import it.uniroma1.lcl.crucy.utils.Dialoghi;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.ConnectionManager;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.MainThreadCallbackVoid;
import it.uniroma1.lcl.crucy.cache.utils.Parameters;
import it.uniroma1.lcl.crucy.utils.GameType;
import it.uniroma1.lcl.crucy.utils.Loader;
import it.uniroma1.lcl.crucy.utils.Parole;
import it.uniroma1.lcl.crucy.utils.TextureConverter;
import sun.util.resources.cldr.en.CalendarData_en_AU;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Classe che gestisce il menu della schermata iniziale
 * Created by Camilla
 */

public class Menu implements Screen
{
    private static MainCrucy game;
    private Menu now;
    private static Stage stage;
    private Table table1;
    private Table table;
    private Texture block;
    private Texture gameTitleTex;
    private TextButton i;
    private ImageButton loginI,cupI;
    private Texture balloon;
    private BitmapFont font;
    private ImageButton playI;
    private ImageButton settingsI,exitI;
    private Array<TextButton> arrayBalloon=new Array<TextButton>();
    private String username;


    public Menu(MainCrucy main) {
        game = main;
        now = this;
        stage = new Stage(main.getViewport(),main.getBatch());
        Gdx.input.setInputProcessor(stage);

        if(main.isLoggedInFile()) {
            System.out.println("is Logged");
            main.checkConnection();
            username = FileManager.getInstance().getUsername();

            System.out.println("RECOVERING ALL STATES");
            RequestManager.getInstance().recoverCrosswordState();
            RequestManager.getInstance().getUserScore(new Callback<Map<String, Float>>() {
                @Override
                public void onSuccess(Map<String, Float> result) {
                    System.out.println("SUCCESSFUL");
                    System.out.println(result.size());
                    for (Float k : result.values())
                        System.out.println(k);

                    FileManager.getInstance().setUserScore(result);
                }

                @Override
                public void onFailure(Throwable cause) {
                    cause.printStackTrace();
                }
            });
        }
        creaMenu();
    }

    private void creaMenu()
    {
        font = Loader.getInstance().get("Font/regularFont/regular.fnt",BitmapFont.class);

        //Texture per i fumetti che spiegano cosa rapprensentano le icone
        balloon=Loader.getInstance().get("bg.png",Texture.class);

        //Title
        gameTitleTex = Loader.getInstance().get("app_logo.png", Texture.class); //logo Crucy
        gameTitleTex.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);


        Image gameTitle = new Image(new TextureRegionDrawable(new TextureRegion(gameTitleTex)));

        //Black Blocks
        block = Loader.getInstance().get("Boxes/block_large_fill.png",Texture.class);
        block.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        Image block1 = new Image(new TextureRegionDrawable(new TextureRegion(block)));
        Image block2 = new Image(new TextureRegionDrawable(new TextureRegion(block)));
        Image block3 = new Image(new TextureRegionDrawable(new TextureRegion(block)));
        Image block4 = new Image(new TextureRegionDrawable(new TextureRegion(block)));



        //Login Button
        Pixmap tile1 =  TextureConverter.toPixmap(Loader.getInstance().get("Boxes/White_Block.png", Texture.class));
        tile1.setFilter(Pixmap.Filter.BiLinear);
        Pixmap login =  TextureConverter.toPixmap(Loader.getInstance().get("MainMenu/Login.png",  Texture.class));
        login.setFilter(Pixmap.Filter.BiLinear);
        tile1.drawPixmap(login, (tile1.getWidth() -login.getWidth()) / 2,
                (tile1.getHeight() - login.getHeight()) / 2);
        Sprite LoginSpriteUp = new Sprite(new Texture(tile1));
        Pixmap tile1c =  TextureConverter.toPixmap(Loader.getInstance().get("Boxes/Checked_Block.png",  Texture.class));
        tile1c.setFilter(Pixmap.Filter.BiLinear);
        tile1c.drawPixmap(login, (tile1c.getWidth() -login.getWidth()) / 2,
                (tile1c.getHeight() - login.getHeight()) / 2);
        Sprite LoginSpriteC = new Sprite(new Texture(tile1c));
        tile1.dispose();
        tile1c.dispose();
        login.dispose();

        ImageButton.ImageButtonStyle loginStyle= new ImageButton.ImageButtonStyle();
        loginStyle.imageUp=new TextureRegionDrawable(new TextureRegion(LoginSpriteUp));
        loginStyle.imageOver=new TextureRegionDrawable(new TextureRegion(LoginSpriteC));
        loginI=new ImageButton(loginStyle);
        loginI.setTransform(true);
        loginI.setName(game.getString("Login"));

        loginI.addListener( new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button){
                game.playSound(SoundFx.SELECT);
                //SupporterClass dc= new SupporterClass(game);
                //Textures.loadTextures();
                //game.setScreen(new ChoiceScreen(game,now));
                activeAnimationOut(new ChoiceScreen(game,now));
                //activeAnimationOut(new ChoiceScreen(game,now));
            }
            @Override
            public boolean longPress(Actor a, float x, float y) {
                now.createText(loginI);
                return true;


            }
        });

        //SettingsScreen Button
        Pixmap tile2 = TextureConverter.toPixmap(Loader.getInstance().get("Boxes/White_Block.png",   Texture.class));
        tile2.setFilter(Pixmap.Filter.BiLinear);
        Pixmap settings = TextureConverter.toPixmap(Loader.getInstance().get("MainMenu/Settings.png",  Texture.class));
        settings.setFilter(Pixmap.Filter.BiLinear);
        tile2.drawPixmap(settings, (tile2.getWidth() -settings.getWidth()) / 2,
                (tile2.getHeight() - settings.getHeight()) / 2);
        Sprite SettingsSpriteUp = new Sprite(new Texture(tile2));

        Pixmap tile2c = TextureConverter.toPixmap(Loader.getInstance().get("Boxes/Checked_Block.png",  Texture.class));
        tile2c.setFilter(Pixmap.Filter.BiLinear);
        tile2c.drawPixmap(settings, (tile2c.getWidth() -settings.getWidth()) / 2,
                (tile2c.getHeight() - settings.getHeight()) / 2);
        Sprite SettingsSpriteC = new Sprite(new Texture(tile2c));
        tile2.dispose();
        settings.dispose();
        tile2c.dispose();

        ImageButton.ImageButtonStyle settingsStyle= new ImageButton.ImageButtonStyle();
        settingsStyle.imageUp=new TextureRegionDrawable(new TextureRegion(SettingsSpriteUp));
        settingsStyle.imageOver=new TextureRegionDrawable(new TextureRegion(SettingsSpriteC));
        settingsI=new ImageButton(settingsStyle);
        settingsI.setTransform(true);
        settingsI.setName(game.getString("Impostazioni"));


        settingsI.addListener( new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button){
                game.playSound(SoundFx.SELECT);
                activeAnimationOut(new SettingsScreen(game));

            }
            @Override
            public boolean longPress(Actor a, float x, float y) {
                now.createText(settingsI);
                return true;


            }
        });


        //Play Button
        Pixmap tile3 = TextureConverter.toPixmap(Loader.getInstance().get("Boxes/White_Block.png", Texture.class));
        tile3.setFilter(Pixmap.Filter.BiLinear);
        Pixmap play = TextureConverter.toPixmap(Loader.getInstance().get("MainMenu/Play.png",  Texture.class));
        play.setFilter(Pixmap.Filter.BiLinear);
        tile3.drawPixmap(play, (tile3.getWidth() -play.getWidth()) / 2,
                (tile3.getHeight() - play.getHeight()) / 2);
        Sprite PlaySpriteUp = new Sprite(new Texture(tile3));
        Pixmap tile3c = TextureConverter.toPixmap(Loader.getInstance().get("Boxes/Checked_Block.png", Texture.class));
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
        playI=new ImageButton(playStyle);
        playI.setTransform(true);
        playI.setName(game.getString("Gioca"));
        playI.addListener( new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button){
                game.playSound(SoundFx.SELECT);
                //activeAnimationOut(new GameModeScreen(game,false));
                System.out.println(FileManager.getInstance().isLoggedIn());
                if (!FileManager.getInstance().isLoggedIn()) {
                    activeAnimationOut(new ChoiceScreen(game,now));
                } else {
                    // Lancio direttamente la modalità Free Play
                    FileManager.getInstance().setGameType(GameType.FREEPLAY);
                    FileManager.getInstance().setCrosswordLevel("-1");
                    activeAnimationOut(new StartedScreen(MainCrucy.getMain()));
                }
            }

            @Override
            public boolean longPress(Actor a, float x, float y) {
                now.createText(playI);
                return true;
            }
        });

        //LeaderboardScreen Button
        Pixmap tile4 = TextureConverter.toPixmap(Loader.getInstance().get("Boxes/White_Block.png", Texture.class));
        tile4.setFilter(Pixmap.Filter.BiLinear);
        Pixmap cup = TextureConverter.toPixmap(Loader.getInstance().get("MainMenu/Cup.png", Texture.class));
        cup.setFilter(Pixmap.Filter.BiLinear);
        tile4.drawPixmap(cup, (tile4.getWidth() -cup.getWidth()) / 2,
                (tile4.getHeight() - cup.getHeight()) / 2);

        Sprite CupSpriteUp = new Sprite(new Texture(tile4));

        Pixmap tile4c = TextureConverter.toPixmap(Loader.getInstance().get("Boxes/Checked_Block.png", Texture.class));
        tile4c.setFilter(Pixmap.Filter.BiLinear);
        tile4c.drawPixmap(cup, (tile4c.getWidth() -cup.getWidth()) / 2,
                (tile4c.getHeight() - cup.getHeight()) / 2);
        Sprite CupSpriteC = new Sprite(new Texture(tile4c));


        tile4.dispose();
        tile4c.dispose();
        cup.dispose();

        ImageButton.ImageButtonStyle cupStyle= new ImageButton.ImageButtonStyle();
        cupStyle.imageUp=new TextureRegionDrawable(new TextureRegion(CupSpriteUp));
        cupStyle.imageOver=new TextureRegionDrawable(new TextureRegion(CupSpriteC));
        cupI=new ImageButton(cupStyle);
        cupI.setTransform(true);
        cupI.setName(game.getString("Classifica"));

        cupI.addListener( new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button){
                game.playSound(SoundFx.SELECT);
                activeAnimationOut(new LeaderboardScreen(game));
            }
            @Override
            public boolean longPress(Actor a, float x, float y) {
                now.createText(cupI);
                return true;


            }
        });


        //Exit Button
        Pixmap tile5 = TextureConverter.toPixmap(Loader.getInstance().get("Boxes/White_Block.png", Texture.class));
        tile5.setFilter(Pixmap.Filter.BiLinear);
        Pixmap exit = TextureConverter.toPixmap(Loader.getInstance().get("MainMenu/Off.png", Texture.class));
        exit.setFilter(Pixmap.Filter.BiLinear);
        tile5.drawPixmap(exit, (tile5.getWidth() -exit.getWidth()) / 2,
                (tile5.getHeight() - exit.getHeight()) / 2);
        Sprite ExitSpriteUp = new Sprite(new Texture(tile5));
        Pixmap tile5c = TextureConverter.toPixmap(Loader.getInstance().get("Boxes/Checked_Block.png", Texture.class));
        tile5c.setFilter(Pixmap.Filter.BiLinear);
        tile5c.drawPixmap(exit, (tile5c.getWidth() -exit.getWidth()) / 2,
                (tile5c.getHeight() - exit.getHeight()) / 2);
        Sprite ExitSpriteC = new Sprite(new Texture(tile5c));


        tile5.dispose();
        tile5c.dispose();
        exit.dispose();

        ImageButton.ImageButtonStyle exitStyle= new ImageButton.ImageButtonStyle();
        exitStyle.imageUp=new TextureRegionDrawable(new TextureRegion(ExitSpriteUp));
        exitStyle.imageOver=new TextureRegionDrawable(new TextureRegion(ExitSpriteC));
        exitI=new ImageButton(exitStyle);
        exitI.setTransform(true);
        exitI.setName(game.getString("Esci"));

        exitI.addListener( new ActorGestureListener(20, 0.4f, 0.4f, 0.15f) {

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button){
                createDialog();
                game.playSound(SoundFx.SELECT);
                game.stopMusic();
                now.dispose();
                Gdx.app.exit();


            }
            @Override
            public boolean longPress(Actor a, float x, float y) {
                now.createText(exitI);
                return true;


            }
        });

        //creo la table contenente il logo
        table=new Table();
        table.row().expandX();
        table.add(gameTitle).center(); //.expandX().size(800,3200/7);
        if(username!=null)
        {
            Label.LabelStyle usernameStyle=new Label.LabelStyle();
            usernameStyle.font=Loader.getInstance().get("Font/fontBold/noto.fnt",BitmapFont.class);
            Label userLabel=new Label(username,usernameStyle);
            userLabel.setFontScale(0.6f);
            table.row().right();
            table.add(userLabel);
        }
        table.pack();


        //creo la table contenente i vari bottoni
        table1 = new Table();
        table1.row().colspan(3).padTop(400f).padRight(20f);
        table1.add(loginI);
        table1.add(block1).padLeft(20f);
        table1.add(settingsI).padLeft(40f);

        table1.row().padTop(10f).colspan(3).padRight(20f);
        table1.add(block2);
        table1.add(playI).padLeft(20f);
        table1.add(block3).padLeft(40f);

        table1.row().padTop(10f).colspan(3).padRight(20f);
        table1.add(cupI);
        table1.add(block4).padLeft(20f);
        table1.add(exitI).padLeft(40f);

        table1.setFillParent(true);
        table1.pack();
        table.setPosition(stage.getWidth()/2-table.getWidth()/2,stage.getHeight()-table.getHeight()-50f);

        stage.addActor(table);

        stage.addActor(table1);

        // se ho creato vari fumetti cliccando sullo schermo scompaiono
        stage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                for(TextButton t:arrayBalloon)
                    t.setVisible(false);
            }

        });

        //game.setCurrentSong(MusicAmbient.MENU);



    }

    //metodo che mi fa apparire le nuvolette con la spiegazione dell'icona
    private void createText(Actor a)
    {
        deleteBalloon();
        TextButton.TextButtonStyle tb=new TextButton.TextButtonStyle();
        TextureRegion tr = new TextureRegion(balloon);

        if(a.getX()<stage.getWidth()/2)
            tr.flip(true,false);
        tb.up = new TextureRegionDrawable(tr);
        tb.font = font;
        tb.font.getData().setScale(0.6f);

        i = new TextButton(a.getName()+"\n",tb);
        i.setSize(670,i.getHeight());
        if(a.getX()+a.getWidth()-670<0)
            i.setPosition(a.getX()+a.getWidth()-80,a.getY()+a.getHeight()-20);
        else
            i.setPosition(a.getX()-310,a.getY()+a.getHeight()-20);

        i.setTransform(true);
        i.addAction(Actions.sequence(Actions.scaleTo(0 ,0), Actions.scaleTo(0.6f, 0.6f, 0.6f, Interpolation.swingOut)));
        stage.addActor(i);
        arrayBalloon.add(i);

    }

    private void deleteBalloon()
    {
        for(TextButton balloon:arrayBalloon)
            balloon.setVisible(false);
    }

    private void createDialog(){

        BitmapFont font2 = font;

        TextButton.TextButtonStyle dialogBS = new TextButton.TextButtonStyle();
        dialogBS.up = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Buttons/dialog_button_pop.png", Texture.class)));
        dialogBS.over = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Buttons/dialog_button_pop_hover.png", Texture.class)));
        dialogBS.down = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Buttons/dialog_button_pop_active.png", Texture.class)));
        dialogBS.font = font2;

        ArrayList<String> set = SupporterClass.splitString(Parole.getInstance().getString("Uscire"));
        ArrayList<Label> setLabel = new ArrayList<Label>();

        for (String strin : set) {
            Label label = new Label(strin, new Label.LabelStyle(font2, font2.getColor()));
            label.setFontScale(0.7f);
            setLabel.add(label);
        }

        final TextButton bottoneSi = new TextButton(Parole.getInstance().getString("Si"), dialogBS);
        bottoneSi.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        bottoneSi.setColor(174f / 255f, 211f / 255f, 157f / 255f, 1f);
        bottoneSi.setTransform(true);
        bottoneSi.setOrigin(bottoneSi.getOriginX() + bottoneSi.getWidth() / 2, bottoneSi.getOriginY() + bottoneSi.getHeight() / 2);
        bottoneSi.setScale(0, 0);

        final TextButton bottoneNo = new TextButton(Parole.getInstance().getString("No"), dialogBS);
        bottoneNo.setColor(229f / 255f, 135f / 255f, 152f / 255f, 1f);
        bottoneNo.setTransform(true);
        bottoneNo.setOrigin(bottoneNo.getOriginX() + bottoneNo.getWidth() / 2, bottoneNo.getOriginY() + bottoneNo.getHeight() / 2);
        bottoneNo.setScale(0, 0);

        bottoneNo.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.playSound(SoundFx.SELECT);
            }
        });

        Label label = new Label("Ciao", new Label.LabelStyle(font2, font2.getColor()));
        label.setFontScale(0.5f);

        Window.WindowStyle ws = new Window.WindowStyle();
        ws.titleFont = font2;

        Dialog dialog = new Dialog("", ws);
        dialog.setModal(true);
        dialog.setBackground(new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("definition_label.jpg", Texture.class))));
        for (Label l : setLabel) {
            l.setFontScale(0.5f);
            dialog.getContentTable().row().colspan(1).center();
            dialog.getContentTable().add(l);
        }
        dialog.getContentTable().row().colspan(2).center();
        dialog.button(bottoneSi);
        dialog.button(bottoneNo);
        dialog.padBottom(25f);
        dialog.pack();

        dialog.setSize(600, 320);
        dialog.setPosition(stage.getCamera().viewportWidth / 2 - dialog.getWidth() / 2, stage.getCamera().viewportHeight / 3 * 2);
        dialog.addAction(moveTo(stage.getCamera().viewportWidth / 2 - dialog.getWidth() / 2, stage.getCamera().viewportHeight / 2 - dialog.getHeight() / 2, .5f, Interpolation.swingOut));
        bottoneSi.addAction(delay(.2f, scaleTo(1, 1, .3f, Interpolation.swingOut)));
        bottoneNo.addAction(delay(.4f, scaleTo(1, 1, .3f, Interpolation.swingOut)));
        stage.addActor(dialog);
    }

    public Runnable buttonOn(final Actor t)
    {
        return new Runnable() {
            @Override
            public void run() {
                t.setTouchable(Touchable.enabled);
            }
        };
    }

    @Override
    public void show() {

        // Effetto del logo che appare al centro e poi sale
        table.getColor().a=0.0f;
        table.setPosition(table.getX(),table.getY()/2);
        table.addAction(sequence(fadeIn(0f),moveTo(table.getX(),table.getY()*2,0.2f,Interpolation.swingOut))); //no fade after loading



        //updateCrosswords();
        // Effetto delle caselle (vengono attivate al termine dell'animazione)
        for(Cell cell : table1.getCells())
        {
            Actor act = cell.getActor();
            float duration = ((float)Math.random()+0.1f)*1f;
            act.getColor().a=0.0f;
            act.setTouchable(Touchable.disabled);
            act.setOrigin(act.getOriginX() + act.getWidth()/2, act.getOriginY() + act.getHeight()/2);
            act.clearActions();
            act.addAction(delay(0.2f,parallel(fadeIn(0.2f),sequence(
                    scaleTo(0, 0),
                    scaleTo(1, 1, duration, Interpolation.bounce),
                    run(buttonOn(act))
            ))));
        }

    }

    @Override
    public void render(float delta)
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            game.playSound(SoundFx.SELECT);
            game.stopMusic();
            dispose();
            Gdx.app.exit();
        }


        stage.act(delta);
        //stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
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

    public MoveToAction animationTitle()
    {
        MoveToAction actionMoveOut = Actions.action(MoveToAction.class);
        actionMoveOut.setPosition(table.getX(), stage.getCamera().viewportHeight);
        actionMoveOut.setDuration(0.3f);
        actionMoveOut.setInterpolation(Interpolation.swing);
        return actionMoveOut;
    }

    public Runnable changeScreen(final Screen screen)
    {
        return  new Runnable() {
            @Override
            public void run() {
                now.dispose();
                game.setScreen(screen);
            }
        };
    }

    public void activeAnimationOut(Screen screen)
    {
        table.addAction(Actions.sequence(animationTitle(), run(changeScreen(screen))));

        for(Cell cell : table1.getCells())
        {
            Actor act = cell.getActor();
            act.clearActions();
            float duration = ((float)Math.random()+0.1f)*0.2f;
            act.setTouchable(Touchable.disabled);
            act.addAction(Actions.scaleTo(0, 0, duration, Interpolation.bounce));
        }
    }
    /**
     * La Callback sarà da implementare bene nel caso in cui questa fallisca
     */
    private class CallbackLoginSync extends MainThreadCallbackVoid
    {
        @Override
        public void onSuccessInMainThread() {
            FileManager.getInstance().setSessionID(ConnectionManager.getInstance().getSessionId());
            game.setFileLog(true);
            game.setConnLog(true);
        }

        @Override
        public void onFailureInMainThread(Throwable cause)
        {
            cause.printStackTrace();
            if(cause instanceof SynchronizedExeption)
            {
                game.setConnLog(false);
                SupporterClass.createDialog(game.getString(Dialoghi.SYNCERROR.name()),stage,game, SupporterClass.DialogDestination.DIALOCHOICES);
            }

            if (cause instanceof SessionExpiredException) {
                System.out.println("Session Expired");
                game.setFileLog(false);
                game.setConnLog(false);
                FileManager.getInstance().logout();
                ConnectionManager.getInstance().logout(new CallbackVoid() {
                    @Override
                    public void onSuccess() { }

                    @Override
                    public void onFailure(Throwable cause) {cause.printStackTrace();}
                });
            }
        }
    }
}
