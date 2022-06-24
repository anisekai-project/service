package me.anisekai.toshiko.helpers.containers;

public class VariablePair<A, B> {

    private final A first;
    private final B second;

    public VariablePair(A first, B second) {

        this.first  = first;
        this.second = second;
    }

    public A getFirst() {

        return this.first;
    }

    public B getSecond() {

        return this.second;
    }
}
