import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashSet;

// local "jdbc:postgresql://localhost:5432/mydatabase" "postgres" "1"
public class DBManager {
     String DB_URL = "jdbc:postgresql://localhost:5432/mydatabase";
     String USER = "postgres";
     String PASS = "1";
     String password;
     String login;
     Connection connection = null;

    public DBManager(String login, String password) {
        this.password = password;
        this.login = login;
    }
    public DBManager() { }

    public void connect() throws SQLException {                // подключаемся к БД
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

    public LinkedHashSet<Dragon> readCollection() {             // считываем из БД в коллекцию
        LinkedHashSet<Dragon> set = new LinkedHashSet<>();
        String selectTableSQL = "SELECT * FROM dragons";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                long x = rs.getLong("x");
                Double y = rs.getDouble("y");
                Coordinates coordinates = new Coordinates(x, y);
                java.time.LocalDateTime date = rs.getDate("creationdate").toLocalDate().atTime(0, 0);
                Long age = rs.getLong("age");
                String description = rs.getString("description");
                Double wingspan = rs.getDouble("wingspan");
                Integer type = rs.getInt("type");
                DragonType dragonType = intToDragonType(type);
                Integer depth = rs.getInt("depth");
                Double number = rs.getDouble("number");
                DragonCave cave = new DragonCave(depth, number);
                String owner = rs.getString("login");
                set.add(new Dragon(id, name, coordinates, date, age, description, wingspan, dragonType, cave, owner));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return set;
    }
    public void update(LinkedHashSet<Dragon> set) {              // по коллекции обновляем БД
        LinkedHashSet<Dragon> previousSet = readCollection();    // сравниваем сеты и все что отличается добавляем в БД
        for (Dragon dragon : previousSet) {
            deleteDragon(dragon.getId());
        }
        for (Dragon dragon : set) {
            addDragon(dragon);
        }
    }
    public HashMap<String, String> readUserHashMap() {             // получаем список юзеров
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

    public void addUser(String login, String password) {        // регистрация нового юзера
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
    public void deleteDragon(Integer id) {
        String deleteTableSQL = "DELETE FROM dragons WHERE id = " + id + ";";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(deleteTableSQL);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void addDragon(Dragon dragon) {        // регистрация нового юзера
        String insertTableSQL = "INSERT INTO dragons" + "(id, name, x, y, creationdate, age, " +
                "description, wingspan, type, depth, number, login)" +
                "VALUES('" + dragon.getId() + "', '" + dragon.getName() +
                "', '" + dragon.getCoordinates().getX() + "', '" + dragon.getCoordinates().getY() +
                "', '" + dragon.getCreationDate() + "', '" + dragon.getAge() + "', '" + dragon.getDescription() +
                "', '" + dragon.getWingspan() + "', '" + dragonTypeToInt(dragon.getType()) + "', '" + dragon.getCave().getDepth() +
                "', '" + dragon.getCave().getNumberOfTreasures() + "', '" + dragon.getOwner() + "')";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(insertTableSQL);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private DragonType intToDragonType(Integer i) {
        if (i == 1) {
            return DragonType.AIR;
        } else if (i == 2) {
            return DragonType.FIRE;
        } else {
            return DragonType.UNDERGROUND;
        }
    }
    public Integer dragonTypeToInt(DragonType type) {
        if (type == DragonType.AIR) {
            return 1;
        } else if (type == DragonType.FIRE) {
            return 2;
        } else {
            return 3;
        }
    }
}
