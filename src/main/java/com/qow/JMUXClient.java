package com.qow;

import com.qow.util.JsonReader;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JMUXClient {
    private final String ip;
    private final int port;

    public JMUXClient(String jsonPath) {
        JsonReader jsonReader = new JsonReader(jsonPath);
        JSONObject controlJs = jsonReader.getJSONObject("control");
        ip = controlJs.getString("client-ip");
        port = controlJs.getInt("port");
    }

    public boolean send(Command command, int tokenID) throws IOException {
        try (Socket socket = new Socket(ip, port)) {
            try (OutputStream out = socket.getOutputStream(); InputStream in = socket.getInputStream()) {
                //受信データバッファ
                byte[] data = new byte[CommandFormatter.BYTE_SIZE];


                //文字列をUTF-8形式のバイト配列に変換して送信
                out.write(line.getBytes(StandardCharsets.UTF_8));

                //データを受信
                int readSize = in.read(data);
                //受信データを読み込んだサイズまで切り詰め
                byte[] receiveData = Arrays.copyOf(data, readSize);
                String receiveLine = new String(receiveData, StandardCharsets.UTF_8);

                return true;
            }
        }
        return false;
    }
}
