import com.qow.jmux.Command;
import com.qow.jmux.JMUXClient;
import com.qow.net.ClosedServerException;
import com.qow.net.UntrustedConnectException;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) throws UntrustedQONException, IOException, NoSuchKeyException {
        System.out.println(Arrays.toString(Command.values()));

        QONObject qon = new QONObject(new File("src/test/resources/jmux.qon"));
        String host = qon.get("server-ip");
        byte[] protocolID = "JMUX".getBytes(StandardCharsets.UTF_8);

        int port = 0;
        boolean autoPorting = Boolean.parseBoolean(qon.get("auto-porting"));
        if (autoPorting) {
            Path portTempPath = Paths.get(qon.get("port-temp"));
            try {
                List<String> lines = Files.readAllLines(portTempPath, StandardCharsets.UTF_8);
                port = Integer.parseInt(lines.get(0));
            } catch (NoSuchFileException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            port = Integer.parseInt(qon.get("port"));
        }

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
            } catch (UntrustedConnectException | ClosedServerException e) {
                throw new RuntimeException(e);
            }

            if (command == Command.EXIT) break;
        }
    }
}
