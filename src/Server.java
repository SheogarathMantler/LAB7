import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
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
        while(true) {
            try {
                File file = null;
                // создаем сокет
                Socket server = serverSocket.accept();
                // создаем потоки
                DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(server.getInputStream());
                logger.info("сокет создан");
                // считываем коллекцию из бд
                DBManager dbManager = new DBManager();
                dbManager.connect();
                set = dbManager.readCollection();
                if (server.isConnected()) {
                    logger.info("server is connected");
                    CommandExecutor executor = new CommandExecutor(dbManager, set, false);
                    executor.execute(inputStream, outputStream);
                    logger.info("session ended. Waiting for new session ... ");
                }
            } catch (SocketException | NoSuchAlgorithmException e) {
                //e.printStackTrace();
                //System.out.println("something went wrong");
                Thread.sleep(100);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}


