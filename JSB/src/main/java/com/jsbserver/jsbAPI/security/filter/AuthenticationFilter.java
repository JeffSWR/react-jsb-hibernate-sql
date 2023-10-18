package com.jsbserver.jsbAPI.security.filter;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsbserver.jsbAPI.entity.LoginUser;
import com.jsbserver.jsbAPI.security.SecurityConstants;
import com.jsbserver.jsbAPI.security.manager.CustomAuthenticationManager;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{
  private CustomAuthenticationManager customAuthenticationManager;
  //login
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
    try{
      LoginUser userAcc = new ObjectMapper().readValue(request.getInputStream(), LoginUser.class);
      Authentication authentication = new UsernamePasswordAuthenticationToken(userAcc.getUsername(), userAcc.getPassword());
      return customAuthenticationManager.authenticate(authentication);
    }catch(IOException e){
      throw new RuntimeException(); 
    }
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException{
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json");
    response.getWriter().write("{\"isLoggedIn\":false,\"error\":\"Failed to Log In\"}");
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException{
    String ipAdd = request.getRemoteAddr();
    String browser = request.getHeader("User-Agent");
    String token = JWT.create()
                      .withSubject(authResult.getName() + ipAdd + browser)
                      .withClaim("username", authResult.getName())
                      .withClaim("ipaddress", ipAdd)
                      .withClaim("browser", browser)
                      .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION))
                      .sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY));

    Cookie cookie = new Cookie("jwt", token);
    response.addCookie(cookie);
    
    // response.addHeader(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);
    response.setContentType("application/json");
    response.getWriter().write("{\"username\":\""+ authResult.getName() + "\",\n\"isLoggedIn\":true,\"error\":null}");
  }
}
