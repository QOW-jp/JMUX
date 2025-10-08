import com.qow.jmux.Command;
import com.qow.jmux.JMUXClient;
import com.qow.qtcp.ClosedServerException;
import com.qow.qtcp.UntrustedConnectException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(Command.values()));

        String host = "localhost";
        int port = 9999;
        byte[] protocolID = "JMUX".getBytes(StandardCharsets.UTF_8);

        JMUXClient jmuxClient = new JMUXClient(host, port, protocolID);
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
            } catch (IOException | UntrustedConnectException | ClosedServerException e) {
                throw new RuntimeException(e);
            }

            if (command == Command.EXIT) break;
        }
    }
}
