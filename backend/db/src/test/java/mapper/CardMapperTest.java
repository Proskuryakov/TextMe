package mapper;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.vsu.cs.textme.backend.db.mapper.CardMapper;

import java.io.Reader;

@RequiredArgsConstructor
public class CardMapperTest {
    private static SqlSession session;
    private final CardMapper mapper;

    @SneakyThrows
    @BeforeClass
    public static void setup() {
        Reader reader = Resources.getResourceAsReader("/db/migration/master.yml");

        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();

        SqlSessionFactory sessionFactory = builder.build(reader);
        session = sessionFactory.openSession();
    }

    @Test
    public void test001() {

    }

    @TestConfiguration
    static class CardMapperConf {
        @SneakyThrows
        @Bean
        public CardMapper create() {
            return session.getMapper(CardMapper.class);
        }
    }

    @AfterClass
    public static void cleanup() {
        session.close();
    }
}
