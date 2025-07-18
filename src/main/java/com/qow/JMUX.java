package com.qow;

import com.qow.util.JsonReader;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JMUX implements Runnable {
    private final Thread server;
    private final ServerSocket serverSocket;
    private final Map<Integer, Token> tokenMap;
    private boolean enable;
    private boolean run;

    public JMUX(String path) throws IOException {
        enable = false;
        JsonReader jsonReader = new JsonReader(path);
        JSONObject json = jsonReader.getJSONObject();

        boolean bind = json.getBoolean("bind-ip");
        int port = json.getInt("port");

        if (bind) {
            String clientIp = json.getString("client-ip");
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName(clientIp));
        } else {
            serverSocket = new ServerSocket(port);
        }
        server = new Thread(this);

        tokenMap = new HashMap<>();
    }

    public synchronized boolean enable() {
        if (enable) return false;
        if (run) return false;
        enable = true;
        run = true;
        server.start();
        return true;
    }

    public void disable() {
        enable = false;
    }

    @Override
    public void run() {
        int err = 0;
        while (enable) {
            try (Socket sock = serverSocket.accept()) {
                try (InputStream in = sock.getInputStream(); OutputStream out = sock.getOutputStream()) {
                    //受信データバッファ
                    byte[] data = new byte[CommandFormatter.BYTE_SIZE];

                    int readSize = in.read(data);
                    if (readSize == -1) continue;

                    //受信データを読み込んだサイズまで切り詰め
                    byte[] receiveData = Arrays.copyOf(data, readSize);

                    CommandFormatter cf = new CommandFormatter(receiveData);
                    Command command = cf.getCommand();
                    int tokenID = cf.getTokenID();

                    try {
                        byte[] redata = {(byte) (command(command, tokenID) ? 1 : 0)};

                        out.write(redata);
                    }catch (NullPointerException ignored){
                        out.write(new byte[]{0});
                    }

                    if (command == Command.EXIT) {
                        break;
                    }

                    err = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                if (1 < err++) e.printStackTrace();
            }
        }
        run = false;
    }

    public boolean command(Command command, int tokenID) {
        return switch (command) {
            case EXIT -> {
                disable();
                yield true;
            }
            case EXIST -> tokenMap.get(tokenID).isEnable();
            case ENABLE -> tokenMap.get(tokenID).enable();
            case DISABLE -> tokenMap.get(tokenID).disable();
            case EXCEPTION -> false;
        };
    }

    public void addToken(Token token) {
        tokenMap.put(token.getTokenID(), token);
    }

    public void removeToken(int tokenID) {
        tokenMap.remove(tokenID);
    }
}