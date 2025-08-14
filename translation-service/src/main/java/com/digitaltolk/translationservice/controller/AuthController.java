package com.digitaltolk.translationservice.controller;

import com.digitaltolk.translationservice.dto.UserLoginRequest;
import com.digitaltolk.translationservice.model.User;
import com.digitaltolk.translationservice.repository.UserRepository;
import com.digitaltolk.translationservice.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API for user login and registration")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns a JWT token for accessing secured endpoints.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6...\" }"))),
                    @ApiResponse(responseCode = "401", description = "Invalid username or password",
                            content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Invalid username or password")))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserLoginRequest.class),
                            examples = @ExampleObject(value = "{ \"username\": \"john_doe\", \"password\": \"secret\" }"))
            ) @org.springframework.web.bind.annotation.RequestBody UserLoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided username and password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully",
                            content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "User registered successfully"))),
                    @ApiResponse(responseCode = "400", description = "Username already exists",
                            content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Username already exists")))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody(
                    description = "New user credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserLoginRequest.class),
                            examples = @ExampleObject(value = "{ \"username\": \"omar\", \"password\": \"password\" }"))
            ) @org.springframework.web.bind.annotation.RequestBody UserLoginRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
