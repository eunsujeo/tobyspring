package study.spring.toby.chapter05.good.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import study.spring.toby.chapter05.good.user.example_5_2_1.dao.UserDao;
import study.spring.toby.chapter05.good.user.example_5_2_1.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_2_1.domain.User;
import study.spring.toby.chapter05.good.user.example_5_2_1.service.UserService;
import study.spring.toby.config.AppConfig;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static study.spring.toby.chapter05.good.user.example_5_2_1.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static study.spring.toby.chapter05.good.user.example_5_2_1.service.UserService.MIN_RECCOMEND_FOR_GOLD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@SpringBootTest(classes = {UserService.class})
public class Page_351_UserServiceTest {

    @Autowired
    @Qualifier("fiveTwoUserDao")
    UserDao userDao;

    @Autowired
    @Qualifier("fiveTwoUserService")
    UserService userService;

    @Autowired
    DataSource dataSource;

    @Test
    public void bean() {
        assertThat(this.userService).isNotNull();
    }

    List<User> users;

    @BeforeEach
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
                new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void upgradeLevels() throws Exception {
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

    @Test
    public void upgradeAllOrNothing() throws Exception {
        // 예외를 발생시킬 네 번째 사용자의 id를 넣어서 테스트용 UserService 대역 오브젝트를 생헌다.
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setDataSource(this.dataSource);

        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        try {
            // TestUserService는 업그레이드 작업 중에 예외가 발생해야한다.
            // 정상 종료라면 문제가 있으니 실패
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {
            // TestUserService가 던져주는 예외를 잡아서 계속 진행되도록 한다. 그 외의 예외라면 테스트 실패.
        }

        // 예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인
        // 실패 이유 : 네 번째 사용자 처리 중 예외가 발생했지만 그대로 유지되고 있는 것이다.
        checkLevelUpgraded(users.get(1), false);
    }

    static class TestUserService extends UserService {
        private String id;

        // 예외를 발생시킬 User 오브젝트의 id를 지정할 수 있게 만든다.
        private TestUserService(String id) {
            this.id = id;
        }

        // UserService의 메소드를 오버라이드한다.
        protected void upgradeLevel(User user) {
            // 지정된 id의 Usesr 오브젝트가 발견되면 예외를 던져서 작업을 강제로 중단시킨다.
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {

    }

}