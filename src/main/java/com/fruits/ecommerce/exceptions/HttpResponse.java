package com.fruits.ecommerce.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class HttpResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String timeStamp;
    private int statusCode; // 200, 201, 400, 500
    private HttpStatus httpStatus;
    private String reason;
    private String message;
    private List<ValidationError> errors;

    public HttpResponse() {
        this.timeStamp = LocalDateTime.now(ZoneOffset.UTC).toString();
    }


    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this();
        this.statusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
        this.errors = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class ValidationError {
        private String field;
        private String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }

}
