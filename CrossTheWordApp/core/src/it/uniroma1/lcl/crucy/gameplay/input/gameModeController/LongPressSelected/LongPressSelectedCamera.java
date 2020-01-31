package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.LongPressSelected;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController;
import it.uniroma1.lcl.crucy.gameplay.input.cameraController.GameCameraController;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;

import java.util.ArrayList;

/**
 * Created by antho on 11/09/2016.
 */
public class LongPressSelectedCamera extends GameCameraController {

    public LongPressSelectedCamera(OrthographicCamera camera, CellController cellController)
    {
        super(camera, cellController);
    }

    public static ArrayList<GestureDetector> buildGameMode(Stage screenStage, OrthographicCamera camera, ObserverCellController... registers)
    {
        ArrayList<GestureDetector> gameControllers = new ArrayList<GestureDetector>();
        LongPressSelectedCell cellController = new LongPressSelectedCell(screenStage);

        // Registro tutti gli Observer
        for (ObserverCellController ob : registers)
            cellController.registerObserver(ob);

        gameControllers.add(new LongPressSelectedDetector(cellController));
        gameControllers.add(new GestureDetector(new LongPressSelectedCamera(camera, cellController)));
        return gameControllers;
    }

}
