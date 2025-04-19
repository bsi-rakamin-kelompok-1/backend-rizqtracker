package id.co.bankbsi.rizqtracker.exception;

import id.co.bankbsi.rizqtracker.dto.response.BaseResponse;
import id.co.bankbsi.rizqtracker.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<BaseResponse> handleMissingRequestBody(HttpMessageNotReadableException ex) {
        BaseResponse errorResponse = new BaseResponse();
        errorResponse.setStatus(false);
        errorResponse.setMessage("Request body is required");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(false);
        errorResponse.setMessage("Validation error occurred");
        errorResponse.setErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<BaseResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
        BaseResponse errorResponse = new BaseResponse();
        errorResponse.setStatus(false);
        errorResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<BaseResponse> handleUserAlreadyExistsExceptions(UserAlreadyExistsException ex) {
        BaseResponse errorResponse = new BaseResponse();
        errorResponse.setStatus(false);
        errorResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGeneralExceptions(Exception ex) {
        BaseResponse errorResponse = new BaseResponse();
        errorResponse.setStatus(false);
        errorResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
