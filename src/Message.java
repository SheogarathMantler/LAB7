import java.io.Serializable;
// класс команды, который передается от клиента на сервер
public class Message implements Serializable {
    Command.CommandType type;
    String argument;
    Dragon dragon = new Dragon();
    boolean metaFromScript;
    boolean isEnd = false;
    public Message(boolean isEnd) {
        this.isEnd = isEnd;
    }
    public Message(Command.CommandType type, String argument, boolean metaFromScript) {
        this.argument = argument;
        this.type = type;
        this.metaFromScript = metaFromScript;
    }
    public Message(Dragon dragon, Command.CommandType type, String argument, boolean metaFromScript) {
        this.argument = argument;
        this.type = type;
        this.dragon = dragon;
        this.metaFromScript = metaFromScript;
    }
}

