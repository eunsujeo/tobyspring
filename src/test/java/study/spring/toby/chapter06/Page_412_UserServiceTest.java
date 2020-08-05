package study.spring.toby.chapter06;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import study.spring.toby.chapter06.example_6_2_2.dao.UserDao;
import study.spring.toby.chapter06.example_6_2_2.domain.Level;
import study.spring.toby.chapter06.example_6_2_2.domain.User;
import study.spring.toby.chapter06.example_6_2_2.service.UserService;
import study.spring.toby.chapter06.example_6_2_2.service.UserServiceImpl;
import study.spring.toby.chapter06.example_6_2_2.service.UserServiceTx;
import study.spring.toby.config.AppConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static study.spring.toby.chapter06.example_6_2_2.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static study.spring.toby.chapter06.example_6_2_2.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@SpringBootTest(classes = {UserServiceImpl.class})
public class Page_412_UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    @Qualifier("userServiceImpl")
    UserServiceImpl userServiceImpl;

    @Autowired
    @Qualifier("chapterSixTwoTwoUserDao")
    UserDao userDao;

    @Autowired
    MailSender mailSender;

    @Autowired
    PlatformTransactionManager transactionManager;

    List<User> users;

    @BeforeEach
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", "eunsujeo.gmail.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
                new User("joytouch", "강명성", "p2", "eunsujeo.gmail.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", "eunsujeo.gmail.com", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
                new User("madnite1", "이상호", "p4", "eunsujeo.gmail.com", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
                new User("green", "오민규", "p5", "eunsujeo.gmail.com", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void upgradeLevels() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);
        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size()).isEqualTo(2);
        checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
        checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size()).isEqualTo(2);
        assertThat(request.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(request.get(1)).isEqualTo(users.get(3).getEmail());

    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId()).isEqualTo(expectedId);
        assertThat(updated.getLevel()).isEqualTo(expectedLevel);
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
    public void upgradeAllOrNothing() {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender(mailSender);

        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setTransactionManager(transactionManager);
        txUserService.setUserService(testUserService);

        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        try {
            txUserService.upgradeLevels();
            fail("TestUseServiceException expected");
        } catch (TestUserServiceException e) {

        }

        checkLevelUpgraded(users.get(1), false);
    }

    static class MockUserDao implements UserDao {
        private List<User> users;
        private List<User> updated = new ArrayList<>();

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }
        public List<User> getAll() {
            return this.users;
        }
        public void update(User user) {
            updated.add(user);
        }

        @Override
        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User get(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException();
        }
    }

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }

        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }
        public void send(SimpleMailMessage[] mailMessage) throws MailException {

        }
    }

    static class TestUserService extends UserServiceImpl {
        private String id;
        private TestUserService(String id) {
            this.id = id;
        }

        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {

    }

}