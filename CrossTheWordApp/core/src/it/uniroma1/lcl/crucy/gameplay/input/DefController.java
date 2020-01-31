package it.uniroma1.lcl.crucy.gameplay.input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import it.uniroma1.lcl.crucy.cache.Callback;
import it.uniroma1.lcl.crucy.cache.exceptions.SessionExpiredException;
import it.uniroma1.lcl.crucy.cache.exceptions.SynchronizedExeption;
import it.uniroma1.lcl.crucy.cache.utils.Parameters;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;
import it.uniroma1.lcl.crucy.gameplay.boxes.Word;
import it.uniroma1.lcl.crucy.gameplay.builderclasses.CasellaCarattere;
import it.uniroma1.lcl.crucy.gameplay.crosswordmain.CrucyMainScreen;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.Popup;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.ScoreManager;
import it.uniroma1.lcl.crucy.gameplay.definitionstage.Sense;
import it.uniroma1.lcl.crucy.gameplay.input.cameraController.GameCameraController;
import it.uniroma1.lcl.crucy.gameplay.input.cellControll.CellController;
import it.uniroma1.lcl.crucy.gameplay.ui.StageUI;
import it.uniroma1.lcl.crucy.gameplay.ui.bonus.BonusButton;
import it.uniroma1.lcl.crucy.utils.Dialoghi;
import it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor;
import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.menu.login.SupporterClass;
import it.uniroma1.lcl.crucy.cache.CallbackVoid;
import it.uniroma1.lcl.crucy.cache.ConnectionManager;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.MainThreadCallback;
import it.uniroma1.lcl.crucy.cache.RequestManager;
import it.uniroma1.lcl.crucy.utils.GameType;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * Created by Delloso on 31/08/2016.
 * classe che gestisce l'inserimento delle parole e l'apparizione e scomparsa dello stageUI
 */
public class DefController implements ObserverCellController {

    private StageUI stage;
    private Word word;
    private BoxActor select;
    private IteratorBox iteratorSelects;
    private GameCameraController gameCameraController;
    private CellController cellController;
    private MainCrucy mainCrucy;
    private boolean selectionMode;
    private float heigth, width;
    private Keyboard keyboard;
    private boolean isMovmentActive;

    private List<Word> completed; //Words to be sended to the Server
    private GameType gameType;


    public DefController(CrucyMainScreen crucyMainScreen, GameType gameType) {
        stage = crucyMainScreen.getStageUI();
        completed = new LinkedList<Word>();
        gameCameraController = crucyMainScreen.getGameCameraController();
        cellController = crucyMainScreen.getSwipe();
        mainCrucy = crucyMainScreen.getMain();
        heigth = stage.getCamera().viewportHeight / 2.6f;
        width = stage.getCamera().viewportWidth - stage.getCamera().viewportWidth / 10f;
        keyboard = stage.getKeyboard();
        this.gameType=gameType;
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }


    /**
     * setta la casella seleziona con il carattere c
     * @param c il carattere da salvare
     */
    public void setCharacter(Character c) {
        c = Character.toLowerCase(c);
        ((it.uniroma1.lcl.crucy.gameplay.builderclasses.CasellaCarattere)word.getActors().get(word.getActors().indexOf(select)).getBox()).setTempCharacter(c);
        word.getActors().get(word.getActors().indexOf(select)).setTempChar(true);
        if (!iteratorSelects.hasNext()) {
            if (controlWord())
                setWord(this.word);
        }
        else
            next();
    }

    /**
     * cancella la lettera della casella selezionata
     */
    public void cancel() {
        select.deactivateTexture(BoxActor.enumTextures.ACTUALINDICATOR);
        while ((select.isLetterOn() || !select.getTempChar()) && iteratorSelects.hasPrev())
            select = iteratorSelects.prev();
        while (select.isLetterOn())
            select = iteratorSelects.next();
        select.activateTexture(BoxActor.enumTextures.ACTUALINDICATOR,word);
        select.setTempChar(false);
    }

    /**
     * seleziona la prossima casella
     */
    private void next() {
        select.deactivateTexture(BoxActor.enumTextures.ACTUALINDICATOR);
        while ((select.isLetterOn() || select.getTempChar()) && iteratorSelects.hasNext())
            select = iteratorSelects.next();
        select.activateTexture(BoxActor.enumTextures.ACTUALINDICATOR,word);
        if (!iteratorSelects.hasNext() && select.isLetterOn()) {
            if (controlWord())
                setWord(this.word);
        }
    }

    public Word getWord() {return word;}

    /**
     * salva la parola scritta
     */
    private void setWord(Word word) {

        mainCrucy.vibrate();
        word.setLettersWordOn();

        // Facciamo il controllo se ci sono altre parole completate oltre quella appena completata
        for(BoxActor boxActor : word.getActors()) {
            Word otherWord;
            if (word.isOrizzontale())
                otherWord = boxActor.getWordVertical();
            else
                otherWord = boxActor.getWordHorizontal();

            if (otherWord == null || otherWord.isParolaInserita()) continue;

            if(otherWord.checkLetterOn() == 0) {
                setWord(otherWord);
            }
        }

        if(!ConnectionManager.getInstance().isLogged() && mainCrucy.isLoggedInFile()) {
            if (FileManager.getInstance().getSocial().equals(Parameters.FACEBOOK)) {
                ConnectionManager.getInstance().loginFb(FileManager.getInstance().getEmail(), FileManager.getInstance().getUsername(), new CallbackVoid() {
                    @Override
                    public void onSuccess() {
                        mainCrucy.setConnLog(true);
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        if (cause instanceof SynchronizedExeption) {
                            mainCrucy.setConnLog(true);
                            SupporterClass.createDialog(mainCrucy.getString(Dialoghi.SYNCERROR.name()), stage, mainCrucy, SupporterClass.DialogDestination.DIALOCHOICES);
                        }
                    }
                });
            } else {
                System.out.println("No Connections");
                ConnectionManager.getInstance().login(FileManager.getInstance().getSessionID(), new CallbackVoid() {
                    @Override
                    public void onSuccess() {
                        mainCrucy.setConnLog(true);
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        if(cause instanceof SynchronizedExeption) {
                            mainCrucy.setConnLog(true);
                            SupporterClass.createDialog(mainCrucy.getString(Dialoghi.SYNCERROR.name()),stage,mainCrucy, SupporterClass.DialogDestination.DIALOCHOICES);
                        }

                        if (cause instanceof SessionExpiredException) {
                            System.out.println("Session Expired");
                            mainCrucy.setFileLog(false);
                            mainCrucy.setConnLog(false);
                            FileManager.getInstance().logout();
                            ConnectionManager.getInstance().logout(new CallbackVoid() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFailure(Throwable cause) {

                                }
                            });

                            SupporterClass.createDialog(mainCrucy.getString(Dialoghi.SESSIONLOST.name()),stage,mainCrucy, SupporterClass.DialogDestination.TOMAIN);
                        }
                    }
                });
            }
            System.out.println("Pass Callback");
        }

        final long valueWordScore = ScoreManager.getValueOf(word);
        FileManager.getInstance().saveStateCrossword(BoxActor.getId(), word.getParola(), true);

        if (gameType.equals(GameType.FREEPLAY)) {
            FileManager.getInstance().increaseMoney(word.getDifficulty().getXp() * 5);
        } else {
            FileManager.getInstance().increaseXP((int) valueWordScore);
            FileManager.getInstance().increaseDiamonds(word.getDifficulty().getXp() * 5);
        }

        completed.add(word);

        CallbackVoid cb = new CallbackVoid() {
            @Override
            public void onSuccess() {
                completed.clear();
            }

            @Override
            public void onFailure(Throwable cause) {
                try {
                    Thread.sleep(10000);
                    System.out.println("Riprovando a inviare");
                                        if (FileManager.getInstance().isLoggedIn()) RequestManager.getInstance().sendCompletedWords(BoxActor.getId(), completed, this);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        };

        RequestManager.getInstance().sendCompletedWords(BoxActor.getId(), completed, cb);
        RequestManager.getInstance().setUserScore(FileManager.getInstance().getUserScore(), new CallbackVoid() {
            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(Throwable cause) {cause.printStackTrace();}
        });

        if(word.hasPortal())
            word.setVisibilePortal();
        setNormal();
        word.setParolaInserita(true);
        stage.setInsertedWord(word);
    }

    /**
     * normalizza le caselle della parola, facendole tornare a deselezionate
     */
    public void setNormal() {
        for (BoxActor boxActor : word.getActors()) {
            boxActor.setTempChar(false);
            boxActor.setBlockSelectionCell(false); // attivo la possiblità di poter selezionare le caselle.
            boxActor.setNormal();
        }
        selectionMode = false;
        stage.setWordSelect(false);
        stage.hideStage();
        stage.showMenuButton();
        stage.hideButtons(stage.getBonusButtons(),stage.getBonusOn());
        word.setParolaInserita(false);
        gameCameraController.setDoubleTapState(true);
        gameCameraController.setSwipeForMovingState(true);
        gameCameraController.setTapMoveToBorderOfScreenState(true);
        gameCameraController.setZoomIsActive(true);
        gameCameraController.zoomTo(GameCameraController.MIN_ZOOM);
    }

    /**
     * controlla che la parola inserita sia corretta
     * @return true se la parola e' corretta, false altrimenti
     */
    private boolean controlWord() {
        select.deactivateTexture(BoxActor.enumTextures.ACTUALINDICATOR);
        for (BoxActor boxActor : word.getActors()) {
            if (boxActor.isLetterOn()) continue;
            CasellaCarattere casellaCarattere = (CasellaCarattere) boxActor.getBox();
            if (!casellaCarattere.getTempCharacter().toString().toLowerCase().equals(casellaCarattere.getCharacter().toString().toLowerCase())){
                mainCrucy.playSound(SoundFx.WRONG);
                ScoreManager.resetRampage();
                word.setWordWrongOn();

                //Pulisce i caratteri temporanei ogni volta che si sbaglia
                clear();
                iteratorSelects = new IteratorBox();
                do {
                    select = iteratorSelects.next();
                } while (select.isLetterOn() || select.getTempChar());
                cellController.setStateSwipe(false);
                cellController.setTapState(false);
                stage.addAction(Actions.delay(0.5f, Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        cellController.setStateSwipe(true);
                        cellController.setTapState(true);
                    }
                })));
                select.activateTexture(BoxActor.enumTextures.ACTUALINDICATOR,word);
                return false;
            }
        }
        mainCrucy.playSound(SoundFx.RIGHT);

        final Word guessed = word; //Cambio in riferimento in modo che anche se viene selezionata una parola nuova
                                    //Si fa comunque riferimento alla precedente indovinata nel caso in cui la richiesya sia lenta
        RequestManager.getInstance().getSenses(guessed.getParola(), guessed.getBabelnetID(), new Callback() {
            @Override
            public void onSuccess(Object result) {
                List<Sense> senses = (List<Sense>) result;
                if (senses.size()!=0) {
                    final Popup popup = new Popup(stage, guessed, senses, cellController, gameCameraController);
                    popup.show();
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                cause.printStackTrace();
            }
        });
        return true;
    }

    /**
     * metodo dell'observe che viene chiamato quando una parola e' selezionata
     * @param word la parola selezionata
     */
    @Override
    public void wordSelected(it.uniroma1.lcl.crucy.gameplay.boxes.Word word) {
        //  mi assicuro che sia selezionate e disabilito la possibilità di attivare l'animazione di selezione o deselezione per ogni casella .
        for (BoxActor boxActor : word.getActors())
        {
            boxActor.activateTexture(BoxActor.enumTextures.SELECTED,word);
            boxActor.setBlockSelectionCell(true);
        }

        if(isSelectionMode()) {
            for (BoxActor boxActor : this.word.getActors()) {
                boxActor.setBlockSelectionCell(false); // Attivo la possiblità di attivale l'animazione di selezione
                boxActor.setTempChar(false);
                if (!word.getActors().contains(boxActor))
                    boxActor.setNormal();
            }
            stage.getLabel().setText("");
            this.word.setParolaInserita(false);
        }

        //if (!(word.checkLetterOn()>word.getParola().length()/3)) {stage.getBonusButtonLetters().deactive();}

        selectionMode = true;
        this.word = word;


        word.setParolaInserita(true);
        iteratorSelects = new IteratorBox();
        do {
            select = iteratorSelects.next();
        } while (select.isLetterOn() || select.getTempChar());
        select.activateTexture(BoxActor.enumTextures.ACTUALINDICATOR,word);
        stage.setDefController(this);
        if (word.getDefinizioneClass() instanceof it.uniroma1.lcl.crucy.gameplay.builderclasses.definizione.DefinizioneImmagine) loadImage();
        else stage.setLabel(word.getDefinizione());
        stage.setWordSelect(true);
        stage.showStage();
        stage.hideButtons(stage.getMenuButtons(),stage.getMenuOn());
        stage.showBonusButton();
        cellController.setStateSwipe(true);
        cellController.setTapState(true);

        float zoom = setZoom();


        if (keyboard.getBoxKeyboards().size()-word.getBonusKeys().size()-1 == word.countDifferentCharacters())
            ((BonusButton)stage.getBonusButtons().get(1)).deactive();

        for (int i = 0; i<keyboard.getBoxKeyboards().size();i++) {
                keyboard.getBoxKeyboards().get(i).addAction(fadeIn(.5f));
                keyboard.getBoxKeyboards().get(i).setTouchable(Touchable.enabled);}
        this.word.setBonusKeysOn();

        //gameCameraController.moveCameraTo(word.getCenter().x, word.getCenter().y + screenHeigth/4.6f);
        Vector2 vector2 = word.getCenter();
        Stage stage1 = word.getStage();
        Vector2 vector2Stage = stage1.screenToStageCoordinates(vector2);


        gameCameraController.moveCameraToWorld(vector2.x,vector2.y - getProportionHeight(zoom));
        gameCameraController.setDoubleTapState(false);
        gameCameraController.setSwipeForMovingState(false);
        gameCameraController.setTapMoveToBorderOfScreenState(false);
        gameCameraController.setZoomIsActive(false);

    }

    private void loadImage() {
        RequestManager.getInstance().getImage(null, ((it.uniroma1.lcl.crucy.gameplay.builderclasses.definizione.DefinizioneImmagine) word.getDefinizioneClass()).getImage(), new MainThreadCallback<Pixmap>() {
            @Override
            public void onSuccessInMainThread(Pixmap result) {
                stage.setDefinitionImage(cutImage(result));
            }

            @Override
            public void onFailureInMainThread(Throwable cause) {
                stage.setLabel(word.getDefinizione());
            }
        });
    }

    private Image cutImage(Pixmap p) {

        int l = Math.min(p.getHeight(), p.getWidth());
        Pixmap result = new Pixmap((int)(l+l*0.1), (int)(l + l*0.1), Pixmap.Format.RGBA4444);
        int centerX = p.getWidth()/2;
        int centerY = p.getHeight()/2;
        System.out.println(word.getDifficulty());
        switch (word.getDifficulty()) {
            case EASY: result.setColor(Color.GREEN); break;
            case MEDIUM: result.setColor(Color.YELLOW); break;
            case HARD: result.setColor(Color.RED); break;
        }
        result.fillCircle(result.getWidth()/2, result.getHeight()/2, result.getHeight()/2);
        for (int i=-l/2; i<l/2; i++) {
            for (int j=-l/2; j<l/2; j++) {
                if (Math.pow((double)(centerX-i - centerX), 2) + Math.pow((double)(centerY-j - centerY), 2) <= Math.pow(l/2, 2))
                    result.drawPixel(result.getHeight() - (i + result.getHeight()/2), result.getHeight() - (j + result.getHeight()/2), p.getPixel(centerX - i, centerY - j));
            }
        }
        return new Image(new SpriteDrawable(new Sprite(new Texture(result))));
    }

    private float getProportionHeight(float zoom)
    {
        float fl = zoom / 1.1f;
        return fl*(stage.getViewport().getWorldHeight()/8f) + stage.getViewport().getWorldHeight()/8f;
    }

    @Override
    /**
     * Questo metodo viene chiamato quando 2 o più caselle sono selezionate.
     */
    public void cellSelected(ArrayList<BoxActor> actors) {
        gameCameraController.setSwipeForMovingState(false);
        if(isSelectionMode()) {
            for (BoxActor boxActor : this.word.getActors()) {
                boxActor.setBlockSelectionCell(false); // Attivo la possiblità di attivale l'animazione di selezione
                boxActor.setTempChar(false);
                if (!actors.contains(boxActor))
                    boxActor.setNormal();
            }
            stage.getLabel().setText("");
            this.word.setParolaInserita(false);
        }
        //TODO se siamo in selection mode allora dobbiamo disabilitare la parola selezionata precedentemente (togliendo l'animazione) e aspettare che il motodo wordSelected venga chiamato per selezionare la nuova parola.
    }

    /**
     * metodo chiamato quando l'utente fa lo swipe
     */
    @Override
    public void swipe() {

        if (selectionMode) setNormal();

    }

    private float setZoom() {
        float zoom = GameCameraController.MIN_ZOOM;
        if (word.isOrizzontale())
            while (((word.getActors().get(0).getWidth() * word.getActors().size()) / zoom ) > width) zoom += 0.1f;
        else
            while (((word.getActors().get(0).getHeight() * word.getActors().size()) / zoom ) > heigth) zoom += 0.1f;
        gameCameraController.zoomTo(zoom);
        return zoom;
    }

    public boolean isMovmentActive()
    {
        return isMovmentActive;
    }
    public void activateMovment()
    {
        isMovmentActive = false;

    }


    /**
     * classe che itera sulla parola selezionata
     */
    private class IteratorBox implements Iterator<BoxActor> {

        private ArrayList<BoxActor> arrayList = word.getActors();
        private int i = -1;

        @Override
        public boolean hasNext() {
            return i < arrayList.size()-1;
        }

        @Override
        public BoxActor next() {
            i++;
            return arrayList.get(i);
        }

        @Override
        public void remove() {

        }

        public boolean hasPrev() {
            return i > 0;
        }

        public BoxActor prev() {
            i--;
            return arrayList.get(i);
        }

    }

    /**
     * Metodo chiamato quando viene attivato il Bonus "Letters.
     * Chiama a sua volta il metodo di Keyboard che disattiva alcuni
     * elementi della tastiera in base alle lettere che compongono la parola
     * @return
     */
    public void activateBonus() { keyboard.activateBonusLetters(word);}

    /*
     * Metodo che viene chiamato per riattivare gli elementi della tastiera disattivati
     * attraverso il bonus "Keyboard"

    public void deactivateBonius() {
        keyboard.deactivateBonusLetters(word);
    }*/

    /**
     * Metodo collegato al Bonus "Auto" che completa la parola selezionata
     * aggiungendo tutte le lettere(ma non aggiorna il punteggio)
     */
    public void wordBonus() {
        setWord(this.word);
        mainCrucy.playSound(SoundFx.RIGHT);
    }

    /**
     * Metodo che elimina tutti i caratteri temporaneamente inseriti dall'utente.
     * Viene utilizzato ogni volta che si sbaglia ad inserire la parola e quando viene chiamato
     * il Bonus "Letters".
     */
    private void clear() {
        for(BoxActor b : word.getActors()) {
            if(!b.isLetterOn() && b.getTempChar())
                b.setTempChar(false);
        }
    }

    /**
     * Metodo chiamato quando viene attivato il Bonus "Letters".
     * Pulisce la word da tutti i caratteri temporanei inseriti dall'user, poi chiama il
     * metodo di Word setSomeLettersOn per scoprire 1/3 delle lettere della parola.
     * Infine aggiorna l'Iterator alla casella corretta.
     */
    public void lettersBonus() {
        clear();
        word.setSomeLettersOn(Math.min(word.getParola().length()/3+1,word.checkLetterOn()));
        while(iteratorSelects.hasPrev()) { select= iteratorSelects.prev();}
        if(select.isLetterOn()) next();
    }

}