package com.jsbserver.jsbAPI.controller; 

import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestBody;

// import org.springframework.web.bind.annotation.PostMapping; 
// import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController;

import com.jsbserver.jsbAPI.entity.Application;

import com.jsbserver.jsbAPI.service.ApplicationService;

import lombok.AllArgsConstructor; 
 
@RestController 
@AllArgsConstructor 
public class ApplicationController { 
    @Autowired 
    ApplicationService applicationService; 

    @PostMapping("/createApplication") 
    public ResponseEntity<Object> createApplication(@RequestBody Application newApplication) { 
        Map<String, Object> data = new HashMap<>();

        try {
            String appName = applicationService.createApplication(newApplication);
            data.put("error", null);
            data.put("response","sucess");
            data.put("message", appName+" successfully created");
            return new ResponseEntity<>(data, HttpStatus.OK); 
            
        } catch (Exception e) {
            data.put("error", e);
            data.put("response","Failure, try again!");
            data.put("message", "Creation error, try again!");
            return new ResponseEntity<>(data, HttpStatus.OK); 
        }

    } 

    @PostMapping("/getAllApplications") 
    public ResponseEntity<Object> getAllApplications() { 
        Map<String, Object> data = new HashMap<>();
        try {
            List<Application> appList = applicationService.getAllApplications();
            data.put("error", null);
            data.put("applications", appList);
            data.put("message","Successfully fetch all Applications");
            return new ResponseEntity<>(data, HttpStatus.OK); 
        } catch (Exception e) {
            data.put("error", e);
            data.put("applications", null);
            data.put("message", "Internal Error");
            return new ResponseEntity<>(data, HttpStatus.OK); 
        }
       
    } 

    @PostMapping("/getApplication")
    public ResponseEntity<Object> getApplication(@RequestBody Map<String, String> requestBody) {
        String appAcronym = requestBody.get("appAcronym");
        System.out.println(appAcronym);
        Map<String, Object> data = new HashMap<>();
    try {
        Application app = applicationService.getApplication(appAcronym);
        data.put("error", null);
        data.put("application", app);
        data.put("message", "Successfully fetched "+ appAcronym);
        return new ResponseEntity<>(data, HttpStatus.OK); // Internal server error
    } catch (Exception e) {
        data.put("error", e);
        data.put("application", null);
        data.put("message", "check input criteria and try again!");
        return new ResponseEntity<>(data, HttpStatus.OK); // Internal server error
        }
    }

    @PostMapping("/updateApplication")
    public ResponseEntity<Object> updateApplication(@RequestBody Application updatedApplication) {
        Map<String, Object> data = new HashMap<>();
    try {
        applicationService.updateApplication(updatedApplication);
        data.put("error", null);
        data.put("application", updatedApplication.getApp_Acronym());
        data.put("message", updatedApplication.getApp_Acronym() +" successfully updated!");
        return new ResponseEntity<>(data, HttpStatus.OK); // Internal server error
    } catch (Exception e) {
        data.put("error", e);
        data.put("application", null);
        data.put("message", "check input criteria and try again!");
        return new ResponseEntity<>(data, HttpStatus.OK); // Internal server error
        }
    }
 




    




    
}
