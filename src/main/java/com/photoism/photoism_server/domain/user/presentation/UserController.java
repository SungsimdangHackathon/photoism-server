package com.photoism.photoism_server.domain.user.presentation;

import com.photoism.photoism_server.domain.user.presentation.dto.request.LoginRequest;
import com.photoism.photoism_server.domain.user.presentation.dto.request.SignUpRequest;
import com.photoism.photoism_server.domain.user.presentation.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.photoism.photoism_server.domain.user.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService; // UserService 의존성 주입

    @PostMapping("/signup")
    public ResponseEntity<Void> signUpParent(
            @Valid @RequestBody SignUpRequest req) {
        userService.create(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(userService.login(req.email(),req.password()));
    }

}
