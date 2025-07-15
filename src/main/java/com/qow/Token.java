package com.qow;

public abstract class Token {
    private final int tokenID;

    public Token(int tokenID) {
        this.tokenID = tokenID;

    }

    public int getTokenID() {
        return tokenID;
    }


    public abstract void start();

    public abstract void stop();
}
