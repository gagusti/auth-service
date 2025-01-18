package org.test.auth.dto;

import org.test.auth.model.User;

import java.util.stream.Collectors;

public class UserLoginDTOMapper {
    public static ResponseLoginDTO mapResponseDTO(User user) {
        return ResponseLoginDTO.builder()
                .id(user.getUuid().toString())
                .email(user.getEmail())
                .created(user.getCreated())
                .name(user.getName())
                .lastLogin(user.getLastLogin())
                .isActive(user.isActive())
                .password(user.getPassword())
                .phones(user.getPhones().stream().map(p ->
                                PhoneDTO.builder()
                                    .number(p.getNumber())
                                    .cityCode(p.getCityCode())
                                    .countryCode(p.getCountryCode()).build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
