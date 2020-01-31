package it.uniroma1.lcl.crucyservlet.ClassiBuilder.definizione;

/**
 * Classe DefinizioneImmagine che contiene una definizione scritta
 * e in aggiunta, un link ad un image associata alla parola
 * @author Papi Suadoni
 *
 */
public class DefinizioneImmagine extends Definizione
{
    /**
     * Link dell'immagine associata alla parola
     */
    private String image;
    
    public DefinizioneImmagine(Long index, String word, String definition, String dominio, 
            DifficultyDef difficulty, String image)
    {
        super(index, word, definition, dominio, difficulty);
        this.image = image;
    }

    public String getImage() { return image; }
    
    @Override
    public String toString()
    {
        return getIndex() + "::" + getWord() + "::" + getDefinition()+ "::" + getDifficulty() + "::" + image;
    }
}
