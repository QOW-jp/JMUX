package com.qow.jmux;

public abstract class Token {
    private final int tokenID;
    private boolean run;

    public Token(int tokenID) {
        this.tokenID = tokenID;
        run = false;
    }

    public int getTokenID() {
        return tokenID;
    }

    public boolean isEnable() {
        return run;
    }

    protected synchronized boolean enable() {
        if (run) return false;
        run = true;
        new Thread(this::start).start();
        return true;
    }

    protected synchronized boolean disable() {
        if (!run) return false;
        stop();
        run = false;
        return true;
    }

    public boolean reset() {
        return true;
    }

    public abstract void start();

    public abstract void stop();

}
