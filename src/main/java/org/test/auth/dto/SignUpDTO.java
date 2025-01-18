package org.test.auth.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
public class SignUpDTO {
    private String name;
    @Pattern(regexp = ValidationPatterns.EMAIL_PATTERN,
            message = ValidationPatterns.EMAIL_PATTERN_MESSAGE)
    private String email;
    @Pattern(regexp = ValidationPatterns.PASSWORD_PATTERN,
            message = ValidationPatterns.PASSWORD_PATTERN_MESSAGE)
    private String password;
    private List<PhoneDTO> phones;
}
