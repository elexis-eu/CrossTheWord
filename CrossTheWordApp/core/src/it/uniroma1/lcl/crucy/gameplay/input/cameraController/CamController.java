package it.uniroma1.lcl.crucy.gameplay.input.cameraController;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import it.uniroma1.lcl.crucy.Animation;

/**
 * Created by antho on 24/08/2016.
 * Camera Controller gestice i movimenti della camera con dei metodi.
 * Gestisce il limite di movimento della camera e lo zoom.
 */
public class CamController implements Animation
{
    protected OrthographicCamera camera;

    protected static final float MOVE_VELOCITY = 5;
    protected static final int INTORNO_CLICK_PRECISION = 50;
    protected static final float INTORNO_ZOOM_PRECISION = 0.1f;
    protected static final float ZOOM_VELOCITY = 3;
    // Più grande è il valore e più accurato sarà il posizionamento vicino al punto di movimento.

    private boolean moveLimit;

    private Vector2 topLeftLimit;
    private Vector2 bottomRightLimit;


    private boolean zoomTo;
    private float zoomPosition;

    private boolean move;

    private Vector3 clickCoords;


    public CamController(OrthographicCamera camera)
    {
        this.camera = camera;
    }

    /**
     * Muove la camera con animazione nella posizione del mondo indicata delle screen Coords.
     * @param screenX
     * @param screenY
     */
    public void moveCameraTo(float screenX, float screenY)
    {
        clickCoords = camera.unproject(new Vector3(screenX,screenY,0));
        move = true;
    }

    /**
     * Muove la camera con animazione nella posizione del mondo indicata (coordinate del mondo).
     * @param worldX
     * @param worldY
     */
    public void moveCameraToWorld(float worldX, float worldY)
    {
        clickCoords = new Vector3(worldX,worldY,0);
        move = true;
    }

    /**
     * Effettua lo zoom della camera fino allo zoom specificato.
     * @param zoom
     */
    public void zoomTo(float zoom)
    {
        this.zoomPosition = zoom;
        this.zoomTo = true;
    }

    /**
     * Attiva il limite della camera e setta le coordinate di limite.
     * @param topLeftLimit
     * @param bottomRightLimit
     */
    public void setLimitAndActive( Vector2 topLeftLimit, Vector2 bottomRightLimit)
    {
        this.topLeftLimit = topLeftLimit;
        this.bottomRightLimit = bottomRightLimit;
        this.moveLimit = true;
    }

    /**
     * disabilità il limite.
     */
    public void disableMoveLimit()
    {
        this.moveLimit = false;
    }


    /**
     * Aggiorna le animazioni
     */
    @Override
    public void animationUpdate()
    {
        cameraTapMoveAnimation();
        updateZoom();
    }

    protected void controlLimit()
    {

        if (moveLimit)
        {
            //Se la camera è al di fuori del rettangolo mi sposto per rientrarci.
            if (camera.position.x < topLeftLimit.x && camera.position.y > topLeftLimit.y )
                moveCameraToWorld(topLeftLimit.x + Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION,
                        topLeftLimit.y - Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION);
            else if (camera.position.x < topLeftLimit.x && camera.position.y < bottomRightLimit.y )
                moveCameraToWorld(topLeftLimit.x + Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION,
                        bottomRightLimit.y + Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION);
            else if (camera.position.x > bottomRightLimit.x && camera.position.y < bottomRightLimit.y)
                moveCameraToWorld(bottomRightLimit.x - Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION,
                        bottomRightLimit.y + Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION);
            else if (camera.position.x > bottomRightLimit.x && camera.position.y > topLeftLimit.y)
                moveCameraToWorld(bottomRightLimit.x - Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION,topLeftLimit.y - Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION);



            else if (camera.position.x < topLeftLimit.x)
                moveCameraToWorld(topLeftLimit.x + Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION, camera.position.y);
            else if (camera.position.x > bottomRightLimit.x)
                moveCameraToWorld(bottomRightLimit.x - Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION, camera.position.y);
            else if (camera.position.y > topLeftLimit.y)
                moveCameraToWorld(camera.position.x, topLeftLimit.y - Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION);
            else if (camera.position.y < bottomRightLimit.y)
                moveCameraToWorld(camera.position.x, bottomRightLimit.y + Gdx.graphics.getHeight() / INTORNO_CLICK_PRECISION);
        }

    }

    /**
     * Muove la telecamera nella posizione del tap
     */
    protected void cameraTapMoveAnimation()
    {
        // se ci troviamo in un intorno del punto e siamo in movimento fermo il movimento.
        if ((move && (camera.position.x < clickCoords.x + Gdx.graphics.getWidth()/INTORNO_CLICK_PRECISION
                && camera.position.x > clickCoords.x - Gdx.graphics.getWidth()/INTORNO_CLICK_PRECISION
                && camera.position.y < clickCoords.y + Gdx.graphics.getHeight()/INTORNO_CLICK_PRECISION
                && camera.position.y > clickCoords.y - Gdx.graphics.getHeight()/INTORNO_CLICK_PRECISION) ))
            stopMoving();

        controlLimit();

        // Se la variabile move è  a true dobbiamo muovere la telecamera nella posizione del tap
        if (move)
        {
            double distanceX = camera.position.x - clickCoords.x;
            double distanceY = camera.position.y - clickCoords.y;
            camera.translate( (float) -(MOVE_VELOCITY * distanceX / 2) * Gdx.graphics.getDeltaTime(),
                    (float) -(MOVE_VELOCITY * distanceY / 2) * Gdx.graphics.getDeltaTime() );
        }


    }

    protected void updateZoom()
    {
        if (camera.zoom > zoomPosition - INTORNO_ZOOM_PRECISION && zoomPosition > camera.zoom)
            stopZoom();
        if (camera.zoom < zoomPosition + INTORNO_ZOOM_PRECISION && zoomPosition < camera.zoom)
            stopZoom();
        if (zoomTo)
        {
            double distZoom = zoomPosition - camera.zoom;
            camera.zoom += Gdx.graphics.getDeltaTime() * ZOOM_VELOCITY * (distZoom);
        }
    }



    /**
     * Ferma il movimento della camera effettuato tramite il metodo moveCameraTo().
     */
    public void stopMoving()
    {
        move = false;
    }

    public void stopZoom()
    {
        zoomTo = false;
    }


    public boolean isMoving(){ return move; }
    public boolean isZoomTo()
    {
        return zoomTo;
    }



}
