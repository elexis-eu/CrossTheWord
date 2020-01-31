package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.TapAndSwipe;

import com.badlogic.gdx.graphics.OrthographicCamera;
import it.uniroma1.lcl.crucy.gameplay.input.cameraController.GameCameraController;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;

/**
 * Created by antho on 11/09/2016.
 */
public class TapAndSwipeCamera extends GameCameraController {

    public TapAndSwipeCamera(OrthographicCamera camera, CellController cellController)
    {
        super(camera, cellController);
    }

    @Override
    public boolean longPress(float x, float y)
    {
        return false;
    }

}
