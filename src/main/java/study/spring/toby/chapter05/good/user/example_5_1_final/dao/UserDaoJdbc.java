package study.spring.toby.chapter05.good.user.example_5_1_final.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import study.spring.toby.chapter05.good.user.example_5_1_final.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_1_final.domain.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) {
        this.jdbcTemplate.update("insert into user(id, name, password, level, login, recommend)" +
                        " values(?,?,?,?,?,?)",
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update(
                "update user set name = ?, password = ?, level = ?, login = ?, " +
                        "recommend = ? where id = ?",
                        user.getName(), user.getPassword(),
                        user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId()
        );
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from user where id = ?",
            new Object[] {id}, this.userMapper);
    }

    public void deleteAll() {
        this.jdbcTemplate.update("delete from user");
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from user", Integer.class);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from user order by id", this.userMapper);
    }

    private RowMapper<User> userMapper =
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setLevel(Level.valueOf(rs.getInt("level")));
                    user.setLogin(rs.getInt("login"));
                    user.setRecommend(rs.getInt("recommend"));
                    return user;
                }
            };
}
