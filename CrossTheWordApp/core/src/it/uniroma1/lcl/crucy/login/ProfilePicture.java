package it.uniroma1.lcl.crucy.login;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.io.File;

import javax.xml.soap.Text;

import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.login.social.utils.TextureDownload;
import it.uniroma1.lcl.crucy.utils.Loader;

//import static it.uniroma1.lcl.crucy.login.social.utils.Assets.DEFAULT_PIC;

public class ProfilePicture extends Image {

    private boolean downloaded;
    private boolean toAnimate;

    private TextureDownload tImg;
    private Drawable base;

    //DA INTEGRARE E NO DOWNLOAD
    public ProfilePicture(Stage stage) {

        super(new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("social/default_pic2.png", Texture.class))));
        base = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("social/default_pic2.png", Texture.class)));
        this.setPosition(stage.getWidth()/2-this.getWidth()/2, stage.getHeight());
        this.addAction(Actions.moveTo(stage.getWidth()/2-this.getWidth()/2, stage.getHeight()/2+150, 0.7f, Interpolation.swing));
        stage.addActor(this);
        System.out.println(super.getWidth()+ " WIDTHEEEE "+super.getHeight());
       // profileImage= new Image(new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("social/default_pic2.png", Texture.class))));
       // profileImage.setPosition(stage.getWidth()/2-profileImage.getWidth()/2, stage.getHeight());
        //profileImage.addAction(Actions.moveTo(stage.getWidth()/2-profileImage.getWidth()/2, stage.getHeight()/2+150, 0.7f, Interpolation.swing));

        //stage.addActor(profileImage);

        tImg = new TextureDownload(stage.getBatch(), FileManager.getInstance().getPic(),this);
    }


    public void fbLoggedIn()
    {
        //System.out.println(downloaded + "   DOWNLOADEDZ");
        if(!downloaded)
        {
            //System.out.println(downloaded + "   DOWNLOADEDZ222222");
            tImg.downloadImage();
            downloaded = true;
        }

    }

/*
    public void log()
    {
        tImg.setID(FileManager.getInstance().getPic());
        tImg.downloadImage();
    }*/

    public void setPicture(TextureRegion t)
    {

        System.out.println(super.getWidth()+ " OLDEE "+super.getHeight());
        System.out.println(t.getTexture().getWidth()+ " newee "+t.getTexture().getHeight());
        this.addAction(Actions.fadeOut(0.3f,Interpolation.swingOut));
        this.setDrawable(new Image(t).getDrawable());
        this.addAction(Actions.sequence(Actions.fadeIn(0.3f,Interpolation.swingIn), Actions.moveBy(0,-100f,1f)));
        //profileImage = new Image(new TextureRegionDrawable(t));
        toAnimate = true;
    }

    public void reset()
    {
        this.addAction(Actions.fadeOut(0.3f,Interpolation.swingOut));
        this.setDrawable(base);
        this.addAction(Actions.sequence(Actions.fadeIn(0.3f,Interpolation.swingIn)));
    }
    public boolean toAnimate()
    {
        if(!toAnimate) return toAnimate;
        else
        {
            toAnimate = false;
            return true;
        }
    }

    @Override
    public float getX()  { return super.getX(); }

    @Override
    public float getY()  { return super.getY(); }

    @Override
    public float getWidth()  { return super.getWidth(); }

    @Override
    public float getHeight()  { return super.getHeight(); }
/*
    @Override
    public void draw(Batch batch, float parentAlpha) {
       // setSize(label.getHeight(), label.getWidth());
       // setPosition(stage.getWidth() / 2f - this.getWidth() / 2f,
       //         stage.getHeight() / 2f - this.getHeight() / 2f + 400);
     //   super.draw(batch, parentAlpha);
    }

    public void setPicture(Texture picture) {

        if (!picture.getTextureData().isPrepared()) picture.getTextureData().prepare();
        Pixmap pixmap = picture.getTextureData().consumePixmap();

        Pixmap res = TextureConverter.toPixmap(Loader.getInstance().get("social/profile_picture_mask.png",Texture.class));
        Pixmap resized = resizePixmap(pixmap);
        pixmapMask(resized, mask, res, false);
        Sprite pictureSprite = new Sprite(mergePixmap(res));
        this.setDrawable(new SpriteDrawable(pictureSprite));
    }

    private Texture mergePixmap(Pixmap pixmap) {
        return new Texture(resizePixmap(pixmap));
    }

    private Pixmap resizePixmap(Pixmap pixmap) {
        Pixmap pixmap2 = new Pixmap(label.getHeight(), label.getWidth(), pixmap.getFormat());
        pixmap2.drawPixmap(pixmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), 0, 0, pixmap2.getWidth(), pixmap2.getHeight());
        pixmap2.drawPixmap(TextureConverter.toPixmap(Loader.getInstance().get("social/profile_picture_label.png",Texture.class)), 0, 0);

        pixmap.dispose();
        return pixmap2;
    }

    /**
     * Permette di applicare una maschera ad una pixmap
     *
     * @param pixmap          Pixmap da modificare
     * @param mask            Maschera per la modifica
     * @param result          Pixmap modificata
     * @param invertMaskAlpha boolean per invertire il taglio della pixmap
     */ /*
    private void pixmapMask(Pixmap pixmap, Pixmap mask, Pixmap result, boolean invertMaskAlpha) {
        int pixmapWidth = pixmap.getWidth();
        int pixmapHeight = pixmap.getHeight();
        Color pixelColor = new Color();
        Color maskPixelColor = new Color();

        Pixmap.Blending blending = Pixmap.getBlending();
        Pixmap.setBlending(Pixmap.Blending.None);
        for (int x = 0; x < pixmapWidth; x++) {
            for (int y = 0; y < pixmapHeight; y++) {
                // get pixel color
                Color.rgba8888ToColor(pixelColor, pixmap.getPixel(x, y));
                // get mask color
                Color.rgba8888ToColor(maskPixelColor, mask.getPixel(x, y));

                maskPixelColor.a = (invertMaskAlpha) ? 1.0f - maskPixelColor.a : maskPixelColor.a;    // IF invert mask
                pixelColor.a = pixelColor.a * maskPixelColor.a;                                       // multiply pixel alpha * mask alpha
                result.setColor(pixelColor);
                result.drawPixel(x, y);
            }
        }
        Pixmap.setBlending(blending);
    }*/
}
