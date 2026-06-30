package com.example.indras.common.exception;

import com.example.indras.common.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Object>> handleBizException(BizException ex) {
        HttpStatus status = mapStatus(ex.getCode());
        return ResponseEntity.status(status).body(ApiResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .data(ex.getData())
                .timestamp(OffsetDateTime.now())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .toList();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("fieldErrors", fieldErrors);
        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(40001)
                .message("参数校验失败")
                .data(data)
                .timestamp(OffsetDateTime.now())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                .code(50000)
                .message(ex.getMessage() == null ? "系统异常" : ex.getMessage())
                .timestamp(OffsetDateTime.now())
                .build());
    }

    private Map<String, String> toFieldError(FieldError error) {
        Map<String, String> item = new LinkedHashMap<>();
        item.put("field", error.getField());
        item.put("message", error.getDefaultMessage());
        return item;
    }

    private HttpStatus mapStatus(int code) {
        return switch (code) {
            case 40001 -> HttpStatus.BAD_REQUEST;
            case 40100 -> HttpStatus.UNAUTHORIZED;
            case 40300 -> HttpStatus.FORBIDDEN;
            case 40400 -> HttpStatus.NOT_FOUND;
            case 40900 -> HttpStatus.CONFLICT;
            case 42200 -> HttpStatus.UNPROCESSABLE_ENTITY;
            case 50200, 50201 -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
