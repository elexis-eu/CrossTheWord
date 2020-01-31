package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.LongPressSelected;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;

/**
 * Created by antho on 11/09/2016.
 */
public class LongPressSelectedCell extends CellController {

    /**
     * @param screenStage Stage screenStage
     */
    public LongPressSelectedCell(Stage screenStage)
    {
        super(screenStage);
    }


    @Override
    public boolean longPress(float x, float y)
    {
        // se non siamo in modalità long press mod

        Actor hitActor = getActorsHitFromScreenCoords(x,y);

        if (!(hitActor instanceof BoxActor))
            return false;
        BoxActor casella = (BoxActor) hitActor;
        stopHighLightAnimationWithResetWord();
        if (casella.isBlock())
        {
            //stopHighLightAnimationWithResetWord();
            return false;
        }

        if(balloon) //se è presente la nuvoletta del tutorial
        {
            screen.getStageUI().hideBaloon();      //chiamo il metodo di sparizione della nuvoletta
            balloon = false;        //setto a false balloon
        }

        activeAnimationHighlight(casella);

        return false;
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

        if (!(actorHit instanceof BoxActor) && selectedActors.size()>= 2 ) {
            super.pan(x,y,deltaX,deltaY);
            return true;
        };
        // Se non è una casellaActor non faccio nulla
        if (!(actorHit instanceof BoxActor) ) {

            super.pan(x,y,deltaX,deltaY);
            return false;
        };



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
        return retThis;

    }

    @Override
    public boolean tap(float x, float y, int count, int button)
    {
        return false;
    }

}
