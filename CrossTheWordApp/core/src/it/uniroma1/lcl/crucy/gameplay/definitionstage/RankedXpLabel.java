package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.gameplay.ui.StageUI;
import it.uniroma1.lcl.crucy.gameplay.ui.bonus.MoneyObserver;
import it.uniroma1.lcl.crucy.mainconstants.RankedXP;
import it.uniroma1.lcl.crucy.utils.Loader;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

/**
 * DA RIMETERE OBSERVERS
 */
public class RankedXpLabel extends Image implements MoneyObserver,ScoreObserver {

    private StageUI stage;
    private static final float WIDTH = 320f;
    private static final float HEIGHT = 110f;
    private static final String name = "RankedLab";
    //private TextureRegion tt;
    private Label lvlLabel;
    private float xLab;
    private float yLab;
    private int oldLevel;

    public RankedXpLabel(TextureRegion t, int y, StageUI stage) {
        super(t);

        this.stage = stage;
        setImage();
        setLvl(y);
        this.setScaleX(FileManager.getInstance().getXP() / 10f);


        //this.update(0,0);
    }


    private void setImage() {
        setVisible(true);
        stage.addActor(this);
        //this.setX(50+105);
    }

    private void setLvl(int y) {
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = Loader.getInstance().get("Font/font2.fnt", BitmapFont.class);
        lvlLabel = new Label(FileManager.getInstance().getLevel() + "", titleStyle);
        lvlLabel.setFontScale(0.8f);
        xLab =110f - lvlLabel.getWidth() / 2f * 0.8f;
        yLab = y + 10;

        lvlLabel.setPosition(xLab, yLab);
    }

    public void addLevel() {
        lvlLabel.setVisible(true);
        stage.addActor(lvlLabel);
    }

    @Override
    public void update(int xp, int rampage) {

        System.out.println("LOGranked"   +xp);
        FileManager f= FileManager.getInstance();
        f.increaseXP(xp);

        this.setScaleX((float) f.getXP() / RankedXP.valueOf("L" + f.getLevel()).getXP());
        this.setX(50 + 105 * (1f - this.getScaleX()));


        int currentLvl= f.getLevel();
        if(oldLevel!=currentLvl)
        {
            lvlLabel.setText(currentLvl+"");
        }
        oldLevel=currentLvl;
    }

    public void hide(float toX)
    {
        lvlLabel.addAction(moveTo(toX, lvlLabel.getY(), .5f, Interpolation.swingIn));
    }

    public void resetLvl()
    {
        lvlLabel.addAction(delay(.5f, moveTo(xLab, yLab, .5f, Interpolation.swingOut)));
    }


    @Override
    public void updateMoney(int value) {
        //setText(""+stage.getMoney().getValue());
        //stage.setScale(this);
    }

    public static String name() { return name; }
}
