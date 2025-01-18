package org.test.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ResponseLoginDTO {
    private String id;
    private String created;
    private String lastLogin;
    private String token;
    private boolean isActive;
    private String name;
    private String email;
    private String password;
    private List<PhoneDTO> phones;
}
