package study.spring.toby.chapter05.good.user.example_5_2_1.service;

import com.sun.xml.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import study.spring.toby.chapter05.good.user.example_5_2_1.dao.UserDao;
import study.spring.toby.chapter05.good.user.example_5_2_1.domain.Level;
import study.spring.toby.chapter05.good.user.example_5_2_1.domain.User;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import java.sql.Connection;
import java.util.List;

@Service("fiveTwoUserService")
public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    UserDao userDao;

    @Autowired
    @Qualifier("fiveTwoUserDao")
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    // Connection 을 생성할 때 사용할 DataSource 를 DI 받도록 한다.
    private DataSource dataSource;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void upgradeLevels() throws Exception {
        //region 리스트 First Example
//        List<User> users = userDao.getAll();
//        for (User user : users) {
//            if (canUpgradeLevel(user)) {
//                upgradeLevel(user);
//            }
//        }
        //endregion

        //region 리스트 5-41
//        // 1. 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
//        TransactionSynchronizationManager.initSynchronization();
//        // 2. DB 커넥션을 생성하고 트랜잭션을 시작한다. 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
//        Connection c = DataSourceUtils.getConnection(dataSource);
//        c.setAutoCommit(false);
//
//        try {
//            List<User> users = userDao.getAll();
//            for (User user : users) {
//                if (canUpgradeLevel(user)) {
//                    upgradeLevel(user);
//                }
//            }
//            // 정상적으로 작업을 마치면 트랜잭션 커밋
//            c.commit();
//        }catch(Exception e) {
//            // 예외가 발생하면 롤백한다.
//            c.rollback();
//            throw e;
//        } finally {
//            DataSourceUtils.releaseConnection(c, dataSource);
//            // 스프링 유틸리티 메소드를 이용해 DB 커넥션을 안전하게 닫는다.
//            TransactionSynchronizationManager.unbindResource(this.dataSource);
//            TransactionSynchronizationManager.clearSynchronization();
//        }
        //endregion

        //region 리스트 5-44
//        // 1. JNDI를 이용해 서버의 UserTransaction 오브젝트를 가져온다.
//        InitialContext ctx = new InitialContext();
//        UserTransaction tx = (UserTransaction)ctx.lookup("silverspoon8");
//
//        tx.begin();
//
//        // 2. JNDI로 가져온 dataSource를 사용해야 한다.
//        Connection c = dataSource.getConnection();
//
//        try {
//            List<User> users = userDao.getAll();
//            for (User user : users) {
//                if (canUpgradeLevel(user)) {
//                    upgradeLevel(user);
//                }
//            }
//            // 정상적으로 작업을 마치면 트랜잭션 커밋
//            tx.commit();
//        }catch(Exception e) {
//            // 예외가 발생하면 롤백한다.
//            tx.rollback();
//            throw e;
//        } finally {
//            c.close();
//        }
        //endregion

        //region 리스트 5-45
        //1. JDBC 트랜잭션 추상 오브젝트 생성
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        //2. 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            // 정상적으로 작업을 마치면 트랜잭션 커밋
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            // 트랜잭션 커밋
            transactionManager.rollback(status);
            throw e;
        }

        //endregion
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
    }

    public void add(User user) {
        if (user.getLevel() == null)
            user.setLevel(Level.BASIC);

        userDao.add(user);
    }
}
