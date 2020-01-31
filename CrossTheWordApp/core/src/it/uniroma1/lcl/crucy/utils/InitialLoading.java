package it.uniroma1.lcl.crucy.utils;

/**
 * Created by D
 */

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.Map;
import java.util.Random;

import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.Callback;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.RequestManager;

public class InitialLoading implements Screen {

    private Texture img;
    private TextureRegion[] animationFrames;
    private Animation animation;
    private MainCrucy game;
    private float elapsedTime;
    private final int bloccoLato = 350;
    private Stage stage;


    private Image logo;
    private Image frameCube;
    private Image bar;
    private Image barTop;
    private Image barShader;
    private Image logos;

    public InitialLoading(MainCrucy game) {
        this.game = game;
        stage = new Stage(game.getViewport(),game.getBatch());
        Loader.getInstance();
        create();

    }

    private void create () {
        logo = new Image(new Texture("app_logo.png"));
        bar= new Image(new Texture("spritesheet/loadbar.png"));
        Loader.getInstance().loadTexture(); //inizio il caricamento vero a proprio

        barTop = new Image(new Texture("spritesheet/loadbar_top.png"));
        barShader = new Image(new Texture("spritesheet/loadbar_shader.png"));

        Texture texture_logo = new Texture("Loghi/babelscape+sapienza_NERO2.png");
        texture_logo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        logos = new Image(new Texture("Loghi/babelscape+sapienza_NERO2.png"));
        logos.setScale(0.9f);

        logo.setPosition(stage.getWidth()/2-logo.getWidth()/2, stage.getHeight()/2-logo.getHeight()/2);
        bar.setPosition(stage.getWidth()/2-bar.getWidth()/2 +10, logo.getY() - bar.getHeight()*2);
        barTop.setPosition(bar.getX(),bar.getY());
        barShader.setPosition(bar.getX(),bar.getY());
        logos.setPosition(stage.getWidth()/2 - logos.getWidth()/2 + 50f,  60f);

        stage.addActor(logo);
        stage.addActor(barShader);
        stage.addActor(bar);
        stage.addActor(barTop);
        stage.addActor(logos);
    }

    @Override
    public void render (float f)
    {
        elapsedTime += f;
        bar.setScaleX(Loader.getInstance().percent());

        int realFps=(int) (1/f);
        if(Loader.getInstance().updateLoader(realFps/8)) {
            game.afterLoading();
        }

        stage.act(f);
        stage.draw();
    }


    @Override
    public void show(){}

    @Override
    public void pause(){}

    @Override
    public void resume(){}

    @Override
    public void resize(int a, int b) {}

    @Override
    public void hide(){}

    @Override
    public void dispose(){ this.dispose(); }

 }
