package com.jsbserver.jsbAPI.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class EmailServiceImpl{
  @Autowired
  private JavaMailSender emailSender;

  public void sendSimpleMessage(String to, String subject, String text){
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("admin@mail.com");
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    emailSender.send(message);
  }

  @Async
  public void sendEmail(String taskId, List<String> email){
    for(String UserMail : email){
      sendSimpleMessage(UserMail, "Task promoted to Done", "Task " + taskId + " has been promoted to Done");
    }
  }
}
