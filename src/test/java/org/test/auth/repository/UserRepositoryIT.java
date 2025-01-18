package org.test.auth.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.test.auth.model.Phone;
import org.test.auth.model.User;
import org.test.auth.util.DateFormatter;

import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenGeneratingUUIDThenUUIDGeneratedVersion4() {
        User user = User.builder()
                .name("Tom Stuart")
                .email("tom.stuart@test.com")
                .password("36YUIO56688wHGF")
                .phones(Arrays.asList(Phone.builder()
                        .number(126256253L)
                        .cityCode(13)
                        .countryCode("US")
                        .build()))
                .created(DateFormatter.getMediumDateTimeStamp())
                .lastLogin(DateFormatter.getMediumDateTimeStamp())
                .isActive(true)
                .build();
        User savedUser = userRepository.save(user);

        var expectedSaved = savedUser.getUuid();

        Assertions.assertThat(expectedSaved).isNotNull();
        Assertions.assertThat(expectedSaved.version()).isEqualTo(4);
    }
}
