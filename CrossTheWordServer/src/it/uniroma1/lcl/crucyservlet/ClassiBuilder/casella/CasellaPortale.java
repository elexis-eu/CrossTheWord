package it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella;

import it.uniroma1.lcl.crucyservlet.ClassiBuilder.Cruciverba;

/**
 * @author Davide Montella
 *
 */
public class CasellaPortale extends CasellaCarattere implements Cloneable, Comparable<CasellaPortale>
{
    private Cruciverba riferimentoCruciverba;
    private Casella casellaSecondoCruciverba;
    private Boolean portalUsed = false;

    public CasellaPortale(int x, int y, Character c)
    {
        super(x, y, c);
    }

    public void setCasella(Casella c) { casellaSecondoCruciverba = c;}
    public void setCruciverba(Cruciverba c) { riferimentoCruciverba = c; }
    public void setPortalUsed(){portalUsed = true;}
    
    public Casella getCasella() { return casellaSecondoCruciverba; }
    public Cruciverba getCruciverba() { return riferimentoCruciverba; }
    public Boolean isPortalUsed(){return portalUsed;}
    
    /**
     * Ritorna la rappresentazione della CasellaPortale. Essa sarà racchiusa da
     * due parentessi tonde per essere differenziata dalle altre caselle.
     * I valori (max 4) sono separati da un punto '.'.
     * Il primo valore è il carattere, il secondo l'id(char) del Cruciverba portale,
     * terzo e quarto le coordinate x e y della CasellaPortale del Cruiciverba portale
     * Gli ultimi 3 valori possono essere assenti se "questo" cruciverba è raggiunto da questa
     * CasellaPortale
     */
    @Override
    public String toString()
    {
        String str;
        if(this.casellaSecondoCruciverba != null)
            str = "." + (int)casellaSecondoCruciverba.getCoordinates().getX() + "." +
                    (int)casellaSecondoCruciverba.getCoordinates().getY();
        else
            str = "";
        return "(" + this.getCharacter() + "." +
                ((riferimentoCruciverba == null) ? "" : riferimentoCruciverba.getId()) + str + ")";
    }
    
    protected String singleToString(){
        return getCharacter().toString();
    }



    @Override
    public int compareTo(CasellaPortale casellaPortale)
    {
        return getCoordinates().compareTo(casellaPortale.getCoordinates());
    }
}