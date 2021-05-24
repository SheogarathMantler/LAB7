import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.logging.Logger;

// принимает от клиента объект Message, преобразовывает его в команду и выполняет её
public class CommandExecutor {
    private final LinkedHashSet<Dragon> set;
    private final boolean fromScript;
    private final Logger logger = Logger.getLogger("server.executor");
    public CommandExecutor(LinkedHashSet<Dragon> set, boolean fromScript) {
        this.set = set;
        this.fromScript = fromScript;
    }

    public void execute(ObjectInputStream inputStream, DataOutputStream outputStream) throws ClassNotFoundException, ParserConfigurationException {
        // принимаем сообщение
        boolean endOfStream = false;
        while (!endOfStream) {
            try {
                Message message = (Message) inputStream.readObject();
                logger.info("message received");
                if (message.isEnd) {
                    logger.info("Ctrl+D ??");
                    break;                                                                 // если кто-то умный нажал Ctrl+D
                }
                if (message.type == Command.CommandType.exit && !message.metaFromScript)
                    endOfStream = true;                                                   // заканчиваем принимать сообщения после команды exit не из скрипта
                if (!validate(message.dragon) || !(message.dragon instanceof Dragon)) throw new IOException();
                Command command = new Command(outputStream, message.argument, message.dragon, set, fromScript);
                command.changeType(message.type);
                command.run();
            } catch (ClassCastException e) {
                logger.info("authorization message got");
                try {
                    logger.info("goy");
                    AuthorizationMessage message = (AuthorizationMessage) inputStream.readObject();
                    System.out.println("login =" + message.login);
                    System.out.println("pass =" + message.password);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } catch (IOException e) { // если убили клиент, то ждём
                logger.info("can't receive message");
                break;
            }
        }

    }
    public boolean validate(Dragon dragon) {
        try {
            Dragon dragon1 = new Dragon(dragon.getId(), dragon.getName(), dragon.getCoordinates(), dragon.getCreationDate(),
                    dragon.getAge(), dragon.getDescription(), dragon.getWingspan(), dragon.getType(), dragon.getCave());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

