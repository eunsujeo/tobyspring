package study.spring.toby.chapter01.user;

import study.spring.toby.chapter01.user.dao.UserDao;
import study.spring.toby.chapter01.user.domain.User;

import java.sql.SQLException;

public class Ch01Application {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        UserDao dao = new UserDao();

        User user = new User();
        user.setId("whiteip");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공 ");

        User user2 = dao.get(user.getId());

        System.out.println(user2.getName() + " 조회 성공 ");
    }
}
