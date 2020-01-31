package it.uniroma1.lcl.crucy.gameplay.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.gameplay.crosswordmain.CrucyMainScreen;
import it.uniroma1.lcl.crucy.gameplay.textures.Textures;
import it.uniroma1.lcl.crucy.utils.GameType;

import static it.uniroma1.lcl.crucy.mainconstants.MainConstants.BACKGROUND_BLUE;
import static it.uniroma1.lcl.crucy.mainconstants.MainConstants.BACKGROUND_GREEN;
import static it.uniroma1.lcl.crucy.mainconstants.MainConstants.BACKGROUND_RED;

public class LoadingScreen implements Screen
{

    private static float MAX_FADEOUT = 1f;
    private static float MAX_LOAD_TIME = 3f;
    private  MainCrucy main;

    private LoadingStage stage;

    private String cruciverba;
    private String id;

    private CrucyMainScreen game;

    private float delata;


    private float red;
    private float green;
    private float blue;
    private float reda;
    private float greena;
    private float bluea;

    private GameType gameType;

    private boolean loading = true;

    public LoadingScreen(String cruciverba, String id, MainCrucy main, GameType gameType) {
        this.main = main;
        this.cruciverba = cruciverba;
        this.id = id;
        red = BACKGROUND_RED * 255;
        green = BACKGROUND_GREEN * 255;
        blue = BACKGROUND_BLUE * 255;
        this.gameType=gameType;

    }

    @Override
    public void show() {
        stage = new LoadingStage(new ExtendViewport(1000f,500f),main);
    }

    @Override
    public void render(float delta)
    {
         Textures.loadTextures();
         game = new CrucyMainScreen(cruciverba, id, main, gameType);
         main.setScreen(game);

         stage.act();
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



    public float getPercentageOut()
    {
        return  1f-(delata);
    }
    public void updateColorOut()
    {
        reda = red * getPercentageOut();
        greena = green * getPercentageOut();
        bluea = blue  * getPercentageOut();

    }

    public float getPercentageIn()
    {
        return  (delata-MAX_LOAD_TIME);
    }
    public void updateColorIn()
    {
        reda = red * getPercentageIn();
        greena = green * getPercentageIn();
        bluea = blue  * getPercentageIn();

    }
}
