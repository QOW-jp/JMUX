package com.qow;

import com.qow.util.JsonReader;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class JMUXClient {
    private final String ip;
    private final int port;

    public JMUXClient(String jsonPath) {
        JsonReader jsonReader = new JsonReader(jsonPath);
        JSONObject controlJs = jsonReader.getJSONObject();
        ip = controlJs.getString("server-ip");
        port = controlJs.getInt("port");
    }

    public boolean send(Command command, int tokenID) throws IOException, UntrustedConnectException {
        try (Socket socket = new Socket(ip, port)) {
            try (OutputStream out = socket.getOutputStream(); InputStream in = socket.getInputStream()) {
                //送受信データバッファ
                byte[] data = new CommandFormatter(command, tokenID).getData();

                //バイト配列を送信
                out.write(data);

                //データを受信
                int readSize = in.read(data);
                //受信データを読み込んだサイズまで切り詰め
                byte[] receiveData = Arrays.copyOf(data, readSize);
                return receiveData[0] == 1;
            } catch (IOException e) {
                throw new UntrustedConnectException("disconnect");
            }
        }
    }
}
