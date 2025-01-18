package org.test.auth.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.test.auth.dto.LoginDTO;
import org.test.auth.dto.PhoneDTO;
import org.test.auth.dto.ResponseLoginDTO;
import org.test.auth.dto.ResponseSignUpDTO;
import org.test.auth.dto.SignUpDTO;
import org.test.auth.exception.InvalidLoginException;
import org.test.auth.exception.InvalidSingUpException;
import org.test.auth.model.Phone;
import org.test.auth.model.User;
import org.test.auth.repository.UserRepository;
import org.test.auth.security.JwtManager;
import org.test.auth.service.AuthService;
import org.test.auth.service.UserService;
import org.test.auth.util.DateFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private static final String USER_NAME = "Eric Pope";
    private static final String USER_EMAIL = "eric.pope@test.com";
    private static final String USER_PASSWORD = "test12345";
    private static final Long USER_PHONE_NUM_1 = 1234567L;
    private static final Long USER_PHONE_NUM_2 = 7895493L;
    private static final Integer USER_CITY_CODE_1 = 10;
    private static final Integer USER_CITY_CODE_2 = 12;
    private static final String USER_COUNTRY_CODE = "US";
    private static final UUID USER_UUID = UUID.randomUUID();
    private static final String USER_CREATED = DateFormatter.getMediumDateTimeStamp();
    private static final String USER_SIGN_UP_RESPONSE_IS_NOT_NULL = "User Sign-Up response is not null";
    private static final String VERIFY_VALID_USER_UUID = "Verify valid user UUID";
    private static final String VERIFY_VALID_USER_CREATED_DATE = "Verify valid user Created date";
    private static final String VERIFY_USER_LAST_LOGIN_MUST_BE_NULL = "Verify user Last Login must be null";
    private static final String VERIFY_USER_JWT_IS_CORRECT = "Verify user JWT is correct";
    private static final String VERIFY_USER_LAST_LOGIN_MUST_NOT_BE_NULL = "Verify user Last Login must not be null";
    private static final String USER_LAST_LOGIN_EQUAL_TO_RESPONSE_LAST_LOGIN = "User Last Login equal to response Last Login";
    private static final String VERIFY_USER_WAS_ACTIVATED = "Verify user was activated";
    private static final String USER_DOES_NOT_EXIST = "User does not exist!";
    private static final String USER_INVALID_CREDENTIAL = "User Invalid Credential!";
    private static final String USER_S_WITH_EMAIL_S_ALREADY_EXISTS = "User %s with email %s already exists";

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Captor
    ArgumentCaptor<User> userCaptor;

    private UserService userService;
    private JwtManager jwtManager;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository);
        jwtManager = new JwtManager();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    private static SignUpDTO getSignUpDTO() {
        SignUpDTO signUpDTO = SignUpDTO.builder()
                .name(USER_NAME)
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .phones(Arrays.asList(
                        PhoneDTO.builder()
                                .number(USER_PHONE_NUM_1)
                                .cityCode(USER_CITY_CODE_1)
                                .countryCode(USER_COUNTRY_CODE)
                                .build(),
                        PhoneDTO.builder()
                                .number(USER_PHONE_NUM_2)
                                .cityCode(USER_CITY_CODE_2)
                                .countryCode(USER_COUNTRY_CODE)
                                .build()
                )).build();
        return signUpDTO;
    }

    private static User getNewUser() {
        User newUser = User.builder()
                .uuid(USER_UUID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .phones(Arrays.asList(
                                Phone.builder()
                                        .number(USER_PHONE_NUM_1)
                                        .cityCode(USER_CITY_CODE_1)
                                        .countryCode(USER_COUNTRY_CODE)
                                        .build(),
                                Phone.builder()
                                        .number(USER_PHONE_NUM_2)
                                        .cityCode(USER_CITY_CODE_2)
                                        .countryCode(USER_COUNTRY_CODE)
                                        .build()
                        )
                )
                .created(USER_CREATED)
                .lastLogin(null)
                .isActive(true)
                .build();
        return newUser;
    }

    private static LoginDTO getLoginDTO() {
        LoginDTO loginDTO = LoginDTO.builder()
                .email(USER_EMAIL)
                .password(USER_PASSWORD).build();
        return loginDTO;
    }

    private static User copyUser(User user) {
        return User.builder()
                .uuid(user.getUuid())
                .name(user.getName())
                .email(user.getEmail())
                .phones(user.getPhones())
                .created(user.getCreated())
                .password(user.getPassword())
                .lastLogin(user.getLastLogin())
                .isActive(user.isActive())
                .build();
    }

    @Test
    public void signUpSummitDataShouldBeSuccessful() {

        SignUpDTO signUpDTO = getSignUpDTO();

        User newUser = getNewUser();

        Mockito.doReturn(Optional.empty()).when(userRepository).findByEmail(Mockito.anyString());
        Mockito.doReturn(newUser).when(userRepository).save(Mockito.any(User.class));

        AuthService authService = new AuthService(authenticationProvider, userService, jwtManager, passwordEncoder);
        ResponseSignUpDTO response = authService.signUp(signUpDTO);

        Assertions.assertNotNull(response, USER_SIGN_UP_RESPONSE_IS_NOT_NULL);
        Assertions.assertEquals(USER_UUID, response.getId(), VERIFY_VALID_USER_UUID);
        Assertions.assertEquals(USER_CREATED, response.getCreated(), VERIFY_VALID_USER_CREATED_DATE);
        Assertions.assertNull(response.getLastLogin(), VERIFY_USER_LAST_LOGIN_MUST_BE_NULL);
        try {
            Assertions.assertTrue(jwtManager.isTokenValid(USER_EMAIL, response.getJwt()), VERIFY_USER_JWT_IS_CORRECT);
        } catch(Exception ex) {

        }
       Assertions.assertTrue(response.isActive(), VERIFY_USER_WAS_ACTIVATED);

    }

    @Test
    public void signUpSummitDataWhenUserAlreadyExistsShouldBeFail() {
        SignUpDTO signUpDTO = getSignUpDTO();
        User newUser = getNewUser();

        Optional<User> isPresentUser = Optional.of(newUser);
        Mockito.doReturn(isPresentUser).when(userRepository).findByEmail(Mockito.anyString());

        AuthService authService = new AuthService(authenticationProvider, userService, jwtManager, passwordEncoder);
        Throwable throwable = Assertions.assertThrows(
                Throwable.class,
                () -> authService.signUp(signUpDTO)
        );
        Assertions.assertEquals(InvalidSingUpException.class, throwable.getClass());
        Assertions.assertEquals(throwable.getMessage(), String.format(USER_S_WITH_EMAIL_S_ALREADY_EXISTS,
                signUpDTO.getName(), signUpDTO.getEmail()));
    }

    @Test
    public void firstTimeLoginSummitDataShouldBeSuccessful() {

        LoginDTO loginDTO = getLoginDTO();

        User foundUser = getNewUser();
        User savedUser = copyUser(foundUser);

        Mockito.doReturn(Optional.of(foundUser)).when(userRepository).findByEmail(Mockito.anyString());
        Mockito.doReturn(savedUser).when(userRepository).save(Mockito.any(User.class));
        Mockito.doReturn(null).when(authenticationProvider).authenticate(Mockito.any());

        AuthService authService = new AuthService(authenticationProvider, userService, jwtManager, passwordEncoder);
        ResponseLoginDTO response = authService.login(loginDTO);

        Assertions.assertNotNull(response, USER_SIGN_UP_RESPONSE_IS_NOT_NULL);
        Assertions.assertEquals(USER_UUID.toString(), response.getId().toString(), VERIFY_VALID_USER_UUID);
        Assertions.assertEquals(USER_CREATED, response.getCreated(), VERIFY_VALID_USER_CREATED_DATE);
        Assertions.assertNull(response.getLastLogin(), VERIFY_USER_LAST_LOGIN_MUST_NOT_BE_NULL);
        try {
            Assertions.assertTrue(jwtManager.isTokenValid(USER_EMAIL, response.getToken()), VERIFY_USER_JWT_IS_CORRECT);
        } catch(Exception ex) {
            Assertions.fail("Login Token is not valid");
        }
        Assertions.assertTrue(response.isActive(), VERIFY_USER_WAS_ACTIVATED);
    }

    @Test
    public void secondAndMoreTimesLoginSummitDataShouldBeSuccessful() {

        MockedStatic mockDateFormater = Mockito.mockStatic(DateFormatter.class);
        String lastLoginExpected = DateFormatter.getMediumDateTimeStamp();

        LoginDTO loginDTO = getLoginDTO();

        User foundUser = getNewUser();

        User savedUser = copyUser(foundUser);

        Mockito.doReturn(Optional.of(foundUser)).when(userRepository).findByEmail(Mockito.anyString());
        Mockito.doReturn(savedUser).when(userRepository).save(Mockito.any(User.class));
        Mockito.doReturn(null).when(authenticationProvider).authenticate(Mockito.any());
        mockDateFormater.when(()-> DateFormatter.getMediumDateTimeStamp()).thenReturn(lastLoginExpected);

        AuthService authService = new AuthService(authenticationProvider, userService, jwtManager, passwordEncoder);
        ResponseLoginDTO response = authService.login(loginDTO);

        verify(userRepository).save(userCaptor.capture());
        User userCaptorValue = userCaptor.getValue();

        Assertions.assertNotNull(response, USER_SIGN_UP_RESPONSE_IS_NOT_NULL);
        Assertions.assertEquals(USER_UUID.toString(), response.getId().toString(), VERIFY_VALID_USER_UUID);
        Assertions.assertEquals(USER_CREATED, response.getCreated(), VERIFY_VALID_USER_CREATED_DATE);
        Assertions.assertNull(response.getLastLogin(), VERIFY_USER_LAST_LOGIN_MUST_NOT_BE_NULL);
        Assertions.assertEquals(lastLoginExpected, userCaptorValue.getLastLogin(), USER_LAST_LOGIN_EQUAL_TO_RESPONSE_LAST_LOGIN);
        try {
            Assertions.assertTrue(jwtManager.isTokenValid(USER_EMAIL, response.getToken()), VERIFY_USER_JWT_IS_CORRECT);
        } catch(Exception ex) {
            Assertions.fail("Login Token is not valid");
        }
        Assertions.assertTrue(response.isActive(), VERIFY_USER_WAS_ACTIVATED);
    }

    @Test
    public void anyTimeLoginSummitEmailThatNotExistShouldBeFail() {

        LoginDTO loginDTO = getLoginDTO();

        Mockito.doReturn(Optional.empty()).when(userRepository).findByEmail(Mockito.anyString());
        Mockito.doReturn(null).when(authenticationProvider).authenticate(Mockito.any());

        AuthService authService = new AuthService(authenticationProvider, userService, jwtManager, passwordEncoder);

        Throwable throwable = Assertions.assertThrows(
                Throwable.class,
                () -> authService.login(loginDTO)
        );
        Assertions.assertEquals(InvalidLoginException.class, throwable.getClass());
        Assertions.assertEquals(throwable.getMessage(), USER_DOES_NOT_EXIST);
    }

    @Test
    public void anyTimeLoginSummitBadCredentialsShouldBeFail() {

        LoginDTO loginDTO = getLoginDTO();

        Mockito.doThrow(BadCredentialsException.class).when(authenticationProvider).authenticate(Mockito.any());

        AuthService authService = new AuthService(authenticationProvider, userService, jwtManager, passwordEncoder);

        Throwable throwable = Assertions.assertThrows(
                Throwable.class,
                () -> authService.login(loginDTO)
        );
        Assertions.assertEquals(InvalidLoginException.class, throwable.getClass());
        Assertions.assertEquals(throwable.getMessage(), USER_INVALID_CREDENTIAL);
    }
}
