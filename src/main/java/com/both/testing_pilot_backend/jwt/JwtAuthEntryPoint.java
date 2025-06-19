package com.both.testing_pilot_backend.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JwtAuthEntryPoint handles unauthorized access attempts to secured REST endpoints.
 * It implements Spring Security's AuthenticationEntryPoint and is used to return
 * a 401 Unauthorized response when a request is made without valid authentication.
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request,
						 HttpServletResponse response,
						 AuthenticationException authException) throws IOException, ServletException {

		// Set the response status to 401 Unauthorized
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		// Create the response body
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString()); // Use ISO-8601 format
		body.put("status", HttpStatus.UNAUTHORIZED.value());
		body.put("error", "Unauthorized");

		// Check the cause of the exception to provide a more specific message
		if (authException.getCause() instanceof ExpiredJwtException) {
			body.put("message", "Your session has expired. Please log in again.");
		} else {
			body.put("message", "Authentication Failed: " + authException.getMessage());
		}

		body.put("path", request.getRequestURI());

		// Write the JSON response to the output stream
		objectMapper.writeValue(response.getOutputStream(), body);
	}
}
