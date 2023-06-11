package de.zappler2k.api.mysql;


import de.zappler2k.api.file.ModuleManager;
import lombok.Getter;

import java.sql.*;

@Getter
public class MySQLConnnector {

    private MySQLModule mySQLModule;
    private Connection connection;

    public MySQLConnnector(ModuleManager moduleManager, String dictionary, String file) {
        new MySQLModule(dictionary, file, moduleManager);
        mySQLModule = (MySQLModule) moduleManager.getIModule(MySQLModule.class);
        connect();
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + mySQLModule.getHOST() + ":" + mySQLModule.getPORT() + "/" + mySQLModule.getDATABASE() + "?autoReconnect=true", mySQLModule.getUSER(), mySQLModule.getPASSWORD());
            System.out.println("Connected to " + mySQLModule.getHOST() + ":" + mySQLModule.getPORT() + "/" + mySQLModule.getDATABASE());
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error while connecting to " + mySQLModule.getHOST() + ":" + mySQLModule.getPORT() + "/" + mySQLModule.getDATABASE() + "Error: Wrong host, port, database or user/password");
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from " + mySQLModule.getHOST() + ":" + mySQLModule.getPORT() + "/" + mySQLModule.getDATABASE());
            }
        } catch (SQLException e) {
            System.out.println("Error while disconnecting from " + mySQLModule.getHOST() + ":" + mySQLModule.getPORT() + "/" + mySQLModule.getDATABASE() + "Error: " + e.getMessage());
        }
    }

    public void update(String qry, Object... objects) {
        try (PreparedStatement ps = connection.prepareStatement(qry)) {
            for (int i = 0; i < objects.length; i++) {
                ps.setObject(i + 1, objects[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            connect();
            e.printStackTrace();
        }
    }


    public ResultSet query(String qry, Object... objects) {
        ResultSet rs = null;

        try {
            PreparedStatement ps = connection.prepareStatement(qry);
            for (int i = 0; i < objects.length; i++) {
                ps.setObject(i + 1, objects[i]);
            }
            rs = ps.executeQuery();
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
        return rs;
    }

    public boolean isConnected() {
        return connection != null;
    }
}
