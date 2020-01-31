package it.uniroma1.lcl.crucy.gameplay.input.gameModeController;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import it.uniroma1.lcl.crucy.gameplay.input.cameraController.GameCameraController;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;

/**
 * Created by antho on 11/09/2016.
 * Classe astratta che va estesa , questa classe deve essere il generatore della modalità di gioco
 * deve creare infatti il cell controller e il camController, in modo tale da eliminare tutte le dipendenze e unificare
 * la modalità con un unica classe.
 */
public abstract class GameModeBuilder
{
    protected CellController cellController;
    protected GameCameraController camController;

    protected Stage screenStage;
    protected OrthographicCamera camera;

    public GameModeBuilder(Stage screenStage, OrthographicCamera camera)
    {
        this.screenStage = screenStage;
        this.camera = camera;
        build();
    }

    /**
     * Questo metodo deve creare e dare i valori alle istanze CellController e al camController
     */
    protected abstract void build();


    public void registerObserverToCellController(it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController... registers)
    {
        for (it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController ob : registers)
            cellController.registerObserver(ob);
    }


    public CellController getCellController()
    {
        return cellController;
    }

    public GameCameraController getCamController()
    {
        return camController;
    }

    /**
     *
     * @return Un array con le GestureDetector sia del camController che del cell controller.
     */
    public abstract GestureDetector getGestureCellController();
    public abstract GestureDetector getGestureCamController();

}
