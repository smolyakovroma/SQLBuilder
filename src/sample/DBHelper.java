package sample;

import com.mysql.jdbc.SQLError;

import java.sql.*;
import java.util.Properties;

public class DBHelper {

    private static Connection connection;
    private static DBHelper dbHelper;

    private static Properties properties = new Properties();
    private static String urlDB = "";

    private DBHelper() {
    }

    public static DBHelper getDbHelper() {
        if (dbHelper == null) {
            dbHelper = new DBHelper();
        }
        return dbHelper;
    }

    public static void setUserPassword(String url, String user, String password) {
        urlDB = url;
        properties.setProperty("user", user);
        properties.setProperty("password", password);
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                if (urlDB.equals("")) {
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?user=root&password=root", new Properties());
                } else {
                    connection = DriverManager.getConnection(urlDB, properties);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    static public boolean testConnection() {

        try {
            connection = DriverManager.getConnection(urlDB, properties);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection!=null;

    }
}
