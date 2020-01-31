package it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella;

import java.util.Random;

import it.uniroma1.lcl.crucyservlet.ClassiBuilder.definizione.Definizione.DifficultyDef;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.definizione.Definizione;

/**
 * @author Davide Montella
 *
 */
public class CasellaCarattere extends Casella
{
    private Character character;

    private Character tempCharacter = ' ';
    
    /**
     * Intero che rappresenta il numero della casella. Se la casella non fa
     * parte dell'inizio di nessuna parola, allora il campo è null.
     **/
    private Integer boxNumber;
    /**
     * Istanza della classe Definizione che contiene la definizione della parola
     * verticale della casella in più forme
     **/
    private Definizione verticalDef;
    /**
     * Istanza della classe Definizione che contiene la definizione della parola
     * orizzontale della casella in più forme
     **/
    private Definizione horizontalDef;
    
    public CasellaCarattere(int x, int y, Character c)
    {
        super(x, y);
        setCharacter(c);
        setTempCharacter(' ');
    }

    public void setCharacter(Character c) { character = c; }
    public void setTempCharacter(Character c) { tempCharacter = c; }
    
    public Character getTempCharacter() { return tempCharacter; }
    public Character getCharacter() {return character;}
    public Definizione getVerticalDef() { return verticalDef; }
    public Definizione getHorizontalDef() { return horizontalDef; }
    //public DifficultyDef getVerticalDifficulty() {return (verticalDef == null) ? null : verticalDef.getDifficulty();}
    //public DifficultyDef getHorizontalDifficulty() {return (horizontalDef == null) ? null : horizontalDef.getDifficulty();}
    public Integer getBoxNumber() {return boxNumber;}
    
    //da sistemare!
    public double getDifficulty()
    {
        if(verticalDef == null && horizontalDef != null)
            return getValoreEnum(horizontalDef.getDifficulty());
        else if( horizontalDef == null && verticalDef != null)
            return getValoreEnum(verticalDef.getDifficulty());
        else if(horizontalDef == null && verticalDef == null)
            return new Random().nextDouble();
        return Math.max(getValoreEnum(verticalDef.getDifficulty()),
                getValoreEnum(horizontalDef.getDifficulty()));
    }
    
    private double getValoreEnum(DifficultyDef dif)
    {
        if(dif == DifficultyDef.E)
            return 0.01;
        else if(dif == DifficultyDef.M)
            return 0.35;
        else 
            return 0.67;
    }
    
    public void setVerticalDef(Definizione d) {verticalDef=d;}
    public void setHorizontalDef(Definizione d) {horizontalDef=d;}
    public void setBoxNumber(int n) {boxNumber = n;}
    
    @Override
    public String toString()
    {
        return getCharacter().toString();
    }
}