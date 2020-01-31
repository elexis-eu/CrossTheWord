package it.uniroma1.lcl.crucy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.GroupLayout;

import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.utils.Loader;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;


/**
 * Screen per la visualizzazione dei credits
 * Created by Camilla on 05/10/2016.
 * Checked on 03/10/2019
 */

public class CreditsScreen implements Screen
{
    private MainCrucy main;
    private Stage stage;

    private Table credits;
    private BitmapFont font;
    private BitmapFont font2;
    private BitmapFont font4;


    public CreditsScreen(MainCrucy main) {
        this.main = main;
        this.stage= new Stage(main.getViewport(),main.getBatch());
        Gdx.input.setInputProcessor(stage);
        createCreditsTable();
    }


    public void createCreditsTable(){
        font = Loader.getInstance().get("Font/font.fnt", BitmapFont.class);
        font2 =Loader.getInstance().get("Font/font2.fnt", BitmapFont.class);

        Label.LabelStyle creditsStyle = new Label.LabelStyle();
        creditsStyle.font = font;

        Label.LabelStyle nameStyle = new Label.LabelStyle();
        nameStyle.font = font2;

        Label.LabelStyle musicStyle = new Label.LabelStyle();
        musicStyle.font = font2;

        Label creditsLabel = new Label("CREDITS",creditsStyle);

        List<String> storm = Arrays.asList("Danger Storm: Danger Storm" , "Kevin MacLeod (incompetech.com)",
                "Licensed under Creative Commons:", "By Attribution 3.0 License",
                "http://creativecommons.org/licenses/by/3.0/");

        List<String> move = Arrays.asList("One Sly Move: One Sly Move","Kevin MacLeod (incompetech.com)",
                "Licensed under Creative Commons:","By Attribution 3.0 License",
                "http://creativecommons.org/licenses/by/3.0/");


        credits = new Table();
        credits.setWidth(stage.getWidth()/2);
        credits.row().top();
        credits.add(creditsLabel);
        credits.row();
        credits.row();
        credits.add(new Label("",nameStyle));
        credits.row();
        credits.add(new Label("Project Leader",creditsStyle));
        credits.row();
        credits.add(new Label("Roberto Navigli",nameStyle));
        credits.row();

        credits.add(new Label("",nameStyle));
        credits.row();

        credits.add(new Label("Project Managers",creditsStyle));
        credits.row();
        credits.add(new Label("Federico Martelli",nameStyle));
        credits.row();
        credits.add(new Label("Francesco Passaretti",nameStyle));
        credits.row();
        credits.add(new Label("Daniel D'Angelo",nameStyle));
        credits.row();

        credits.add(new Label("",nameStyle));
        credits.row();

        credits.add(new Label("Senior Developers",creditsStyle));
        credits.row();
        credits.add(new Label("Antonio Musolino",nameStyle));
        credits.row();
        credits.add(new Label("Marzia Riso",nameStyle));
        credits.row();

        credits.add(new Label("",nameStyle));
        credits.row();

        credits.add(new Label("API server",creditsStyle));
        credits.row();
        credits.add(new Label("Giovannino Manni",nameStyle));
        credits.row();
        credits.add(new Label("Paolo Mondillo",nameStyle));
        credits.row();
        credits.add(new Label("",nameStyle));

        credits.row();
        credits.add(new Label("Builder",creditsStyle));
        credits.row();
        credits.add(new Label("Matteo Costantini",nameStyle));
        credits.row();
        credits.add(new Label("Federico De Lellis",nameStyle));
        credits.row();
        credits.add(new Label("Davide Montella",nameStyle));
        credits.row();
        credits.add(new Label("Valerio Papi Suadoni",nameStyle));
        credits.row();
        credits.add(new Label("Emanuele Zamattio",nameStyle));
        credits.row();
        credits.add(new Label("",nameStyle));

        credits.row();
        credits.add(new Label("Caching",creditsStyle));
        credits.row();
        credits.add(new Label("Paolo Mazza",nameStyle));
        credits.row();
        credits.add(new Label("Dario Montagnini",nameStyle));
        credits.row();
        credits.add(new Label("Eleonora Rocchi",nameStyle));
        credits.row();
        credits.add(new Label("",nameStyle));

        credits.row();
        credits.add(new Label("Definition difficulty",creditsStyle));
        credits.row();
        credits.add(new Label("Tiziana Catena",nameStyle));
        credits.row();
        credits.add(new Label("Davide Sforza",nameStyle));
        credits.row();
        credits.add(new Label("Giovanni Varricchione",nameStyle));
        credits.row();
        credits.add(new Label("",nameStyle));

        credits.row();
        credits.add(new Label("Definition processing",creditsStyle));
        credits.row();
        credits.add(new Label("Francesca Romana Mattei",nameStyle));
        credits.row();
        credits.add(new Label("Emilian Postolache",nameStyle));
        credits.row();
        credits.add(new Label("",nameStyle));

        credits.row();
        credits.add(new Label("Graphical Interface",creditsStyle));
        credits.row();
        credits.add(new Label("Andrei Stefan Bejgu",nameStyle));
        credits.row();
        credits.add(new Label("Valerio Bianchini",nameStyle));
        credits.row();
        credits.add(new Label("Maurizio Dell'Oso",nameStyle));
        credits.row();
        credits.add(new Label("Claudio Marchese",nameStyle));
        credits.row();
        credits.add(new Label("Alessandro Mazzucchelli",nameStyle));
        credits.row();
        credits.add(new Label("Antonio Musolino",nameStyle));
        credits.row();
        credits.add(new Label("Riccardo Orlando",nameStyle));
        credits.row();
        credits.add(new Label("Marzia Riso",nameStyle));
        credits.row();
        credits.add(new Label("Camilla Serino",nameStyle));
        credits.row();
        credits.add(new Label("Gabriel-Radu Taranciuc",nameStyle));

        credits.row();
        credits.add(new Label("",nameStyle));
        credits.row();
        credits.add(new Label("Music",creditsStyle));

        for (String s: storm) {
            credits.row();
            credits.add(new Label(s, musicStyle));
        }

        credits.row();
        credits.add(new Label("",nameStyle));
        credits.row();

        for (String m: move) {
            credits.row();
            credits.add(new Label(m, musicStyle));
        }

        credits.row();
        credits.add(new Label("",nameStyle));

        for(Cell c: credits.getCells()) {
            Label l = (Label) c.getActor();
            if (l.getStyle().equals(creditsStyle)) l.setFontScale(0.7f);
            else if (l.getStyle().equals(nameStyle)) l.setFontScale(0.5f);
            else if (l.getStyle().equals(musicStyle)) l.setFontScale(0.4f);
        }


        credits.row().pad(5f);
        Image logos = new Image(Loader.getInstance().get("Loghi/babelscape+sapienza_NERO2.png", Texture.class));
        logos.setScaling(Scaling.fit);
        credits.add(logos).height(100f);

        credits.setFillParent(false);
        credits.setTouchable(Touchable.enabled);
        credits.pack();
        credits.setPosition(stage.getWidth()/2-credits.getWidth()/2,-credits.getHeight());

        creditsLabel.setFontScale(1f);

        credits.addAction(sequence(moveTo(credits.getX(), stage.getHeight(),20f, Interpolation.linear),
                Actions.run(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        main.setScreen(new SettingsScreen(main));
                        dispose();
                    }


                })));

        stage.addActor(credits);
        stage.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                main.playSound(SoundFx.SELECT);
                main.stopMusic();
                dispose();
                main.setScreen(new SettingsScreen(main));
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta){
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)
                || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            main.playSound(SoundFx.SELECT);
            main.stopMusic();
            dispose();
            main.setScreen(new SettingsScreen(main));

        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        font.dispose();
        font2.dispose();
    }
}
