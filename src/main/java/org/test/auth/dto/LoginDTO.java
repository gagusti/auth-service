package org.test.auth.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class LoginDTO {
    @Pattern(regexp = ValidationPatterns.EMAIL_PATTERN,
            message = ValidationPatterns.EMAIL_PATTERN_MESSAGE)
    private String email;
    @Pattern(regexp = ValidationPatterns.PASSWORD_PATTERN,
            message = ValidationPatterns.PASSWORD_PATTERN_MESSAGE)
    private String password;
}
