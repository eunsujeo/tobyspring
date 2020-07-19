package study.spring.toby.chapter03.exercise.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import study.spring.toby.chapter03.domain.User;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class UserDaoByJdbcTemplate {

    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }
    public void add(User user) throws SQLException {
        this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
    }
}
