package com.qow.jmux;

/**
 * {@link JMUX}を操作するためのコマンド<br>
 * {@link JMUXClient#send(Command, int)}を用いて送信する。
 *
 * @version 2025/08/14
 * @since 1.0.0
 */
public enum Command {
    /**
     * 例外が発生した場合これを返される。
     */
    EXCEPTION(-1),
    /**
     * {@link JMUX}を終了する。<br>
     * 以降の受付を終了し実行中の{@link Token}のみ{@link Token#disable()}を呼び出し終了処理をする。
     */
    EXIT(0),
    /**
     * 指定されたtokenIDの{@link Token}が有効化しているかを確かめる。<br>
     * {@link JMUXClient#send(Command, int)}の返り値は{@link Token#start()}が実行中の場合True
     */
    EXIST(1),
    /**
     * 指定されたtokenIDの{@link Token}が追加されているかを確かめる。<br>
     * {@link JMUXClient#send(Command, int)}の返り値は{@link Token}が存在している場合True
     */
    LISTED(2),
    /**
     * 指定されたtokenIDの{@link Token#start()}を実行する。<br>
     * {@link JMUXClient#send(Command, int)}の返り値は正常に実行できた場合True
     */
    ENABLE(3),
    /**
     * 指定されたtokenIDの{@link Token#stop()} を実行する。<br>
     * {@link JMUXClient#send(Command, int)}の返り値は正常に実行できた場合True
     */
    DISABLE(4),
    /**
     * 指定されたtokenIDの{@link Token#reset()} を実行する。<br>
     * {@link JMUXClient#send(Command, int)}の返り値は正常に実行できた場合True
     */
    RESET(5);

    private final int id;

    Command(int id) {
        this.id = id;
    }

    /**
     * @return ID
     */
    public int getID() {
        return id;
    }
}
