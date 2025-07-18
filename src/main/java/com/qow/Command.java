package com.qow;

public enum Command {
    EXCEPTION(-1),
    EXIT(0),
    EXIST(1),
    ENABLE(2),
    DISABLE(3);

    private final int id;
    Command(int id) {
        this.id = id;
    }
    public int getID(){
        return id;
    }
}
