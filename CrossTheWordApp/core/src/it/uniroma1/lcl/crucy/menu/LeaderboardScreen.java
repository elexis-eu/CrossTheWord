package it.uniroma1.lcl.crucy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import javax.swing.GroupLayout;

import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.MainThreadCallback;
import it.uniroma1.lcl.crucy.cache.RequestManager;
import it.uniroma1.lcl.crucy.utils.Loader;

/**
 * Classe che gestisce lo Screen con punteggio personale e classifica
 * @author Camilla
 */
public class LeaderboardScreen implements Screen
{
    private final static int USERS_NUMBER=10;

    private Viewport viewport;
    private Batch batch;

    private Stage stage;
    private MainCrucy game;

    private BitmapFont font;
    private Texture scroll_horizontal, knob_scroll;
    private ScrollPane scroll;

    private ArrayList<String> usersRanking;
    private UserTag uTag;

    private float yStartScroll;
    private Table punt;
    private Table bonus;
    private Image backgroundBoard;


    public LeaderboardScreen(MainCrucy main) {
        game=main;
        viewport= main.getViewport();
        batch=main.getBatch();
        stage=new Stage(viewport,batch);
        Gdx.input.setInputProcessor(stage);

        font = Loader.getInstance().get("Font/fontBold/noto.fnt", BitmapFont.class);
        create();
    }



    @Override
    public void show() {

    }

    public void create() {
        //carico la classifica prendendo i primi 10 dal server
        RequestManager.getInstance().getHighScores("" + USERS_NUMBER, new MainThreadCallback<ArrayList<String>>(){
            @Override
            public void onSuccessInMainThread(ArrayList<String> result) {
                usersRanking = result;
                for (String rank : usersRanking)
                    System.out.println("USER RANKING: " + rank);
                classifica();
            }

            @Override
            public void onFailureInMainThread(Throwable cause) {}
        });

        Label.LabelStyle lwhite = new Label.LabelStyle(font, font.getColor());
        Label.LabelStyle ls = new Label.LabelStyle(font, new Color(37/255f,61/255f,79/255f,1f));

        float fontScale = 0.8f;
        uTag = new UserTag(stage, game.getString("Classifica"));

        punt = new Table();
        punt.row().colspan(2);
        Label lab = new Label(game.getString("Livello"), ls);
        lab.setFontScale(fontScale);
        punt.add(lab);
        lab = new Label(FileManager.getInstance().getLevel()+"", ls);
        lab.setFontScale(fontScale);
        punt.add(lab);
        punt.pack();
        punt.setPosition(stage.getWidth()/2- punt.getWidth()/2, uTag.getBackground().getY()-punt.getHeight()*2f);
        stage.addActor(punt);

        bonus = new Table();
        bonus.row();
        bonus.add(new Image(Loader.getInstance().get("diam.png", Texture.class))).padLeft(50f).padRight(20f);
        lab = new Label(FileManager.getInstance().getDiamonds()+"", ls);
        lab.setFontScale(fontScale);
        bonus.add(lab).padRight(100f);
        bonus.add(new Image(Loader.getInstance().get("gold.png", Texture.class))).padLeft(100f);
        lab = new Label(FileManager.getInstance().getMoney()+"", ls);
        lab.setFontScale(fontScale);
        bonus.add(lab).padLeft(20f).padRight(50f);
        bonus.pack();
        bonus.setPosition(stage.getWidth()/2- bonus.getWidth()/2, punt.getY()-bonus.getHeight()*1.5f);
        yStartScroll = bonus.getY();
        stage.addActor(bonus);

        animateIn(0.3f);
    }

    private void classifica() {
        Label.LabelStyle lwhite = new Label.LabelStyle(font, font.getColor());
        Label.LabelStyle ls = new Label.LabelStyle(font, new Color(240/255f,240/255f,170/255f,1f));

        ScrollPane.ScrollPaneStyle sps = new ScrollPane.ScrollPaneStyle();
        scroll_horizontal = Loader.getInstance().get("scroll_horizontal2.png", Texture.class);
        knob_scroll = Loader.getInstance().get("knob_scroll2.png", Texture.class);
        sps.vScroll = new TextureRegionDrawable(new TextureRegion(scroll_horizontal));
        sps.vScrollKnob = new TextureRegionDrawable(new TextureRegion(knob_scroll));

        Table t = new Table();
        t.setSize(stage.getWidth(), stage.getHeight());
        t.row().colspan(2);

        for (int i = 0; i< usersRanking.size(); i++) {
            //mi viene passata una cosa del tipo "username\tpunteggio"
            String[] userScore=usersRanking.get(i).split("\t");
            t.row().colspan(2);

            String userscore = userScore[0];
            if (userscore.length()> 13)
                userscore = userscore.substring(0,12) + "...";
            Label name = new Label(""+(i+1)+". " + userscore, lwhite);
            name.setFontScale(0.6f);
            Label score = new Label(userScore[1], lwhite);
            score.setFontScale(0.6f);
            t.add(name).expandX().left().padLeft(40f).padRight(40f);
            t.add(score).expandX().right().padLeft(40f).padRight(40f);
        }

        scroll = new ScrollPane(t,sps);
        scroll.setSize(stage.getWidth()*0.8f, yStartScroll - 100);
        scroll.setPosition(stage.getWidth()/2 - scroll.getWidth()/2, stage.getHeight()/2 - scroll.getHeight()*0.8f);
        stage.addActor(scroll);
        //animateInLadder(0.3f);
    }

    @Override
    public void render(float delta) {
        // Se premo il tasto indietro del telefono
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) ||
                Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            game.playSound(SoundFx.SELECT);
            game.setScreen(new Menu(game));
            this.dispose();

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
        stage.dispose();
    }

    public void animationOutBtnBack(float duration)
    {
        stage.addAction(Actions.delay(duration, Actions.run(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new Menu(game));
                dispose();
            }
        })));
    }

    public void animateOut(float duration)
    {
        uTag.getLabel().addAction(Actions.fadeOut(duration));
        punt.addAction(Actions.fadeOut(duration));
        bonus.addAction(Actions.fadeOut(duration));
        if(backgroundBoard!=null) backgroundBoard.addAction(Actions.moveBy(0,-backgroundBoard.getHeight()*3,duration));
        if(scroll!=null)scroll.addAction(Actions.moveBy(0,-scroll.getHeight()*3,duration));
    }

    public void animateIn(float duration)
    {
        for(Actor a :stage.getActors())
            if(!a.equals(backgroundBoard) && !a.equals(scroll)) a.addAction(Actions.fadeIn(duration));

    }

    public void animateInLadder(float duration)
    {
        backgroundBoard.addAction(
                Actions.sequence(
                        Actions.moveTo(backgroundBoard.getX(),-backgroundBoard.getHeight()*3),
                        Actions.moveTo(backgroundBoard.getX(),backgroundBoard.getY(),duration)
                ));

        scroll.addAction(
                Actions.sequence(
                        Actions.moveTo(scroll.getX(),-scroll.getHeight()*3),
                        Actions.moveTo(scroll.getX(),scroll.getY(),duration)
                ));
    }
}