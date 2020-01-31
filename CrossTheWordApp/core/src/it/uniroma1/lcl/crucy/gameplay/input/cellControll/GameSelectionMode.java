package it.uniroma1.lcl.crucy.gameplay.input.cellControll;

/**
 * Created by antho on 10/09/2016.
 * Enum che la modalità di gioco.
 */
public enum GameSelectionMode {
    LONG_PRESS_SELECTED_MOD("press"),
    TAP_AND_SWIPE_MOD("tap"),
    LONG_PRESS_MOVE_MOD("swipe");

    private String mode;

    GameSelectionMode(String mode) { this.mode = mode; }

    public String getModeName() { return mode; }
}
