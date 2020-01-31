package it.uniroma1.lcl.crucyservlet.ClassiBuilder.definizione;


/**
 * Contiene la definizione scritta di una parola. Contiene la parola stessa, 
 * l'indice secondo l'indicizzazione, dominio e difficolta.
 * @author Papi Suadoni
 *
 */
public class Definizione
{
    /**
     * Indice univoco della definizione-parola
     */
    private Long index;
    /**
     * Parola stessa
     */
    private String word;
    /**
     * Difficolta secondo l'enum
     */
    private DifficultyDef difficulty;

    /**
     * Semplice definizione scritta della parola
     */
    private String definition;
    
    public enum DifficultyDef
    {
        E, //facile
        M, //medio
        H; //difficile
    }

    public Definizione(Long index, String word, String definition, String dominio, 
            DifficultyDef difficulty)
    {
        this.index = index;
        this.word = word;
        this.definition = definition;
        this.difficulty = difficulty;
    }

    //getter:
    public Long getIndex() { return this.index; }
    public String getWord() { return this.word; }
    public DifficultyDef getDifficulty() { return this.difficulty; }
    public String getDefinition() { return this.definition; }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj.getClass() != this.getClass())
            return false;
        Definizione def = (Definizione) obj;
        return this.word == def.word && this.difficulty == def.difficulty  
                && this.getDefinition().equals(def.getDefinition());
    }

    @Override
    public String toString()
    {
        return index + "::" + word + "::" + definition  + "::" + difficulty;
    }

}
