package com.donaton.apigateway_donaton.coontroller;


import com.donaton.apigateway_donaton.dto.LoginRequest;
import com.donaton.apigateway_donaton.dto.LoginResponse;
import com.donaton.apigateway_donaton.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        if ("admin".equals(request.username) && "1234".equals(request.password)) {
            String token = JwtUtil.generateToken(request.username);
            return new LoginResponse(token);
        }
        throw new RuntimeException("Credenciales inválidas");
    }
}