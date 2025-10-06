package com.example.customer.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理客户不存在异常 - 同时支持Web和API请求
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public Object handleCustomerNotFoundException(CustomerNotFoundException ex,
                                                   HttpServletRequest request,
                                                   RedirectAttributes redirectAttributes) {
        logger.error("Customer not found: {}", ex.getMessage());

        // 判断是否为API请求
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    ex.getMessage(),
                    request.getRequestURI(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return new ModelAndView("redirect:/customers");
    }

    /**
     * 处理重复手机号异常 - 同时支持Web和API请求
     */
    @ExceptionHandler(DuplicatePhoneException.class)
    public Object handleDuplicatePhoneException(DuplicatePhoneException ex,
                                                HttpServletRequest request,
                                                RedirectAttributes redirectAttributes) {
        logger.error("Duplicate phone number: {}", ex.getMessage());

        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    ex.getMessage(),
                    request.getRequestURI(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return new ModelAndView("redirect:/customers");
    }

    /**
     * 处理无效数据异常 - 同时支持Web和API请求
     */
    @ExceptionHandler(InvalidCustomerDataException.class)
    public Object handleInvalidCustomerDataException(InvalidCustomerDataException ex,
                                                     HttpServletRequest request,
                                                     RedirectAttributes redirectAttributes) {
        logger.error("Invalid customer data: {}", ex.getMessage());

        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    ex.getMessage(),
                    request.getRequestURI(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return new ModelAndView("redirect:/customers");
    }

    /**
     * 处理验证异常 - 同时支持Web和API请求
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationExceptions(MethodArgumentNotValidException ex,
                                             HttpServletRequest request,
                                             RedirectAttributes redirectAttributes) {
        logger.error("Validation failed: {}", ex.getMessage());

        if (isApiRequest(request)) {
            Map<String, String> errors = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            fieldError -> fieldError.getDefaultMessage() != null ?
                                    fieldError.getDefaultMessage() : "验证失败"
                    ));

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "数据验证失败");
            response.put("errors", errors);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.badRequest().body(response);
        }

        // Web请求 - 重定向回表单页面
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
        return new ModelAndView("redirect:/customers");
    }

    /**
     * 处理通用异常 - 同时支持Web和API请求
     */
    @ExceptionHandler(Exception.class)
    public Object handleGlobalException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred: ", ex);

        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "服务器内部错误: " + ex.getMessage(),
                    request.getRequestURI(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        ModelAndView mav = new ModelAndView("error/error");
        mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        mav.addObject("message", "服务器内部错误，请稍后重试");
        mav.addObject("exception", ex.getMessage());
        return mav;
    }

    /**
     * 判断是否为API请求
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return uri.startsWith("/api/") ||
               (accept != null && accept.contains("application/json"));
    }

    /**
     * 错误响应DTO
     */
    public static class ErrorResponse {
        private int status;
        private String message;
        private String path;
        private LocalDateTime timestamp;

        public ErrorResponse(int status, String message, String path, LocalDateTime timestamp) {
            this.status = status;
            this.message = message;
            this.path = path;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }
}
