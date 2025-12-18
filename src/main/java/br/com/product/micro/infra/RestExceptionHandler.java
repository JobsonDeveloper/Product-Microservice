package br.com.product.micro.infra;

import br.com.product.micro.exception.ErrorCreatingProductException;
import br.com.product.micro.exception.ProductAlreadyRegisteredException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private Map<String, String> mapError(FieldError fieldError) {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("field", fieldError.getField());
        mapping.put("message", fieldError.getDefaultMessage());

        return mapping;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            org.springframework.http.HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");

        List<Map<String, String>> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapError)
                .toList();
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ErrorCreatingProductException.class)
    private ResponseEntity<DefaultErrorResponse> errorCreatingProductHandler(ErrorCreatingProductException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(defaultErrorResponse);
    }

    @ExceptionHandler(ProductAlreadyRegisteredException.class)
    private ResponseEntity<DefaultErrorResponse> productAlreadyRegisteredHandler(ProductAlreadyRegisteredException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(defaultErrorResponse);
    }
}
