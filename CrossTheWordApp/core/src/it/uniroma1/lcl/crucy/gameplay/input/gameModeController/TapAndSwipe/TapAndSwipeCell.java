package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.TapAndSwipe;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;

/**
 * Created by antho on 11/09/2016.
 */
public class TapAndSwipeCell extends CellController {

    /**
     * @param screenStage Stage screenStage
     */
    public TapAndSwipeCell(Stage screenStage)
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


        // effettuo la notifica che sono state selezionate due caselle.
        // in modo tale che se ci troviamo in modalità inserimento e stiamo selezionando un altra parola l'animazione della vecchia
        // parola viene eliminata.
        if ( selectedActors.size() == 2 && !isCallTwoCellSelection)
        {
            observerNotifyTwoCellSelection();
            isCallTwoCellSelection = true;
        }

        if (!(actorHit instanceof BoxActor) && !selectedActors.isEmpty())
        {
            super.pan(x,y,deltaX,deltaY);
            return true;
        }

        if (!(actorHit instanceof BoxActor) )
        {
            super.pan(x,y,deltaX,deltaY);
            return false;
        }

        //cast to BoxActor
        BoxActor cellHit = (BoxActor) actorHit;

        /*
             LONG_PRESS_SELECTED_MOD o TAP_AND_SWIPE_MOD
         */
        // escludo dalla selezione blocchi
        // escludo dalla selezione se ci sono parole già inserite
        // escludo dalla selezione se la parola non è uguale alla parola selezionata dalla animazione
        if ( !cellHit.isBlock()
                && ((cellHit.getWordHorizontal() != null && !cellHit.getWordHorizontal().isParolaInserita())
                || (cellHit.getWordVertical() != null && !cellHit.getWordVertical().isParolaInserita()))
                && ((cellHit.getWordHorizontal() != null &&cellHit.getWordHorizontal().equals(wordOrizz) )
                || (cellHit.getWordVertical() != null &&cellHit.getWordVertical().equals(wordVert))))
        {
            controlSelectCell(cellHit);
            return true;
        }
        super.pan(x,y,deltaX,deltaY);
        stopHighLightAnimationWithResetWord();
        return !selectedActors.isEmpty();

    }




}
