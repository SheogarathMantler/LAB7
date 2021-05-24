import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner consoleScanner = new Scanner(System.in);
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 5000);
        CommandReader commandReader = new CommandReader(address);
        boolean needNormalMode = true;
        while (needNormalMode) {
            needNormalMode = commandReader.read(consoleScanner, false);
        }
    }
}








