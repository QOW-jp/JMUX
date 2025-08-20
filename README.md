# JMUX (Java Multiplexer)

### Requirements

Java 17 or later

## Getting started

| 項目 | 詳細               |
|----|------------------|
| OS | Ubuntu 22.04 LTS |

```
1. java ServerTest
2. java ClientTest
```

#### ServerTest.java

```java
import com.qow.jmux.JMUX;
import com.qow.jmux.Token;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        int port = 9999;
        String clientIp = "localhost";

        JMUX jmux = new JMUX(port, clientIp);

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
```

#### ClientTest.java

```java
import com.qow.jmux.ClosedServerException;
import com.qow.jmux.Command;
import com.qow.jmux.JMUXClient;
import com.qow.jmux.UntrustedConnectException;

import java.util.Arrays;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(Command.values()));

        String host = "localhost";
        int port = 9999;

        JMUXClient jmuxClient = new JMUXClient(host, port);
        Scanner sc = new Scanner(System.in);
        while (true) {
            Command command;
            int tokenID;

            System.out.print("command>");
            command = Command.valueOf(sc.nextLine());

            System.out.print("tokenID>");
            tokenID = Integer.parseInt(sc.nextLine());

            try {
                System.out.println("send : " + jmuxClient.send(command, tokenID));
            } catch (UntrustedConnectException e) {
                System.err.println(e.getMessage());
            } catch (ClosedServerException e) {
                System.err.println("server closed");
            }

            if (command == Command.EXIT) break;
        }
    }
}
```