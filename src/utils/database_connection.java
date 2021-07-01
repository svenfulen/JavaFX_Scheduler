package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database_connection {
    // JDBC URL components
    private static final String PROTOCOL = "jdbc";
    private static final String VENDOR_NAME = "mysql";
    private static final String DATABASE_ADDRESS = "wgudb.ucertify.com";
    private static final String DATABASE_NAME = "WJ07Xhv";

    // JDBC URL concatenation
    private static final String JDBC_URL = PROTOCOL+":"+VENDOR_NAME+"://"+DATABASE_ADDRESS+"/"+DATABASE_NAME;

    // JDBC Driver Interface Reference
    private static final String MYSQL_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static Connection connection_object = null;

    // Database Auth
    private static final String LOGIN_USERNAME = "U07Xhv";
    private static final String LOGIN_PASSWORD = "53689157237";

    /**
     * Start the database connection.
     */
    public static void startConnection() {
        try {
            Class.forName(MYSQL_JDBC_DRIVER);
            connection_object = DriverManager.getConnection(JDBC_URL, LOGIN_USERNAME, LOGIN_PASSWORD);
            System.out.println("The connection to database " + DATABASE_NAME + " was successful.");
        }
        catch(ClassNotFoundException | SQLException exception) {
            System.out.println(exception.getMessage());
            System.out.println("Printing stack trace:");
            exception.printStackTrace();
        }

    }

    /**
     * Retrieve the existing database connection.
     * @return The Connection object associated with the existing database connection.
     */
    public static Connection getConnection(){
        return connection_object;
    }

    /**
     * Close the Database connection.
     */
    public static void closeConnection(){
        try{
            connection_object.close();
        }
        catch(Exception exception){
            //do nothing because the program is closing
        }
    }


}
