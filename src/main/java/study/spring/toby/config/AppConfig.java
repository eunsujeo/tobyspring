package study.spring.toby.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import study.spring.toby.chapter05.good.user.example_5_1_1.dao.UserDao;
import study.spring.toby.chapter05.good.user.example_5_1_1.dao.UserDaoJdbc;
import study.spring.toby.chapter05.good.user.example_5_1_1.service.UserService;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean
    public UserDao userDao() {
        return new UserDaoJdbc();
    }

    @Bean
    public study.spring.toby.chapter05.good.user.example_5_1_5.dao.UserDao refactorUserDao() {
        return new study.spring.toby.chapter05.good.user.example_5_1_5.dao.UserDaoJdbc();
    }

    @Bean
    public study.spring.toby.chapter05.good.user.example_5_1_final.dao.UserDao finalUserDao() {
        return new study.spring.toby.chapter05.good.user.example_5_1_final.dao.UserDaoJdbc();
    }

    @Bean
    public DataSource h2DataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/test;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        return dataSource;
    }


}
