package study.spring.toby.chapter03.exercise.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDao {
    public void deleteAll() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = getSa();
            ps = c.prepareStatement("delete from users");

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {

                }
            }

            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {

                }
            }
        }

    }

    private Connection getSa() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:h2:tcp://localhost/~/test", "sa", ""
        );
    }
}
