package study.spring.toby.chapter03.exercise.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDao {
    public void deleteAll() throws SQLException {
        Connection c = DriverManager.getConnection(
                "jdbc:h2:tcp://localhost/~/test", "sa", ""
        );

        PreparedStatement ps = c.prepareStatement("delete from users");
        ps.executeUpdate();

        ps.close();
        c.close();
    }
}
