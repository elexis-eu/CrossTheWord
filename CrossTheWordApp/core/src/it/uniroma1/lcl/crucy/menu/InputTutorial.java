package it.uniroma1.lcl.crucy.menu;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.gameplay.input.cameraController.GameCameraController;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.GameSelectionMode;
import it.uniroma1.lcl.crucy.gameplay.ui.StageUI;
import it.uniroma1.lcl.crucy.utils.Loader;
import it.uniroma1.lcl.crucy.utils.Parole;

import static com.badlogic.gdx.math.Interpolation.swingIn;
import static com.badlogic.gdx.math.Interpolation.swingOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static it.uniroma1.lcl.crucy.gameplay.textures.Textures.font;


public class InputTutorial extends Table {

    private MainCrucy game;
    private StageUI stageUI;
    private CellController cellController;
    private GameCameraController cameraController;

    private BitmapFont font1;

    private static final float WIDTH = 600f;
    private static final float HEIGHT = 1430f;

    public InputTutorial(MainCrucy game, StageUI stageUI, GameCameraController cameraController, CellController cellController) {
        super();
        this.game = game;
        this.stageUI = stageUI;
        this.cameraController = cameraController;
        this.cellController = cellController;
        create(game.getGameMode().getModeName());
    }

    private void create(String modeName) {
        setBackground(new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("definition_label.jpg", Texture.class))));
        setSize(WIDTH + 100, HEIGHT);
        setPosition(stageUI.getCamera().viewportWidth / 2f - this.getWidth() / 2f - 70f, stageUI.getCamera().viewportHeight/2f - this.getHeight() / 2f);
        setOrigin(this.getOriginX() + this.getWidth() / 2, this.getOriginY() + this.getHeight() / 2);
        setVisible(true);

        font1 = Loader.getInstance().get("Font/regularFont/regular.fnt", BitmapFont.class);

        createTable(modeName);
        stageUI.addActor(this);
    }


    private void createTable(String modeName) {
        Label.LabelStyle textLabel = new Label.LabelStyle();
        textLabel.font = font1;

        Label labelTap = new Label(game.getString(modeName + "_tap"), new Label.LabelStyle(font, Color.WHITE));
        labelTap.setWrap(true);
        labelTap.setFontScale(0.5f);
        labelTap.invalidate();

        Label labelDT = new Label(game.getString(modeName + "_doubleTap"), new Label.LabelStyle(font, Color.WHITE));
        labelDT.setWrap(true);
        labelDT.setFontScale(0.5f);
        labelDT.invalidate();

        Label labelPress = new Label(game.getString(modeName + "_press"), new Label.LabelStyle(font, Color.WHITE));
        labelPress.setWrap(true);
        labelPress.setFontScale(0.5f);
        labelPress.invalidate();

        Label labelMove = new Label(game.getString(modeName + "_move"), new Label.LabelStyle(font, Color.WHITE));
        labelMove.setWrap(true);
        labelMove.setFontScale(0.5f);
        labelMove.invalidate();

        Label labelZoom = new Label(game.getString(modeName + "_zoom"), new Label.LabelStyle(font, Color.WHITE));
        labelZoom.setWrap(true);
        labelZoom.setFontScale(0.5f);
        labelZoom.invalidate();

        TextButton okButton = new TextButton(Parole.getInstance().getString("Continue"), getStyle());
        okButton.getLabel().setFontScale(0.7f);
        okButton.setTouchable(Touchable.enabled);
        okButton.addListener(new ClickListener() {
                                 public void clicked(InputEvent event, float x, float y) {
                                    hideTutorial();
                                 }
                             });


        setName(modeName);
        add(labelTap).width(WIDTH).center().pad(20f);
        row();
        add(labelDT).width(WIDTH).center().pad(20f);
        row();
        add(labelPress).width(WIDTH).center().pad(20f);
        row();
        add(labelMove).width(WIDTH).center().pad(20f);
        row();
        add(labelZoom).width(WIDTH).center().pad(20f);
        row();
        add(okButton).center().pad(20f);;
        setVisible(false);
    }


    public void showTutorial() {
        setVisible(true);
        setTouchable(Touchable.enabled);
        setTransform(true);
        addAction(Actions.sequence(Actions.scaleTo(0, 0), Actions.scaleTo(1, 1, 0.6f, swingOut)));
        cellController.setStateSwipe(false);
        cellController.setTapState(false);
        cameraController.setZoomIsActive(false);
        cameraController.setDoubleTapState(false);
        cameraController.setSwipeForMovingState(false);
        cameraController.setTapMoveToBorderOfScreenState(false);
    }

    public void hideTutorial(){
        setTouchable(Touchable.disabled);
        setTransform(true);
        RunnableAction hide = new RunnableAction();
        hide.setRunnable(new Runnable() {
            @Override
            public void run() {
                setVisible(false);
            }
        });
        addAction(Actions.sequence(scaleTo(0, 0, 0.6f, swingIn), hide));
        cellController.setStateSwipe(true);
        cellController.setTapState(true);
        cameraController.setZoomIsActive(true);
        cameraController.setDoubleTapState(true);
        cameraController.setSwipeForMovingState(true);
        cameraController.setTapMoveToBorderOfScreenState(true);
    }

    private TextButton.TextButtonStyle getStyle() {
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = Loader.getInstance().get("Font/fontWhite.fnt", BitmapFont.class);
        tbs.fontColor = tbs.font.getColor();
        tbs.up = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/new_crossword_small.png", Texture.class)));
        tbs.down = new TextureRegionDrawable(new TextureRegion(Loader.getInstance().get("Boxes/new_crossword_pressed_small.png", Texture.class)));
        return tbs;
    }

    private Actor[] getIcons() {
        final Actor[] ret = new Actor[5];
        final Image tap = new Image(Loader.getInstance().get("MainMenu/tutorialTap.png", Texture.class));
        tap.setName("tap");
        tap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tap.addAction(Actions.sequence(Actions.fadeOut(0.2f, Interpolation.circleOut), Actions.fadeIn(0.2f, Interpolation.circleIn)));
            }
        });
        ret[0] = tap;

        final Image doubleTap = new Image(Loader.getInstance().get("MainMenu/tutorialDoubleTap.png", Texture.class));
        doubleTap.setName("doubleTap");
        doubleTap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                doubleTap.addAction(Actions.sequence(Actions.fadeOut(0.2f, Interpolation.circleOut), Actions.fadeIn(0.2f, Interpolation.circleIn)));
            }
        });
        ret[1] = doubleTap;

        final Image press = new Image(Loader.getInstance().get("MainMenu/tutorialPress.png", Texture.class));
        press.setName("press");
        press.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                press.addAction(Actions.sequence(Actions.fadeOut(0.2f, Interpolation.circleOut), Actions.fadeIn(0.2f, Interpolation.circleIn)));
            }
        });
        ret[2] = press;

        final Image move = new Image(Loader.getInstance().get("MainMenu/tutorialMove.png", Texture.class));
        move.setName("move");
        move.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                move.addAction(Actions.sequence(Actions.fadeOut(0.2f, Interpolation.circleOut), Actions.fadeIn(0.2f, Interpolation.circleIn)));
            }
        });
        ret[3] = move;

        final Image zoom = new Image(Loader.getInstance().get("MainMenu/tutorialZoom.png", Texture.class));
        zoom.setName("zoom");
        zoom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                zoom.addAction(Actions.sequence(Actions.fadeOut(0.2f, Interpolation.circleOut), Actions.fadeIn(0.2f, Interpolation.circleIn)));
            }
        });
        ret[4] = zoom;

        return ret;
    }

}
