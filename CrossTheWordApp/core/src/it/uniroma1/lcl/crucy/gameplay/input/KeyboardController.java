package it.uniroma1.lcl.crucy.gameplay.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Disposable;

import it.uniroma1.lcl.crucy.gameplay.definitionstage.BoxKeyboard;
import it.uniroma1.lcl.crucy.gameplay.ui.StageUI;


/**
 * Created by Delloso on 31/08/2016.
 * controller della tastiera
 */
public class KeyboardController implements GestureDetector.GestureListener, Disposable {

    private StageUI stage;
    private DefController defController;

    public KeyboardController(StageUI stage, DefController defController) {
        this.stage = stage;
        this.defController = defController;

    }


    /**
     *
     * @return Ritorna il listener per la tastiera Desktop.
     */
    public InputListener getListenerDesktopKeyboard()
    {
        return new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode)
            {
                if (!defController.isSelectionMode())
                    return false;
                switch (keycode)
                {
                    case Input.Keys.A:
                        defController.setCharacter('A');
                        break;
                    case Input.Keys.B:
                        defController.setCharacter('B');
                        break;
                    case Input.Keys.C:
                        defController.setCharacter('C');
                        break;
                    case Input.Keys.D:
                        defController.setCharacter('D');
                        break;
                    case Input.Keys.E:
                        defController.setCharacter('E');
                        break;
                    case Input.Keys.F:
                        defController.setCharacter('F');
                        break;
                    case Input.Keys.G:
                        defController.setCharacter('G');
                        break;
                    case Input.Keys.H:
                        defController.setCharacter('H');
                        break;
                    case Input.Keys.I:
                        defController.setCharacter('I');
                        break;
                    case Input.Keys.L:
                        defController.setCharacter('L');
                        break;
                    case Input.Keys.M:
                        defController.setCharacter('M');
                        break;
                    case Input.Keys.N:
                        defController.setCharacter('N');
                        break;
                    case Input.Keys.O:
                        defController.setCharacter('O');
                        break;
                    case Input.Keys.P:
                        defController.setCharacter('P');
                        break;
                    case Input.Keys.Q:
                        defController.setCharacter('Q');
                        break;
                    case Input.Keys.R:
                        defController.setCharacter('R');
                        break;
                    case Input.Keys.S:
                        defController.setCharacter('S');
                        break;
                    case Input.Keys.T:
                        defController.setCharacter('T');
                        break;
                    case Input.Keys.U:
                        defController.setCharacter('U');
                        break;
                    case Input.Keys.V:
                        defController.setCharacter('V');
                        break;
                    case Input.Keys.Z:
                        defController.setCharacter('Z');
                        break;
                    case Input.Keys.Y:
                        defController.setCharacter('Y');
                        break;
                    case Input.Keys.W:
                        defController.setCharacter('W');
                        break;
                    case Input.Keys.X:
                        defController.setCharacter('X');
                        break;
                    case Input.Keys.K:
                        defController.setCharacter('K');
                        break;
                    case Input.Keys.J:
                        defController.setCharacter('J');
                        break;
                    case Input.Keys.BACKSPACE:
                        defController.cancel();
                        break;
                }
                return false;

            }
        };
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Vector2 coords = stage.screenToStageCoordinates(new Vector2(x, y));
        Actor actor = stage.hit(coords.x, coords.y, true);
        if (actor instanceof BoxKeyboard) {
            BoxKeyboard boxKeyboard = ((BoxKeyboard) actor);
            // animazione di pressione tasto
            actor.addAction(Actions.sequence(
                    Actions.scaleTo(1.2f,1.2f,0.2f, Interpolation.bounce),
                    Actions.scaleTo(1,1,0.2f,Interpolation.bounce)));
            stage.getMain().playSound(it.uniroma1.lcl.crucy.gameplay.audio.SoundFx.TAP);
            if (boxKeyboard.getCharacter() == ' ')
                defController.cancel();
            else
                defController.setCharacter(boxKeyboard.getCharacter());
            return true;
        }
        else if (actor instanceof it.uniroma1.lcl.crucy.gameplay.definitionstage.KeyboardBackground)
            return true;
        return false;
    }


    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    @Override
    public void dispose() {}
}
