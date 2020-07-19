package study.spring.toby.chapter04;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import study.spring.toby.chapter03.domain.User;
import study.spring.toby.chapter03.exercise.dao.UserDaoByJdbcTemplate;

import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class list_4_9_example {

    private final UserDaoByJdbcTemplate userDaoByJdbcTemplate;

    public void add(User user) throws DuplicateUserIdException, SQLException {
        try {
            userDaoByJdbcTemplate.add(user);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062)
                throw new DuplicateUserIdException();
            else
                throw e;
        }
    }
}
