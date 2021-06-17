package mapper;

import liquibase.pro.packaged.E;
import lombok.SneakyThrows;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.User;
import ru.vsu.cs.textme.backend.db.model.request.RegistrationRequest;

import java.io.Reader;

@RunWith(SpringRunner.class)
public class UserMapperTest {
    private static SqlSession session;
    @Autowired
    UserMapper mapper;

    @SneakyThrows
    @BeforeClass
    public static void setup() {
        Reader reader = Resources.getResourceAsReader("/db/migration/master.yml");

        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();

        SqlSessionFactory sessionFactory = builder.build(reader);
        session = sessionFactory.openSession();
    }

    public static String EMAIL = "tester@gmail.com";
    public static String NAME = "testername";
    public static String PASS = "testpassword";

    public void test000() {
        var request = new RegistrationRequest();
        request.setEmail(EMAIL);
        request.setNickname(NAME);
        request.setPassword(PASS);
        var user = mapper.createUser(request);
        if (user == null) user =  mapper.findUserByEmail(EMAIL);
        test(user, request);
        var other = mapper.findUserByEmail(EMAIL);
        test(user, other);
        other = mapper.findUserById(user.getId());
        test(user, other);
    }


    private void test(User user, RegistrationRequest request) {
        Assert.assertEquals(user.getNickname(), request.getNickname());
        Assert.assertEquals(user.getPassword(), request.getPassword());
        Assert.assertEquals(user.getEmail(), request.getEmail());
    }

    private void test(User user, User other) {
        Assert.assertEquals(user.getNickname(), other.getNickname());
        Assert.assertEquals(user.getPassword(), other.getPassword());
        Assert.assertEquals(user.getEmail(), other.getEmail());
    }

    @TestConfiguration
    static class UserMapperConf {
        @SneakyThrows
        @Bean
        public UserMapper create() {
            return session.getMapper(UserMapper.class);
        }
    }

    @AfterClass
    public static void cleanup() {
        session.close();
    }
}
