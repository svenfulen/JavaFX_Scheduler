package utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class database_query {
    // Statement object
    private static Statement statement_object;

    /**
     * Link a Connection object to a Statement object.
     * @param conn Connection object to link to the Statement object
     * @throws SQLException
     */
    public static void setStatement(Connection conn) throws SQLException {
        statement_object = conn.createStatement();
    }

    /**
     * @return Statement object
     */
    public static Statement getStatement(){
        return statement_object;
    }
}
