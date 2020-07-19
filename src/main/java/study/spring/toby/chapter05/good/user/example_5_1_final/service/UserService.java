package study.spring.toby.chapter05.good.user.example_5_1_final.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import study.spring.toby.chapter05.good.user.example_5_1_final.dao.UserDao;
import study.spring.toby.chapter05.good.user.example_5_1_final.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_1_final.domain.User;

import java.util.List;

@Service("finalUserService")
public class UserService {
    UserDao userDao;
    UserLevelUpgradePolicy userLevelUpgradePolicy;

    @Autowired
    @Qualifier("finalUserDao")
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
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
        return userLevelUpgradePolicy.canUpgradeLevel(user);
    }

    private void upgradeLevel(User user) {
        userLevelUpgradePolicy.upgradeLevel(user);
        userDao.update(user);
    }

    public void add(User user) {
        if (user.getLevel() == null)
            user.setLevel(Level.BASIC);

        userDao.add(user);
    }
}
