package study.spring.toby.chapter03.exercise.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import study.spring.toby.chapter03.domain.User;

import javax.sql.DataSource;
import java.sql.*;

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
}
