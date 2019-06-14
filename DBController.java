import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class DBController {

    private JDBCConnector connector;

    public DBController(JDBCConnector connector){
        this.connector = connector;
    }

    public String addPasswordToDB(String login, String email) throws SQLException {
        try {
            String password = new PasswordsGenerator().getPassword();
            ResultSet rs = connector.execSQLQuery("select * from users where login = '" + login + "'").getValue();
            while (rs.next()) {
                if (rs.getString("login").equals(login)) {
                    return "This login is busy. Registration failed.";
                }
            }


            connector.execSQLUpdate(String.format("insert into %s (%s,%s,%s) values ('%s','%s','%s')",
                    DBConst.USER_TABLE, DBConst.USERS_LOGIN, DBConst.USERS_PASSWORD, DBConst.USERS_EMAIL,
                    login, password, email));

            if (new MailSender(MailService.OUTLOOK, "264447@niuitmo.ru","12345Pasha").send("PASSWORD", password, email))
                return "Account registered. Password sent to email";
            else return "Something went wrong while registration. Check email... or antivirus... or something else. We don't care";

        } catch (Exception e) {
            return "Something went wrong while registration. Check email... or antivirus... or something else. We don't care";
        }
    }
    public String addSomethingToDB(Human human, String login, String password){
        if (connector.execSQLUpdate(human.getInsertSqlQuery(login, password), human.date)) {
            return "Added element to db";
        }
        else {
            return "Element wasn't added.";
        }
    }

    public String addIfMaxToDB(Human human, String login, String password) throws SQLException {
        //TODO: adding if max to db using connector
        /*
        if (connector.execSQLUpdate(human.getInsIfMaxSqlQuery(login, password), human.date)) {
            return "Added element to db.";
        }
        else {
            return "Element wasn't added.";
        }

         */
        String query = "select * from humans";
        ResultSet rs = connector.execSQLQuery(query).getValue();

        String answer = "";
        boolean toInsert = true;
        while (rs.next()) {
            String name = rs.getString("name");
            if (human.name.compareTo(name) <= 0) toInsert = false;
        }
        if (toInsert) return addSomethingToDB(human, login, password);
        return "Element wasn't added";

    }

    public String removeSomethingFromDB(Human human, String login, String password) throws SQLException{
        if (connector.execSQLUpdate(human.getDelSqlQuery(login, password))) {
            return "Removed element to db";
        }
        else {
            return "Somehow element wasn't removed.";
        }




    }

    public String removeLowerFromDB(Human human, String login, String password) {
        if (connector.execSQLUpdate(human.getDeleteLowerSqlQuery(login, password)))
            return "Removed lower element(s) from db.";
        else return "Nothing was removed.";

    }

    public String showDB() throws SQLException {
        String query = "select * from humans";
        ResultSet rs = connector.execSQLQuery(query).getValue();

        String answer = "";
        while (rs.next()) {
            Human h = new Human(rs.getString("name"),rs.getInt("size"),
                    Space.values()[rs.getInt("space")],rs.getInt("x"),rs.getInt("y"),
                    OffsetDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp("time").getTime()), ZoneId.systemDefault()));
            answer += h.toString() + " of " + rs.getString("login") + "\n";
        }
        return answer;
    }

    public String showDB1() throws SQLException {
        String query = "select * from users";
        ResultSet rs = connector.execSQLQuery(query).getValue();

        String answer = "";
        while (rs.next()) {
            answer += rs.getString("login")+" " + rs.getString("password") + "\n";
        }
        return answer;
    }

    public String clearDB(String login, String password) {
        String query = "delete from humans where login = '" + login+ "'";
        String answer;
        if (connector.execSQLUpdate(query)) {
            answer = "Elements of this user deleted";
        }
        else answer = "Something went wrong while clearing database";
        return answer;
    }

    public boolean passwordIsCorrect (String login, String password) throws SQLException {
        ResultSet rs = connector.execSQLQuery("select * from users where login = '" + login+"'").getValue();
        while (rs.next()) {
            String correctPassword = rs.getString("password");
            return correctPassword.equals(password);
        }
        return false;
    }
}
