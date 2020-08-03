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
//
//        // 2. DB 커넥션을 생성하고 트랜잭션을 시작한다. 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
//        //    DataSource에서 Connection을 직접 가져오지 않고, 스프링이 제공하는 유틸리티 메소드를 쓰는 이유는
//        //    이 DataSourceUtils의 getConnection() 메소드는 Connection 오브젝트를 생성해줄 뿐만 아니라
//        //    트랜잭션 동기화에 사용하도록 저장소에 바인딩까지 해준다.
//
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
//      // 간단한 트랜잭션 동기화 작업만 붙여줌으로써, 지저분한 Connection 파라미터의 문제를 깔끔히 해결.
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
////         트랜잭션의 처리 방법 및 코드 구조는 비슷하지만 JDBC 로컬 트랜잭션을 JTA를 이용하는 글로벌 트랜잭션으로
////         바꾸려면 UserService 의 코드를 수정해야 한다는 점이다.
////         또한 하이버네이트를 이용하게 되면 JDBC나 JTA의 코드와 다르다. Connection을 직접 사용하지 않고
////         Session 이라는 것을 사용하고, 독자적인 트랜잭션 관리 API를 사용한다.
//        endregion

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
