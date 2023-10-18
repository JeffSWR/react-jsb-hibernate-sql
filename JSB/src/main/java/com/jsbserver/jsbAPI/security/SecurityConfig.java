package com.jsbserver.jsbAPI.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.jsbserver.jsbAPI.security.filter.AuthenticationFilter;
import com.jsbserver.jsbAPI.security.filter.JWTAuthorizationFilter;
import com.jsbserver.jsbAPI.security.manager.CustomAuthenticationManager;
import com.jsbserver.jsbAPI.service.AccountService;


import org.springframework.web.cors.UrlBasedCorsConfigurationSource;



import lombok.AllArgsConstructor;



@Configuration
@AllArgsConstructor
public class SecurityConfig {
  private final CustomAuthenticationManager customAuthenticationManager;

  @Autowired
  AccountService accountService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

    AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager);
    authenticationFilter.setFilterProcessesUrl("/login");

    //JWTAuthorizationFilter jWTAuthorizationFilter = new JWTAuthorizationFilter();
   
    http
        .cors((cors) -> cors.configurationSource(corsFilter())) 
        .csrf((csrf) -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
           .anyRequest().authenticated())
        .addFilter(authenticationFilter)
        .addFilterAfter(new JWTAuthorizationFilter(accountService), AuthenticationFilter.class) 
        .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
    return http.build();
  }
  
    @Bean
    CorsConfigurationSource corsFilter() {
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // Allow all origins
        config.addAllowedHeader("*"); // Allow all headers
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
