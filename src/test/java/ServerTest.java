import com.qow.JMUX;
import com.qow.Token;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }
        String path = args[0];

        JMUX jmux = new JMUX(path);
        jmux.addToken(new TokenTest(1));
        System.out.println("start JMUX : " + jmux.enable());
    }

    public static class TokenTest extends Token {
        private boolean loop;

        public TokenTest(int tokenID) {
            super(tokenID);
            loop = false;
        }

        @Override
        public void start() {
            System.out.println("<TokenTest>start();");
            loop = true;
            for (int i = 0; loop; i++) {
                System.out.printf("<TokenTest>%d\n", i);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
            }
        }

        @Override
        public void stop() {
            System.out.print("<TokenTest>stop();");
            loop = false;
        }
    }
}
