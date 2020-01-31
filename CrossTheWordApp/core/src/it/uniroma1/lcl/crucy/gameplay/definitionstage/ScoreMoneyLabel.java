package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.gameplay.ui.StageUI;
import it.uniroma1.lcl.crucy.gameplay.ui.bonus.MoneyObserver;
import it.uniroma1.lcl.crucy.utils.GameType;

/**
 * Created by Marzia Riso on 29/09/2016.
 *
 * Classe che gestisce lo ScoreLabel e il MoneyLabel.
 * Implementa il MoneyObserver e lo ScoreObserver in modo da
 * aggiornare automaticamente score e money.
 */
public class ScoreMoneyLabel extends Label implements MoneyObserver,ScoreObserver
{

    private StageUI stage;
    private static final float WIDTH=320f;
    private static final float HEIGHT=110f;
    private String name;

    //private GameType gt; METTERE UNA VOLTA SEPARATI GLI STAGE

    /**
     * Costruisce un label scrivendo sopra un value(che può essere score o money)
     * @param font con cui viene scritto il value
     * @param value valore iniziare che deve essere scritto
     * @param stage stage su cui deve essere aggiunto
     */
    public ScoreMoneyLabel(BitmapFont font, int value, StageUI stage, String name) // , GameType gt)
    {
        super(value+"",new LabelStyle(font,font.getColor()));
        this.stage=stage;
        this.name = name;
        setLabel();
    }

    /**
     * Metodo privato che imposta tutte le caratteristiche del label e lo aggiunge allo stage
     */
    private void setLabel()
    {
        setWrap(true);
        setAlignment(Align.center);
        setVisible(true);
        setSize(WIDTH,HEIGHT);
        stage.addActor(this);
    }


    @Override
    public void update(int x, int rampage) {
        setText(""+x);
        stage.setScale(this);


    }

    @Override
    public void updateMoney(int value) {
        /*
        System.out.println("STAGE MONEY "+stage.getMoney().getValue());
        //setText(""+stage.getMoney().getValue());
        stage.setScale(this);
        */
    }

    public String getName() { return name; }
}
