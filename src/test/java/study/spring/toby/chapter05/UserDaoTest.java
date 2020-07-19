package study.spring.toby.chapter05;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import study.spring.toby.chapter05.good.user.example_5_1_1.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_1_1.domain.User;
import study.spring.toby.chapter05.good.user.example_5_1_1.dao.UserDao;
import study.spring.toby.config.AppConfig;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class UserDaoTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    UserDao dao;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    public void setUp() {
        this.user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0);
        this.user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10);
        this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40);
    }

    @Test
    public void addAnsGet() {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);
    }

    @Test
    public void update() {
        dao.deleteAll();
        dao.add(user1);
        dao.add(user2);

        user1.setName("오민규");
        user1.setPassword("springno6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);

        dao.update(user1);
        User user1Update = dao.get(user1.getId());
        checkSameUser(user1, user1Update);
        User user2Same = dao.get(user2.getId());
        checkSameUser(user2, user2Same);
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getName()).isEqualTo(user2.getName());
        assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
        assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
        assertThat(user1.getLogin()).isEqualTo(user2.getLogin());
        assertThat(user1.getRecommend()).isEqualTo(user2.getRecommend());
    }
}
