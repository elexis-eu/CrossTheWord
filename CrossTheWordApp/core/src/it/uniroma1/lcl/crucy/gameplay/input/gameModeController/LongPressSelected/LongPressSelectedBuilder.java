package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.LongPressSelected;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by antho on 11/09/2016.
 */
public class LongPressSelectedBuilder extends it.uniroma1.lcl.crucy.gameplay.input.gameModeController.GameModeBuilder
{

    public LongPressSelectedBuilder(Stage screenStage, OrthographicCamera camera)
    {
        super(screenStage, camera);
    }

    @Override
    protected void build()
    {
        cellController = new LongPressSelectedCell(screenStage);
        camController = new LongPressSelectedCamera(camera, cellController);
    }



    public GestureDetector getGestureCellController()
    {
        return new LongPressSelectedDetector(cellController);
    }
    public GestureDetector getGestureCamController()
    {
        return new GestureDetector(camController);
    }
}
