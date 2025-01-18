package org.test.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.test.auth.dto.LoginDTO;
import org.test.auth.dto.PhoneDTO;
import org.test.auth.dto.ResponseLoginDTO;
import org.test.auth.dto.ResponseSignUpDTO;
import org.test.auth.dto.SignUpDTO;
import org.test.auth.repository.UserRepository;
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIT {

    private static final String USER_NAME = "Eric Pope";
    private static final String USER_VALID_EMAIL = "eric.pope@test.com";
    private static final String USER_NOT_VALID_EMAIL = "eric.pope@test";
    private static final String USER_WRONG_EMAIL = "eric.pope@example.com";
    private static final String USER_NOT_VALID_PASSWORD = "test12345";
    private static final String USER_VALID_PASSWORD = "H90kjska";
    private static final String USER_WRONG_PASSWORD = "H90kjqqa";
    private static final Long USER_PHONE_NUM_1 = 1234567L;
    private static final Integer USER_CITY_CODE_1 = 10;
    private static final String USER_COUNTRY_CODE = "US";
    private static final String USER_LOGIN_URL = "/api/auth/login";
    private static final String USER_SIGNUP_URL = "/api/auth/signup";
    private static final String $_ERROR = "$.error";
    private static final String $_ERROR_DETAIL_0 = "$.error[0].detail";
    private static final String $_ERROR_DETAIL_1 = "$.error[1].detail";
    private static final String EXPECTED_VALUE_VALIDATE_PASSWORD = "Field Name: password - Password must follow this conventions: " +
            "One Capital letter, two numbers (could be non consecutive), " +
            "lower cases letters, max 12 and min 8 characters.";
    private static final String EXECTED_VALUE_VALIDATE_EMAIL = "Field Name: email - Email must be appropriate (for example, abc@test.com)";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String SIGN_UP_AND_LOGIN_TOKENS_MUST_BE_NOT_EQUALS = "SignUp and Login tokens must be not equals";
    private static final String EMAIL_NOT_FOUND_NOT_ABLE_TO_AUTHENTICATE = "Email not found! Not able to authenticate";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    private SignUpDTO getSignUpDTO(String userEmail, String userPassword) {
        return  SignUpDTO.builder()
                .name(USER_NAME)
                .email(userEmail)
                .password(userPassword)
                .phones(Arrays.asList(
                        PhoneDTO.builder()
                                .number(USER_PHONE_NUM_1)
                                .cityCode(USER_CITY_CODE_1)
                                .countryCode(USER_COUNTRY_CODE)
                                .build()
                )).build();
    }

    private LoginDTO getLoginDTO(String email, String password) {
       return LoginDTO.builder()
                .email(email)
                .password(password)
                .build();
    }

    @Test
    public void signUpWithValidDataShouldBeSuccessful() throws Exception {
        SignUpDTO signUpDTO = getSignUpDTO(USER_VALID_EMAIL, USER_VALID_PASSWORD);
        mvc.perform(MockMvcRequestBuilders.post(USER_SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void signUpWithInvalidPasswordShouldBeFail() throws Exception {
        SignUpDTO signUpDTO = getSignUpDTO(USER_VALID_EMAIL, USER_NOT_VALID_PASSWORD);
        mvc.perform(MockMvcRequestBuilders.post(USER_SIGNUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDTO))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR).exists())
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR_DETAIL_0)
                        .value(EXPECTED_VALUE_VALIDATE_PASSWORD));
    }

    @Test
    public void signUpWithInvalidEmailShouldBeFail() throws Exception {
        SignUpDTO signUpDTO = getSignUpDTO(USER_NOT_VALID_EMAIL, USER_VALID_PASSWORD);
        mvc.perform(MockMvcRequestBuilders.post(USER_SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR).exists())
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR_DETAIL_0)
                        .value(EXECTED_VALUE_VALIDATE_EMAIL));
    }

    @Test
    public void signUpWithInvalidEmailAndPasswordShouldBeFail() throws Exception {
        SignUpDTO signUpDTO = getSignUpDTO(USER_NOT_VALID_EMAIL, USER_NOT_VALID_PASSWORD);
        mvc.perform(MockMvcRequestBuilders.post(USER_SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR).exists())
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR_DETAIL_0)
                        .value(EXECTED_VALUE_VALIDATE_EMAIL))
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR_DETAIL_1)
                        .value(EXPECTED_VALUE_VALIDATE_PASSWORD));
    }

    @Test
    public void signUpExistentEmailShouldBeFail() throws Exception {
        SignUpDTO signUpDTO = getSignUpDTO(USER_VALID_EMAIL, USER_VALID_PASSWORD);
        mvc.perform(MockMvcRequestBuilders.post(USER_SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());;

        mvc.perform(MockMvcRequestBuilders.post(USER_SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR).exists())
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR_DETAIL_0)
                        .value(String.format("User %s with email %s already exists", USER_NAME, USER_VALID_EMAIL)));
    }

    @Test
    public void loginWithValidDataShouldBeSuccessful() throws Exception {
        SignUpDTO signUpDTO = getSignUpDTO(USER_VALID_EMAIL, USER_VALID_PASSWORD);
        MvcResult mvcSignUpResult = mvc.perform(MockMvcRequestBuilders.post(USER_SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ResponseSignUpDTO signedUp = objectMapper.readValue(mvcSignUpResult.getResponse().getContentAsString(),
                ResponseSignUpDTO.class);

        LoginDTO loginDTO = getLoginDTO(USER_VALID_EMAIL, USER_VALID_PASSWORD);

        MvcResult mvcLoginResult = mvc.perform(MockMvcRequestBuilders.post(USER_LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + signedUp.getJwt())
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ResponseLoginDTO loggedIn = objectMapper.readValue(mvcLoginResult.getResponse().getContentAsString(),
                ResponseLoginDTO.class);
        Assertions.assertNotEquals(loggedIn.getToken(), signedUp.getJwt(), SIGN_UP_AND_LOGIN_TOKENS_MUST_BE_NOT_EQUALS);
    }

    @Test
    public void loginWithWrongEmailShouldBeFail() throws Exception {
        SignUpDTO signUpDTO = getSignUpDTO(USER_WRONG_EMAIL, USER_VALID_PASSWORD);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(USER_SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ResponseSignUpDTO signedUp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                ResponseSignUpDTO.class);

        LoginDTO loginDTO = getLoginDTO(USER_VALID_EMAIL, USER_VALID_PASSWORD);

        mvc.perform(MockMvcRequestBuilders.post(USER_LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + signedUp.getJwt())
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR_DETAIL_0)
                        .value(EMAIL_NOT_FOUND_NOT_ABLE_TO_AUTHENTICATE));
    }

    @Test
    public void loginWithWrongPasswordShouldBeFail() throws Exception {
        SignUpDTO signUpDTO = getSignUpDTO(USER_VALID_EMAIL, USER_VALID_PASSWORD);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(USER_SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ResponseSignUpDTO signedUp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                ResponseSignUpDTO.class);

        LoginDTO loginDTO = getLoginDTO(USER_VALID_EMAIL, USER_WRONG_PASSWORD);

        mvc.perform(MockMvcRequestBuilders.post(USER_LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + signedUp.getJwt())
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR_DETAIL_0)
                        .value("Unauthorized access"));;
    }

    @Test
    public void loginWithWrongEmailAndPasswordShouldBeFail() throws Exception {
        SignUpDTO signUpDTO = getSignUpDTO(USER_VALID_EMAIL, USER_VALID_PASSWORD);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(USER_SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ResponseSignUpDTO signedUp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                ResponseSignUpDTO.class);

        LoginDTO loginDTO = getLoginDTO(USER_WRONG_EMAIL, USER_WRONG_PASSWORD);

        mvc.perform(MockMvcRequestBuilders.post(USER_LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + signedUp.getJwt())
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath($_ERROR_DETAIL_0)
                        .value(EMAIL_NOT_FOUND_NOT_ABLE_TO_AUTHENTICATE));;
    }
}
