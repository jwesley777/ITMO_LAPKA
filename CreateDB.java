

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CreateDB {
    public static void main (String[] args) {
        try {
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres","password");

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
