package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.LongPressSwipe;

import com.badlogic.gdx.input.GestureDetector;

import it.uniroma1.lcl.crucy.gameplay.input.cameraController.GameCameraController;

/**
 * Created by antho on 11/09/2016.
 */
public class LongPressSwipeDetector extends GestureDetector {

    LongPressSwipeCamera cameraController;

    public LongPressSwipeDetector(LongPressSwipeCamera cameraController)
    {
        super(cameraController);
        this.cameraController = cameraController;
        this.setLongPressSeconds(0.3f);
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button)
    {
        if (cameraController.moveMode)
        {
            cameraController.getCellController().setStateSwipe(true);
            cameraController.getCellController().setTapState(true);
            cameraController.stopMoving();
            cameraController.moveMode = false;
            cameraController.getCellController().stopHighLightAnimationWithResetWord();
            cameraController.zoomTo(GameCameraController.MIN_ZOOM);
        }
        return super.touchUp(x,y,pointer,button);

    }
}
