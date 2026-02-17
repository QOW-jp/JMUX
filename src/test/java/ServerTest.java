import com.qow.jmux.JMUX;
import com.qow.jmux.Token;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerTest {
    public static void main(String[] args) throws IOException, UntrustedQONException, NoSuchKeyException {
        byte[] protocolID = "JMUX".getBytes(StandardCharsets.UTF_8);

        QONObject qon = new QONObject(new File("src/test/resources/jmux.qon"));
        String clientIp = qon.get("client-ip");

        boolean autoPorting = Boolean.parseBoolean(qon.get("auto-porting"));
        int port;
        if (autoPorting) {
            port = 0;
        } else {
            port = Integer.parseInt(qon.get("port"));
        }

        try (JMUX jmux = new JMUX(port, protocolID, clientIp)) {

            if (autoPorting) {
                int activatedPort = jmux.getLocalPort();
                File temp = new File(qon.get("port-temp"));
                Path parent = Path.of(temp.getParent());
                Files.createDirectories(parent);
                try (FileWriter fw = new FileWriter(temp)) {
                    try (PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {
                        pw.println(activatedPort);
                    }
                }
            }

            jmux.addToken(new TokenTest(1));
            System.out.println("start JMUX : " + jmux.enable());
            jmux.waitForServer();
        }
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
