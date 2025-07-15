package com.qow;

import com.qow.util.JsonReader;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JMUX implements Runnable {
    private final Thread server;
    private final ServerSocket serverSocket;
    private final int byteSize;
    private final String stopCommand;
    private boolean enable;
    private boolean run;

    private final Map<Integer, Token> tokenMap;

    public JMUX(String path) throws IOException {
        enable = false;
        JsonReader jsonReader = new JsonReader(path);
        JSONObject json = jsonReader.getJSONObject();

        byteSize = json.getInt("byte-size");
        stopCommand = json.getString("stop-command");
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

    public boolean enable() {
        if (enable) return false;
        if (run) return true;
        enable = true;
        server.start();
        return false;
    }

    @Override
    public void run() {
        run = true;
        int err = 0;
        while (run) {
            try (Socket sock = serverSocket.accept()) {
                try (InputStream in = sock.getInputStream(); OutputStream out = sock.getOutputStream()) {
                    //受信データバッファ
                    byte[] data = new byte[byteSize];

                    int readSize = in.read(data);
                    if (readSize == -1) continue;

                    //受信データを読み込んだサイズまで切り詰め
                    byte[] receiveData = Arrays.copyOf(data, readSize);

                    //バイト配列を文字列に変換
                    String line = new String(receiveData, StandardCharsets.UTF_8);

//                    commandRule.command(line);
                    byte[] redata = command(new Byte2Command(receiveData));

                    //送られてきた文字列をUTF-8形式のバイト配列に変換して返信
                    out.write(redata);

                    if (line.equals("STOP_JMUX")) {
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

    public byte[] command(Byte2Command command) {
        switch (command.getCommand()) {
            case EXIST:
                break;
            case STOP:
                break;
            case REQ:
                break;
            case EXCEPTION:
                break;
        }
    }

    public void addToken(Token token) {
        tokenMap.put(token.getTokenID(), token);
    }
}