import java.sql.*;
import java.util.HashMap;
// local "jdbc:postgresql://localhost:5432/mydatabase" "postgres" "1"
public class DBManager {
     String DB_URL = "jdbc:postgresql://pg:5432/studs";
     String USER = "s312551";
     String PASS = "wvz604";
     String password;
     String login;
     Connection connection = null;

    public DBManager(String login, String password) {
        this.password = password;
        this.login = login;
    }

    public void connect() throws SQLException {                // подключаемся к БД (БД юзеров?)
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }
        System.out.println("PostgreSQL JDBC Driver successfully connected");
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }
        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
    }
    public HashMap<String, String> getUsersTable() {             // получаем список юзеров
        String selectTableSQL = "SELECT login, password FROM users";
        HashMap<String, String> users = new HashMap<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                String login = rs.getString("login");
                String password = rs.getString("password");
                users.put(login, password);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return users;
    }

    public void addUser(String login, String password) {
        String insertTableSQL = "INSERT INTO users" + "(login, password) " +
                "VALUES('" + login + "', '" + password + "')";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(insertTableSQL);
            System.out.println("New user added!");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
