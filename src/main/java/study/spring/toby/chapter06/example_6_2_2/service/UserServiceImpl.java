package study.spring.toby.chapter06.example_6_2_2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import study.spring.toby.chapter06.example_6_2_2.dao.UserDao;
import study.spring.toby.chapter06.example_6_2_2.domain.Level;
import study.spring.toby.chapter06.example_6_2_2.domain.User;

import java.util.List;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private UserDao userDao;
    private MailSender mailSender;

    @Autowired
    @Qualifier("chapterSixTwoTwoUserDao")
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    private PlatformTransactionManager transactionManager;

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC:
                return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER:
                return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD:
                return false;
            default:
                throw new IllegalArgumentException("Unknown Level" + currentLevel);
        }
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEMail(user);
    }

    private void sendUpgradeEMail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("eunsujeeo@gmail.com");
        mailMessage.setSubject("Upgrade");
        mailMessage.setText("레벨업 축하 " + user.getLevel().name());

        this.mailSender.send(mailMessage);
    }

    public void add(User user) {
        if (user.getLevel() == null)
            user.setLevel(Level.BASIC);

        userDao.add(user);
    }
}
