package it.uniroma1.lcl.crucy.cache.utils;

import com.badlogic.gdx.graphics.Color;

public enum Difficult
{
    //EASY(4, new Color(0.00719424460431654676258992805755f, 0.00518134715025906735751295336788f, 0.00869565217391304347826086956522f,0.5f)),
    EASY(2, new Color(139/255f, 193/255f, 115/255f,1f)),
    MEDIUM(4, new Color(240/255f,215/255f,77/255f,1f)),
    HARD(8, new Color(200/255f,80/255f,80/255f,1f));


    private Difficult(int xp, Color c)
    {
        this.xp = xp;
        this.c = c;
    }

    private int xp;
    private Color c;

    public int getXp() { return xp; }
    public Color getColor() { return c; }

}
