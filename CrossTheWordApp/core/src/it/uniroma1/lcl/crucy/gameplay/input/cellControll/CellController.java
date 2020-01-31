package it.uniroma1.lcl.crucy.gameplay.input.cellControll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import it.uniroma1.lcl.crucy.Animation;
import it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor;
import it.uniroma1.lcl.crucy.gameplay.input.supportClass.ActorAnimation;

import java.util.ArrayList;

/**
 * Created by antho on 16/08/2016.
 * Gestisce la selezione della parola le animazioni sul cruciverba, implementa il pattern observer, manda a tutti gli observer
 * la parola selezionata, manda un segnale quando sono state selezionate almeno due caselle e quando stiamo effettuando lo swipe
 * che rispetta dei vincoli.
 */
public class CellController implements GestureDetector.GestureListener , it.uniroma1.lcl.crucy.gameplay.input.SubjectForObserver, Animation {
    // stage
    protected Stage screenStage;

    protected boolean stateSwipe;

    protected boolean tapState;

    protected GameSelectionMode mode; // modalità di selezione per la parola.

    // Attori selezionati dallo swipe
    protected ArrayList<it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor> selectedActors;

    //Observer registrati
    protected ArrayList<it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController> register;

    protected it.uniroma1.lcl.crucy.gameplay.boxes.Word selectWord;

    protected static final long VELOCITY_ANIMATION_SELECTED = 200;
    protected static final long PIXEL_MARGIN_CASELLA = 11;

    protected boolean isCallTwoCellSelection; // serve per far chiamare una sola volta per swipe la notifica di selezione di due caselle all'observer.

    protected long startTime;
    protected boolean animationSelection;

    protected boolean isActiveAnimationVert;
    protected boolean isActiveAnimationOrizz;

    protected it.uniroma1.lcl.crucy.gameplay.boxes.Word wordOrizz;
    protected it.uniroma1.lcl.crucy.gameplay.boxes.Word wordVert;
    protected ActorAnimation positionAnimOrizz;
    protected ActorAnimation positionAnimVert;
    protected it.uniroma1.lcl.crucy.gameplay.input.supportClass.VoidFunction orizz;
    protected it.uniroma1.lcl.crucy.gameplay.input.supportClass.VoidFunction vert;

    protected it.uniroma1.lcl.crucy.gameplay.input.supportClass.TutorialAnimation tutorialAnimationOrizz = new it.uniroma1.lcl.crucy.gameplay.input.supportClass.TutorialAnimation();
    protected it.uniroma1.lcl.crucy.gameplay.input.supportClass.TutorialAnimation tutorialAnimationVert = new it.uniroma1.lcl.crucy.gameplay.input.supportClass.TutorialAnimation();
    protected boolean tutorial = true;
    protected boolean balloon = true;
    protected it.uniroma1.lcl.crucy.gameplay.crosswordmain.CrucyMainScreen screen;

    private boolean active;
    private float startXPan;
    private float startYPan;

    /**
     * @param screenStage Stage screenStage
     */
    public CellController(Stage screenStage)
    {
        // Le caselle partono selezionabili dall'inizio
        this.setCellsActivable(true);
        this.setTapState(true);

        this.screenStage = screenStage;
        this.register = new ArrayList<it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController>();
        selectedActors = new ArrayList<it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor>();
        setSelectionMode(GameSelectionMode.TAP_AND_SWIPE_MOD);
    }

    /**
     * Metodo che setta la modalità di gioco.
     * @param mode modalità di gioco selezionata (tap, presso o swipe)
     */
    public void setSelectionMode(GameSelectionMode mode)
    {
        this.mode = mode;
    }

    /**
     * Metoro che ritorna la modalità di gioco
     * @return modalità di gioco
     */
    public GameSelectionMode getSelectionMode()
    {
        return mode;
    }


    public void setStateSwipe(boolean val)
    {
        this.stateSwipe = val;
    }


    void caselleReset()
    {
        for (it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor s : selectedActors)
            s.setNormal();
    }

    /*
     * Manda agli observer i dati
     */
    @Override
    public void observerNotify()
    {
        // mando i dati agli observer
        for (it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController s : register) {
            s.wordSelected(selectWord);
        }
        this.selectWord = null;
    }

    @Override
    public void observerNotifyTwoCellSelection()
    {

        for (it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController s : register) {
            s.cellSelected(getSwipeSelectedActors());
        }
        System.out.println("TwoCellSelection");
    }

    @Override
    public void observerNotifySwipe()
    {
        for (it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController s : register) {
            s.swipe();
        }
    }

    /**
     * Effettua Update di tutte le animazioni delle Caselle.
     */
    @Override
    public void animationUpdate()
    {
        swipeAnimatioUpdate();
        animationHighlightUpdate();
    }

    public void drawAnimation(SpriteBatch batch)
    {
        batch.begin();
        if (isActiveAnimationVert) positionAnimVert.draw(batch, 1);
        if (isActiveAnimationOrizz) positionAnimOrizz.draw(batch, 1);
        if (tutorialAnimationVert.isActive()) tutorialAnimationVert.draw(batch);
        if (tutorialAnimationOrizz.isActive()) tutorialAnimationOrizz.draw(batch);
        batch.end();
    }

    public void stopHighlightAnimation()
    {
        this.isActiveAnimationOrizz = false;
        this.isActiveAnimationVert = false;
        if(tutorialAnimationVert.isActive()) tutorialAnimationVert.setActive(false);
        if(tutorialAnimationOrizz.isActive()) tutorialAnimationOrizz.setActive(false);
    }

    public void stopHighLightAnimationWithResetWord()
    {
        stopHighlightAnimation();
        if(this.wordOrizz != null)
        {
            this.wordOrizz.getFirst().deactivateTexture(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor.enumTextures.WORDINDICATOR);
            this.wordOrizz = null;
        }
        if(this.wordVert != null)
        {
            this.wordVert.getFirst().deactivateTexture(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor.enumTextures.WORDINDICATOR);
            this.wordVert = null;
        }
    }

    void animationHighlightUpdate()
    {

        if (isActiveAnimationOrizz)
        {
            Vector2 screenCoords = screenStage.stageToScreenCoordinates(new Vector2(wordOrizz.getFirst().getX(),wordOrizz.getFirst().getY()));
            Vector2 screenCoordsLast = screenStage.stageToScreenCoordinates(new Vector2(wordOrizz.getLast().getX(),wordOrizz.getLast().getY()));

            Vector3 worldCoords = getStage().getCamera().unproject(new Vector3(screenCoords.x,screenCoords.y,0));
            Vector3 worldCoordsLast = getStage().getCamera().unproject(new Vector3(screenCoordsLast.x,screenCoordsLast.y,0));

            if (positionAnimOrizz.getX() >= worldCoordsLast.x)
            {
                positionAnimOrizz.setPosition(worldCoords.x, positionAnimOrizz.getY());

                if(tutorialAnimationOrizz.isActive())
                    tutorialAnimationOrizz.setPosition(worldCoords.x, positionAnimOrizz.getY());

                wordOrizz.getFirst().addAction(Actions.sequence(Actions.rotateTo(-30,0.05f),Actions.rotateTo(30,0.05f),Actions.rotateTo(0,0.05f)));
            }

            orizz.doIt();
        }
        if (isActiveAnimationVert)
        {
            Vector2 screenCoords = screenStage.stageToScreenCoordinates(new Vector2(wordVert.getFirst().getX(),wordVert.getFirst().getY()));
            Vector2 screenCoordsLast = screenStage.stageToScreenCoordinates(new Vector2(wordVert.getLast().getX(),wordVert.getLast().getY()));

            final Vector3 worldCoords = getStage().getCamera().unproject(new Vector3(screenCoords.x,screenCoords.y,0));
            Vector3 worldCoordsLast = getStage().getCamera().unproject(new Vector3(screenCoordsLast.x,screenCoordsLast.y,0));

            if (positionAnimVert.getY() <= worldCoordsLast.y)
            {
                positionAnimVert.setPosition(positionAnimVert.getX(), worldCoords.y);

                if(tutorialAnimationVert.isActive())
                     tutorialAnimationVert.setPosition(positionAnimVert.getX(), worldCoords.y);

                wordVert.getFirst().addAction(Actions.sequence(Actions.rotateTo(-30,0.05f),Actions.rotateTo(30,0.05f),Actions.rotateTo(0,0.05f)));
            }

            vert.doIt();
        }
    }

    protected void activeAnimationHighlight(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor casella)
    {
        if ((casella.getWordHorizontal() == null ||casella.getWordHorizontal().isParolaInserita())
                && casella.getWordVertical() != null
                && !casella.getWordVertical().isParolaInserita()) // la parola non deve essere completa.
        { //La parola da selezionare e Verticale

            this.isActiveAnimationVert = true;
            this.isActiveAnimationOrizz = false;
            this.wordVert = casella.getWordVertical();
            this.wordOrizz = null;
            Vector2 screenCoords = screenStage.stageToScreenCoordinates(new Vector2(wordVert.getFirst().getX(),wordVert.getFirst().getY()));
            Vector3 worldCoords = getStage().getCamera().unproject(new Vector3(screenCoords.x,screenCoords.y,0));
            worldCoords.add(PIXEL_MARGIN_CASELLA,0,0);
            this.positionAnimVert = new ActorAnimation((int) worldCoords.x ,(int)worldCoords.y,wordVert);
            wordVert.getFirst().activateTexture(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor.enumTextures.WORDINDICATOR,wordVert);
            wordVert.getFirst().addAction(Actions.sequence(Actions.rotateTo(-30,0.05f),Actions.rotateTo(30,0.05f),Actions.rotateTo(0,0.05f)));
            if(tutorial)
                tutorialAnimationVert.setActive(true);
            vert = new it.uniroma1.lcl.crucy.gameplay.input.supportClass.VoidFunction() {
                @Override
                public void doIt() {
                    positionAnimVert.setPosition(positionAnimVert.getX(), positionAnimVert.getY() - (wordVert.getFirst().getY() - wordVert.getLast().getY())* Gdx.graphics.getDeltaTime() * 1.5f);

                    if(tutorialAnimationVert.isActive())
                    {//tutorialAnimationVert.setPosition(positionAnimVert.getX()-15, positionAnimVert.getY()+10);
                        tutorialAnimationVert.setPosition(positionAnimVert.getX()+25, positionAnimVert.getY()-50);}
                }
            };
        }
        else if ((casella.getWordVertical() == null ||casella.getWordVertical().isParolaInserita())
                && casella.getWordHorizontal()!= null
                && !casella.getWordHorizontal().isParolaInserita())  // la parola non deve essere completa anche qui.
        { // La parola da selezionare e Orizzontale

            this.isActiveAnimationVert = false;
            this.isActiveAnimationOrizz = true;
            this.wordOrizz = casella.getWordHorizontal();
            this.wordVert = null;
            Vector2 screenCoords = screenStage.stageToScreenCoordinates(new Vector2(wordOrizz.getFirst().getX(), wordOrizz.getFirst().getY()));
            Vector3 worldCoords = getStage().getCamera().unproject(new Vector3(screenCoords.x,screenCoords.y,0));
            worldCoords.add(0,PIXEL_MARGIN_CASELLA,0);
            this.positionAnimOrizz = new ActorAnimation((int) worldCoords.x ,(int)worldCoords.y,wordOrizz);
            wordOrizz.getFirst().activateTexture(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor.enumTextures.WORDINDICATOR,wordOrizz);
            wordOrizz.getFirst().addAction(Actions.sequence(Actions.rotateTo(-30,0.05f),Actions.rotateTo(30,0.05f),Actions.rotateTo(0,0.05f)));
            if(tutorial)
                tutorialAnimationOrizz.setActive(true);
            orizz = new it.uniroma1.lcl.crucy.gameplay.input.supportClass.VoidFunction() {
                @Override
                public void doIt()
                {
                    positionAnimOrizz.setPosition(positionAnimOrizz.getX() - (wordOrizz.getFirst().getX() - wordOrizz.getLast().getX()) * Gdx.graphics.getDeltaTime() * 1.5f, positionAnimOrizz.getY());
                    if(tutorialAnimationOrizz.isActive())
                    {
                        //tutorialAnimationOrizz.setPosition(positionAnimOrizz.getX()-15, positionAnimOrizz.getY()+10);
                        tutorialAnimationOrizz.setPosition(positionAnimOrizz.getX()+25, positionAnimOrizz.getY()-50);
                    }
                }
            };
        }
        else// La casella ha due parole entrambe da selezionare
        {
            if (casella.getWordHorizontal() != null
                    && !casella.getWordHorizontal().isParolaInserita())
            {
                this.isActiveAnimationOrizz = true;
                this.wordOrizz = casella.getWordHorizontal();
                Vector2 screenCoords = screenStage.stageToScreenCoordinates(new Vector2(wordOrizz.getFirst().getX(), wordOrizz.getFirst().getY()));
                Vector3 worldCoords = getStage().getCamera().unproject(new Vector3(screenCoords.x,screenCoords.y,0));
                worldCoords.add(0,PIXEL_MARGIN_CASELLA,0);
                this.positionAnimOrizz = new ActorAnimation((int) worldCoords.x ,(int)worldCoords.y,wordOrizz);
                wordOrizz.getFirst().activateTexture(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor.enumTextures.WORDINDICATOR,wordOrizz);
                wordOrizz.getFirst().addAction(Actions.sequence(Actions.rotateTo(-30,0.05f),Actions.rotateTo(30,0.05f),Actions.rotateTo(0,0.05f)));
                if(tutorial)
                    tutorialAnimationOrizz.setActive(true);
                orizz = new it.uniroma1.lcl.crucy.gameplay.input.supportClass.VoidFunction() {
                    @Override
                    public void doIt()
                    {
                        positionAnimOrizz.setPosition(positionAnimOrizz.getX() - (wordOrizz.getFirst().getX() - wordOrizz.getLast().getX()) * Gdx.graphics.getDeltaTime() * 1.5f, positionAnimOrizz.getY());
                        if(tutorialAnimationOrizz.isActive())
                        {
                            //tutorialAnimationOrizz.setPosition(positionAnimOrizz.getX()-15, positionAnimOrizz.getY()+10);
                            tutorialAnimationOrizz.setPosition(positionAnimOrizz.getX()+25, positionAnimOrizz.getY()-50);
                        }
                    }
                };
            }
            if (casella.getWordVertical() != null
                    && !casella.getWordVertical().isParolaInserita())
            {

                this.isActiveAnimationVert = true;
                this.wordVert = casella.getWordVertical();
                Vector2 screenCoords = screenStage.stageToScreenCoordinates(new Vector2(wordVert.getFirst().getX(),wordVert.getFirst().getY()));
                Vector3 worldCoords = getStage().getCamera().unproject(new Vector3(screenCoords.x,screenCoords.y,0));
                worldCoords.add(PIXEL_MARGIN_CASELLA,0,0);
                this.positionAnimVert = new ActorAnimation((int) worldCoords.x ,(int)worldCoords.y,wordVert);
                wordVert.getFirst().activateTexture(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor.enumTextures.WORDINDICATOR,wordVert);
                wordVert.getFirst().addAction(Actions.sequence(Actions.rotateTo(-30,0.05f),Actions.rotateTo(30,0.05f),Actions.rotateTo(0,0.05f)));
                if(tutorial) {
                    tutorialAnimationVert.setActive(true);
                    tutorialAnimationVert.setBounds((int) worldCoords.x, (int)worldCoords.y, tutorialAnimationVert.getImageWidth(), tutorialAnimationVert.getImageHeight());
                }
                vert = new it.uniroma1.lcl.crucy.gameplay.input.supportClass.VoidFunction() {
                    @Override
                    public void doIt() {
                        positionAnimVert.setPosition(positionAnimVert.getX(), positionAnimVert.getY() - (wordVert.getFirst().getY() - wordVert.getLast().getY())* Gdx.graphics.getDeltaTime() * 1.5f);
                        if(tutorialAnimationVert.isActive())
                        {
                            //tutorialAnimationVert.setPosition(positionAnimVert.getX()-15, positionAnimVert.getY()+10);
                            tutorialAnimationVert.setPosition(positionAnimVert.getX()+25, positionAnimVert.getY()-50);
                        }
                    }
                };
            }
        }
    }

    /*
     * Effettua l'animazione di selezione Word.
     * Questo metodo va chiamato ad ogni frame.
     */
    void swipeAnimatioUpdate()
    {
        if (animationSelection)
        {
            setTapState(false);
            setStateSwipe(false);
            stopHighlightAnimation();
            tutorial = false;
            long time = System.currentTimeMillis();
            if (time - startTime < VELOCITY_ANIMATION_SELECTED)
                for (it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor s : selectWord.getActors())
                    s.activateTexture(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor.enumTextures.SELECTEDNOSOUND,selectWord);
            else if( time - startTime < VELOCITY_ANIMATION_SELECTED * 2)
                for (it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor s : selectWord.getActors())
                    s.setNormal();
            else if (time - startTime < VELOCITY_ANIMATION_SELECTED * 3)
                for (it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor s : selectWord.getActors())
                    s.activateTexture(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor.enumTextures.SELECTEDNOSOUND,selectWord);
            else
            {
                // settiamo l'animazione a false, e mandiamo la parola all'observer
                animationSelection = false;
                //Invio la parola.
                observerNotify();
            }
        }
    }

    /**
     * Attiva L'animazione della parola in questo momento selezionata.
     */
    void activeSelectionWordAnimation()
    {
        ArrayList<it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor> set  = selectWord.getActors();
        animationSelection = true;
        this.startTime =  System.currentTimeMillis();
    }


    protected Actor getActorsHitFromScreenCoords(float x, float y)
    {
        // Converto le coordinate dello schermo in quelle dello stage
        Vector2 coords = screenStage.screenToStageCoordinates(new Vector2(x, y));

        // ritorno attore dello stage nelle coordinate calcolate
        return screenStage.hit(coords.x, coords.y, true);
    }


    public Stage getStage()
    {
        return screenStage;
    }


    public void setTapState(boolean state)
    {
        this.tapState = state;
    }

    public boolean getTapState()
    {
        return this.tapState;
    }

    public boolean isAnimationSelection() { return animationSelection; }

    @Override
    public void registerObserver(it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController observer)
    {
        if (!register.contains(observer))
            register.add(observer);
    }

    @Override
    public void removeObserver(it.uniroma1.lcl.crucy.gameplay.input.ObserverCellController observer)
    {
        register.remove(observer);
    }


    public ArrayList<it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor> getSwipeSelectedActors()
    {
        return new ArrayList<it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor>(selectedActors);
    }

    /**
     * Seleziona la casella
     * @param cellHit Casella da selezionare
     */
    void selectActors(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor cellHit)
    {
        selectedActors.add(cellHit);
        cellHit.activateTexture(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor.enumTextures.SELECTED,selectWord);
    }

    /**
     * Effettua i controlli sulla casella selezionata trovando la parola da selezionare. Se ci sono abbastanza caselle per capirlo.
     * @param cellHit
     */
    protected void controlSelectCell(it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor cellHit)
    {
         if(!active) return;

        if (!selectedActors.contains(cellHit))
        {
            // Se non ci sono caselle selezionate seleziono la casella
            if (selectedActors.isEmpty())
            {
                selectActors(cellHit);
            } // se abbiamo una casella nelle caselle selezionate.
            // vado a trovare quale parola hanno in comune la prima casella con la seconda e me la salvo.
            else if (selectedActors.size() == 1)
            {
                it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor prec = selectedActors.get(0);
                if (prec.getWordVertical() != null && cellHit.getWordVertical() != null && !prec.getWordVertical().isParolaInserita() && prec.getWordVertical().equals(cellHit.getWordVertical()))
                {
                    selectActors(cellHit);
                    this.selectWord = cellHit.getWordVertical();
                }
                else if (prec.getWordHorizontal() != null && cellHit.getWordHorizontal() != null && !prec.getWordHorizontal().isParolaInserita() && prec.getWordHorizontal().equals(cellHit.getWordHorizontal()))
                {
                    selectActors(cellHit);
                    this.selectWord = cellHit.getWordHorizontal();
                }
            }
            else if ((cellHit.getWordVertical() != null && selectWord.equals(cellHit.getWordVertical())) || (cellHit.getWordHorizontal() != null && selectWord.equals(cellHit.getWordHorizontal())))
            // una volta che ho la parola che abbiamo selezionato le successive casella le confronto con la parola e le seleziono
            // se sono casella della parola.
            {
                selectActors(cellHit);
            }
        }
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button)
    {
        // se non siamo in modalità tap and swipe o in LONG PRESS MOVE il tap non deve funzionare.
        if (!tapState)
            return false;
        // Con il tap dobbiamo attivare l'animazione di Hightlight delle parole corrispondenti.
        Actor hitActor = getActorsHitFromScreenCoords(x,y);

        if(balloon)
        {
            screen.getStageUI().hideBaloon();
            balloon = false;
        }

        if (!(hitActor instanceof it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor) || ((BoxActor) hitActor).isBlock()){
            observerNotifySwipe();
            return false;
        }
        it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor casella = (it.uniroma1.lcl.crucy.gameplay.boxes.BoxActor) hitActor;
        stopHighLightAnimationWithResetWord();
        if (casella.isBlock())
        {
            //stopHighLightAnimationWithResetWord();
            return false;
        }

        activeAnimationHighlight(casella);

        return false;
    }

    @Override
    public boolean longPress(float x, float y)
    {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button)
    {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY)
    {

        // Mi salvo la direzione di swipe
        Vector3 touch = getStage().getCamera().unproject(new Vector3(x, y, 0));
        Vector3 deltas = getStage().getCamera().unproject(new Vector3(x + deltaX, y+ deltaY, 0));
        deltaX = deltas.x - touch.x;
        deltaY = deltas.y - touch.y;

        startXPan += deltaX;
        startYPan += deltaY;



        if (selectedActors.isEmpty())
            observerNotifySwipe();

        if(balloon)     //se è presente la nuvoletta del tutorial
        {
            screen.getStageUI().hideBaloon();       //chiamo il metodo di sparizione della nuvoletta
            balloon = false;    //setto a false balloon
        }

        return false;

    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button)
    {
        // risetto tutto nel modo precedente
        caselleReset();
        //fermo sia l'animazione sia la selezione della parola.
        stopHighLightAnimationWithResetWord();

        float diff = Math.abs(startXPan) - Math.abs(startYPan);
        // Se c'è una sola cella allora controllo la direzione dello swipe
        if (selectedActors.size() == 1) {
            // Swipe orizontale
            if (diff > 0 ) {
                this.selectWord = selectedActors.get(0).getWordHorizontal();
                if (this.selectWord != null && !selectWord.isParolaInserita()) activeSelectionWordAnimation();

                System.out.println("Horizontal");
            } else {
                // Swipe verticale
                this.selectWord = selectedActors.get(0).getWordVertical();
                if (this.selectWord != null && !selectWord.isParolaInserita()) activeSelectionWordAnimation();
            }
        } else if ( selectedActors.size() >= 2) {
            activeSelectionWordAnimation();
        }

        startXPan = 0;
        startYPan = 0;

        // se abbiamo più di 2 Caselle selezionate possiamo effettuare l'animazione di selezione.
        selectedActors.clear();
        isCallTwoCellSelection = false;

        return false;
    }


    @Override
    public boolean zoom(float initialDistance, float distance)
    {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2)
    {
        return false;
    }

    @Override
    public void pinchStop()
    {

    }

    public void setCrucyMainScreen(it.uniroma1.lcl.crucy.gameplay.crosswordmain.CrucyMainScreen crucyMainScreen) { screen = crucyMainScreen; }

    public it.uniroma1.lcl.crucy.gameplay.crosswordmain.CrucyMainScreen getScreen() { return screen; }

    public boolean isBalloon() { return balloon; }

    public void setBalloon(boolean balloon) { this.balloon = balloon; }


    public void setCellsActivable(boolean activable) { active = activable; }
}