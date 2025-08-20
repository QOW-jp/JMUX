package com.qow.jmux;

import com.qow.util.ThreadStopper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * JMUX(Java Multiplexer)は複数のJavaプログラムを同時に遠隔で操作することができる<br>
 * {@link Token}を継承したクラスを{@link JMUX#addToken(Token)}により追加し、遠隔の場合は{@link JMUXClient#send(Command, int)}により呼び出す
 *
 * @version 2025/08/20
 * @since 1.0.0
 */
public class JMUX implements Runnable {
    private final Thread server;
    private final ServerSocket serverSocket;
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
        enable = false;

        serverSocket = new ServerSocket(port, 50, InetAddress.getByName(host));

        server = new Thread(this);

        tokenMap = new HashMap<>();

        stopper = new ThreadStopper();
    }

    /**
     * ポート番号を設定する。
     *
     * @param port サーバーポート番号
     * @throws IOException サーバーソケットに例外が発生した場合
     */
    public JMUX(int port) throws IOException {
        enable = false;

        serverSocket = new ServerSocket(port);

        server = new Thread(this);

        tokenMap = new HashMap<>();

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
            try (Socket sock = serverSocket.accept()) {
                try (InputStream in = sock.getInputStream(); OutputStream out = sock.getOutputStream()) {
                    //受信データバッファ
                    byte[] data = new byte[CommandFormatter.BYTE_SIZE];

                    int readSize = in.read(data);
                    if (readSize == -1) continue;

                    //受信データを読み込んだサイズまで切り詰め
                    byte[] receiveData = Arrays.copyOf(data, readSize);

                    CommandFormatter cf = new CommandFormatter(receiveData);
                    Command command = cf.getCommand();
                    int tokenID = cf.getTokenID();

                    try {
                        byte[] redata = {(byte) (command(command, tokenID) ? 1 : 0)};

                        out.write(redata);
                    } catch (NullPointerException ignored) {
                        out.write(new byte[]{0});
                    }

                    if (command == Command.EXIT) {
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
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            run = false;
            enable = false;
            stopper.start();
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