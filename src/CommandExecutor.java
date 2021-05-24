import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

// принимает от клиента объект Message, преобразовывает его в команду и выполняет её
public class CommandExecutor {
    static String owner = null;
    private final LinkedHashSet<Dragon> set;
    private final boolean fromScript;
    private final Logger logger = Logger.getLogger("server.executor");
    Map<String, String> users = new HashMap<>(); // список пользователей
    public CommandExecutor(LinkedHashSet<Dragon> set, boolean fromScript) {
        this.set = set;
        this.fromScript = fromScript;
    }

    public void execute(ObjectInputStream inputStream, DataOutputStream outputStream) throws ClassNotFoundException, ParserConfigurationException {
        users.put("a", "a");
        users.put("b", "b");
        // принимаем сообщение
        boolean endOfStream = false;
        while (!endOfStream) {
            try {
                Object objMessage = inputStream.readObject();                                                // считали хоть что-то
                try {
                    Message message = (Message) objMessage;                                                  // если обычное сообщение
                    logger.info("message received");
                    if (message.isEnd) {
                        logger.info("Ctrl+D ??");
                        break;                                                                                // если кто-то умный нажал Ctrl+D
                    }
                    if (message.type == Command.CommandType.exit && !message.metaFromScript)
                        endOfStream = true;                                                                   // заканчиваем принимать сообщения после команды exit не из скрипта
                    if (!validate(message.dragon) || !(message.dragon instanceof Dragon)) throw new IOException();
                    Command command = new Command(outputStream, message.argument, message.dragon, set, fromScript); // создаем Command и выполняем команду
                    command.changeType(message.type);
                    command.run();
                } catch (ClassCastException e) {                                                                // если сообщение авторизации
                    logger.info("authorization message got");
                    AuthorizationMessage message = (AuthorizationMessage) objMessage;
                    if (message.alreadyExist) {                                                                 // если авторизация то ищем в списке
                        if (users.containsKey(message.login) && users.get(message.login).equals(message.password)) {
                            owner = message.login;// теперь юзер может все делать
                            logger.info("sign in successful");
                            outputStream.writeUTF("sign in successful");
                            // TODO
                        } else {
                            logger.info("sign in not successful");
                            outputStream.writeUTF("sign in not successful");
                            // TODO
                        }
                    } else {                                                                                    // если регистрация создаем новый
                        if (users.containsKey(message.login)) {
                            outputStream.writeUTF("There is already user with this login, try again");
                        } else{
                            users.put(message.login, message.password);
                            owner = message.login;
                            outputStream.writeUTF("registration successful!");
                        }
                    }

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

