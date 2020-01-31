package it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella;

/**
 * @author Davide Montella
 *
 */
public class CasellaSpeciale extends Casella
{
    public enum TipoCasella
    {
        BLACK, INVISIBLE
    };

    /**
     * Valore booleano; se è true allora la casella è una casella nera,
     * altrimenti potrebbe essere una casella con carattere o invisibile
     **/
    private Boolean isBlack = false;
    /**
     * Valore booleano; se è true allora la casella è una casella invisibile,
     * altrimenti potrebbe essere una casella con carattere nera
     **/
    private Boolean isInvisible = false;

    public CasellaSpeciale(int x, int y, TipoCasella t)
    {
        super(x, y);
        setTipoCasella(t);
    }

    private void setTipoCasella(TipoCasella t)
    {
        switch (t)
        {
        case BLACK:
            isBlack = true;
            break;
        case INVISIBLE:
            isInvisible = true;
        }
    }

    public boolean isBlack() { return isBlack;}
    public boolean isInvisible() { return isInvisible;}
    
    @Override
    public String toString()
    {
        return (isBlack) ? "*" : " ";
    }
}
