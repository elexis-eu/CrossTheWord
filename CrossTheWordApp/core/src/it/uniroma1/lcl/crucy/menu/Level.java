package it.uniroma1.lcl.crucy.menu;


import it.uniroma1.lcl.crucy.gameplay.definitionstage.RankedXpLabel;
import it.uniroma1.lcl.crucy.mainconstants.RankedXP;
import it.uniroma1.lcl.crucy.utils.LevelStructure;

public  class Level {
    public static final int MAX = 50;
    public static final int MIN = 1;

    public static boolean levelUp(int level, int experience) {
        return experience>= RankedXP.valueOf("L"+level).getXP();
    }


}
