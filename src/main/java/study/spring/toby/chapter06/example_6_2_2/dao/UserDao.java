package study.spring.toby.chapter06.example_6_2_2.dao;


import study.spring.toby.chapter06.example_6_2_2.domain.User;

import java.util.List;

public interface UserDao {

    void add(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    int getCount();
    void update(User user);
}
