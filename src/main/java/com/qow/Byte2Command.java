package com.qow;

public class Protocol4JMUX {
    private final int orderID;
    public Protocol4JMUX(byte[] data){
        orderID = data[0];
        

    }
    public int getOrderID(){
        return orderID;
    }
}
