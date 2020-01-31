package it.uniroma1.lcl.crucy.menu;

/**
 * Created by Daniel on 23/02/2018.
 */

public class Tmp {

    private static int lvl;
    private static Tmp instance;

    public static Tmp getInstance()
    {
        if (instance == null) { instance = new Tmp(); }
        return instance;
    }

    public  void setLVL(int x) { lvl = x;}

    public  int getLVL() { return lvl; }


}
