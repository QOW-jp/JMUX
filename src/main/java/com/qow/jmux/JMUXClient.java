package com.qow.jmux;

import com.qow.qtcp.ClosedServerException;
import com.qow.qtcp.TCPClient;
import com.qow.qtcp.UntrustedConnectException;

import java.io.IOException;

/**
 * {@link JMUX}と通信するためのクライアント
 *
 * @version 2025/10/08
 * @since 1.0.0
 */
public class JMUXClient extends TCPClient {
    /**
     * 対象サーバーIPアドレスとポート番号を設定する。
     *
     * @param host       サーバーIPアドレス
     * @param port       ポート番号
     * @param protocolID 識別子
     */
    public JMUXClient(String host, int port, byte[] protocolID) {
        super(host, port, protocolID);
    }

    /**
     * {@link JMUX}にデータを送信する。
     *
     * @param command 送信するコマンド
     * @param tokenID 送信するID
     * @return {@link Command}の結果を返す
     * @throws UntrustedConnectException 接続が途切れた場合
     */
    public boolean send(Command command, int tokenID) throws UntrustedConnectException, IOException, ClosedServerException {
        //送受信データバッファ
        byte[] data = new CommandFormatter(command, tokenID).getData();
        //受信データを読み込んだサイズまで切り詰め
        byte[] receiveData = request(data);
        return receiveData[0] == 1;
    }
}
