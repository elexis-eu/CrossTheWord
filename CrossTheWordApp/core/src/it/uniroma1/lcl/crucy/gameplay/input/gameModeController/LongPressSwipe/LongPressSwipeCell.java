package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.LongPressSwipe;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;

/**
 * Created by antho on 11/09/2016.
 */
public class LongPressSwipeCell extends CellController{
    /**
     * @param screenStage Stage screenStage
     */
    public LongPressSwipeCell(Stage screenStage)
    {
        super(screenStage);
    }


    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY)
    {
        if (!stateSwipe)
            return false;

        //Fermo solo la visibilità dell'animazione ma la parola rimane selezionata.
        stopHighlightAnimation();

        Actor actorHit = getActorsHitFromScreenCoords(x,y);

        boolean retThis = false;
        // Se il numero di caselle selezionate è maggiore di 2 ritornerò true alla fine del comando "togliendo il movimento alla classe
        // GameCameController"
        if (selectedActors.size() >= 2)retThis = true;

        // effettuo la notifica che sono state selezionate due caselle.
        // in modo tale che se ci troviamo in modalità inserimento e stiamo selezionando un altra parola l'animazione della vecchia
        // parola viene eliminata.
        if ( selectedActors.size() == 2 && !isCallTwoCellSelection)
        {
            observerNotifyTwoCellSelection();
            isCallTwoCellSelection = true;
        }

        if (!(actorHit instanceof BoxActor) && selectedActors.size()>= 2 ) return true;
        // Se non è una casellaActor non faccio nulla
        if (!(actorHit instanceof BoxActor) ) return false;

        //cast to BoxActor
        BoxActor cellHit = (BoxActor) actorHit;

        /*
                  LONG_PRESS_MOVE_MOD
         */
        // controllo se siamo in modlaità LONG_PRESS MOVE MODE ed escludo solamente se la casella block.
        if (!cellHit.isBlock())
        {
            controlSelectCell(cellHit); // effettua i controlli per trovare la parola.
            // se ci sono abbastanza celle selezionate.
            stopHighLightAnimationWithResetWord();
        }

        super.pan(x,y,deltaX,deltaY);
        return retThis;

    }




}
