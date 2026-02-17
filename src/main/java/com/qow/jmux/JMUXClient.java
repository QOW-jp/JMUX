package com.qow.jmux;

import com.qow.net.ClosedServerException;
import com.qow.net.UntrustedConnectException;
import com.qow.net.qtcp.TCPClient;

import java.net.SocketTimeoutException;

/**
 * {@link JMUX}と通信するためのクライアント
 *
 * @version 2025/11/25
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
     * @throws ClosedServerException     サーバーのポートが無効化されている場合
     */
    public boolean send(Command command, int tokenID) throws UntrustedConnectException, ClosedServerException, SocketTimeoutException {
        byte[] data = new CommandFormatter(command, tokenID).getData();
        byte[] receiveData = request(data, true);
        return receiveData[0] == 1;
    }
}
