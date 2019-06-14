
import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.postgresql.Driver;

public class JDBCConnector extends DBConfigs {

    private final String DB_URL = "jdbc:postgresql://"+dbHost+":"+dbPort+"/" + dbName;
    private final String USER = dbUser;
    private final String PASS = dbPassword;

    private static final FileLogger logger = new FileLogger();

    private Connection connection;

    public JDBCConnector() {
        LoadingPrinter loadingPrinter = new LoadingPrinter();
        boolean firstCreation = true;
        while(true) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                loadingPrinter.stop();
                System.out.println("\nConnected to DB!");
                break;
            } catch (ClassNotFoundException e) {
                System.err.println("Error! JDBC Driver not found!");
                System.exit(1);
            } catch (SQLException e) {
                if (e.getSQLState().equals("08001")) {
                    if(firstCreation) {
                        System.out.println("Error: Can't reach server!");
                        System.out.println("Trying to reconnect...");
                        new Thread(loadingPrinter::printLoadingLine).start();
                        firstCreation = false;
                    }
                }
            }
        }

	/*
		????????:
		String table = "CREATE TABLE IF NOT EXISTS table(id INT, name VARCHAR, PRIMARY KEY(id));";
		execSQLUpdate(table);
	*/

    //execSQLUpdate("DROP TABLE IF EXISTS humans");
    //execSQLUpdate("DROP TABLE IF EXISTS users");

    String table = "CREATE TABLE IF NOT EXISTS users (login VARCHAR PRIMARY KEY, password VARCHAR, email VARCHAR)";
    execSQLUpdate(table);
	table = "CREATE TABLE IF NOT EXISTS humans (login VARCHAR REFERENCES users(login), name VARCHAR, size INT, x INT, y INT, space INT, time TIMESTAMP)";//, PRIMARY KEY(id), SERIAL(id))";
	execSQLUpdate(table);
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Executing a query and returning resultSet. After calling this method and processing resultSet, you must call PreparedStatement.close() method.
     * @param query SQL query
     * @return Pair of PreparedStatement and ResultSet
     */
    public Pair<PreparedStatement, ResultSet> execSQLQuery(String query){
            try {
                PreparedStatement statement = connection.
                        prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                return new Pair<>(statement, resultSet);
            } catch (SQLException e) {
                if (e.getSQLState().equals("08001") || e.getSQLState().equals("08006")) {
                    resetConnection();
                    return execSQLQuery(query);
                }
                else {
                    logger.log(e.getMessage() + "\nSqlState = " + e.getSQLState());
                    return null;
                }
            }
    }

    private boolean resetConnection(){
        LoadingPrinter loadingPrinter = new LoadingPrinter();
        boolean firstCreation = true;
        while(true) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                loadingPrinter.stop();
                System.out.println("\nConnected to DB!");
                return true;
            } catch (ClassNotFoundException e) {
                System.err.println("JDBC driver not found!");
                System.exit(1);
            } catch (SQLException e) {
                if (e.getSQLState().equals("08001")) {
                    if(firstCreation) {
                        new Thread(loadingPrinter::printLoadingLine).start();
                        firstCreation = false;
                    }
                }
            }
        }
    }

    public boolean execSQLUpdate(String query){
        try (PreparedStatement statement = connection.
                prepareStatement(query)) {
            if (statement.executeUpdate() > 0) return true;
            return false;
        } catch (SQLException e) {
            if (e.getSQLState().equals("08001") || e.getSQLState().equals("08006")) {
                resetConnection();
                return execSQLUpdate(query);
            }
            else {
                e.printStackTrace();
                logger.log(e.getMessage() + "\nSqlState = " + e.getSQLState());
                return false;
            }
        }
    }
    public boolean execSQLUpdate(String query, OffsetDateTime time){
        try (PreparedStatement statement = connection.
                prepareStatement(query)) {
            statement.setObject(1, time);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getSQLState().equals("08001") || e.getSQLState().equals("08006")) {
                resetConnection();
                return execSQLUpdate(query);
            }
            else {
                e.printStackTrace();
                logger.log(e.getMessage() + "\nSqlState = " + e.getSQLState());
                return false;
            }
        }
    }
}

