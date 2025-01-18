package org.test.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.test.auth.dto.LoginDTO;
import org.test.auth.dto.ResponseLoginDTO;
import org.test.auth.dto.ResponseSignUpDTO;
import org.test.auth.dto.SignUpDTO;
import org.test.auth.dto.UserLoginDTOMapper;
import org.test.auth.dto.UserSingUpDTOMapper;
import org.test.auth.exception.InvalidSingUpException;
import org.test.auth.exception.InvalidLoginException;
import org.test.auth.exception.UserNotFoundException;
import org.test.auth.security.JwtManager;
import org.test.auth.util.DateFormatter;

@Service
@AllArgsConstructor
public class AuthService {

    private AuthenticationProvider authenticationProvider;
    private UserService userService;
    private JwtManager jwtManager;
    private PasswordEncoder passwordEncoder;

    public ResponseSignUpDTO signUp(SignUpDTO signUpDTO) throws InvalidSingUpException {
        try {
            userService.loadUserByEmail(signUpDTO.getEmail());
        } catch(UserNotFoundException ex) {
            var signUpUser = UserSingUpDTOMapper.mapDTO(signUpDTO);
            signUpUser.setPassword(passwordEncoder.encode(signUpUser.getPassword()));
            var newUser = userService.saveUser(signUpUser);
            var token = jwtManager.generateSignUpToken(newUser.getEmail());
            var responseSignUpDTO = UserSingUpDTOMapper.mapResponseDTO(newUser);
            responseSignUpDTO.setJwt(token);

            return responseSignUpDTO;
        }
        throw new InvalidSingUpException(String.format("User %s with email %s already exists",
                signUpDTO.getName(), signUpDTO.getEmail()));
    }

    public ResponseLoginDTO login(LoginDTO loginDTO) throws InvalidLoginException {
        try {
            authenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),
                                loginDTO.getPassword())
            );
            var loadUser = userService.loadUserByEmail(loginDTO.getEmail()).get();
            var responseLoginDTO = UserLoginDTOMapper.mapResponseDTO(loadUser);
            loadUser.setLastLogin(DateFormatter.getMediumDateTimeStamp());
            userService.saveUser(loadUser);
            var token = jwtManager.generateToken(loadUser.getEmail());
            responseLoginDTO.setToken(token);
            return responseLoginDTO;
        } catch(UserNotFoundException ex) {
            throw new InvalidLoginException("User does not exist!");
        } catch (BadCredentialsException bc) {
            throw new InvalidLoginException("User Invalid Credential!");
        }
    }
}
