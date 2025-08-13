package com.qow.jmux;

/**
 * {@link JMUX}を操作するためのコマンド
 *
 * @version 2025/08/01
 * @since 1.0.0
 */
public enum Command {
    EXCEPTION(-1),
    EXIT(0),
    EXIST(1),
    LISTED(2),
    ENABLE(3),
    DISABLE(4),
    RESET(5);

    private final int id;

    Command(int id) {
        this.id = id;
    }

    /**
     * @return ID
     */
    public int getID(){
        return id;
    }
}
