package it.uniroma1.lcl.crucyservlet.ClassiBuilder;

import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.Casella;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaCarattere;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella.CasellaPortale;

import java.util.*;

/**
 *
 * @author Papi Suadoni
 *
 */
public class CruciverbaServer extends Cruciverba implements Cloneable
{
    /**
     * Definzioni delle parole presenti nel cruciverba
     */
    private String definizioni;
    /**
     * Dizionario che ha come valore una CasellaPortale e come chiave
     * una stringa che identifica in maniera univoca in quale cruverba
     * e in quale casella si deve collegare.
     */
    private Map<Character, CasellaPortale> casellePortali;

    private TreeSet<CasellaPortale> portals = new TreeSet<>();
    /**
     * Set che contiene tutte le parole contenute nel Cruciverba
     */
    private List<String> internalWords;
    /**
     * Set che contiene tutti i prefissi (le prime 5 lettere) di
     * tutte le parole contenute nel cruciverba;
     */
    private Set<String> internalPrefixWords;

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
     * @param definizioni: indici delle parole
     * @param internalWords: set delle paroleDefinizioni
     * @param casellePortali: dizionario delle casellePortali
     */
    public CruciverbaServer(int altezza, int larghezza, Casella[][] board, Character id, String definizioni,
                            Set<String> internalWords, Set<String> internalPrefixWords,
                            Map<Character, CasellaPortale> casellePortali)
    {
        super(altezza, larghezza, board, id, casellePortali);
        this.definizioni = definizioni;
        this.casellePortali = casellePortali;
        this.portals.addAll(casellePortali.values());
        this.internalWords = new ArrayList<>(internalWords);
        this.internalPrefixWords = internalPrefixWords;
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
        if (x < 0 || x >= this.getLarghezza() || y < 0 || y >= this.getAltezza())
            return null;
        return getBoard()[y][x];
    }
   
    public List<String> getInternalWords() { return this.internalWords; }
    public Set<String> getInternalPrefixWords() { return this.internalPrefixWords; }
    public Map<Character, CasellaPortale> getCasellePortali(){ return this.casellePortali; }
    /**
     * Prende in input un Character che indica l'id della casellaPortale
     * e ne ritorna il suo carattere (la lettera che ha nel Cruciverba)
     */
    public Character getCharOfPortal(Character c)
    {
        CasellaPortale cp = this.casellePortali.get(c);
        if (cp == null)
            return null;
        return cp.getCharacter();
    }
    public void setInternalWords(Set<String> words){ this.internalWords = new ArrayList<>(words);}
    public void setInternalPrefixWords(Set<String> prefixWords){ this.internalWords = new ArrayList<>(prefixWords);}
    public void setCasellePortali(Map<Character, CasellaPortale> map) { this.casellePortali = map; }

    /**
     * Clone che serve per chi si occupa di unire i mini-schemi.
     * Durante le prove di incastro dei vari mini-schemi vengono modificate
     * solo le casellePortale, percio' basta clonare solo queste caselle
     */
    @Override
    public CruciverbaServer clone() {
        try {
            CruciverbaServer cS = (CruciverbaServer) super.clone();
            Map<Character, CasellaPortale> mappa = new HashMap<>();
            for(Map.Entry<Character, CasellaPortale> entry : cS.casellePortali.entrySet())
            {
                CasellaPortale temp = (CasellaPortale) entry.getValue();
                Point punto = temp.getCoordinates();
                CruciverbaServer crucyToSet = (CruciverbaServer) temp.getCruciverba();
                if(temp.getCasella() == null) {
                    mappa.put(entry.getKey(), temp);
                    continue;
                }
                Character c = ((CasellaCarattere)temp.getCasella()).getCharacter();
                CasellaCarattere cPort = new CasellaPortale(temp.getCasella().getCoordinates().getX(),
                        temp.getCasella().getCoordinates().getY(), c);
                temp = new CasellaPortale(punto.x, punto.y, temp.getCharacter());
                temp.setCasella(cPort);
                temp.setCruciverba(crucyToSet);
                Casella[][] boardClone = copyBoard();
                boardClone[punto.y][punto.x] = temp;
                cS.setBoard(boardClone);
                mappa.put(entry.getKey(), temp);
            }
            cS.casellePortali = new HashMap<>(mappa);
            return  cS;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Copia il la board del Cruciverba
     * @return
     */
    public Casella[][] copyBoard(){
        Casella[][] boardClone = new Casella[getAltezza()][getLarghezza()];
        for (int i = 0; i < getAltezza(); i++) {
            boardClone[i] = Arrays.copyOf(getBoard()[i], getBoard()[i].length);
        }
        return boardClone;
    }

    @Override
    public String toString()
    {
        return generaSchema(new StringBuilder(), new ArrayList<Character>(), this).toString();
    }

    /**
     * Metodo ricorsivo che ritorna uno StringBuilder contenente la rappresentazione
     * di tutti i cruciverba che si trovano all'interno di questo Cruciverba, compreso
     * se stesso. Se uno Cruiciverba e' stato gia' inserito non lo reinserisce!
     * @param sb
     * @param chars tiene traccia dei Cruciverba inseriti
     * @param crucy Cruciverba corrente
     * @return
     */
    private StringBuilder generaSchema(StringBuilder sb, ArrayList<Character> chars,CruciverbaServer crucy)
    {
        if(crucy == null) return null; //caso base
        sb.append(crucy.crucyToString());
        chars.add(crucy.getId());
        for(Map.Entry<Character, CasellaPortale> entry : crucy.getCasellePortali().entrySet()){
            //controlla che non che questo crucy non sia stato gia' inserito
            if(!chars.contains(entry.getKey()))
            {
                chars.add(entry.getKey());
                generaSchema(sb, chars, (CruciverbaServer)entry.getValue().getCruciverba());
            }
        }
        return sb;
    }

    /**
     * @return la rappresentazione in stringa della matrice del Cruciverba
     */
    public String crucyToString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getId()).append(" ").append(getAltezza()).append("x")
                .append(getLarghezza()).append("\n");
        for (int i = 0; i < getAltezza(); i++)
        {
            for (int j = 0; j < getLarghezza(); j++)
                sb.append(getBoard()[i][j]).append(",");
            sb.append("\n");
        }
        sb.append(this.definizioni);
        return sb.toString();
    }

    /**
     * Stampa il cruciverba senza utilizzare generaSchema()
     * Utilizzato nella classe UnioneSchemi per effettuare dei controlli
     * @return
     */
    public String singleToString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getAltezza(); i++)
        {
            for (int j = 0; j < getLarghezza(); j++)
                if (getBoard()[i][j].isPortal()){
                    CasellaPortale casellaPortale = (CasellaPortale) getBoard()[i][j];
                    sb.append(casellaPortale.getCharacter() + " ");
                }
                else sb.append(getBoard()[i][j]).append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getOrderedPortals()
    {
        StringBuffer stringBuffer = new StringBuffer();
        for(CasellaPortale casellaPortale : portals)
        {
            stringBuffer.append(casellaPortale.getCharacter());
        }
        return stringBuffer.toString();
    }
}