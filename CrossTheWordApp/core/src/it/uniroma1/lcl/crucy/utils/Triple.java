package it.uniroma1.lcl.crucy.utils;

public class Triple<T, U, V> {

    private  T first;
    private  U second;
    private  V third;

    public Triple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() { return first; }
    public U getSecond() { return second; }
    public V getThird() { return third; }
    public void setFirst(T s) {  first = s; }
    public void setSecond(U s) { second = s; }
    public void setThird(V s) { third = s; }

}