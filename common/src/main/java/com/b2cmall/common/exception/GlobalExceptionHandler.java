package com.b2cmall.common.exception;

import com.b2cmall.common.response.BaseResponseVO;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** MVC 服务显式导入此处理器；只返回安全的错误说明，不向客户端暴露 SQL 或堆栈。 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponseVO<Void>> handleBusiness(BusinessException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(BaseResponseVO.failure(exception.getStatus(), exception.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        // 仅使用校验提示，不拼接 rejectedValue，防止密码出现在错误响应中。
        String message = exception.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage()).distinct().sorted()
                .collect(Collectors.joining("；"));
        return new ResponseEntity<>(BaseResponseVO.failure(status.value(), message), headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(BaseResponseVO.failure(status.value(), "请求 JSON 格式错误"), headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(BaseResponseVO.failure(status.value(), status.getReasonPhrase()), headers, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseVO<Void>> handleUnexpected(Exception exception) {
        log.error("请求处理失败", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponseVO.failure(500, "服务器内部错误"));
    }
}
