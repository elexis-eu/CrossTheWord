package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.LongPressSwipe;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import it.uniroma1.lcl.crucy.gameplay.input.gameModeController.GameModeBuilder;

/**
 * Created by antho on 11/09/2016.
 * Classe di cui ha bisogno il GameModeBuilder per lavorare sulle specifiche degli input
 */
public class LongPressSwipeBuilder extends GameModeBuilder {

    public LongPressSwipeBuilder(Stage screenStage, OrthographicCamera camera)
    {
        super(screenStage, camera);
    }


    @Override
    protected void build()
    {
        super.cellController = new LongPressSwipeCell(screenStage);
        super.camController = new it.uniroma1.lcl.crucy.gameplay.input.gameModeController.LongPressSwipe.LongPressSwipeCamera((OrthographicCamera) screenStage.getCamera(),cellController);
    }

    public GestureDetector getGestureCellController()
    {
        return new GestureDetector(cellController);
    }
    public GestureDetector getGestureCamController()
    {
        return new LongPressSwipeDetector((it.uniroma1.lcl.crucy.gameplay.input.gameModeController.LongPressSwipe.LongPressSwipeCamera) camController);
    }
}
