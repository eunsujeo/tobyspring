package study.spring.toby.chapter05.good.user.example_5_2_1.dao;


import study.spring.toby.chapter05.good.user.example_5_2_1.domain.User;

import java.util.List;

public interface UserDao {

    void add(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    int getCount();
    void update(User user);
}
