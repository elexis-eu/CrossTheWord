package it.uniroma1.lcl.crucy.gameplay.loading;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.utils.Loader;

/**
 * Da mettere il framer come util di supporto
 */
public class LoadingActor extends Image
{
    private Texture img;
    private TextureRegion[] animationFrames;
    private Animation animation;
    private float elapsedTime;
    private final int bloccoLato = 350;
    private final int screenW;
    private final int screenH;

    public LoadingActor(MainCrucy main, Stage stage)
    {
        screenH=Gdx.graphics.getHeight();
        screenW=Gdx.graphics.getWidth();
        img = Loader.getInstance().get("background/Loadingcross.png", Texture.class);
        TextureRegion[][] tmpFrames = TextureRegion.split(Loader.getInstance().get("spritesheet/spriteLoad.png", Texture.class),bloccoLato,bloccoLato);
        animationFrames = new TextureRegion[37];

        for (int i = 0; i < 37; i++){ animationFrames[i] = tmpFrames[0][i]; }
        animation = new Animation(1f/259f, animationFrames); // velocita' del cubo
    }
    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        elapsedTime += Gdx.graphics.getDeltaTime();
        batch.draw((TextureRegion) animation.getKeyFrame(elapsedTime,true), screenW/2-bloccoLato/2-20,screenH/2+150);
        batch.draw(img, screenW/2-img.getWidth()/2-20, screenH/2-img.getHeight()/2);
    }
        /*
        elapsedTime += Gdx.graphics.getDeltaTime();
        if(scal>9)
        {
            batch.draw(animation.getKeyFrame(elapsedTime,true),650, screenH/2-img.getHeight()/2-bloccoLato*0.2f, 0, 0, bloccoLato, bloccoLato, 0.2f, 0.2f, 0);
            if(scal>18)
            {
                batch.draw(animation.getKeyFrame(elapsedTime,true),650+(bloccoLato*0.2f), screenH/2-img.getHeight()/2-bloccoLato*0.2f, 0, 0, bloccoLato, bloccoLato, 0.2f, 0.2f, 0);
                if(scal>27)batch.draw(animation.getKeyFrame(elapsedTime,true),650+(bloccoLato*0.2f*2), screenH/2-img.getHeight()/2-bloccoLato*0.2f, 0, 0, bloccoLato, bloccoLato, 0.2f, 0.2f, 0);
            }
        }
         batch.draw(img, 0, screenH/2-img.getHeight());

        if(scal>36)scal = 0;
        else scal+=1;
        */

}

