package com.qow.jmux;

import com.qow.qtcp.TCPServer;
import com.qow.qtcp.UntrustedConnectException;
import com.qow.util.ThreadStopper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JMUX(Java Multiplexer)は複数のJavaプログラムを同時に遠隔で操作することができる<br>
 * {@link Token}を継承したクラスを{@link JMUX#addToken(Token)}により追加し、遠隔の場合は{@link JMUXClient#send(Command, int)}により呼び出す
 *
 * @version 2025/09/30
 * @since 1.0.0
 */
public class JMUX extends TCPServer implements Runnable {
    public static final byte[] PROTOCOL_ID = "jmux-s1.2.0".getBytes(StandardCharsets.UTF_8);
    private final Thread server;
    private final Map<Integer, Token> tokenMap;
    private final ThreadStopper stopper;
    private boolean enable;
    private boolean run;

    /**
     * ポート番号と対象クライアントIPアドレスを指定し、入力を受け付けるIPアドレスを制限する。
     *
     * @param port サーバーポート番号
     * @param host クライアントIPアドレス
     * @throws IOException サーバーソケットに例外が発生した場合
     */
    public JMUX(int port, String host) throws IOException {
        super(port, PROTOCOL_ID, host);

        tokenMap = new HashMap<>();
        enable = false;

        server = new Thread(this);
        stopper = new ThreadStopper();
    }

    /**
     * ポート番号を設定する。
     *
     * @param port サーバーポート番号
     * @throws IOException サーバーソケットに例外が発生した場合
     */
    public JMUX(int port) throws IOException {
        super(port, PROTOCOL_ID);

        tokenMap = new HashMap<>();
        enable = false;

        server = new Thread(this);
        stopper = new ThreadStopper();
    }

    /**
     * 入力受付するサーバーを有効化する。
     *
     * @return 正常に実行できた場合
     */
    public synchronized boolean enable() {
        if (enable) return false;
        if (run) return false;
        enable = true;
        run = true;
        server.start();
        return true;
    }

    /**
     * 次回からの入力受付を無効化する
     */
    public void disable() {
        enable = false;
    }

    @Override
    public void run() {
        int err = 0;
        while (enable) {
            try {
                listeningRequest();
                err = 0;
            } catch (IOException e) {
                if (1 < err++) System.err.println(e.getMessage());
            } catch (UntrustedConnectException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            run = false;
            enable = false;
            stopper.start();
        }
    }

    @Override
    public byte[] read(byte[] data) {
        CommandFormatter cf = new CommandFormatter(data);
        Command command = cf.getCommand();

        if (command == Command.EXIT) {
            enable = false;
        }

        int tokenID = cf.getTokenID();

        try {
            return new byte[]{(byte) (command(command, tokenID) ? 1 : 0)};
        } catch (NullPointerException ignored) {
            return new byte[]{0};
        }
    }

    /**
     * コマンドを実行する
     *
     * @param command コマンド
     * @param tokenID 対象ID
     * @return 各メソッドの実行結果を返す
     */
    public boolean command(Command command, int tokenID) {
        return switch (command) {
            case EXIT -> {
                disable();
                List<Token> tokenList = new ArrayList<>(tokenMap.values());
                for (Token token : tokenList) {
                    if (token.isEnable()) token.disable();
                }
                yield true;
            }
            case EXIST -> tokenMap.get(tokenID).isEnable();
            case LISTED -> tokenMap.containsKey(tokenID);
            case ENABLE -> tokenMap.get(tokenID).enable();
            case DISABLE -> tokenMap.get(tokenID).disable();
            case RESET -> tokenMap.get(tokenID).reset();
            case EXCEPTION -> false;
        };
    }

    /**
     * {@link Token}を追加する
     *
     * @param token 追加するToken
     */
    public void addToken(Token token) {
        tokenMap.put(token.getTokenID(), token);
    }

    /**
     * {@link Token}を削除する
     *
     * @param tokenID 対象のID
     */
    public void removeToken(int tokenID) {
        tokenMap.remove(tokenID);
    }

    /**
     * 入力受付するサーバーが終了するまで待機する
     */
    public void waitForServer() {
        stopper.stop();
    }
}