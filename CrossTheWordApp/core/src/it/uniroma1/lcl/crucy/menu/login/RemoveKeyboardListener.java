package it.uniroma1.lcl.crucy.menu.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class RemoveKeyboardListener extends InputListener {
    private Stage screenStage;

    public RemoveKeyboardListener(Stage screenStage) {
        this.screenStage = screenStage;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        // Se non colpisco nulla tolto la visibilità della tastiera
        if (screenStage.hit(x, y, true) == null) {
            Gdx.input.setOnscreenKeyboardVisible(false);
        }
        return super.touchDown(event, x, y, pointer, button);
    }

}
