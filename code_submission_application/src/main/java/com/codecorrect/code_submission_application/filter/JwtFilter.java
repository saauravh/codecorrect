package com.codecorrect.code_submission_application.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.codecorrect.code_submission_application.repository.UserRepository;
import com.codecorrect.code_submission_application.util.JwtUtil;



public class JwtFilter extends OncePerRequestFilter{    //OncePerRequestFilter is to make sure only one jwt is created for one user
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
      

        //Get authorization token and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header.isBlank() || !header.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }

        //Get Jwt token and validate
        final String token = header.split(" ")[1].trim();
            
        UserDetails userDetails = userRepository.findByUsername(jwtUtil.extractUsername(token)).orElse(null);

        if(!jwtUtil.validateToken(token,userDetails)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = 
        new UsernamePasswordAuthenticationToken( userDetails,null,
         userDetails == null ? List.of() : userDetails.getAuthorities());

        authenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
         



    }


    
    
}
