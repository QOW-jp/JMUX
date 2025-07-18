package com.qow;

public class CommandFormatter {
    public static final int BYTE_SIZE = 5;
    private Command command;
    private int tokenID;
    private byte[] data;

    public CommandFormatter(Command command, int tokenID) {
        init();
        data = new byte[BYTE_SIZE];
        data[0] = (byte) command.getID();
        for (int i = 0; i < 4; i++) {
            data[1 + i] = (byte) ((tokenID >> (8 * (3 - i))) & Byte.MAX_VALUE);
        }
    }

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

    public Command getCommand() {
        return command;
    }

    public int getTokenID() {
        return tokenID;
    }

    public byte[] getData() {
        return data;
    }
}
