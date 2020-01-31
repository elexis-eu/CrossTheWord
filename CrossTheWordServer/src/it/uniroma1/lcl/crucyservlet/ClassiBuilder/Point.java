package it.uniroma1.lcl.crucyservlet.ClassiBuilder;

/**
 * Created by bejgu on 05/08/2016.
 */
public class Point implements Comparable<Point>
{
    public Integer x;
    public Integer y;
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int compareTo(Point point)
    {
        int comparatorX = x.compareTo(point.x);
        if(comparatorX == 0)
            return y.compareTo(point.y);
        return comparatorX;
    }
}
