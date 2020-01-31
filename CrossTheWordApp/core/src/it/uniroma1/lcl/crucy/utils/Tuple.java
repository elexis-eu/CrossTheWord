package it.uniroma1.lcl.crucy.utils;

import java.io.Serializable;

public class Tuple<X, Y> implements Serializable {
    private X x;
    private Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getOne() { return x; }

    public Y getTwo() { return y; }
}