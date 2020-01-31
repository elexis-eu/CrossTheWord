package it.uniroma1.lcl.crucyservlet.ClassiBuilder;

import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.Casella;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaCarattere;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaPortale;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.definizione.Definizione;

import java.util.Map;

/**
 * @author Papi Suadoni
 */
public class Cruciverba
{
    private int altezza;
    private int larghezza;
    /** 
     * Carattere che indentifica il mini-schema, per essere differenziato da
     * tutti gli altri che compongono lo schema finale
     */
    private Character id;
    /**
     * Stringa che indica il dominio del cruciverba, null se il dominio
     * del cruciverba è misto
     */
    private String dominio;
    /**
     * Double che indica la difficoltà, null se la difficoltà è mista
     */
    private Double difficulty;
    /**
     * Matrice contenente le Caselle che compongono il Cruciverba
     */
    private Casella[][] board;
    /**
     * Dizionario che ha come valore una CasellaPortale e come chiave
     * una stringa che identifica in maniera univoca in quale cruverba
     * e in quale casella si deve collegare.
     */
    private Map<Character, CasellaPortale> casellePortali;

    public enum Orientamento
    {
        VERTICALE, ORIZZONTALE,
    }

    /**
     * Costrutture del cruciverba
     * @param altezza
     * @param larghezza
     * @param board: matrice di casella
     * @param id: Character che indica l'identificativo del mini-schema
     */
    public Cruciverba(int altezza, int larghezza, Casella[][] board, Character id, 
            Map<Character, CasellaPortale> casellePortali)
    {
        this.altezza = altezza;
        this.larghezza = larghezza;
        this.board = board;
        this.id = id;
        this.casellePortali = casellePortali;
    }

    /**
     * Metodo che, data in input una coordinata(punto) qualsiasi della parola e
     * un Enum che specifica se la parola è oriz. oppure vertic., restituisce la
     * definizione relativa alla Parola in quella coordinata. Ritorna null se le
     * cordinate del punto sono errate, o se la casella non è di tipo
     * CasellaCarattere.
     * @param c
     * @return
     */
    public Definizione getWordDefinition(Point c, Enum orientamento)
    {
        if (!(getCasella(c) instanceof CasellaCarattere) || orientamento == null)
            return null;
        CasellaCarattere casella = (CasellaCarattere) getCasella(c);
        return (orientamento == Orientamento.VERTICALE) ? casella.getVerticalDef() : casella.getHorizontalDef();
    }
    
    /**
     * Metodo get che ritorna la matrice di Caselle che compongono il Cruciverba.
     * @return
     */
    public Casella[][] getBoard()
    {
        return this.board; 
    }
    
    /**
     * imposta la board del cruciverba
     * @param board
     */
    protected void setBoard(Casella[][] board) {
        this.board = board;
    }

    /**
     * Dato in input un oggetto di tipo Point ritorna la casella che si trova
     * nelle coordinate indicate dal Point. Ritorna null se le coordinate sono
     * errate.
     * @param c
     * @return la Casella indicata dal Point
     */
    public Casella getCasella(Point c)
    {
        int x = c.x, y = c.y;
        if (x < 0 || x >= this.larghezza || y < 0 || y >= this.altezza)
            return null;
        return board[y][x];
    }

    /**
     * @return id del mini-schema. Utilizzato nel toString()
     */
    public Character getId() { return this.id; }
    public Integer getAltezza() { return this.altezza; }
    public Integer getLarghezza() { return this.larghezza; }
    public String getDominio() { return this.dominio; }
    public Double getDifficulty() { return this.difficulty; }
    public Map<Character, CasellaPortale> getCasellePortali(){ return this.casellePortali; }
    
    public void setDominio(String dominio) { this.dominio = dominio; }
    public void setDifficulty(Double difficulty) { this.difficulty = difficulty; }
   
}