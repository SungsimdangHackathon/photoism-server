package com.photoism.photoism_server.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.photoism.photoism_server.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.photoism.photoism_server.common.exception.ErrorCode.*;

@Slf4j  // 클래스에서 로그를 기록할 수 있도록 Lombok 애너테이션 추가
@RestControllerAdvice  // 모든 REST 컨트롤러에서 발생하는 예외를 처리하기 위한 애너테이션
public class GlobalExceptionHandler {

    // IllegalArgumentException 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            HttpServletRequest request, IllegalArgumentException e
    ) {
        logInfo(e, request.getRequestURI());  // 예외 정보 로깅

        // HTTP 400 Bad Request 응답 생성
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(BAD_REQUEST.getMessage(), request.getRequestURI(), null));
    }

    // MethodArgumentNotValidException 예외 처리 (메서드 인자 유효성 검사 실패)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            HttpServletRequest request, MethodArgumentNotValidException e
    ) {
        logInfo(e, request.getRequestURI());  // 예외 정보 로깅

        // HTTP 400 Bad Request 응답 생성
        return ResponseEntity
                .badRequest()
                .body(
                        ErrorResponse.of(
                                BAD_REQUEST.getMessage(),
                                request.getRequestURI(),
                                makeFieldErrorsFromBindingResult(e.getBindingResult())
                        )
                );
    }

    // ConstraintViolationException 예외 처리 (제약 조건 위반)
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(
            HttpServletRequest request, ConstraintViolationException e
    ) {
        logInfo(e, request.getRequestURI());  // 예외 정보 로깅

        // HTTP 400 Bad Request 응답 생성
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(
                        BAD_REQUEST.getMessage(),
                        request.getRequestURI(),
                        makeFieldErrorsFromConstraintViolations(e.getConstraintViolations())
                ));
    }

    // BindException 예외 처리 (바인딩 오류)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            HttpServletRequest request, BindException e
    ) {
        logInfo(e, request.getRequestURI());  // 예외 정보 로깅

        // HTTP 400 Bad Request 응답 생성
        return ResponseEntity
                .badRequest()
                .body(
                        ErrorResponse.of(
                                BAD_REQUEST.getMessage(),
                                request.getRequestURI(),
                                makeFieldErrorsFromBindingResult(e.getBindingResult())
                        )
                );
    }

    // MethodArgumentTypeMismatchException 예외 처리 (메서드 인자 타입 불일치)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            HttpServletRequest request, MethodArgumentTypeMismatchException e
    ) {
        logInfo(e, request.getRequestURI());  // 예외 정보 로깅

        // HTTP 400 Bad Request 응답 생성
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getMessage(),
                request.getRequestURI(),
                List.of(new ErrorResponse.FieldError(
                        e.getName(),
                        Objects.requireNonNull(e.getValue()).toString(),
                        e.getParameter().getParameterName()
                ))
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // InvalidFormatException 예외 처리 (잘못된 형식)
    @ExceptionHandler(InvalidFormatException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidFormatException(
            HttpServletRequest request, InvalidFormatException e
    ) {
        logInfo(e, request.getRequestURI());  // 예외 정보 로깅

        // HTTP 400 Bad Request 응답 생성
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(e.getMessage(), request.getRequestURI(), null));
    }

    // NullPointerException 예외 처리 (널 포인터 예외)
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponse> handleNullPointerException(
            HttpServletRequest request, NullPointerException e
    ) {
        logInfo(e, request.getRequestURI());  // 예외 정보 로깅

        // HTTP 400 Bad Request 응답 생성
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(e.getMessage(), request.getRequestURI(), null));
    }

    // BusinessException 예외 처리 (비즈니스 로직 관련 예외)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(HttpServletRequest request, BusinessException e) {
        logInfo(e, request.getRequestURI());  // 예외 정보 로깅

        // HTTP 상태 코드를 비즈니스 예외의 상태 코드로 설정하여 응답 생성
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(e.getErrorCode().getMessage(), request.getRequestURI(), null));
    }

    // AccessDeniedException 예외 처리 (액세스 거부)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(HttpServletRequest request,
                                                                     AccessDeniedException e) {
        logInfo(e, request.getRequestURI());  // 예외 정보 로깅
        throw new AccessDeniedException(e.getMessage());  // 액세스 거부 예외를 다시 던져서 전역 예외 처리기로 전달
    }

    // RuntimeException 예외 처리 (런타임 예외)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(HttpServletRequest request, RuntimeException e) {
        logWarn(e, request.getRequestURI());  // 예외 정보 로깅

        // HTTP 400 Bad Request 응답 생성
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(e.getMessage(), request.getRequestURI(), null));
    }

    // 모든 예외 처리 (기본 예외 처리)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception e) {
        logError(e, request.getRequestURI());  // 예외 정보 로깅

        // HTTP 400 Bad Request 응답 생성
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(e.getMessage(), request.getRequestURI(), null));
    }

    // BindingResult에서 필드 에러를 리스트로 변환
    private List<ErrorResponse.FieldError> makeFieldErrorsFromBindingResult(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),  // 필드 이름
                        error.getRejectedValue(),  // 거부된 값
                        error.getDefaultMessage()  // 기본 오류 메시지
                ))
                .toList();  // 리스트로 반환
    }

    // ConstraintViolation에서 필드 에러를 리스트로 변환
    private List<ErrorResponse.FieldError> makeFieldErrorsFromConstraintViolations(
            Set<ConstraintViolation<?>> constraintViolations
    ) {
        return constraintViolations.stream()
                .map(violation -> new ErrorResponse.FieldError(
                        getFieldFromPath(violation.getPropertyPath()),  // 필드 이름
                        violation.getInvalidValue().toString(),  // 잘못된 값
                        violation.getMessage()  // 오류 메시지
                ))
                .toList();  // 리스트로 반환
    }

    // 경로에서 필드 이름 추출
    private String getFieldFromPath(Path fieldPath) {
        PathImpl pathImpl = (PathImpl)fieldPath;  // PathImpl로 캐스팅
        return pathImpl.getLeafNode().toString();  // 리프 노드에서 필드 이름 반환
    }

    // 정보 수준 로그 기록
    private void logInfo(Exception e, String url) {
        log.info("URL = {}, Exception = {}, Message = {}", url, e.getClass().getSimpleName(), e.getMessage());
    }

    // 경고 수준 로그 기록
    private void logWarn(Exception e, String url) {
        log.warn("URL = {}, Exception = {}, Message = {}", url, e.getClass().getSimpleName(), e.getMessage());
    }

    // 오류 수준 로그 기록
    private void logError(Exception e, String url) {
        log.error("URL = {}, Exception = {}, Message = {}", url, e.getClass().getSimpleName(), e.getMessage());
    }
}

