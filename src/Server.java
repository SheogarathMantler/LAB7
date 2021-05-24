import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

public class Server {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException, InterruptedException {
        Logger logger = Logger.getLogger("server.main");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(5000);
        } catch (SocketException e) {
            logger.info("Can't make server socket. Server is turning off");
            System.exit(0);
        }
        LinkedHashSet<Dragon> set = null;
        FileCollectionReader fileCollectionReader;
        while(true) {
            try {
                File file = null;
                // создаем сокет
                Socket server = serverSocket.accept();
                // создаем потоки

                DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(server.getInputStream());
                logger.info("сокет создан");
                // считываем коллекцию из файла
                fileCollectionReader = new FileCollectionReader(file, outputStream);

                try {
                    set = fileCollectionReader.readCollection(file);
                } catch (FileCollectionException e) {
                    set = new LinkedHashSet<>();
                    set.add(new Dragon(true));
                    System.out.println("создан пипец");
                }
                if (server.isConnected()) {
                    logger.info("server is connected");
                    CommandExecutor executor = new CommandExecutor(set, false);
                    executor.execute(inputStream, outputStream);
                    logger.info("session ended. Waiting for new session ... ");
                }
            } catch (SocketException | NoSuchAlgorithmException e) {
                //e.printStackTrace();
                //System.out.println("something went wrong");
                Thread.sleep(100);
            }
        }
    }
}


