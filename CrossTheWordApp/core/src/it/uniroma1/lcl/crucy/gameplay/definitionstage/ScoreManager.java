package it.uniroma1.lcl.crucy.gameplay.definitionstage;

import java.util.ArrayList;

import it.uniroma1.lcl.crucy.MainCrucy;
import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.gameplay.boxes.Word;
import it.uniroma1.lcl.crucy.gameplay.ui.crosswordCompleted.CompletedDialogue;

/**
 * Created by bejgu on 22/09/2016.
 */
public class ScoreManager
{

        /**
         * Classe che serve a gestire lo score inernamente
         * utilizzo di pattern observer così da aggiornare lo stage quando lo score cambia
         *
         * Classe statica per semplicità di utilizzo e resterà sempre la stessa durante l'utilizzo della schermata del cruciverba
         *
         */

        //Score
    private static int score;

    private static CompletedDialogue dialogue;
    // numero di parole totali del cruciverba
    private static long maxWords = 0;
    // numero di parole inserite del cruciverba
    private static long insertedWords = 0;

    /**
     * Tiene conto di quante parole giuste si sta facendo in sequenza
     */
    private static int rampage = 1;

    /**
     * boolean che controlla se lo score è già stato settato -> può essere settato una volta sola
     */
    private static boolean setted = false;
    /**
     * lista degli observers
     */
    private static ArrayList<ScoreObserver> observersList = new ArrayList<ScoreObserver>();


    // Aggiungo il dialogo di fine cruciverba.
    public static void setCompletedDialogue(CompletedDialogue dialogue) {
        ScoreManager.dialogue = dialogue;
    }


    // Lancio il dialogo di fine cruciverba.
    public static void lauchDialogue() {
        dialogue.createDialogue();
    }



    /**
     * metodo che aggiorna gli score degli observer
     */
    public static void updateScore() {
            for(ScoreObserver so : observersList)
                so.update(score, rampage);
    }

    /**
     * Metodo chiamato quando l'utente sbaglia che resetta la rampage
     */
    public static void resetRampage() {rampage = 1;}

    /**
     * Metodo usato quando l'utente inserisce una parola la prende in input
     * ne calcola il valore (compreso di rampage) e:
     *
     * @return :  il valore in log che servirà ad essere passato ala cache
     *
     *
     */
    public static long getValueOf(Word word) {
        insertedWords++;
        updateScore();
        rampage++;
        return word.getDifficulty().getXp();
    }

    /**
     * Metodo che aggiunge un observer alla lista
     * @param observer
     */
    public static void addObserver(ScoreObserver observer)
    {
        observersList.add(observer);
    }

    public static void resetScore() {
        rampage = 1;
        score = 0;
        maxWords = 0;
        insertedWords = 0;
    }

    /**
     * Indica se il cruciverba è stato completato o meno
     * @return
     */
    public static boolean ended() {
        return maxWords == insertedWords;
    }

    /**
     * metodo che setta lo score e può essere fatto una volta sola : serve per quando si riprende lo score da file
     * @param score
     */
    public static void setScore(int score, long maxWords, long insertedWords) {
        if(!setted){
            ScoreManager.score = score;
            ScoreManager.maxWords = maxWords;
            ScoreManager.insertedWords = insertedWords;
            updateScore();
        }
    }
}
