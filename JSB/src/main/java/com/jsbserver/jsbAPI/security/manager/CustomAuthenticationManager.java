package com.jsbserver.jsbAPI.security.manager;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.jsbserver.jsbAPI.entity.Account;
import com.jsbserver.jsbAPI.service.AccountService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager{
  private AccountService accountService;
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException{
    Account userAcc = accountService.getOneUser(authentication.getName());
    if(userAcc == null){
      throw new BadCredentialsException("You provided an incorrect username or password");
    }
    else if(!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), userAcc.getPassword())){
      throw new BadCredentialsException("You provided an incorrect username or password");
    }
    else if(userAcc.isActive() != true){
      throw new BadCredentialsException("You provided an inactive account");
    }
    return new UsernamePasswordAuthenticationToken(authentication.getName(), userAcc.getPassword());
  }
}
