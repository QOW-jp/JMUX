package com.qow.jmux;

/**
 * 接続先のサーバーとの接続が切れた場合に呼び出される
 *
 * @version 2025/08/01
 * @since 1.0.0
 */
public class UntrustedConnectException extends Exception {
    /**
     * @param message 例外メッセージ
     */
    public UntrustedConnectException(String message) {
        super(message);
    }
}
