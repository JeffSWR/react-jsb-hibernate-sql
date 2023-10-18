package com.jsbserver.jsbAPI.service; 
 
import java.util.List; 
import java.util.stream.Collectors; 


import org.springframework.beans.factory.annotation.Autowired; 

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service; 
import org.springframework.web.bind.annotation.RequestBody; 
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import BCryptPasswordEncoder 
// import org.springframework.transaction.annotation.Transactional; 
 
import com.jsbserver.jsbAPI.entity.Account;
import com.jsbserver.jsbAPI.repository.AccountDAO;


import lombok.AllArgsConstructor;
 
@Service 
@AllArgsConstructor 
public class AccountService { 
 
    @Autowired 
    //AccountRepository accountRepository; 
    AccountDAO accountDAO;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
 
    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccount(); 
    } 

    public ResponseEntity<List<String>> getAllUsernames() { 
        List<Account> accounts = accountDAO.getAllAccount(); 
        List<String> usernames = accounts.stream() 
                .map(Account::getUsername) // Extract usernames from Account objects 
                .collect(Collectors.toList()); 
        return ResponseEntity.ok(usernames); 
    }  
 
    public String createUser(@RequestBody Account newUser) {
        //Encrypt user password 
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        return accountDAO.createUser(newUser);
    } 

    public Account getOneUser(String username) {
        return accountDAO.getOneUser(username);
    }
    
    public boolean checkGroup(String username, String group) {
        try {
            // Get the active user's groups from the database
            Account user = accountDAO.getOneUser(username);

            if (user != null && user.isActive() && user.getGroups() != null) {
                // Check if the user's groups contain the specified group
                String[] userGroups = user.getGroups().split(",");
                for (String userGroup : userGroups) {
                    if (userGroup.trim().equals(group)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    //TO-DO:
    //Encryption of password required.
    public String updateUser(@RequestBody Account newUser) { 
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        return accountDAO.updateUser(newUser);
    } 
    
    public String updateUserWithoutPW(@RequestBody Account newUser) { 
        return accountDAO.updateUserWithoutPW(newUser);
    } 

    public Boolean checkToken(String username){
        return accountDAO.checkToken(username);
    }
}


