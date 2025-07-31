package com.qow.jmux;

/**
 * 接続先のサーバーポートが閉じていた場合に呼び出される
 *
 * @version 2025/08/01
 * @since 1.0.0
 */
public class ClosedServerException extends Exception {
    /**
     * @param message 例外メッセージ
     */
    public ClosedServerException(String message) {
        super(message);
    }
}
