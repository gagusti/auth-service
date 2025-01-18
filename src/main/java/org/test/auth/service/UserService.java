package org.test.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.auth.exception.UserNotFoundException;
import org.test.auth.model.User;
import org.test.auth.repository.UserRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor()
public class UserService {

    final private UserRepository userRepository;

    public Optional<User> loadUserByEmail(String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user;
        }
        throw new UserNotFoundException(String.format("Email not found: %s", email));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
