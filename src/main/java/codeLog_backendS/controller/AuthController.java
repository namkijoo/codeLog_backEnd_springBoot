package codeLog_backendS.controller;


import codeLog_backendS.dto.LoginRequest;
import codeLog_backendS.dto.LoginResponse;
import codeLog_backendS.dto.SignupRequest;
import codeLog_backendS.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String,String>> signup(@RequestBody SignupRequest request){
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","회원가입 완료"));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

}
