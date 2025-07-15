package com.qow;

public class Byte2Command {
    private final Command command;

    public Byte2Command(byte[] data) {
        /*
         * byte[0] = orderID
         * byte[1]~byte[4] = TokenID
         * byte[5]~byte[64] =
         */
        int orderID = data[0];
        switch (orderID) {
            case 4:
                command = Command.EXIST;
                break;
            case 16:
                command = Command.STOP;
                break;
            case 64:
                command = Command.REQ;
                break;
            default:
                command = Command.EXCEPTION;
        }


    }

    public Command getCommand() {
        return command;
    }
}
