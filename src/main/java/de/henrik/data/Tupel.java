package de.henrik.data;

import java.util.Objects;

public class Tupel<T, U> {
    private final T first;
    private final U second;

    public Tupel(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return first;
    }

    public U second() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Tupel) obj;
        return this.first().equals(that.first) && this.second().equals(that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "["  + first + "/" + second + ']';
    }

}

