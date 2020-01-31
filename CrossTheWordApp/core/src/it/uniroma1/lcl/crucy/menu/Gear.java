package it.uniroma1.lcl.crucy.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.utils.Loader;

/* Attore gear

 */
public class Gear extends Image
{
    //Integrare con Classe Level
    private ScaledLabel[] numberLabel=new ScaledLabel[60];
    private Image shadow;
    private Image lock;
    private Stage stage;
    private int rotationCounter;
    private int currentVisible;
    private ExperienceBar bar;

    public Gear(Stage stage)
    {
        super(new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("MainMenu/gear.png", Texture.class))));
        this.stage=stage;
        this.setPosition(stage.getWidth()/2-this.getWidth()/2,stage.getHeight()/2-this.getHeight()/2);
        this.setOrigin(this.getWidth()/2, this.getHeight()/2);
        stage.addActor(this);


        Label.LabelStyle numstyle=new Label.LabelStyle();
        numstyle.font=Loader.getInstance().get("Font/numFont.fnt",BitmapFont.class);;

        for(int i=0; i<Level.MAX; i++)
        {
            numberLabel[i] = new ScaledLabel((i+1) + "", numstyle, 2.5f);
            numberLabel[i].setFontScale(3);
            numberLabel[i].setPosition(this.getCenterX() - numberLabel[i].getPrefWidth() / 2 +10, this.getCenterY() - numberLabel[i].getPrefHeight() / 2 + 70);
            numberLabel[i].setVisible(false);
            stage.addActor(numberLabel[i]);

        }

        //aggiungo l'ombreggiatura in caso di livello non sbloccato
        shadow = new Image(Loader.getInstance().get("MainMenu/shadow_lvl_gear.png", Texture.class));
        shadow.setPosition(stage.getWidth()/2-shadow.getWidth()/2,stage.getHeight()/2-shadow.getHeight()/2);
        shadow.setZIndex(200);
        shadow.setTouchable(Touchable.disabled);
        shadow.setVisible(false);

        stage.addActor(shadow);

        lock = new Image(Loader.getInstance().get("MainMenu/lock.png", Texture.class));
        lock.setPosition(stage.getWidth()/2-lock.getWidth()/2,stage.getHeight()/2-lock.getHeight()/2);
        lock.setZIndex(250);
        lock.setTouchable(Touchable.disabled);
        lock.addAction(Actions.parallel(
                Actions.scaleTo(0,0),
                Actions.moveTo(stage.getWidth()/2,stage.getHeight()/2)));
        stage.addActor(lock);


        numberLabel[FileManager.getInstance().getLevel()-1].setVisible(true);
        currentVisible = FileManager.getInstance().getLevel()-1;

        addListeners();

    }

    public void setBar(ExperienceBar bar) { this.bar=bar; }

    private void addListeners() {
        this.addListener(new DragListener() {
            float initialrot;
            float oldrot=0;
            public void dragStart(InputEvent event, float x, float y, int pointer)
            {
                initialrot= MathUtils.radiansToDegrees * MathUtils.atan2(event.getStageY() - Gear.this.getCenterY(), event.getStageX() - Gear.this.getCenterX())-90-(Gear.this.getRotation()%360);
            }

            public void drag(InputEvent event, float x, float y, int pointer)
            {
                float rot= MathUtils.radiansToDegrees * MathUtils.atan2(event.getStageY() - Gear.this.getCenterY(), event.getStageX() - Gear.this.getCenterX())-90;

                rot-=initialrot;
                Gear.this.setRotation(rot);
                rotate(oldrot-Gear.this.getRotation());
                oldrot=Gear.this.getRotation();
            }

        });
    }

    public int getLevel()
    {
        return currentVisible+1;
    }


    public void putOnTop(int max)
    {
        this.setZIndex(max+1);
        for(ScaledLabel x:numberLabel) if(x!=null) x.setZIndex(max+1);
    }

    public void rotate(float deltaRotation)
    {
        rotationCounter+=1;
        if(rotationCounter%7 == 0)
        {
            if(deltaRotation>0) increase();
            else decrease();
        }
    }

    public void increase()
    {
        //recalculate();

        if (Level.MAX > currentVisible+1)
        {
            numberLabel[currentVisible].setVisible(false);
            numberLabel[++currentVisible].setVisible(true);

            if(currentVisible+1>FileManager.getInstance().getLevel()-1)
            {
                shadow.setVisible(true);
                lock.addAction(Actions.sequence(
                        //Actions.moveTo(stage.getWidth()/2,stage.getHeight()/2),
                        Actions.parallel(
                                Actions.scaleTo(1,1,0.15f),
                                Actions.moveTo(stage.getWidth()/2-lock.getWidth()/2,stage.getHeight()/2-lock.getHeight()/2,0.15f))));
            }
        }
    }

    public boolean levelAllowed() { return !shadow.isVisible(); }

    public void decrease()
    {
       // recalculate();
        if (Level.MIN < currentVisible+1)
        {
            numberLabel[currentVisible].setVisible(false);
            numberLabel[--currentVisible].setVisible(true);

            if(currentVisible+1<= FileManager.getInstance().getLevel())
            {
                shadow.setVisible(false);
                lock.addAction(Actions.parallel(
                            Actions.scaleTo(0,0,0.15f),
                            Actions.moveTo(stage.getWidth()/2,stage.getHeight()/2,0.15f)));
                //lock.setVisible(false);
            }
        }
    }

    public float getCenterX() {
        return getX() + getOriginX();
    }

    public float getCenterY() {
        return getY() + getOriginY();
    }

    //public void recalculate()
   // {
    //    bar.recalculate();
    //}

}

