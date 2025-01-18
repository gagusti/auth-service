package org.test.auth.exception;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ExceptionResponse {
    @JsonDeserialize(as=ExceptionResponseBody.class)
    private List<ExceptionResponseBody> error;
}

