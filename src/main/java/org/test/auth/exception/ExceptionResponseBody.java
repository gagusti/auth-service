package org.test.auth.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionResponseBody {
    private String timestamp;
    private Integer code;
    private String detail;
}
