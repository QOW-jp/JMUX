import com.qow.jmux.JMUX;
import com.qow.jmux.Token;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws IOException, UntrustedQONException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }
        String path = args[0];

        QONObject qon = new QONObject(new File(path));
        int port = Integer.parseInt(qon.get("port"));

        JMUX jmux;
        if (Boolean.parseBoolean(qon.get("bind-ip"))) {
            String clientIp = qon.get("client-ip");
            jmux = new JMUX(port, clientIp);
        } else {
            jmux = new JMUX(port);
        }

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
