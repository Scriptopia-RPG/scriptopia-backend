package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.exception.ErrorResponse;
import com.scriptopia.demo.exception.ErrorCode;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (statusCode != null) {
            int status = Integer.parseInt(statusCode.toString());

            if (status == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity
                        .status(ErrorCode.E_404.getStatus())
                        .body(new ErrorResponse(ErrorCode.E_404));
            }
        }

        return ResponseEntity
                .status(ErrorCode.E_500.getStatus())
                .body(new ErrorResponse(ErrorCode.E_500));
    }
}
