package com.photoism.photoism_server.domain.user.service;

import com.photoism.photoism_server.common.dto.TokenResponse;
import com.photoism.photoism_server.common.exception.BusinessException;
import com.photoism.photoism_server.common.exception.ErrorCode;
import com.photoism.photoism_server.common.jwt.Jwt;
import com.photoism.photoism_server.domain.user.domain.entity.User;
import com.photoism.photoism_server.domain.user.domain.repository.UserRepository;
import com.photoism.photoism_server.domain.user.presentation.dto.request.SignUpRequest;
import com.photoism.photoism_server.domain.user.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Jwt jwt;

    @Transactional
    public void create(SignUpRequest req) {
        try {
            userRepository.save(User.builder()
                    .username(req.username()) // 사용자 아이디
                    .password(new BCryptPasswordEncoder().encode(req.password())) // 비밀번호
                    .email(req.email())
                    .build()
            );
        } catch (Exception e) {
            log.error("회원 생성 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public LoginResponse login(String email, String password) {
        try {
            // 이메일로 사용자 찾기
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_NOT_FOUND));  // 이메일이 존재하지 않을 때

            // 비밀번호 일치 여부 확인
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BusinessException(ErrorCode.INVALID_PASSWORD); // 비밀번호가 틀렸을 때
            }

            // 비밀번호가 일치하면 로그인 응답 객체를 생성하여 반환합니다.
            return getLoginResponse(user);
        } catch (BusinessException e) {
            log.error("로그인 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("로그인 중 알 수 없는 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public LoginResponse getLoginResponse(User user)
    {
        var tokens = publishToken(user);
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return new LoginResponse(tokens); //isNewUser은 떄떄로 필요함

        }
        else {
            return new LoginResponse(tokens);
        }
    }

    @Transactional
    public TokenResponse publishToken(User user) {
        TokenResponse tokenResponse = jwt.generateAllToken(
                Jwt.Claims.from(user.getId())
        );

        // 새로운 User 객체를 생성하여 변경 사항을 반영
        User updatedUser = User.builder()
                .id(user.getId()) // 기존 User의 다른 필드들도 모두 설정
                .username(user.getUsername())
                .email(user.getEmail())
                .refreshToken(tokenResponse.refreshToken()) // 새로운 refreshToken 설정
                .build();

        // userRepository나 영속성 컨텍스트를 통해 updatedUser 저장
        userRepository.save(updatedUser); // 필요 시 userRepository 이용

        return tokenResponse;
    }
}
