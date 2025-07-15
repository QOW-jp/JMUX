import com.qow.JMUX;
import com.qow.Token;

import java.io.IOException;
import java.util.Scanner;

public class ServerSample {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }
        String path = args[0];
//        String path = "";

        JMUX jmux = new JMUX(path);
        jmux.addToken(new TokenSample());
        System.out.println("start JMUX : " + jmux.enable());
    }
    public class TokenSample extends Token {

        public TokenSample(int tokenID) {
            super(tokenID);
        }

        @Override
        public void start() {
            System.out.print("");
            Scanner sc = new Scanner(System.in);
        }

        @Override
        public void stop() {

        }
    }
}
