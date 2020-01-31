package it.uniroma1.lcl.crucyservlet.ClassiBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementa un dizionario attraverso una Trie. Ogni Trie è costituita da una
 * sequenza di caratteri ciascuno dei quali la collega ad una Trie differente.
 * Considerando una parola come una sequenza di caratteri ad essi corrisponde un
 * percorso in un ideale grafo a partire dalla Trie principale. Nel grafo, ad
 * ogni carattere corrisponde un percorso che partendo dalla Trie principale
 * identifica un prefisso ed una serie di possibili percorsi che corrispondono
 * ai possibili suffissi che possono seguire al prefisso finora individuato.
 * ATTENZIONE: Questa Trie non implementa l'aggiunta di caratteri terminatori
 * che indicano quando una parola del dizionario finisce. E' pensata per
 * lavorare solo su dizionari contenenti parole tutte della stessa lunghezza.
 * 
 * @author Ele1955
 */
public class Trie
{
    /**
     * Una mappa che associa ad un carattere una Trie che contiene i possibili
     * completamenti della parola.
     */
    private Map<Character, Trie> nodo;
    /**
     * Una mappa che associa al carattere quante parole del dizionario lo
     * contengono in quella data posizione(rappresentata dalla Trie corrente).
     */
    private Map<Character, Integer> nrSon;
    /**
     * Una stringa che contiene il percorso compiuto per arrivare nella
     * posizione corrente. In questa implementazione è valorizzzato solo per le
     * foglie del dizionario altrimenti è impostato di default alla stringa
     * vuota
     */
    private String prefix = "";

    /**
     * Costruttore della Trie, inizializza i parametri.
     */
    public Trie()
    {
        nodo = new HashMap<>();
        nrSon = new HashMap<>();
    }

    /**
     * Costruttore della Trie, inizializza i parametri ed aggiunge alla Trie
     * data la stringa s.
     * 
     * @param s stringa che si vuole inserire nella Trie.
     */
    public Trie(String s)
    {
        this();
        addWord(s);
    }

    /**
     * Funzione che ritorna il campo privato nodo della Trie.
     * 
     * @return il riferimento ai collegamenti della Trie corrente.
     */
    public Map<Character, Trie> getNodo()
    {
        return nodo;
    }

    /**
     * @return sum il numero delle parole che passano per la Trie corrente
     */
    public Integer getNrSons()
    {
        int sum = 0;
        for (Character c : nrSon.keySet())
            sum += nrSon.get(c);
        return sum;
    }

    /**
     * @param c il carattere di cui si vuole ottenere l'informazione
     * @return un booleano che vale true se la Trie corrente corrisponde ad un
     * prefisso che nel nostro dizionario può essere seguito dal carattere c.
     */
    public boolean isSon(Character c)
    {
        return nodo.containsKey(c) && nrSon.get(c) > 0;
    }

    /**
     * @param c indica il carattere che si vuole concatenare al prefisso
     * corrente
     * @return una Trie che rappresenta i possibili completamenti della parola
     * se al prefisso corrente(Trie su cui viene chiamato il metodo) si aggiunge
     * il carattere c.
     */
    public Trie getSon(Character c)
    {
        return nodo.get(c);
    }

    /**
     * @return una stringa contenente il prefisso che si è costituito muovendosi
     * di carattere in carattere dalla Trie principale fino a quella corrente.
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * @return un insieme di tutti i possibili caratteri che possono seguire al
     * prefisso corrente.
     */
    public Set<Character> getSons()
    {
        Set<Character> sons = new HashSet<>();
        for (Character c : nodo.keySet())
            if (nrSon.get(c) > 0)
                sons.add(c);
        return sons;
    }

    /**
     * Metodo privato che consente di aggiungere un percorso dalla Trie corrente
     * alla Trie a cui corrisponde il carattere c. Ossia si indica che a quel
     * prefisso da questo momento corrisponderà una parola in più.
     * 
     * @param c il carattere da cui si vuole aggiungere il collegamento
     */
    private void addSon(Character c)
    {
        Integer k = 0;
        if (nrSon.containsKey(c))
            k = nrSon.get(c);
        nrSon.put(c, k + 1);
    }

    /**
     * Metodo privato che consente di modificare le informazioni interne della
     * Trie "nascondendo" un percorso e quindi una parola se possibile.
     * 
     * @param c carattere del quale si vuole "nascondere" il collegamento.
     * @return true se è possibile "nascondere" il collegamento.
     */
    private boolean hideSon(Character c)
    {
        if (nrSon.get(c) <= 0)
            return false;
        nrSon.put(c, nrSon.get(c) - 1);
        return true;
    }

    /**
     * Funzione che permette di aggiungere una parola alla Trie corrispondente e
     * valorizza al valore della parola il prefisso della Trie a cui si arriva
     * "percorrendo" la parola stessa.
     * 
     * @param w stringa che rappresenta la parola che si vuole aggiungere al
     * dizionario.
     */
    public void addWord(String w)
    {
        Trie curr = this;
        Character temp;
        Map<Character, Trie> m = null;
        for (int i = 0; i < w.length(); i++)
        {
            temp = w.charAt(i);
            curr.addSon(temp);
            m = curr.nodo;
            if (!m.containsKey(temp))
                m.put(temp, new Trie());
            curr = m.get(temp);
        }
        curr.prefix = w;
    }

    /**
     * Metodo che permette di eliminare una parola all'interno del dizionario
     * senza però modificare i collegamenti tra le varie Trie, come se venisse
     * "nacosta".
     * 
     * @param w parola che si vuole "nascondere" all'interno del dizionario.
     */
    public void hideWord(String w)
    {
        Trie curr = this;
        Character temp;
        for (int i = 0; i < w.length(); i++)
        {
            temp = w.charAt(i);
            curr.hideSon(temp);
            curr = curr.nodo.get(temp);
        }
    }

    /**
     * Metodo che permette, se possibile, di eliminare una parola all'interno
     * del dizionario senza però modificare i collegamenti tra le varie Trie,
     * come se venisse "nacosta".
     * 
     * @param w parola che si vuole "nascondere" all'interno del dizionario.
     */
    public boolean hideIfPossible(String w)
    {
        Trie curr = this;
        Character temp;
        for (int i = 0; i < w.length(); i++)
        {
            temp = w.charAt(i);
            if (!curr.hideSon(temp))
            {
                this.addWord(w.substring(0, i));
                return false;
            }
            curr = curr.nodo.get(temp);
        }
        return true;
    }

    /**
     * Permette di muoversi dalla Trie corrente che rappresenta il prefisso di
     * una parola alla Trie collegata attraverso il carattere c, che rappresenta
     * a sua volta un prefisso uguale a quello della Trie precedente con
     * l'aggiunta del carattere c.
     * 
     * @param c carattere a cui corrisponde il collegamento che si vuole
     * percorrere.
     * @return la Trie corrispondente al prefisso corrente con l'aggiunta di c
     * se quel prefisso è presente neldizionario altrimenti null.
     */
    public Trie moveTo(Character c)
    {
        if (nodo.containsKey(c))
            return nodo.get(c);
        return null;
    }
}