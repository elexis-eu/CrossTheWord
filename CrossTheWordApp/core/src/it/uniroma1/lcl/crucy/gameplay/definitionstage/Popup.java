package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import it.uniroma1.lcl.crucy.cache.CallbackVoid;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.RequestManager;
import it.uniroma1.lcl.crucy.cache.utils.Difficult;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.gameplay.boxes.Word;
import it.uniroma1.lcl.crucy.gameplay.input.cameraController.GameCameraController;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;
import it.uniroma1.lcl.crucy.gameplay.ui.StageUI;
import it.uniroma1.lcl.crucy.utils.Loader;
import it.uniroma1.lcl.crucy.utils.Parole;

import static com.badlogic.gdx.math.Interpolation.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.font;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.font3;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.font4;

public class Popup extends Table {

    private StageUI stage;
    private CellController cellController;
    private GameCameraController cameraController;
    private Word word;
    private ArrayList<SenseButton> sbArray;
    private final int COLS = 2;
    private final int MAX_ROWS = 3;
    private final int MAX_SENSES = 6;


    public Popup(StageUI stage, Word word, List<Sense> senses, CellController cell, GameCameraController cameraController)  {
        super();
        this.stage = stage;
        this.cellController = cell;
        this.cameraController = cameraController;
        this.word = word;
        this.sbArray = new ArrayList<SenseButton>();
        createMiddle(word, senses);
        createBottom();
        setScale(0,0);
    }


    private void createMiddle(Word word, List<Sense> senses){

        Label explain = new Label(Parole.getInstance().getString("Popup"), new Label.LabelStyle(font4, Color.WHITE));
        explain.setFontScale(0.5f);
        add(explain).center().colspan(2).padBottom(40).padLeft(20f).padRight(20f);
        row().expandX();

        String definition = String.format(Parole.getInstance().getString("Popup_text"), "____", stage.cutDefinition(word.getDefinizione(),125));
        Label text_label = new Label(definition, new Label.LabelStyle(font4, Color.WHITE));
        text_label.setFontScale(0.5f);
        text_label.setAlignment(Align.center);
        text_label.setWrap(true);
        text_label.setWidth(stage.getCamera().viewportWidth - stage.getCamera().viewportWidth / 10f - 60f); // Set the width directly
        text_label.pack(); // Label calculates it's height here, but resets width to 0 (bug?)
        text_label.setWidth(stage.getCamera().viewportWidth - stage.getCamera().viewportWidth / 10f - 60f); // Set width again
        add(text_label).center().colspan(2).padBottom(40).padLeft(20f).padRight(20f)
                .width(stage.getCamera().viewportWidth - stage.getCamera().viewportWidth / 10f - 60f);

        if (senses.size() > MAX_SENSES)
            senses = getRandomSenses(senses);


        int tot_rows = senses.size()%2 == 0 ? senses.size()/2 : senses.size()/2 +1;

        row();
        for (int rows = 0 ; rows < tot_rows; rows++) {
            for (int cols = 0; cols < COLS; cols++) {
                SenseButton sb = new SenseButton();
                sb.getLabel().setFontScale(0.3f);
                sb.getLabelCell().pad(10f);
                sb.getLabel().setWrap(true);
                sb.invalidate();
                sb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.getMain().playSound(SoundFx.SELECT);
                    }
                });
                sbArray.add(sb);
                add(sb).pad(10f);
            }
            row().expandX().padLeft(20f).padRight(20f);
        }

        for (int i = 0 ; i < senses.size(); i++){
            String text = "";
            try {
                text = new String(stage.cutDefinition(senses.get(i).getLemma().replace("_"," ").toUpperCase(),20).getBytes(), "UTF8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sbArray.get(i).setText(text);
            sbArray.get(i).setSense(senses.get(i));
        }

        for (int i = senses.size() ; i < tot_rows*COLS; i++){
            sbArray.get(i).setVisible(false);
            sbArray.get(i).setTouchable(Touchable.disabled);
        }

        pad(30,80,30,80);
        setBackground(new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("definition_label.jpg", Texture.class))));
        setSize(stage.getCamera().viewportWidth - stage.getCamera().viewportWidth / 10f,
                Math.abs(100f*(senses.size()/2+6))+text_label.getPrefHeight());
        setOrigin(this.getOriginX() + this.getWidth() / 2, this.getOriginY() + this.getHeight() / 2);
        setPosition(stage.getCamera().viewportWidth / 2f - this.getWidth() / 2f, stage.getCamera().viewportHeight / 2f - this.getHeight() / 2f);
        setVisible(true);
    }

    private void createBottom(){
        TextButton popup_continue = new TextButton(Parole.getInstance().getString("Continue"), getStyle());
        popup_continue.getLabel().setFontScale(.6f);
        popup_continue.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                stage.getMain().playSound(SoundFx.SELECT);

                int wordnetCorrect = 0;
                int wordnetTotal = 0;
                int bonus = 0;

                for (SenseButton sb : sbArray) {
                    if (sb.isVisible()) {
                        if (sb.isChecked()) {
                            if (sb.getSense().getSources().contains("WN")){
                                wordnetTotal++;
                                wordnetCorrect++;
                                bonus+=5;
                            }

                            RequestManager.getInstance().sendStatistics(sb.getSense().getLemma(), sb.getSense().getSynsetID(), true, new CallbackVoid() {
                                @Override
                                public void onSuccess() { System.out.println("Statistics right sent"); }

                                @Override
                                public void onFailure(Throwable cause) { cause.printStackTrace(); }
                            });
                        } else {
                            if (sb.getSense().getSources().contains("WN"))
                                wordnetTotal++;

                            RequestManager.getInstance().sendStatistics(sb.getSense().getLemma(), sb.getSense().getSynsetID(), false, new CallbackVoid() {
                                @Override
                                public void onSuccess() { System.out.println("Statistics wrong sent");}

                                @Override
                                public void onFailure(Throwable cause) { cause.printStackTrace(); }
                            });
                        }
                    }
                }

                RequestManager.getInstance().sendUserStatistics(wordnetCorrect, wordnetTotal, new CallbackVoid() {
                    @Override
                    public void onSuccess() { }

                    @Override
                    public void onFailure(Throwable cause) { cause.printStackTrace(); }
                });

                if (bonus != 0) stage.setPopupBonusGold(bonus);
                hide();
            }
        });
        add(popup_continue).center().colspan(2).padTop(50).padLeft(20f).padRight(20f);;
        stage.addActor(this);

    }

    public void show(){
        setTouchable(Touchable.enabled);
        setTransform(true);
        addAction(Actions.sequence(Actions.scaleTo(0, 0), Actions.scaleTo(1, 1, 0.7f, swingOut)));
        cellController.setStateSwipe(false);
        cellController.setTapState(false);
        cameraController.setZoomIsActive(false);
        cameraController.setDoubleTapState(false);
        cameraController.setSwipeForMovingState(false);
        cameraController.setTapMoveToBorderOfScreenState(false);
    }

    public void hide(){
        setTouchable(Touchable.disabled);
        setTransform(true);
        addAction(scaleTo(0, 0, 0.7f, swingIn));
        cellController.setStateSwipe(true);
        cellController.setTapState(true);
        cameraController.setZoomIsActive(true);
        cameraController.setDoubleTapState(true);
        cameraController.setSwipeForMovingState(true);
        cameraController.setTapMoveToBorderOfScreenState(true);
    };

    private TextButton.TextButtonStyle getStyle() {
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = Loader.getInstance().get("Font/font.fnt", BitmapFont.class);
        tbs.fontColor = tbs.font.getColor();
        tbs.up = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Popup/button_up.png", Texture.class)));
        tbs.down = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Popup/button_down.png", Texture.class)));
        return tbs;
    }

    public List<Sense> getRandomSenses(List<Sense> senses) {

        List<Sense> randomSenses = new ArrayList<Sense>();
        List<String> upperSenses = new ArrayList<String>();
        Random rand = new Random(System.currentTimeMillis());

        int chosen = 0;
        while (chosen < MAX_SENSES) {
            Integer idx = rand.nextInt(senses.size());

            Sense sense = senses.get(idx);
            String upperLemma = sense.getLemma().toUpperCase();
            if (!upperSenses.contains(upperLemma)) {
                randomSenses.add(sense);
                upperSenses.add(upperLemma);
                chosen++;
            }
        }

        return randomSenses;
    }


}
