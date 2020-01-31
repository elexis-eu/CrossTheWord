package it.uniroma1.lcl.crucy.gameplay.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Antonio on 01/12/2016.
 * Questa classe disegna la Texture passata in input, se è troppo piccola per riempire il display
 * disegna la texture finche non riempe il display.
 */

public class BackGround implements Disposable
{
    private Texture texture;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    public BackGround(Texture texture, SpriteBatch batch)
    {
        this.texture = texture;
        this.batch = batch;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2,0);

    }

    /**
     * Disegna un pezzo della texture nelle coordinate indicate.
     * @param x
     * @param y
     */
    private void draw(float x,float y)
    {
        batch.draw(texture,x,y);
    }

    /**
     * Effettua il draw della texture per tutto lo schermo.
     */
    private void drawing()
    {
        for (int x = 0;  x*texture.getWidth() <= Gdx.graphics.getWidth()  ;x++)
        {
            for (int y = 0; y*texture.getHeight() <= Gdx.graphics.getHeight(); y++)
            {
                draw(x*texture.getWidth(),y*texture.getHeight());
            }
        }
    }

    public void render()
    {
        // salvo i colori precedenti del batch.
        Color color = batch.getColor();
        //cambio i colori settando l'opacity a 1.
        batch.setColor(batch.getColor().r,batch.getColor().g,batch.getColor().b,1);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawing();
        batch.end();

        camera.update();
        // resetto i colori precedenti del batch.
        batch.setColor(color);
    }

    @Override
    public void dispose()
    {
        texture.dispose();
    }
}
