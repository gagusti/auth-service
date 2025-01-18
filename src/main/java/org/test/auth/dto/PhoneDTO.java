package org.test.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneDTO {
    private Long number;
    private Integer cityCode;
    private String countryCode;
}
