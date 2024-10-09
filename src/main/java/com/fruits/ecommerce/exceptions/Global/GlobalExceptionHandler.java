package com.fruits.ecommerce.exceptions.Global;


import com.fruits.ecommerce.exceptions.HttpResponse;
import com.fruits.ecommerce.exceptions.ExceptionsDomain.*;
import com.fruits.ecommerce.exceptions.products.InvalidProductDataException;
import com.fruits.ecommerce.exceptions.products.ProductNotFoundException;
import com.fruits.ecommerce.models.enums.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> handleUsernameExistException(UsernameExistException ex) {
        return createHttpResponse(HttpStatus.CONFLICT, "Username already exists", ex.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> handleEmailExistException(EmailExistException ex) {
        return createHttpResponse(HttpStatus.CONFLICT, "Email already exists", ex.getMessage());
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<HttpResponse> handleInvalidRoleException(InvalidRoleException ex) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, "Invalid role", ex.getMessage());
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<HttpResponse> handleInvalidUserDataException(InvalidUserDataException ex) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, "Invalid user data", ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return createHttpResponse(HttpStatus.UNAUTHORIZED, "Bad credentials", ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<HttpResponse> handleAuthenticationException(AuthenticationException ex) {
        return createHttpResponse(HttpStatus.UNAUTHORIZED, "Authentication failed", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> handleAuthenticationException(AccessDeniedException ex) {
        return createHttpResponse(HttpStatus.FORBIDDEN, "You do not have permission to access this resource",
                ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return createHttpResponse(HttpStatus.NOT_FOUND, "User not found", ex.getMessage());
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<HttpResponse> handleAccountLockedException(AccountLockedException ex) {
        return createHttpResponse(HttpStatus.LOCKED, "USER_ALREADY_LOCKED", ex.getMessage());
    }
    @ExceptionHandler(UnLockedAccountException.class)
    public ResponseEntity<HttpResponse> handleAccountUnLockedException(UnLockedAccountException ex) {
        return createHttpResponse(HttpStatus.CONFLICT, "USER_ALREADY_UNLOCKED", ex.getMessage());
    }
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String reason, String message) {
        HttpResponse httpResponse = new HttpResponse(
                httpStatus.value(),
                httpStatus,
                reason,
                message
        );
        return new ResponseEntity<>(httpResponse, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred", ex);
        HttpResponse httpResponse = new HttpResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred: " + ex.getMessage()
        );
        return new ResponseEntity<>(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<HttpResponse.ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new HttpResponse.ValidationError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        HttpResponse httpResponse = new HttpResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                "Multiple validation errors occurred"
        );
        httpResponse.setErrors(errors); // إضافة الأخطاء للـ response

        return new ResponseEntity<>(httpResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<HttpResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String type = ex.getRequiredType().getSimpleName();
        Object value = ex.getValue();
        String message = String.format("Invalid value for %s: '%s'.", name, value);

        if ("RoleType".equals(type)) {
            String availableRoles = Arrays.stream(RoleType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            message += " Available roles are: " + availableRoles;
        }

        return createHttpResponse(HttpStatus.BAD_REQUEST, "Invalid Parameter", message);
    }

    /** Product-Exceptions **/
    @ExceptionHandler(InvalidProductDataException.class)
    public ResponseEntity<HttpResponse> handleInvalidProductDataException(InvalidProductDataException ex) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, "Invalid product data", ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<HttpResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        return createHttpResponse(HttpStatus.NOT_FOUND, "Product not found", ex.getMessage());
    }

}