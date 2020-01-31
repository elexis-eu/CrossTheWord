package it.uniroma1.lcl.crucy.gameplay.input;

/**
 * Created by antho on 22/08/2016.
 */
public interface SubjectForObserver
{
    void registerObserver(ObserverCellController observer);
    void removeObserver(ObserverCellController observer);
    void observerNotify();
    void observerNotifyTwoCellSelection();
    void observerNotifySwipe();
}
