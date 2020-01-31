package it.uniroma1.lcl.crucy.gameplay.input.gameModeController.LongPressSelected;

import com.badlogic.gdx.input.GestureDetector;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;

/**
 * Created by antho on 11/09/2016.
 * Classe di cui ha bisogno il GameModeBuilder per lavorare sulle specifiche degli input
 */
public class LongPressSelectedDetector extends GestureDetector {

    private CellController cellController;

    public LongPressSelectedDetector(CellController cellController)
    {
        super(cellController);
        this.cellController = cellController;
        this.setLongPressSeconds(0.3f);
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button)
    {
        cellController.stopHighLightAnimationWithResetWord();
        super.touchUp(x,y,pointer,button);
        return false;
    }
}
