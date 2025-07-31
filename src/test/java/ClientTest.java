import com.qow.jmux.ClosedServerException;
import com.qow.jmux.Command;
import com.qow.jmux.JMUXClient;
import com.qow.jmux.UntrustedConnectException;

import java.util.Arrays;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }
        String path = args[0];

        System.out.println(Arrays.toString(Command.values()));

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
            } catch (UntrustedConnectException e) {
                System.err.println(e.getMessage());
            } catch (ClosedServerException e) {
                System.err.println("server closed");
            }

            if (command == Command.EXIT) break;
        }
    }
}
