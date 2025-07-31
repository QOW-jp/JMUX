package com.qow.jmux;

import com.qow.util.JsonReader;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * {@link JMUX}と通信するためのクライアント
 *
 * @version 2025/08/01
 * @since 1.0.0
 */
public class JMUXClient {
    private final String ip;
    private final int port;

    /**
     * configに従い、対象サーバーIPアドレスとポート番号を設定する
     *
     * @param jsonPath configファイルのパス
     */
    public JMUXClient(String jsonPath) {
        JsonReader jsonReader = new JsonReader(jsonPath);
        JSONObject controlJs = jsonReader.getJSONObject();
        ip = controlJs.getString("server-ip");
        port = controlJs.getInt("port");
    }

    /**
     * {@link JMUX}にデータを送信する
     *
     * @param command 送信するコマンド
     * @param tokenID 送信するID
     * @return 正常に送信できた場合True
     * @throws UntrustedConnectException 接続が途切れた場合
     * @throws ClosedServerException     接続ができなかった場合
     */
    public boolean send(Command command, int tokenID) throws UntrustedConnectException, ClosedServerException {
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
        } catch (IOException e) {
            throw new ClosedServerException("no server");
        }
    }
}
