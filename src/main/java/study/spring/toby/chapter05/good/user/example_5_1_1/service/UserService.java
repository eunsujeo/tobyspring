package study.spring.toby.chapter05.good.user.example_5_1_1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import study.spring.toby.chapter05.good.user.example_5_1_1.dao.UserDao;
import study.spring.toby.chapter05.good.user.example_5_1_1.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_1_1.domain.User;

import java.util.List;

@Service
public class UserService {
    UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            Boolean changed = null;
            if (user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
                user.setLevel(Level.SILVER);
                changed = true;
            } else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
                user.setLevel(Level.GOLD);
                changed = true;
            } else if (user.getLevel() == Level.GOLD) {
                changed = false;
            } else {
                changed = false;
            }

            if (changed) {
                userDao.update(user);
            }
        }
    }

    public void add(User user) {
        if (user.getLevel() == null)
            user.setLevel(Level.BASIC);

        userDao.add(user);
    }
}
