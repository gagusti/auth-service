package org.test.auth.exception;

public enum AuthErrors {
    EMAIL(10),
    PASSWORD(11),
    SIGNUP_FAIL(12),
    LOGIN_FAIL(13);

    public final Integer errorCode;

    private AuthErrors(Integer code) {
        this.errorCode = code;
    }

    public static AuthErrors valueOfErrorCode(String label) {
        for (AuthErrors e: values()) {
            if (e.name().equals(label)) {
                return e;
            }
        }
        return null;
    }
}
