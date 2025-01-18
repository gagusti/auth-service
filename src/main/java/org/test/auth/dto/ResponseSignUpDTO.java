package org.test.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ResponseSignUpDTO {
    private UUID id;
    private String created;
    private String lastLogin;
    private String Jwt;
    private boolean isActive;
}
