package com.qow.jmux;

/**
 * {@link JMUX}と通信するためのプロトコル
 *
 * @version 2025/08/01
 * @since 1.0.0
 */
public class CommandFormatter {
    public static final int BYTE_SIZE = 5;
    private Command command;
    private int tokenID;
    private byte[] data;

    /**
     * {@link Command}とtokenIDをbyte配列に変換する
     *
     * @param command 変換するコマンド
     * @param tokenID 変換するID
     */
    public CommandFormatter(Command command, int tokenID) {
        init();
        data = new byte[BYTE_SIZE];
        data[0] = (byte) command.getID();
        for (int i = 0; i < 4; i++) {
            data[1 + i] = (byte) ((tokenID >> (8 * (3 - i))) & Byte.MAX_VALUE);
        }
    }

    /**
     * byte配列を{@link Command}とtokenIDに変換する
     *
     * @param data 変換したbyte配列
     */
    public CommandFormatter(byte[] data) {
        init();
        for (Command cmd : Command.values()) {
            if (data[0] == cmd.getID()) {
                command = cmd;
            }
        }
        tokenID = 0;
        for (int i = 0; i < 4; i++) {
            tokenID += (int) data[1 + i] << (8 * (3 - i));
        }
    }

    private void init() {
        command = Command.EXCEPTION;
        tokenID = -1;
        data = null;
    }

    /**
     * @return 変換されたコマンド
     */
    public Command getCommand() {
        return command;
    }

    /**
     * @return 変換されたtokenID
     */
    public int getTokenID() {
        return tokenID;
    }

    /**
     * @return 変換されたbyte配列
     */
    public byte[] getData() {
        return data;
    }
}
