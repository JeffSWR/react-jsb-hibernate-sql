package com.jsbserver.jsbAPI.security.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jsbserver.jsbAPI.security.SecurityConstants;
import com.jsbserver.jsbAPI.service.AccountService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

  @Autowired
  AccountService accountService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException, JWTVerificationException {

    String header;
    try {
      header = WebUtils.getCookie(request, "jwt").getValue();
    } catch (Exception e) {
      response.setContentType("application/json");
      response.getWriter().write("{\"isLoggedIn\":false,\"error\":\"No Token Received\"}");
      response.getWriter().flush();
      return;
    }

    String token = header;

    DecodedJWT decodedJWT;

    try {
      decodedJWT = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET_KEY))
        .build()
        .verify(token);
    } catch (Exception e) {
      // TODO: handle exception
      response.setContentType("application/json");
      response.getWriter().write("{\"isLoggedIn\":false,\"error\":\"Invalid Token Received\"}");
      response.getWriter().flush();
      return;
    }


    String user = decodedJWT.getClaim("username").toString();
    String ipAddr = decodedJWT.getClaim("ipaddress").toString();
    String browser = decodedJWT.getClaim("browser").toString();

    // Remove double quotes from string
    String username = user.substring(1, user.length() - 1);
    String ipAddrStr = ipAddr.substring(1, ipAddr.length() - 1);
    String browserStr = browser.substring(1, browser.length() - 1);

    if (request.getRemoteAddr().contentEquals(ipAddrStr) && request.getHeader("User-Agent").contentEquals(browserStr)
        && accountService.getOneUser(username) != null) {
      Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList());
      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json");
      response.getWriter().write("{\"isLoggedIn\":false,\"error\":\"No Token Received\"}");
      response.getWriter().flush();
    }
  }
}
