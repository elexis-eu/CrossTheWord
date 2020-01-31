package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.TapAndSwipe;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import it.uniroma1.lcl.crucy.gameplay.input.gameModeController.GameModeBuilder;

/**
 * Created by antho on 11/09/2016.
 */
public class TapAndSwipeBuilder extends GameModeBuilder
{


    public TapAndSwipeBuilder(Stage screenStage, OrthographicCamera camera)
    {
        super(screenStage, camera);
    }

    @Override
    protected void build()
    {
        cellController = new TapAndSwipeCell(screenStage);
        camController = new TapAndSwipeCamera(camera, cellController);
    }


    public void UpdateAnimation()
    {
        cellController.animationUpdate();
        camController.animationUpdate();
    }


    public void drawAnimation(SpriteBatch batch)
    {
        this.cellController.drawAnimation(batch);
    }

    public GestureDetector getGestureCellController()
    {
        return new GestureDetector(cellController);
    }
    public GestureDetector getGestureCamController()
    {
        return new GestureDetector(camController);
    }





}
