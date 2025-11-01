package com.expencetracker.authservice.filter;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.expencetracker.authservice.exceptions.UserServiceExceptionHandler;
import com.expencetracker.authservice.service.impl.JWTService;
import com.expencetracker.authservice.service.impl.UserDetailsServiceImpl;
import com.expencetracker.authservice.util.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token=null;
		String username=null;
		
		try {
		if(authHeader!=null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
			token= authHeader.substring(7).trim();
			username=jwtService.extractUserName(token);
			
			if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
				UserDetails userByUsername = applicationContext.getBean(UserDetailsServiceImpl.class).loadUserByUsername(username);
				if(jwtService.validateToken(token, userByUsername)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
	                        userByUsername, null, userByUsername.getAuthorities());
	                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authToken);
				}
				else {
					throw new UserServiceExceptionHandler("Token is invalid!", HttpStatus.UNAUTHORIZED);
				}
				
			}
			else {
				throw new UserServiceExceptionHandler("Token is invalid for getting username", HttpStatus.UNAUTHORIZED);
			}
		}
		} catch (ExpiredJwtException e) {
            // Handle expired token error and send a custom message
            handleException(response, "Token has expired. Please log in again.", HttpStatus.UNAUTHORIZED);
            return; // Prevent further processing of the filter chain
        } catch (Exception e) {
            // Handle other JWT-related exceptions
            handleException(response, "Invalid token. Please log in again.", HttpStatus.UNAUTHORIZED);
            return; // Prevent further processing of the filter chain
        }
		filterChain.doFilter(request, response);
			
		}
	
private void handleException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
    response.setStatus(status.value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String jsonResponse = objectMapper.writeValueAsString(
            new ErrorResponse(status.value(), message, LocalDateTime.now(), null)
    );

    response.getWriter().write(jsonResponse);
}
}


