package study.spring.toby.chapter05.good.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import study.spring.toby.chapter05.good.user.example_5_1_final.dao.UserDao;
import study.spring.toby.chapter05.good.user.example_5_1_final.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_1_final.domain.User;
import study.spring.toby.chapter05.good.user.example_5_1_final.service.UserLevelUpgradePolicy;
import study.spring.toby.chapter05.good.user.example_5_1_final.service.UserLevelUpgradePolicyImpl;
import study.spring.toby.chapter05.good.user.example_5_1_final.service.UserService;
import study.spring.toby.config.AppConfig;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.spring.toby.chapter05.good.user.example_5_1_final.service.UserLevelUpgradePolicyImpl.MIN_LOGCOUNT_FOR_SILVER;
import static study.spring.toby.chapter05.good.user.example_5_1_final.service.UserLevelUpgradePolicyImpl.MIN_RECCOMEND_FOR_GOLD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@SpringBootTest(classes = {UserService.class, UserLevelUpgradePolicyImpl.class})
public class Page_348_UserServiceTest {

    @Autowired
    @Qualifier("finalUserDao")
    UserDao userDao;

    @Autowired
    @Qualifier("finalUserService")
    UserService userService;

    @Test
    public void bean() {
        assertThat(this.userService).isNotNull();
    }

    List<User> users;

    @BeforeEach
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
                new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void upgradeLevels() {
        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        userService.upgradeLevels();
        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);
    }


    private void checkLevel(User user, Level level) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel()).isEqualTo(level);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
        } else
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
    }

}