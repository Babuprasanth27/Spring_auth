package com.example.authication.demo.exception;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException e){
        return ResponseEntity.status(401)
                .body(new ErrorResponse("Token Expired ", 401));
    }
    @ExceptionHandler(SignatureException.class)
        public ResponseEntity<ErrorResponse> handleSignature(SignatureException e){
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("Invalid token Signature ",401));

    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformed(MalformedJwtException e){
        return ResponseEntity.status(400)
                .body(new ErrorResponse("Malformed token ", 400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e){
        return ResponseEntity.status(500)
                .body(new ErrorResponse("Something went wrong ", 500));
    }
}
