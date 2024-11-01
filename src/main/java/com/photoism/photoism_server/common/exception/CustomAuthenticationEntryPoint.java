package com.photoism.photoism_server.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.photoism.photoism_server.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 인증 실패 시 호출되는 메서드
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 응답의 문자 인코딩과 콘텐츠 타입 설정
        response.setCharacterEncoding("UTF-8");  // 문자 인코딩을 UTF-8로 설정
        response.setContentType(APPLICATION_JSON.toString());  // 콘텐츠 타입을 JSON으로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // HTTP 상태 코드를 401 (Unauthorized)으로 설정

        // 에러 응답 객체 생성
        ErrorResponse errorResponse = ErrorResponse.of(
                "사용자 인증에 실패하였습니다.",  // 에러 메시지
                request.getRequestURI(),  // 요청 URI
                null  // 입력 에러 정보 (없음)
        );

        // Jackson ObjectMapper를 사용하여 ErrorResponse 객체를 JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Java 8 날짜 및 시간 API 모듈 등록
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 날짜를 타임스탬프로 변환하지 않도록 설정

        // 응답에 JSON 문자열을 작성
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(errorResponse));  // ErrorResponse 객체를 JSON 문자열로 변환하여 응답 본문에 작성
        writer.flush();  // 버퍼를 플러시하여 응답을 클라이언트에 전송
        writer.close();  // PrintWriter 닫기
    }
}
