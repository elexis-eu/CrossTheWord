package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.LongPressSwipe;

import com.badlogic.gdx.graphics.OrthographicCamera;
import it.uniroma1.lcl.crucy.gameplay.input.cameraController.GameCameraController;

/**
 * Created by antho on 11/09/2016.
 */
public class LongPressSwipeCamera extends GameCameraController {


    protected boolean moveMode;

    public LongPressSwipeCamera(OrthographicCamera camera, it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController cellController)
    {
        super(camera, cellController);
    }

    @Override
    public boolean longPress(float x, float y)
    {
        cellController.setStateSwipe(false);
        cellController.setTapState(false);
        if(cellController.isBalloon())
        {
            cellController.getScreen().getStageUI().hideBaloon();
            cellController.setBalloon(false);
        }
        stopMoving();
        moveMode = true;
        zoomMode = true;
        cellController.stopHighLightAnimationWithResetWord();
        zoomTo(MAX_ZOOM/2);
        // Do il segnale di swipe in modo tale da uscire dalla modalità inserimento.
        cellController.observerNotifySwipe();
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY)
    {
        if (!moveMode && zoomMode)
            return false;
        return super.pan(x,y,deltaX,deltaY);
    }
}
