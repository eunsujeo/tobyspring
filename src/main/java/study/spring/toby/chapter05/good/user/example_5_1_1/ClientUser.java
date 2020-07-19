package study.spring.toby.chapter05.good.user.example_5_1_1;

import study.spring.toby.chapter05.good.user.example_5_1_1.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_1_1.domain.User;

public class ClientUser {
    public static void main(String[] args) {
        User user = new User();
        if (user.getLevel() == Level.BASIC) {
//            user.setLevel(getSum(100));
        }
    }

    private static int getSum(int value) {
        value++;
        return value;
    }
}
