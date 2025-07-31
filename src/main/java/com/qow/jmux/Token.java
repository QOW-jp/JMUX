package com.qow.jmux;

/**
 * {@link JMUX}クラスのtokenIDによって呼び出されるクラスの雛形<br>
 * このクラスを継承したクラスを用いて{@link JMUX#addToken(Token)}する。
 *
 * @version 2025/08/01
 * @since 1.0.0
 */
public abstract class Token {
    private final int tokenID;
    private boolean run;

    /**
     * @param tokenID 識別番号
     */
    public Token(int tokenID) {
        this.tokenID = tokenID;
        run = false;
    }

    /**
     * @return 識別番号を返す
     */
    public int getTokenID() {
        return tokenID;
    }

    /**
     * 実行中かを判別する<br>
     * {@link Token#enable()}が呼び出された後Trueとなり{@link Token#disable()}または{@link Token#stopped()}が呼ばれるとFalseとなる
     *
     * @return {@link Token#enable()}が呼び出された場合True
     */
    public boolean isEnable() {
        return run;
    }

    /**
     * 実行する
     *
     * @return 正常に実行できた場合True
     */
    protected synchronized boolean enable() {
        if (run) return false;
        run = true;
        new Thread(this::start).start();
        return true;
    }

    /**
     * 停止する
     *
     * @return 正常に停止できた場合True
     */
    protected synchronized boolean disable() {
        if (!run) return false;
        stop();
        stopped();
        return true;
    }

    /**
     * オーバーライドする
     *
     * @return 正常に初期化できた場合True
     */
    public boolean reset() {
        return true;
    }

    /**
     * {@link Token#enable()}により実行後最後に呼び出す必要がある<br>
     * または{@link Token#disable()}により呼び出される
     */
    public final void stopped() {
        run = false;
    }

    /**
     * {@link JMUX}が{@link Command#ENABLE}を受け取った際に実行する
     */
    public abstract void start();

    /**
     * {@link JMUX}が{@link Command#DISABLE}を受け取った際に実行する
     */
    public abstract void stop();
}
