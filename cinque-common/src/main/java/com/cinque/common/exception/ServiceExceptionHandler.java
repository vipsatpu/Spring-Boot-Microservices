package com.cinque.common.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.cinque.common.config.ApiErrorResponse;
import com.cinque.common.util.ErrorMessageConstants;

@ControllerAdvice
public class ParameterValidationExceptionHandler extends ResponseEntityExceptionHandler{

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<String, Object>();
		body.put("timestamp", Instant.now());
		body.put("status", status.value());

		// Get all errors
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());

		body.put("errors", errors);
		return new ResponseEntity<>(body, headers, status);

	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public final ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			WebRequest request) {
		List<String> details = ex.getConstraintViolations().parallelStream().map(e -> e.getMessage())
				.collect(Collectors.toList());
		
		ApiErrorResponse error = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
				String.join(ErrorMessageConstants.JOINING_DELIMETER, details));
		
		return new ResponseEntity<ApiErrorResponse>(error, HttpStatus.BAD_REQUEST);
	}
}
