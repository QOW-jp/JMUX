import com.qow.jmux.JMUX;
import com.qow.jmux.Token;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        int port = 9999;
        byte[] protocolID = "JMUX".getBytes(StandardCharsets.UTF_8);
        String clientIp = "localhost";

        JMUX jmux = new JMUX(port, protocolID, clientIp);

        jmux.addToken(new TokenTest(1));
        System.out.println("start JMUX : " + jmux.enable());
        jmux.waitForServer();
        System.exit(3);
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
            System.out.println("<TokenTest>stop();");
            loop = false;
        }
    }
}
