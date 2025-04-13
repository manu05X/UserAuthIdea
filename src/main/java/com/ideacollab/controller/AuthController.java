package com.ideacollab.controller;

import com.ideacollab.dto.*;
import com.ideacollab.dto.ResponseStatus;
import com.ideacollab.exception.UserAlreadyExistsException;
import com.ideacollab.exception.UserDoesNotExistException;
import com.ideacollab.model.SessionStatus;
import com.ideacollab.model.TokenValidationResult;
import com.ideacollab.model.User;
import com.ideacollab.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request) throws UserAlreadyExistsException {
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword(), request.getName());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) throws UserDoesNotExistException {
        ResponseEntity<UserDto> userResponse = authService.login(request.getEmail(), request.getPassword());
        if (userResponse == null || userResponse.getBody() == null) {
            throw new UserDoesNotExistException("User does not exist with email: " + request.getEmail());
        }
        return userResponse;
    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                       HttpServletResponse response) {
        authService.logout(request, response);
        //return ResponseEntity.ok().build();
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Logout successful"
        ));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody ValidateTokenRequestDto request) {
        TokenValidationResult result = authService.validateToken(request.getToken());

        return switch (result.getStatus()) {
            case ACTIVE -> ResponseEntity.ok(Map.of(
                    "status", "ACTIVE",
                    "userId", result.getUserId(),
                    "email", result.getEmail(),
                    "roles", result.getRoles()
            ));
            case EXPIRED -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", "EXPIRED",
                            "message", "Token has expired. Please login again."
                    ));
            case LOGGED_OUT -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", "LOGGED_OUT",
                            "message", "User has logged out. Please login again."
                    ));
            case INVALID -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", "INVALID",
                            "message", "Invalid token. Access denied."
                    ));
        };
    }
}