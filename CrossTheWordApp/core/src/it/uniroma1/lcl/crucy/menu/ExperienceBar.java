package it.uniroma1.lcl.crucy.menu;

import com.badlogic.gdx.graphics.Texture;

import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.mainconstants.RankedXP;
import it.uniroma1.lcl.crucy.utils.Loader;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;


public class ExperienceBar extends Image
{
    private Stage stage;

    private Image xpBase;
    private Texture xpEasyRegion;
    private Texture xpMediumRegion;
    private Texture xpHardRegion;
    private Texture xpTopRegion;

    boolean isTouched;
    private Texture indicatorXP;

    private int wx;
    private final int leftOff=155;
    private final int rightOff=115;
    private float y;
    private Gear gear;
    private int xp;
    private boolean easy, medium, hard;

    public ExperienceBar(Stage stage, int level, float y, Gear gear)
    {
        this.stage = stage;
        this.y = y;
        this.gear=gear;
        xp = FileManager.getInstance().getXP();
        create();
    }


    public void create() {
        xpBase = new Image(Loader.getInstance().get("MainMenu/base2_exp.png", Texture.class));
        xpBase.setPosition(stage.getWidth() / 2 - xpBase.getWidth() / 2, y - xpBase.getHeight() - 20);
        xpHardRegion = Loader.getInstance().get("MainMenu/top_exp_red.png", Texture.class);
        xpMediumRegion = Loader.getInstance().get("MainMenu/top_exp_yellow.png", Texture.class);
        xpEasyRegion = Loader.getInstance().get("MainMenu/top_exp_green.png", Texture.class);
        xpTopRegion = Loader.getInstance().get("MainMenu/top2_exp.png", Texture.class);

        wx = getCurrentBarWidth(xpTopRegion.getWidth());
        indicatorXP = Loader.getInstance().get("MainMenu/indicator_exp.png", Texture.class);
    }

    public void addListeners(ActorGestureListener l) {
       xpBase.addListener(l);
    }

    public void touched() {
        isTouched = !isTouched;
    }

    public void addActors(){
        stage.addActor(xpBase);
    }

    private int getCurrentBarWidth(int width) {
        int xp = FileManager.getInstance().getXP();
        return (int)((width-leftOff-rightOff) * (float) xp / RankedXP.valueOf("L"+FileManager.getInstance().getLevel()).getXP());
    }

    public void update(int n)
    {
       // level = n;
        //System.out.println( " Updato con j  "+n);

        //System.out.println(xpEasyRegion.getRegionWidth());
        //xpEasyRegion.setRegion(xpTop.getWidth()+leftOff+n*10); /// QUI, X NOn deve essere uguale a 0 YO

       // xpEasyRegion.setRegion((int)(0),(int)(0), (int)(leftOff+xpTop.getWidth()+n*10) , (int)(xpEasy.getHeight())); /// QUI, X NOn deve essere uguale a 0 YO
       // System.out.println(xpEasyRegion.getRegionWidth());


        // xpEasy.setPosition(xpEasy.getX()-leftOff,xpEasy.getY());
        //xpEasy.setScaleX(n*0.04f);
        //xpMedium.setScaleX(n*0.04f);
        //xpHard.setScaleX(n*0.04f);

        //System.out.println(xpEasy.getScaleX() +  "  "+xpMedium.getScaleX()  +  "  "+xpHard.getScaleX());
    }

    public Image getImg()
    {
        return xpBase;
    }

    public void render(int lvl) {
        stage.getBatch().begin();
        int max = FileManager.getInstance().getLevel();
        if(lvl<= max)
        {
            int rem = xpEasyRegion.getWidth()-rightOff-leftOff-wx;

            System.out.println(xpEasyRegion.getWidth() +  "  "+ xpMediumRegion.getWidth()+  " "+ xpHardRegion.getWidth());
            //stage.getBatch().draw(xpEasyRegion,  xpBase.getX(), xpBase.getY(), 0,0, wx+leftOff +rem*lvl/max/3,xpEasyRegion.getHeight());
            //stage.getBatch().draw(xpMediumRegion, xpBase.getX(), xpBase.getY(), 0 ,0,wx+leftOff+rem*lvl/max/2,xpMediumRegion.getHeight());
           // stage.getBatch().draw(xpHardRegion, xpBase.getX(), xpBase.getY(), 0 ,0,(int)(wx+leftOff+rem*lvl/max/1.5f),xpHardRegion.getHeight());
            if(easy) stage.getBatch().draw(xpEasyRegion,  xpBase.getX(), xpBase.getY(), 0,0, wx+leftOff +rem/9+rem/10*9*lvl/max/4,xpEasyRegion.getHeight());
            if(medium) stage.getBatch().draw(xpMediumRegion, xpBase.getX(), xpBase.getY(), 0 ,0,wx+leftOff+rem/7+rem/10*9*lvl/max/3,xpMediumRegion.getHeight());
            if(hard) stage.getBatch().draw(xpHardRegion, xpBase.getX(), xpBase.getY(), 0 ,0,wx+leftOff+rem/5+rem/10*9*lvl/max/2,xpHardRegion.getHeight());
        }
        stage.getBatch().draw(xpTopRegion,xpBase.getX(), xpBase.getY(), 0 ,0,wx+leftOff,xpHardRegion.getHeight());



        if(isTouched)
        {
            stage.getBatch().draw(indicatorXP,  xpBase.getX(), xpBase.getY(), indicatorXP.getWidth(),indicatorXP.getHeight());
            BitmapFont font = Loader.getInstance().get("Font/font3.fnt",BitmapFont.class); ///
            font.getData().setScale(0.8f);

            String toWrite = FileManager.getInstance().getXP()+"/"+ RankedXP.valueOf("L"+FileManager.getInstance().getLevel()).getXP();
            GlyphLayout glyph = new GlyphLayout(font, toWrite);
            font.draw(stage.getBatch(), glyph,xpBase.getX()+leftOff-rightOff + xpBase.getWidth()/2 -glyph.width/2, xpBase.getY()+xpBase.getHeight()-15);
        }
        stage.getBatch().end();
    }

}
