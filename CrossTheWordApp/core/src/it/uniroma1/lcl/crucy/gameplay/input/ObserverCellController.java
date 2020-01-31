package it.uniroma1.lcl.crucy.gameplay.input;

import it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor;

import java.util.ArrayList;

/**
 * Created by antho on 16/08/2016.
 */
public interface ObserverCellController
{
    void wordSelected(it.uniroma1.lcl.crucy.gameplay.boxes.Word word);
    void cellSelected(ArrayList<BoxActor> actors);
    void swipe();


}
