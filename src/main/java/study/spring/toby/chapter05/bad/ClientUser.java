package study.spring.toby.chapter05.bad;

import study.spring.toby.chapter05.bad.domain.User;

public class ClientUser {
    public static void main(String[] args) {
        User user  = new User();

        if (user.getLevel() == User.BASIC) {
            user.setLevel(getSum(100));
        }
    }

    private static int getSum(int value) {
        value++;
        return value;
    }
}
