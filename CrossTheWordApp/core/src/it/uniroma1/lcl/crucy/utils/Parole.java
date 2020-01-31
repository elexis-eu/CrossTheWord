package it.uniroma1.lcl.crucy.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import it.uniroma1.lcl.crucy.cache.FileManager;
import it.uniroma1.lcl.crucy.cache.utils.Difficult;
import it.uniroma1.lcl.crucy.cache.utils.Language;

/**
 * Created by Camilla on 26/08/2016.
 */
public class Parole {
    private HashMap<Language, HashMap<String, String>> map = new HashMap();

    private static Parole instance;

    public static Parole getInstance() {
        if (instance == null)
            instance = new Parole();
        return instance;
    }

    private Parole() {
        //##########################################################
        //##################### ITALIAN WORDS ######################
        //##########################################################

        HashMap<String, String> mapIta = new HashMap();
        mapIta.put("Gioca", "Gioca");
        mapIta.put("Lingua", "Lingua");
        mapIta.put("Impostazioni", "Impostazioni");
        mapIta.put("Classifica"," Classifica");
        mapIta.put("Login", "Login");
        mapIta.put("Registra", "Registra");
        mapIta.put("Esci", "Esci");
        mapIta.put("Livello", "Il tuo livello: ");
        mapIta.put("CrosswordLevel", "Livello");
        mapIta.put("Difficolta", "Difficoltà");
        mapIta.put("Continue", "Continua");
        mapIta.put("Ospite", "Ospite");
        mapIta.put("Completato", "Hai completato il cruciverba!");
        mapIta.put("Crosswords","Cruciverba");

        mapIta.put("TapTutorial", " Tocca una casella \n per iniziare! ");
        mapIta.put("PressTutorial", " Tieni premuto su una casella \n e trascina il dito!");
        mapIta.put("SwipeTutorial", "Scorri il dito sulle \n caselle per iniziare!");
        mapIta.put("Cruciverba"," Scegli un \ncruciverba");
        mapIta.put("Delete", "Se vuoi iniziare un nuovo cruciverba, cancellane uno vecchio tenendo premuto sull'icona corrispondente!");
        mapIta.put("Delete?", "Vuoi eliminare questo cruciverba?");
        mapIta.put("Dominio","Scegli un\n dominio");
        mapIta.put("NoCrosswords","Non sono disponibili \ncruciverba di questo tipo.\n Connettiti per nuovi cruciverba!");
        mapIta.put("NotLogged","Registrati o effettua il log-in per giocare!");
        mapIta.put("MaxCrosswords","Troppi cruciverba iniziati! Cancellare uno dei cruciverba tenendo premuto sul suo pulsante");

        mapIta.put("AccessoTimestamp", "\nUltimo accesso:\n");
        mapIta.put("leader", "Classifica");
        mapIta.put("unlock", "  Portali\nSbloccati");
        mapIta.put("Esempio", "(e.g. Dentista -> Odontoiatria è sbagliato)");

        mapIta.put("tap_tap", "Tocco: evidenzia le parole incidenti");
        mapIta.put("tap_doubleTap", "Doppio tocco: avvicina ed allontana la visuale al massimo");
        mapIta.put("tap_press", "Premuto: non disponibile in questa modalità");
        mapIta.put("tap_move", "Movimento: mostra la definizione della parola oppure muove la camera");
        mapIta.put("tap_zoom", "Zoom in/out: avvicina ed allontana la visuale");

        mapIta.put("press_tap", "Tocco: riposiziona la camera");
        mapIta.put("press_doubleTap", "Doppio tocco: avvicina ed allontana la visuale al massimo");
        mapIta.put("press_press", "Premuto: evidenzia le parole incidenti");
        mapIta.put("press_move", "Movimento: mostra la definizione della parola oppure muove la camera");
        mapIta.put("press_zoom", "Zoom in/out: avvicina ed allontana la visuale");

        mapIta.put("swipe_tap", "Tocco: evidenzia le parole incidenti");
        mapIta.put("swipe_doubleTap", "Doppio tocco: avvicina ed allontana la visuale al massimo");
        mapIta.put("swipe_press", "Premuto: attiva la modalità \"movimento camera\"");
        mapIta.put("swipe_move", "Movimento: mostra la definizione della parola oppure muove la camera");
        mapIta.put("swipe_zoom", "Zoom in/out: avvicina ed allontana la visuale");

        mapIta.put("Popup", "Seleziona i sostituti validi!");
        mapIta.put("Popup_text", "%s: %s");
        mapIta.put("Credits", "Credits");

        mapIta.put(Language.ENGLISH.name(),"inglese");
        mapIta.put(Language.ITALIAN.name(),"italiano");

        mapIta.put(Difficult.EASY.name(), "Facile");
        mapIta.put(Difficult.MEDIUM.name(), "Normale");
        mapIta.put(Difficult.HARD.name(), "Difficile");

        // nuovi domini in italiano
        mapIta.put(LimitedDomains.ANIMALS.name(), " Animali ");
        mapIta.put(LimitedDomains.ART.name(), " Arte ");
        mapIta.put(LimitedDomains.FOOD.name()," Cibo ");
        mapIta.put(LimitedDomains.ECONOMICS_AND_POLITICS.name()," Politica \n ed Economia");
        mapIta.put(LimitedDomains.GAMES_AND_VIDEOGAMES.name()," Giochi e \n Videogiochi ");
        mapIta.put(LimitedDomains.WARFARE_AND_DEFENSE.name()," Guerra \n e Difesa ");
        mapIta.put(LimitedDomains.LAW_AND_CRIME.name()," Legge \n e Crimine ");
        mapIta.put(LimitedDomains.MEDIA.name()," Media ");
        mapIta.put(LimitedDomains.SCIENCE.name()," Scienze ");
        mapIta.put(LimitedDomains.LITERATURE_AND_PHILOSOPHY.name()," Letteratura \n e filosofia ");
        mapIta.put(LimitedDomains.SPORT.name()," Sport ");
        mapIta.put(LimitedDomains.HISTORY.name()," Storia ");
        mapIta.put(LimitedDomains.TECHNOLOGY.name(), " Tecnologia ");
        mapIta.put(LimitedDomains.TRAVELS_AND_GEOGRAPHY.name(), " Geografia \n e viaggi ");
        mapIta.put(LimitedDomains.MIXED.name(), " Misto ");

        //Parole del menu nel cruciverba
        mapIta.put("Si", "Si");
        mapIta.put("No", "No");
        mapIta.put("Uscire", "Vuoi uscire dal gioco?");
        mapIta.put("Main", "Vuoi tornare al menù?");
        mapIta.put("Mode", "Scegli modalità");
        mapIta.put("Level", "Scegli livello");
        mapIta.put("Nuovo", "Nuovo");
        mapIta.put("Next", "Prossimo");
        mapIta.put("Play", "Gioca");

        //Dialoghi errori Login/Register
        mapIta.put(Dialoghi.LOGINCOMPLETED.name(), "Login completato");
        mapIta.put(Dialoghi.USERDOESNTEXIST.name(), "L'utente non esiste");
        mapIta.put(Dialoghi.USERWRONGPASSWORD.name(), "Password errata");

        mapIta.put(Dialoghi.ACCOUNTCREATED.name() , "Account creato con successo");
        mapIta.put(Dialoghi.ACCOUNTCREATEDLOGINFAILED.name(), "Account creato, login fallito");
        mapIta.put(Dialoghi.CONNECTIONERROR.name(), "Errore di connessione, riprova più tardi");
        mapIta.put(Dialoghi.EMAILUSED.name(), "Email già in uso");
        mapIta.put(Dialoghi.USERUSED.name(), "Username già in uso: ");
        mapIta.put(Dialoghi.INVALIDEMAIL.name(), "Email non valida ");
        mapIta.put(Dialoghi.INVALIDPSWD.name(), "La password deve essere composta da minimo 8 caratteri, una lettera maiuscola, un numero e un carattere speciale (@#$%^&+=*) ");
        mapIta.put(Dialoghi.INVALIDUSER.name(), "Username non valido ");
        mapIta.put(Dialoghi.ACCOUNTCREATEDLOGINCOMPLETED.name(), "Account creato e login effettuato con successo");
        mapIta.put(Dialoghi.LOGINERROR.name(), "Username o password errati");
        mapIta.put(Dialoghi.SYNCERROR.name(), "Hai salvato su un altro dispositivo. Vuoi aggiornare?");
        mapIta.put(Dialoghi.LOGOUTCOMPLETED.name(), "Logout completato");
        mapIta.put(Dialoghi.SYNCCOMPLETE.name(), "Syncronizzazione avvenuta con succeso.");
        mapIta.put(Dialoghi.ALREADYLOGGEDIN.name(), "Sei già loggato!");
        mapIta.put(Dialoghi.SESSIONLOST.name(), "La sessione utente è andata persa.");




        //##########################################################
        //##################### ENGLISH WORDS ######################
        //##########################################################

        HashMap<String, String> mapEn = new HashMap();
        mapEn.put("Gioca", "Play");
        mapEn.put("Lingua", "Language");
        mapEn.put("Impostazioni", "Settings");
        mapEn.put("Classifica"," Leaderboard");
        mapEn.put("Difficolta", "Difficulty");
        mapEn.put("Login", "Login");
        mapEn.put("Registra", "Register");
        mapEn.put("Esci", "Exit");
        mapEn.put("Livello", "Your level: ");
        mapEn.put("CrosswordLevel", "Level");
        mapEn.put("Continue", "Continue");
        mapEn.put("Ospite", "Host");
        mapEn.put("Completato", "Crossword completed!");
        mapEn.put("Crosswords","Crosswords");

        mapEn.put("TapTutorial", " Tap a box to start! ");
        mapEn.put("PressTutorial", "Hold on a box \n and drag your finger!");
        mapEn.put("SwipeTutorial", " Swipe your finger on \n boxes to get started!");
        mapEn.put("Cruciverba"," Choose a\n crossword");
        mapEn.put("Delete","If you want to start a new crossword, delete an old one holding down the corresponding icon!");
        mapEn.put("Delete?","Do you want to delete this crossword?");
        mapEn.put("Dominio","Choose a\ndomain");
        mapEn.put("NoCrosswords","There are no crosswords of this type.\n Connect for new crosswords!");
        mapEn.put("NotLogged","Register or log-in to play!");
        mapEn.put("MaxCrosswords","Too many crosswords! Please delete a started one by holding down its button");

        mapEn.put("AccessoTimestamp", "\nLast access:\n");
        mapEn.put("unlock", "Unlocked\nPortals");

        //DA TRADURRE
        mapEn.put("tap_tap", "Tap: highlights the definitions crossing the cell");
        mapEn.put("tap_doubleTap", "Double Tap: zoom-in or zoom-out the camera");
        mapEn.put("tap_press", "Press: does nothing in this mode");
        mapEn.put("tap_move", "Move: if it is on a selected word it shows the definition, it moves the camera otherwise");
        mapEn.put("tap_zoom", "Pinch: zoom-in or zoom-out the camera");

        mapEn.put("press_tap", "Tap: Moves the camera to the touched point");
        mapEn.put("press_doubleTap", "Double Tap: zoom-in or zoom-out the camera");
        mapEn.put("press_press", "Press: highlights the definitions crossing the cell");
        mapEn.put("press_move", "Move: if it is on a selected word it shows the definition, it moves the camera otherwise");
        mapEn.put("press_zoom", "Pinch: zoom-in or zoom-out the camera");

        mapEn.put("swipe_tap", "Tap: highlights the definitions crossing the cell");
        mapEn.put("swipe_doubleTap", "Double Tap: zoom-in or zoom-out the camera");
        mapEn.put("swipe_press", "Press: activates movement modality");
        mapEn.put("swipe_move", "Move: selects a word if movement modality is activated, it moves the camera otherwise");
        mapEn.put("swipe_zoom", "Pinch: zoom-in or zoom-out the camera");

        mapEn.put("Popup", "Select valid substitutes!");
        mapEn.put("Popup_text", "%s: %s");

        mapEn.put("Credits", "Credits");

        mapEn.put(Language.ENGLISH.name(),"english");
        mapEn.put(Language.ITALIAN.name(),"italian");

        mapEn.put(Difficult.EASY.name(), "Easy");
        mapEn.put(Difficult.MEDIUM.name(), "Medium");
        mapEn.put(Difficult.HARD.name(), "Hard");

        //Nuovi domini in inglese
        mapEn.put(LimitedDomains.ANIMALS.name(), "Animals");
        mapEn.put(LimitedDomains.ART.name(), "Art");
        mapEn.put(LimitedDomains.FOOD.name(),"Food");
        mapEn.put(LimitedDomains.ECONOMICS_AND_POLITICS.name()," Politics \n and Business ");
        mapEn.put(LimitedDomains.GAMES_AND_VIDEOGAMES.name()," Videogames \n and Games ");
        mapEn.put(LimitedDomains.WARFARE_AND_DEFENSE.name()," Warfare \n and Defense ");
        mapEn.put(LimitedDomains.LAW_AND_CRIME.name()," Law \n and Crime ");
        mapEn.put(LimitedDomains.MEDIA.name()," Media ");
        mapEn.put(LimitedDomains.SCIENCE.name()," Science ");
        mapEn.put(LimitedDomains.LITERATURE_AND_PHILOSOPHY.name()," Literature \n and Philosophy ");
        mapEn.put(LimitedDomains.SPORT.name()," Sport ");
        mapEn.put(LimitedDomains.HISTORY.name()," History ");
        mapEn.put(LimitedDomains.TECHNOLOGY.name(), " Technology ");
        mapEn.put(LimitedDomains.TRAVELS_AND_GEOGRAPHY.name(), " Geography \n and Travels ");
        mapEn.put(LimitedDomains.MIXED.name()," Mixed ");

        //Parole del menu nel cruciverba
        mapEn.put("Si", "Yes");
        mapEn.put("No", "No");
        mapEn.put("Uscire", "Do you want to quit the game?");
        mapEn.put("Main", "Do you want to return to the menu?");
        mapEn.put("Mode", "Choose the mode");
        mapEn.put("Level", "Choose the level");
        mapEn.put("Nuovo", "New");
        mapEn.put("Next", "Next");
        mapEn.put("Play", "Play");

        mapEn.put(Dialoghi.LOGINCOMPLETED.name(), "Login completed");
        mapEn.put(Dialoghi.ACCOUNTCREATED.name() , "Account created with success");
        mapEn.put(Dialoghi.USERDOESNTEXIST.name(), "User doesn't exist");
        mapEn.put(Dialoghi.USERWRONGPASSWORD.name(), "Wrong password");

        mapEn.put(Dialoghi.ACCOUNTCREATEDLOGINFAILED.name(), "Account created, login failed");
        mapEn.put(Dialoghi.CONNECTIONERROR.name(),"Connection error, try again later");
        mapEn.put(Dialoghi.EMAILUSED.name(), "Email already used");
        mapEn.put(Dialoghi.USERUSED.name(),"Username already used: ");
        mapEn.put(Dialoghi.INVALIDEMAIL.name(), "Invalid email ");
        mapEn.put(Dialoghi.INVALIDPSWD.name(), "The password must be minimum 8 characters, at least one uppercase letter, one number and one special character (@#$%^&+=*-_)");
        mapEn.put(Dialoghi.INVALIDUSER.name(), "Invalid Username ");
        mapEn.put(Dialoghi.ACCOUNTCREATEDLOGINCOMPLETED.name(), "Account Created and Login successful");
        mapEn.put(Dialoghi.LOGINERROR.name(), "Wrong username or password");
        mapEn.put(Dialoghi.SYNCERROR.name(), "You have saved on another device, do you want to update?");
        mapEn.put(Dialoghi.LOGOUTCOMPLETED.name(), "Logout completed");
        mapEn.put(Dialoghi.SYNCCOMPLETE.name(), "Your progress has ben syncronized");
        mapEn.put(Dialoghi.ALREADYLOGGEDIN.name(), "You are logged-in!");
        mapEn.put(Dialoghi.SESSIONLOST.name(), "User session lost.");


        map.put(Language.ITALIAN, mapIta);
        map.put(Language.ENGLISH, mapEn);

    }

    public HashMap<Language, HashMap<String, String>> getMap() {
        return map;
    }

    public String getString(Language l , String s) {
        return map.get(l).get(s);
    }

    public String getString(String s) {
        return this.getString(FileManager.getInstance().getLanguage(), s);
    }

}
