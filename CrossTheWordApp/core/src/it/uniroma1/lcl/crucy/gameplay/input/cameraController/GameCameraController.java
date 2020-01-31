package it.uniroma1.lcl.crucy.gameplay.input.cameraController;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;

/**
 * Created by antho on 16/08/2016.
 * Estende CamController aggiungendo la gestione degli input, rappresenta la classe concreta di CamController.
 *
 */
public class GameCameraController extends CamController implements GestureDetector.GestureListener {

    protected it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController cellController;


    public static final float MAX_ZOOM = 4f;
    public static final float MIN_ZOOM = 1f;
    public static final int DELTA_DOUBLE_TAP = 400;
    public static final float DRAG_VELOCITY_X = 1000f;
    public static final float DRAG_VELOCITY_Y = 1800F;
    public static final int INTORNO_DOUBLE_CLICK = 20;
    public static final float MAX_SWIPE_MOVEMENT = 3;
    public static final float DISTANCE_FOR_CHANGE_ZOOM_MODE = 50;

    private static final int SCREEN_DISTANCE = 150;

    protected boolean doubleTapState;
    protected boolean swipeForMovingState;
    protected boolean tapMoveToBorderOfScreenState;
    protected boolean zoomIsActive;


    protected long startTime;

    // siamo in modalità zoom
    protected boolean zoomMode;


    protected Vector2 positionFirstClick;


    protected int counterNumberForSwipeMovement;

    private boolean tutorial = true;


    private Vector2 oldInitialFirstPointer;
    private Vector2 oldInitialSecondPointer;
    private float oldScale;

    public boolean isZoomMode() {
        return zoomMode;
    }


    public GameCameraController(OrthographicCamera camera, it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController cellController) {
        super(camera);
        this.camera.zoom = MIN_ZOOM + INTORNO_ZOOM_PRECISION;
        this.camera.position.x = +400;
        this.camera.position.y = -600;
        this.positionFirstClick = new Vector2(0, 0);
        this.cellController = cellController;
        this.startTime = System.currentTimeMillis();
        this.zoomMode = true;
        this.doubleTapState = true;
        this.swipeForMovingState = true;
        this.tapMoveToBorderOfScreenState = true;
        this.zoomIsActive = true;


    }

    /**
     * Questa funzione effettua tutte le animazioni di movimento della camera va chiamata ad ogni frame.
     */
    @Override
    public void animationUpdate() {
        super.animationUpdate();
    }

    public void setDoubleTapState(boolean state) {
        this.doubleTapState = state;
    }

    public void setSwipeForMovingState(boolean state) {
        this.swipeForMovingState = state;
    }

    public void setZoomIsActive(boolean state) {this.zoomIsActive = state;}

    public void setTapMoveToBorderOfScreenState(boolean tapMoveToBorderOfScreenState) {
        this.tapMoveToBorderOfScreenState = tapMoveToBorderOfScreenState;
    }

    public it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController getCellController() {
        return cellController;
    }

    /**
     * Cambia modalità di zoom, passando da zoom a dezoom e viceversa.
     */
    public void changeZoomMode() {
        if (zoomMode) zoomOut();
        else zoomIn();

    }

    public void zoomIn() {
        System.out.println("zoom-in");
        zoomMode = true;
        //cellController.setStateSwipe(true);
        //cellController.setTapState(true);
        zoomTo(MIN_ZOOM);
    }

    public void zoomOut()
    {
        System.out.println("zoom-out");
        //cellController.setStateSwipe(false);
        //cellController.setTapState(false);
        stopMoving();
        zoomMode = false;
        cellController.stopHighLightAnimationWithResetWord();
        zoomTo(MAX_ZOOM);
    }




    public OrthographicCamera getCamera()
    {
        return camera;
    }

    public double getMoveVelocity()
    {
        return MOVE_VELOCITY;
    }

    public float getZoomVelocity()
    {
        return ZOOM_VELOCITY;
    }
    // start from here GestureListener

    @Override
    public boolean touchDown(float x, float y, int pointer, int button)
    {
        return true;
    }

    @Override
    public boolean tap(float x, float y, int count, int button)
    {
        System.out.println("ZOOOMY "+ getCamera().zoom);
        // in base al tempo riconosce se c'è un doppio tap

        long thsTime = System.currentTimeMillis();

        if(!swipeForMovingState) return false;

        // Se il tempo tra il primo tap e il secondo è più piccolo di Delta e il secondo click è in un intorno del secondo
        // effettuiamo lo zoom or il dezoom

        if (thsTime - startTime < DELTA_DOUBLE_TAP
                && (x < positionFirstClick.x + Gdx.graphics.getWidth()/INTORNO_DOUBLE_CLICK
                && x > positionFirstClick.x - Gdx.graphics.getWidth()/INTORNO_DOUBLE_CLICK
                && y < positionFirstClick.y + Gdx.graphics.getHeight()/INTORNO_DOUBLE_CLICK
                && y > positionFirstClick.y - Gdx.graphics.getHeight()/INTORNO_DOUBLE_CLICK) && doubleTapState)
        {
            if (!zoomMode)
                moveCameraTo(x,y); //doubletap
            changeZoomMode();
        }

        // se siamo in modalità zoom Mode il tap deve far muovere la camera della casella selezionata.
        else if (zoomMode)
        {
            Vector2 coords = cellController.getStage().screenToStageCoordinates(new Vector2(x, y));
            Actor attore = cellController.getStage().hit(coords.x , coords.y , true);
            if (attore instanceof it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor && !(((it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor) attore).isBlock()) && tapMoveToBorderOfScreenState)
            {
                Vector2 cordsScreen = cellController.getStage().stageToScreenCoordinates(new Vector2(attore.getX() + attore.getWidth()/2,attore.getY() + attore.getHeight()/2));
                moveCameraTo(cordsScreen.x, cordsScreen.y);
            }

        }

        // Resetto il tempo per un prossimo doubleTap
        startTime = thsTime;
        positionFirstClick = new Vector2(x,y);
        return false;
    }

    public void moveCameraToActor(Actor attore)
    {
        Vector2 cordsScreen = cellController.getStage().stageToScreenCoordinates(new Vector2(attore.getX() + attore.getWidth()/2,attore.getY() + attore.getHeight()/2));
        moveCameraTo(cordsScreen.x, cordsScreen.y + 175f);
    }

    @Override
    public boolean longPress(float x, float y)
    {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button)
    {
        if (counterNumberForSwipeMovement > MAX_SWIPE_MOVEMENT)
            moveCameraToWorld(camera.position.x - (velocityX/ Gdx.graphics.getDensity())*camera.zoom,camera.position.y + (velocityY / Gdx.graphics.getDensity())*camera.zoom);
        counterNumberForSwipeMovement = 0;
        return false;
    }



    @Override
    public boolean zoom(float initialDistance, float distance)
    {

        if (!swipeForMovingState)
            return false;

        if(cellController.isBalloon())  //se è presente la nuvoletta del tutorial
        {
            cellController.getScreen().getStageUI().hideBaloon();       //chiamo il metodo di sparizione della nuvoletta
            cellController.setBalloon(false);       //setto a false balloon del cellController
        }


        return false;
    }

/*
    public void zoomInOut(float different,boolean pinchIn)
    {

        stopMoving();
        zoomMode = !pinchIn;


       // System.out.println(different);
        //zoomTo(Math.max(camera.zoom+different/500f,);

        if(pinchIn)
        {
            //System.out.println(camera.zoom+different/1000f);
            if(camera.zoom+different/1000f>MIN_ZOOM)
                zoomTo(Math.min(camera.zoom+different/1000f, MAX_ZOOM));
        }
        else
        {
            if(camera.zoom+different/1000f<MAX_ZOOM)
                zoomTo(Math.max(camera.zoom+different/1000f, MIN_ZOOM));

            cellController.stopHighLightAnimationWithResetWord();
        }
    }

*/


    @Override
    public boolean pinch(Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer)
    {
        if (!zoomIsActive) return false;
        cellController.setCellsActivable(false);
        if(!(initialFirstPointer.equals(oldInitialFirstPointer)&&initialSecondPointer.equals(oldInitialSecondPointer))){
            oldInitialFirstPointer = initialFirstPointer.cpy();
            oldInitialSecondPointer = initialSecondPointer.cpy();
            oldScale = getCamera().zoom;
        }
        Vector3 center = new Vector3(
                (firstPointer.x+initialSecondPointer.x)/2,
                (firstPointer.y+initialSecondPointer.y)/2,
                0
        );
        pinchZoomCamera(center, oldScale*initialFirstPointer.dst(initialSecondPointer)/firstPointer.dst(secondPointer));
        return true;
    }

    //supporto per pinch(...)
    private void pinchZoomCamera(Vector3 origin, float scale)
    {
        getCamera().update();
        Vector3 oldUnprojection = getCamera().unproject(origin.cpy()).cpy();
        System.out.println("===== " );
        System.out.println("ZOOOMY "+ getCamera().zoom);

        getCamera().zoom = Math.min(MAX_ZOOM, Math.max(scale, MIN_ZOOM));
        System.out.println("ZOOOMY "+ getCamera().zoom);
        System.out.println("===== " );

        getCamera().update();
        Vector3 newUnprojection = getCamera().unproject(origin.cpy()).cpy();
        getCamera().position.add(oldUnprojection.cpy().add(newUnprojection.cpy().scl(-1f)));
    }


    @Override
    public void pinchStop()
    {
        Actions.delay(0.5f, Actions.run(new Runnable() {
            @Override
            public void run() {
                cellController.setCellsActivable(true);
        }}));

    }

    public void activeAll() { swipeForMovingState = true; }
   /*  @Override

    public boolean pan(float x, float y, float deltaX, float deltaY) {
        getCamera().update();
        getCamera().position.add(
                getCamera().unproject(new Vector3(0, 0, 0))
                        .add(getCamera().unproject(new Vector3(deltaX, deltaY, 0)).scl(-1f))
        );
        return true;

    }
 */

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY)
    {
        cellController.setCellsActivable(false);
        cellController.stopHighLightAnimationWithResetWord();

        if(!swipeForMovingState) return false;

        counterNumberForSwipeMovement += 1;
        if (!(counterNumberForSwipeMovement > MAX_SWIPE_MOVEMENT) || !swipeForMovingState )
            return false;

        stopMoving();
        camera.translate((-deltaX * DRAG_VELOCITY_X * camera.zoom) /Gdx.graphics.getWidth(),  (deltaY *DRAG_VELOCITY_Y * camera.zoom)/Gdx.graphics.getHeight()); //Translate by subtracting the vectors

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button)
    {
        cellController.setCellsActivable(true);
        return true;
    }


}

