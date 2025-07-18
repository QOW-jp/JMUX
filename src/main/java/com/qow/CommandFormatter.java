package com.qow;

public class CommandFormatter {
    public static final int BYTE_SIZE = 5;
    private Command command;
    private int tokenID;
    private byte[] data;

    public CommandFormatter(Command command, int tokenID) {
        data = new byte[BYTE_SIZE];
        data[0] = (byte) command.ordinal();
        for (int i = 0; i < 4; i++) {
            data[1 + i] = (byte) ((tokenID >> 8 * (3 - i)) & Byte.MAX_VALUE);
        }
    }

    public CommandFormatter(byte[] data) {
        command = Command.valueOf(String.valueOf(data[0]));
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
