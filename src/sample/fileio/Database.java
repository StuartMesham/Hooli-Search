package sample.fileio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Class for connecting to the database
 */

public class Database {

    Connection c;

    public static final int TYPE_SQLITE = 0;

    public Database(String file, int type) throws ClassNotFoundException, SQLException {
        switch (type) {
            case TYPE_SQLITE:
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:" + file);
                break;
        }
    }

    public ResultSet query(String sql, Object... params) throws SQLException {
        PreparedStatement statement = c.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement.executeQuery();
    }

    public int exec(String sql, Object... params) throws SQLException {
        PreparedStatement statement = c.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement.executeUpdate();
    }

    public void close() throws SQLException {
        c.close();
    }
}
