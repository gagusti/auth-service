package org.test.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.test.auth.model.User;
import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByEmail(@NotEmpty String email);
}
