import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        Logger logger = Logger.getLogger("server.main");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(5000);
        } catch (SocketException e) {
            logger.info("Can't make server socket. Server is turning off");
            System.exit(0);
        }
        LinkedHashSet<Dragon> set = null;
        Executor inPool = Executors.newCachedThreadPool();        // создаем пуллы потоков для многопоточности
        Executor handlePool = Executors.newFixedThreadPool(5);
        Executor outPool = Executors.newFixedThreadPool(5);
        ArrayList<Thread> allThreads = new ArrayList<>();
        ArrayList<Socket> allSockets = new ArrayList<>();
        while(true) {
            try {
                File file = null;
                // создаем сокет
                Socket server = serverSocket.accept();
                // создаем потоки
                Runnable r = () -> {
                    try {
                        DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());
                        ObjectInputStream inputStream = new ObjectInputStream(server.getInputStream());
                        logger.info("сокет создан");
                        // считываем коллекцию из бд
                        DBManager dbManager = new DBManager();
                        boolean isDBManagerConnected = false;
                        while (!isDBManagerConnected) {
                            isDBManagerConnected = dbManager.connect();
                            if (!isDBManagerConnected) {
                                Thread.sleep(2000);
                            }
                        }
                        // считали коллекцию из дб
                        if (server.isConnected()) {
                            logger.info("server is connected");
                            inPool.execute(() -> {
                                CommandExecutor executor = new CommandExecutor(dbManager, false, handlePool, outPool);
                                try {
                                    executor.execute(inputStream, outputStream);
                                } catch (ClassNotFoundException | NoSuchAlgorithmException | ParserConfigurationException e) {
                                    e.printStackTrace();
                                }
                                logger.info("session ended. Waiting for new session ... ");
                            });
                        }
                    } catch (SQLException | IOException | InterruptedException e ) {
                        e.printStackTrace();
                    }
                };
                allThreads.add(new Thread(r));
                allThreads.get(allThreads.size()-1).start();
                allSockets.add(server);

            } catch (SocketException e) {
                //e.printStackTrace();
                //System.out.println("something went wrong");
                Thread.sleep(100);
            }
        }
    }
}


