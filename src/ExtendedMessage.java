import java.io.Serializable;

public class ExtendedMessage implements Serializable {
    private final Message message;
    private final String login;
    private final String password;
    public ExtendedMessage(Message message, String login, String password) {
        this.login = login;
        this.password = password;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
