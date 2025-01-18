package org.test.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.test.auth.exception.UserNotFoundException;
import org.test.auth.model.User;
import org.test.auth.service.UserService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthServiceUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        Optional<User> userLoad = Optional.empty();
        try {
            userLoad = userService.loadUserByEmail(email);
        } catch(UserNotFoundException ex) {
            throw new AuthenticationCredentialsNotFoundException("Email not found! Not able to authenticate");
        }
        if (userLoad.isPresent() && passwordEncoder.matches(pwd,userLoad.get().getPassword())) {
            return new UsernamePasswordAuthenticationToken(email, pwd);
        }
        throw new AuthenticationCredentialsNotFoundException("Unauthorized access");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
