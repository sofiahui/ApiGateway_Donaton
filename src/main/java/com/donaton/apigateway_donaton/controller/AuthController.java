package com.donaton.apigateway_donaton.controller;

import com.donaton.apigateway_donaton.dto.LoginRequest;
import com.donaton.apigateway_donaton.dto.LoginResponse;
import com.donaton.apigateway_donaton.security.JwtUtil;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Base de datos en memoria de usuarios registrados
    private static final Map<String, Map<String, String>> usuarios = new ConcurrentHashMap<>();

    static {
        // Usuario por defecto para pruebas
        Map<String, String> admin = new ConcurrentHashMap<>();
        admin.put("password", "1234");
        admin.put("nombre", "Admin");
        admin.put("email", "admin@donaton.cl");
        usuarios.put("admin", admin);
    }

    @PostMapping("/register")
    public Mono<Map<String, String>> register(@RequestBody Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");
        String nombre   = body.getOrDefault("nombre", "");

        if (email == null || password == null) {
            return Mono.error(new RuntimeException("Email y contraseña son requeridos"));
        }

        // Usa el email como username
        if (usuarios.containsKey(email)) {
            return Mono.error(new RuntimeException("El email ya está registrado"));
        }

        Map<String, String> nuevoUsuario = new ConcurrentHashMap<>();
        nuevoUsuario.put("password", password);
        nuevoUsuario.put("nombre", nombre);
        nuevoUsuario.put("email", email);
        usuarios.put(email, nuevoUsuario);

        String token = JwtUtil.generateToken(email);
        return Mono.just(Map.of(
            "token", token,
            "username", nombre.isEmpty() ? email : nombre,
            "email", email,
            "mensaje", "Registro exitoso"
        ));
    }

    @PostMapping("/login")
    public Mono<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String email    = body.getOrDefault("email", body.get("username"));
        String password = body.get("password");

        if (email == null || password == null) {
            return Mono.error(new RuntimeException("Credenciales requeridas"));
        }

        Map<String, String> usuario = usuarios.get(email);

        if (usuario == null || !usuario.get("password").equals(password)) {
            return Mono.error(new RuntimeException("Credenciales inválidas"));
        }

        String nombre = usuario.getOrDefault("nombre", email);
        String token  = JwtUtil.generateToken(email);

        return Mono.just(Map.of(
            "token", token,
            "username", nombre,
            "email", email
        ));
    }
}