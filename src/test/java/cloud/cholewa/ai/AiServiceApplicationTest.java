package cloud.cholewa.ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

//the profile is set here as well as in surefire, so the class logs plain text when started from an IDE
@SpringBootTest
@ActiveProfiles("test")
class AiServiceApplicationTest {

    @Test
    void contextLoads() {
    }
}