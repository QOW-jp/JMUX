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
