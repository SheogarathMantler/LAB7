import java.io.Serializable;

public class AuthorizationMessage implements Serializable {
    String login;
    String password;
    boolean alreadyExist;
    public AuthorizationMessage(String login, String password, boolean alreadyExist){
        this.alreadyExist = alreadyExist;
        this.password = password;
        this.login = login;
    }
}
