package it.uniroma1.lcl.crucy.gameplay.audio;

import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;

import it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor;
import it.uniroma1.lcl.crucy.MainCrucy;

/**
 * Created by Taraz on 12/09/2016.
 */
public class SelectedAudio implements it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController
{

    private Sound selected;
    private MainCrucy main;

    public SelectedAudio(MainCrucy main) {
        this.main = main;
        //selected = this.main.playSound(SoundFx.SELECT);

    }

    @Override
    public void wordSelected(it.uniroma1.lcl.crucy.gameplay.boxes.Word word) {
        //if (main.getSound()) selected.play();
    }

    @Override
    public void cellSelected(ArrayList<BoxActor> actors) {

    }

    @Override
    public void swipe()
    {

    }
}
