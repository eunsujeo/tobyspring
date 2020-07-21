package study.spring.toby.chapter05.good.user.example_5_1_final.service;

import org.springframework.stereotype.Service;
import study.spring.toby.chapter05.good.user.example_5_1_final.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_1_final.domain.User;

@Service("beforeEvent")
public class UserLevelUpgradePolicyImpl implements UserLevelUpgradePolicy {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    @Override
    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC : return (user.getLogin() >=MIN_LOGCOUNT_FOR_SILVER);
            case SILVER : return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD : return false;
            default:
                throw new IllegalArgumentException("Unknown Level" + currentLevel);
        }
    }

    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
    }
}
