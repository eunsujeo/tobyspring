package study.spring.toby.chapter05.good.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import study.spring.toby.chapter05.good.user.example_5_1_5.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_1_5.domain.User;

import static org.assertj.core.api.Assertions.assertThat;

public class Page_344_UserTest {
    User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void upgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if (level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel()).isEqualTo(level.nextLevel());
        }
    }

    @Test
    public void cannotUpgradeLevel() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
           Level[] levels = Level.values();
            for (Level level : levels) {
                if (level.nextLevel() != null) continue;
                user.setLevel(level);
                user.upgradeLevel();
            }
        });
    }
}
