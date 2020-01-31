package it.uniroma1.lcl.crucy.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Created by Daniel on 13/05/2017.
 * da completare con aggiunta di util di centramento
 */

public class ScaledLabel extends Label {


    public ScaledLabel (String text, Label.LabelStyle ls, float scaleFactor)
    {
        super(text, ls);
        setFontScale(scaleFactor);
    }

    public float getRealY()
    {
        return getY()-getPrefHeight()/2;
    }

    public float getRealX()
    {
        return getX()-getPrefWidth()/2;
    }




}
