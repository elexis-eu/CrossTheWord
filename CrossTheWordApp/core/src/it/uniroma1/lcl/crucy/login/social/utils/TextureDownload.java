package it.uniroma1.lcl.crucy.login.social.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.StreamUtils;

import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.login.ProfilePicture;

//DA INTEGRARE CON PROFILEPICTURE, DOWNLOAD TO CACHE

public class TextureDownload {
    private TextureRegion image;

    private ProfilePicture reference;


    public TextureDownload( Batch batch, String ID,  ProfilePicture reference) {
        this.reference = reference;
    }


    public void downloadImage() {
        if (image == null)
            new Thread(new Runnable() {
                /**
                 * Downloads the content of the specified url to the array. The
                 * array has to be big enough.
                 */
                private int download(byte[] out, String url) {
                    InputStream in = null;
                    try {
                        HttpURLConnection conn = null;
                        conn = (HttpURLConnection) new URL(url) .openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(false);
                        conn.setUseCaches(true);
                        conn.connect();
                        in = conn.getInputStream();
                        int readBytes = 0;
                        while (true) {
                            int length = in.read(out, readBytes, out.length
                                    - readBytes);
                            if (length == -1)
                                break;
                            readBytes += length;
                        }
                        return readBytes;
                    } catch (Exception ex) {
                        return 0;
                    } finally {
                        StreamUtils.closeQuietly(in);
                    }
                }

                @Override
                public void run() {
                    byte[] bytes = new byte[200 * 1024]; //non oltre 200kb

                    //int numBytes = download(bytes, "https://graph.facebook.com/" + FBid + "/picture?type=square");
                    String id = FileManager.getInstance().getPic();
                    int numBytes = download(bytes, id.substring(0,id.lastIndexOf("400")));
                    if (numBytes != 0) {
                        // load the pixmap, make it a power of two if necessary (not needed for GL ES 2.0!)
                        Pixmap pixmap = new Pixmap(bytes, 0, numBytes);
                        final int originalWidth = pixmap.getWidth();
                        final int originalHeight = pixmap.getHeight();
                        int width = MathUtils.nextPowerOfTwo(pixmap.getWidth());
                        int height = MathUtils.nextPowerOfTwo(pixmap
                                .getHeight());
                        final Pixmap potPixmap = new Pixmap(width, height,
                                pixmap.getFormat());
                        potPixmap.drawPixmap(pixmap, 0, 0, 0, 0,
                                pixmap.getWidth(), pixmap.getHeight());
                        pixmap.dispose();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(originalHeight +  " sadasdasd "+ originalWidth);
                                image = new TextureRegion(
                                        new Texture(potPixmap), 0, 0,
                                        originalWidth, originalHeight);

                                reference.setPicture(image); //autoset su profile picture
                            }
                        });
                    }
                }
            }).start();

    }
}