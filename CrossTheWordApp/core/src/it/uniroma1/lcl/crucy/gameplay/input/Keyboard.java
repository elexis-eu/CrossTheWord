package it.uniroma1.lcl.crucy.gameplay.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import it.uniroma1.lcl.crucy.gameplay.definitionstage.BoxKeyboard;
import it.uniroma1.lcl.crucy.gameplay.ui.StageUI;
import it.uniroma1.lcl.crucy.gameplay.ui.bonus.BonusButton;
import it.uniroma1.lcl.crucy.utils.Loader;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * Created by Delloso on 31/08/2016.
 * tastiera del gioco
 */
public class Keyboard {

    private StageUI stage;
    private it.uniroma1.lcl.crucy.gameplay.definitionstage.KeyboardBackground keyboardBackground;
    private ArrayList<BoxKeyboard> boxKeyboards;
    private static final float HEIGHT_KEYBOARD = 4f;

    public Keyboard(StageUI stage)
    {
        this.stage = stage;
        boxKeyboards = new ArrayList<BoxKeyboard>();
        BoxKeyboard.HEIGTH_BOX = stage.getCamera().viewportHeight / 16f;
        BoxKeyboard.WIDTH_BOX = stage.getCamera().viewportWidth / 10.8f;
        keyboardBackground = new it.uniroma1.lcl.crucy.gameplay.definitionstage.KeyboardBackground(Loader.getInstance().get("back.png",Texture.class));
        keyboardBackground.setBounds(0, 0, stage.getCamera().viewportWidth, BoxKeyboard.HEIGTH_BOX * 7.5f);
        keyboardBackground.setVisible(true);
        keyboardBackground.setTouchable(Touchable.enabled);
        stage.addActor(keyboardBackground);
        Character[] chars = {'Z', 'X', 'C', 'V', 'B', 'N', 'M', ' ', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'};
        Iterator<Character> iterator = Arrays.asList(chars).iterator();
        BoxKeyboard box;

        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 10; x++) {
                if (y == 0 && x < 8) {
                    box = new BoxKeyboard((x * BoxKeyboard.WIDTH_BOX) + BoxKeyboard.WIDTH_BOX,
                            y * BoxKeyboard.HEIGTH_BOX + HEIGHT_KEYBOARD,
                            iterator.next());
                    stage.addActor(box);
                    boxKeyboards.add(box);
                }
                if (y == 1 && x < 9) {
                    box = new BoxKeyboard((x * BoxKeyboard.WIDTH_BOX) + BoxKeyboard.WIDTH_BOX * 0.5f,
                            y * BoxKeyboard.HEIGTH_BOX + HEIGHT_KEYBOARD,
                            iterator.next());
                    stage.addActor(box);
                    boxKeyboards.add(box);
                }
                if (y == 2) {
                    box = new BoxKeyboard(x * BoxKeyboard.WIDTH_BOX,
                            y * BoxKeyboard.HEIGTH_BOX + HEIGHT_KEYBOARD,
                            iterator.next());
                    stage.addActor(box);
                    boxKeyboards.add(box);
                }
            }
        for (Actor act : this.boxKeyboards) {
            act.setOrigin(act.getOriginX() + act.getWidth()/2,act.getOriginY() + act.getHeight()/2);
        }

    }

    public ArrayList<BoxKeyboard> getBoxKeyboards() { return boxKeyboards; }

    public Image getKeyboardBackground()
    {
        return this.keyboardBackground;
    }

    public void activateBonusLetters(it.uniroma1.lcl.crucy.gameplay.boxes.Word word)
    {

        String s = word.getParola().toUpperCase();
        int min =4;
        if (word.getBonusKeys().size()==0) min=5;
        int count = Math.min(min,boxKeyboards.size()-word.getBonusKeys().size()-word.countDifferentCharacters()-1);
        if (count<4) ((BonusButton)stage.getBonusButtons().get(1)).deactive();
        BoxKeyboard boxKeyboard;
        Random random = new Random();
        while (count>0)
        {
            boxKeyboard = boxKeyboards.get(random.nextInt(boxKeyboards.size()));
            if (!word.getBonusKeys().contains(boxKeyboard) && !s.contains(boxKeyboard.getCharacter() + "") && boxKeyboard.getCharacter() != ' ')
            {
                word.getBonusKeys().add(boxKeyboard);
                count--;
            }
        }
        word.setBonusKeysOn();

    }
}
