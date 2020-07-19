package study.spring.toby.chapter04;

import study.spring.toby.chapter03.exercise.dao.UserDaoDeleteAll;

import java.sql.SQLException;

public class choNanGamExample {

    public void choNanGam () {
        UserDaoDeleteAll userDaoDeleteAll = new UserDaoDeleteAll();
        try {
            userDaoDeleteAll.deleteAll();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
