package org.test.auth.dto;

public class ValidationPatterns {
    public static final String EMAIL_PATTERN = "^\\S+@\\S+\\.\\S+$";
    public static final String EMAIL_PATTERN_MESSAGE = "Email must be appropriate (for example, abc@test.com)";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9]{1,2})(?=.*[a-z])(?=.*[A-Z]{1})(?=\\S+$).{8,12}$";
    public static final String PASSWORD_PATTERN_MESSAGE = "Password must follow this conventions: One Capital letter, two numbers (could be non consecutive), " +
            "lower cases letters, max 12 and min 8 characters.";
}
