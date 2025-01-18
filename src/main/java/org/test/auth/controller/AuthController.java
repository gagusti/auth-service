package org.test.auth.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.auth.dto.LoginDTO;
import org.test.auth.dto.ResponseSignUpDTO;
import org.test.auth.dto.SignUpDTO;
import org.test.auth.service.AuthService;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseSignUpDTO> signUp(@RequestBody @Valid SignUpDTO signUpDTO) {
        return ResponseEntity.ok(authService.signUp(signUpDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginDTO loginDTO) {
        return ResponseEntity.ok(authService.login(loginDTO));
    }
}
