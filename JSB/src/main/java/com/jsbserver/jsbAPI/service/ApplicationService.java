package com.jsbserver.jsbAPI.service; 
 
import java.util.List; 

import org.springframework.beans.factory.annotation.Autowired; 

import org.springframework.stereotype.Service; 
import org.springframework.web.bind.annotation.RequestBody; 
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import BCryptPasswordEncoder 
// import org.springframework.transaction.annotation.Transactional; 
 
import com.jsbserver.jsbAPI.entity.Application;

import com.jsbserver.jsbAPI.repository.ApplicationDAO;


import lombok.AllArgsConstructor;
 
@Service 
@AllArgsConstructor 
public class ApplicationService { 
 
    @Autowired 
    ApplicationDAO applicationDAO;

    public String createApplication(Application newApplication) { 
        return applicationDAO.createApplication(newApplication);
    }  

    public String updateApplication(Application updatedApplication) { 
        return applicationDAO.updateApplication(updatedApplication);
    }  

    public List<Application> getAllApplications() {
        return applicationDAO.getAllApplications(); 
    } 

    public Application getApplication(@RequestBody String appAcronym) {
        return applicationDAO.getApplication(appAcronym); 
    } 



    
}

