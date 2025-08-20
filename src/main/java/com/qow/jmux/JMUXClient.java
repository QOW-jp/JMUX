package com.qow.jmux;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * {@link JMUX}と通信するためのクライアント
 *
 * @version 2025/08/20
 * @since 1.0.0
 */
public class JMUXClient {
    private final String host;
    private final int port;

    /**
     * 対象サーバーIPアドレスとポート番号を設定する。
     *
     * @param host サーバーIPアドレス
     * @param port ポート番号
     */
    public JMUXClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * {@link JMUX}にデータを送信する。
     *
     * @param command 送信するコマンド
     * @param tokenID 送信するID
     * @return {@link Command}の結果を返す
     * @throws UntrustedConnectException 接続が途切れた場合
     * @throws ClosedServerException     接続ができなかった場合
     */
    public boolean send(Command command, int tokenID) throws UntrustedConnectException, ClosedServerException {
        try (Socket socket = new Socket(host, port)) {
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
