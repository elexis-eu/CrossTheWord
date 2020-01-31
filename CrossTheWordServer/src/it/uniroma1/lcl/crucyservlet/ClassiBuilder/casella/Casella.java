package it.uniroma1.lcl.crucyservlet.ClassiBuilder.casella;

import it.uniroma1.lcl.crucyservlet.ClassiBuilder.Point;

/**
 * @author Davide Montella
 *
 */
public abstract class Casella
{
    /**
     * Un istanza Coordinate che racchiude le informazioni (x,y) della casella
     **/
    private Point coordinates;

    public Casella(int x, int y)
    {
        this.coordinates = new Point(x,y);
    }
    
    public Point getCoordinates() { return coordinates; }
    
    public boolean isCharacter() { return this instanceof CasellaCarattere; }
    public boolean isPortal() { return this.getClass().equals(CasellaPortale.class); }
    public boolean isSpecial() { return this.getClass().equals(CasellaSpeciale.class); }
    
}
