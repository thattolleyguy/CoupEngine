package com.ttolley.coup;

/**
 * Created by tylertolley on 2/8/17.
 */
public class MutableInteger {
    private int value;
    public MutableInteger(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public void increment(){
        value++;
    }
}
