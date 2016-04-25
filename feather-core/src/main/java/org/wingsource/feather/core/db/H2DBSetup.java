package org.wingsource.feather.core.db;

import org.h2.tools.RunScript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by samikc on 16/4/16.
 */
public class H2DBSetup {
    private static final Logger LOG = Logger.getLogger(H2DBSetup.class.getName());
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    private Connection dbConnection = null;
    public H2DBSetup() {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.severe(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION_URL, DB_USER, DB_PASSWORD);

        } catch (SQLException e) {
            LOG.severe(e.getMessage());
        }
    }
    public void runScript(File file) throws FileNotFoundException, SQLException {
        if (dbConnection != null)
            RunScript.execute(dbConnection,new FileReader(file));
    }

    public static String getDbDriver() {
        return DB_DRIVER;
    }

    public static String getDbConnectionUrl() {
        return DB_CONNECTION_URL;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }

    public List<String> listTables() throws SQLException {
        List<String> result = new ArrayList<String>();
        String query = "SHOW TABLES";
        Statement stmt = dbConnection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String tableName = rs.getString(1);
            System.out.println(tableName);
            result.add(tableName);
        }
        return result;
    }
}
