package com.example.pillmasterjfx;

import com.pi4j.Pi4J;

public class Dispenser {
    private static final int NUMBER_OF_CANISTERS = 5;

    public enum Canister {
        A,B,C,D,E
    }
    private Canister currentCanister;

    public Dispenser() {
        currentCanister = Canister.A;
    }

    public void dispense() {
        var pi4j = Pi4J.newAutoContext();
        System.out.println("Context loaded!");
        pi4j.shutdown();
    }
}
