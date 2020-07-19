package study.spring.toby.chapter05.good.user.example_5_1_5.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import study.spring.toby.chapter05.good.user.example_5_1_5.dao.UserDao;
import study.spring.toby.chapter05.good.user.example_5_1_5.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_1_5.domain.User;

import java.util.List;

@Service
public class UserServiceImpl {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    UserDao userDao;

    @Autowired
    @Qualifier("refactorUserDao")
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC : return (user.getLogin() >=MIN_LOGCOUNT_FOR_SILVER);
            case SILVER : return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD : return false;
            default:
                throw new IllegalArgumentException("Unknown Level" + currentLevel);
        }
    }

    private void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }

    public void add(User user) {
        if (user.getLevel() == null)
            user.setLevel(Level.BASIC);

        userDao.add(user);
    }
}
