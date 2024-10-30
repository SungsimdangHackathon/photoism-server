package com.photoism.photoism_server.config.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

// API 응답에서 에러 정보를 표현하는 클래스
public record ErrorResponse(
        String message,  // 에러 메시지
        String path,  // 에러가 발생한 요청의 경로
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime time,  // 에러 발생 시간, JSON 직렬화 시 특정 포맷으로 변환됨
        List<FieldError> inputErrors  // 입력 필드의 에러 정보 리스트
) {
    // 입력 필드의 에러 정보를 표현하는 클래스
    public record FieldError(
            String field,  // 에러가 발생한 필드명
            Object rejectedValue,  // 해당 필드에서 거부된 값
            String message  // 필드 에러 메시지
    ) {
    }

    // ErrorResponse 객체를 생성하는 정적 메서드
    public static ErrorResponse of(String message, String path, List<FieldError> inputErrors) {
        // 현재 시간으로 ErrorResponse 객체를 생성하여 반환
        return new ErrorResponse(message, path, LocalDateTime.now(), inputErrors);
    }
}
