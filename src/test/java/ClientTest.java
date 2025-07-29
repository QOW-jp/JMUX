import com.qow.Command;
import com.qow.JMUXClient;
import com.qow.UntrustedConnectException;

import java.io.IOException;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }
        String path = args[0];

        JMUXClient jmuxClient = new JMUXClient(path);
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
            } catch (UntrustedConnectException ignore) {
            }

            if (command == Command.EXIT) break;
        }
    }
}
