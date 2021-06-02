import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        Scanner consoleScanner = new Scanner(System.in);
        InetSocketAddress address = null;
        try {
            address = new InetSocketAddress(InetAddress.getLocalHost(), 5000);
        } catch (UnknownHostException e) {
            System.out.println("Problems with local host. Can't run client app");
        }
        CommandReader commandReader = new CommandReader(address);
        boolean needNormalMode = true;
        while (needNormalMode) {
            try {
                needNormalMode = commandReader.read(consoleScanner, false);
            } catch (IOException ignored) { }
        }
    }
}








