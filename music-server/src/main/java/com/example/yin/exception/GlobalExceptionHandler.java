package com.example.yin.exception;

import com.example.yin.common.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().isEmpty()
                ? "参数校验失败"
                : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.error(msg));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<R> handleBindException(BindException ex) {
        String msg = ex.getBindingResult().getAllErrors().isEmpty()
                ? "参数校验失败"
                : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.error(msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<R> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getMessage() == null ? "参数校验失败" : ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.error(msg));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<R> handleRuntime(RuntimeException ex) {
        if (ex.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(R.fatal("系统错误"));
        }
        if ("非法状态转换".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.error("非法状态转换"));
        }
        if ("权限不足".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(R.error("权限不足"));
        }
        if ("用户未登录".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(R.error("用户未登录"));
        }
        if ("用户未找到".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(R.error("用户未找到"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(R.fatal(ex.getMessage()));
    }
}
