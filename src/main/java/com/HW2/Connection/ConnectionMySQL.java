package com.HW2.Connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionMySQL implements AutoCloseable {


    private final String SERVER_PATH;

    private final String DB_NAME;

    private final String DB_LOGIN;

    private final String DB_PASSWORD;
    private final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    private Connection connection;
    private Statement statement;


    public ConnectionMySQL(
            final String SERVER_PATH,
            final String DB_NAME,
            final String DB_LOGIN,
            final String DB_PASSWORD) {

        this.SERVER_PATH = SERVER_PATH;
        this.DB_NAME = DB_NAME;
        this.DB_LOGIN = DB_LOGIN;
        this.DB_PASSWORD = DB_PASSWORD;

        init();

    }

    public ConnectionMySQL() {
        SERVER_PATH = "Localhost:3306";
        DB_NAME = "hw1";
        DB_LOGIN = "root";
        DB_PASSWORD = "0755";

        init();
    }

    //-----------------------------------------------------------------
    public String messegeStartConnectionTrue() {
        return "Server running :\n" + toString();
    }

    public String messegeStartConnectionFalse() {
        return "Server not running";
    }

    public String messegeConnectionClose() {
        return "Connection close";
    }

    //init--------------------------------------------------------------

    public void init() {
        initDriver();
        initConnection();
    }

    protected void initDriver() {
        try {
            Class.forName(this.getDB_DRIVER());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    protected void initConnection() {

        try {
            connection = DriverManager.getConnection(getConnectionURL(), getDB_LOGIN(), getDB_PASSWORD());
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected String getConnectionURL() {
        return "jdbc:mysql://" + this.getSERVER_PATH() + "/" + this.getDB_NAME()
                + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    }

    //---------------------------------------------------------------

    public String getSERVER_PATH() {
        return SERVER_PATH;
    }

    public String getDB_NAME() {
        return DB_NAME;
    }

    public String getDB_LOGIN() {
        return DB_LOGIN;
    }

    public String getDB_PASSWORD() {
        return DB_PASSWORD;
    }

    public String getDB_DRIVER() {
        return DB_DRIVER;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

//Close-------------------------------------------------------------

    @Override
    public void close() {
        closeStatement();
        closeConnection();


    }


    private void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }

    private void closeStatement() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                statement = null;
            }
        }
    }

    @Override
    public String toString() {
        return "ConnectionMySQL{" +
                "\nSERVER_PATH='" + SERVER_PATH + '\'' +
                "\n DB_NAME='" + DB_NAME + '\'' +
                "\n DB_LOGIN='" + DB_LOGIN + '\'' +
                "\n DB_PASSWORD='" + DB_PASSWORD + '\'' +
                "\n DB_DRIVER='" + DB_DRIVER + '\'' +
                "\n connection=" + connection +
                "\n statement=" + statement +
                '}';
    }
}


