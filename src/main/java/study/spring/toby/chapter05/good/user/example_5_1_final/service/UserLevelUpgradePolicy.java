package study.spring.toby.chapter05.good.user.example_5_1_final.service;

import study.spring.toby.chapter05.good.user.example_5_1_final.domain.User;

public interface UserLevelUpgradePolicy {
    boolean canUpgradeLevel(User user);
    void upgradeLevel(User user);
}
