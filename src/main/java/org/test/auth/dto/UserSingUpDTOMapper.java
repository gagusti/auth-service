package org.test.auth.dto;

import org.test.auth.model.Phone;
import org.test.auth.model.User;
import org.test.auth.util.DateFormatter;
import java.util.stream.Collectors;

public class UserSingUpDTOMapper {
    public static User mapDTO(SignUpDTO dto) {
        return User.builder()
                .name(dto.getName())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .phones(dto.getPhones().stream().map(p ->
                        Phone.builder()
                                .number(p.getNumber())
                                .cityCode(p.getCityCode())
                                .countryCode(p.getCountryCode())
                                .build())
                        .collect(Collectors.toList()))
                .created(DateFormatter.getMediumDateTimeStamp())
                .isActive(true)
                .build();
    }

    public static ResponseSignUpDTO mapResponseDTO(User user) {
        return ResponseSignUpDTO.builder()
                .id(user.getUuid())
                .created(user.getCreated())
                .lastLogin(user.getLastLogin())
                .isActive(user.isActive())
                .build();
    }
}
