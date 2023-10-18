package com.jsbserver.jsbAPI.controller; 

import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;

// import org.springframework.web.bind.annotation.PostMapping; 
// import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jsbserver.jsbAPI.entity.Account;
import com.jsbserver.jsbAPI.security.SecurityConstants;
import com.jsbserver.jsbAPI.service.AccountService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor; 
 
@RestController 
@AllArgsConstructor 
@RequestMapping("/admin")
public class AccountController { 
    @Autowired 
    AccountService accountService; 
 
    //CheckGroup Method
    public ResponseEntity<Object> checkGroup(String username, String group) {
        //##########TOKEN VALIDATION LOGIC################

        boolean isInGroup = accountService.checkGroup(username, group);
        String message = isInGroup
                ? username + " is a " + group + "!"
                : username + " is not a " + group + " member!";
        if (isInGroup) {
            Account user = accountService.getOneUser(username);
            if (user != null) {
                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("groups", user.getGroups());

                Map<String, Object> data = new HashMap<>();
                // data.put("error", null);
                data.put("message", message);
                data.put("isGroup", isInGroup);

                return new ResponseEntity<>(data, HttpStatus.OK); // User retrieval successful
            } else {
                Map<String, Object> datafail = new HashMap<>();
                datafail.put("error", message);
                datafail.put("isGroup", isInGroup);

                // datafail.put("message", null);
                return new ResponseEntity<>(datafail, HttpStatus.OK); // User retrieval failed
            }
        } else {
            // Handle the case where the user is not in the group
            Map<String, Object> datafail = new HashMap<>();
            datafail.put("error", message);
            datafail.put("message", null);
            datafail.put("isGroup", isInGroup);

            return new ResponseEntity<>(datafail, HttpStatus.OK); // User not in the group
        }
    }
    
    @PostMapping("/checkGroup")
    public ResponseEntity<Object> checkingGroup(HttpServletRequest request){

        
        // System.out.println(WebUtils.getCookie(request, "jwt").getValue());
        String group="";
        String token = WebUtils.getCookie(request, "jwt").getValue();
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET_KEY))
            .build()
            .verify(token);

        String user = decodedJWT.getClaim("username").toString();

        String username = user.substring(1, user.length() - 1);
        // String ipAddr = decodedJWT.getClaim("ipaddress").toString();
        // String browser = decodedJWT.getClaim("browser").toString();

    

        try {
            ObjectMapper mapper = new ObjectMapper();

            TypeFactory factory = mapper.getTypeFactory();

            MapType maptype = factory.constructMapType(HashMap.class, String.class, String.class);

            HashMap<String, String> body = mapper.readValue(request.getInputStream(), maptype);

            // System.out.println(body.get("group"));
            group = body.get("group");
            
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return checkGroup(username,group);
        
    }
    

    @PostMapping("/getAllUsers") 
    public ResponseEntity<Object> getAllAccounts() { 
        Map<String, Object> data = new HashMap<>();
        data.put("error", null);
        List<Account> accList = accountService.getAllAccounts();
        data.put("message", accList);
        return new ResponseEntity<>(data, HttpStatus.OK); 
    } 
 
    @PostMapping("/createUser") 
    public ResponseEntity<Object> createUser(@RequestBody Account newUser) { 
        try {
            Map<String, Object> data = new HashMap<>();
            String message = "User " + accountService.createUser(newUser) + " created!";
            data.put("error", null);
            data.put("message", message);
            return new ResponseEntity<>(data, HttpStatus.OK); // User creation successful
        } catch (Exception e) { 
            Map<String, Object> datafail = new HashMap<>();
            datafail.put("error", e.toString());
            datafail.put("message", "check input criteria and try again!");
            return new ResponseEntity<>(datafail, HttpStatus.OK); // User creation failed 
        } 
    } 

    @PostMapping("/getOneUser")
    public ResponseEntity<Object> getOneUser(@RequestBody Map<String, String> requestBody) {
    try {
        String username = requestBody.get("username");
        Account user = accountService.getOneUser(username);

        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("err", null);

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("username", user.getUsername());
            userDetails.put("email", user.getEmail());
            userDetails.put("active", user.isActive());
            userDetails.put("groups", user.getGroups());

            data.put("message", userDetails);

            return new ResponseEntity<>(data, HttpStatus.OK); // User retrieval successful
        } else {
            Map<String, Object> datafail = new HashMap<>();
            datafail.put("err", "User not found");
            datafail.put("message", null);
            return new ResponseEntity<>(datafail, HttpStatus.OK); // User retrieval failed
        }
    } catch (Exception e) {
        Map<String, Object> datafail = new HashMap<>();
        datafail.put("err", e.toString());
        datafail.put("message", "check input criteria and try again!");
        return new ResponseEntity<>(datafail, HttpStatus.OK); // Internal server error
        }
    }

    //TO-DO:
    //Encryption of password required.
    @PostMapping("/updateUser")
    public ResponseEntity<Object> updateUser(@RequestBody Account updatedUser) {
        try {
            Map<String, Object> data = new HashMap<>();
            String message = "User " + accountService.updateUser(updatedUser) + " updated!";
            data.put("error", null);
            data.put("message", message);
            return new ResponseEntity<>(data, HttpStatus.OK); // User update successful
        } catch (Exception e) {
            Map<String, Object> datafail = new HashMap<>();
            datafail.put("error", e.toString());
            datafail.put("message", "Check input criteria and try again!");
            return new ResponseEntity<>(datafail, HttpStatus.OK); // User update failed
        }
    }

    //TO-DO:
    //Encryption of password required.
    @PostMapping("/checkPasswordLogin")
    public ResponseEntity<Object> checkPasswordLogin(@RequestBody Account userLogin) {
        String username = userLogin.getUsername();
        String password = userLogin.getPassword();

        // Check for empty input
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("error", "Invalid input");
            data.put("message", "Username and password cannot be empty");
            data.put("isLoggedIn", false); // Set isLoggedIn to false
            return new ResponseEntity<>(data, HttpStatus.OK);
        }

        try {
            // Check for active user via input username and active status
            Account user = accountService.getOneUser(username);

            if (user == null || !user.isActive()) {
                // Active user not found
                Map<String, Object> data = new HashMap<>();
                data.put("error", "User not found or not active");
                data.put("message", "Invalid Username or User not active");
                data.put("isLoggedIn", false); // Set isLoggedIn to false
                return new ResponseEntity<>(data, HttpStatus.OK);
            } else {
                // Active user found
                // Generate JWT token

                // Check for valid password
                // BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

                // if (passwordEncoder.matches(password, user.getPassword())) {
                if (password.equals(user.getPassword())) {

                    // Password verified
                    Map<String, Object> data = new HashMap<>();
                    data.put("error", null);
                    data.put("message", "Login success!");
                    data.put("isLoggedIn", true); // Set isLoggedIn to false
                    return new ResponseEntity<>(data, HttpStatus.OK);

                } else {
                    // Incorrect Password
                    Map<String, Object> data = new HashMap<>();
                    data.put("error", "Invalid password");
                    data.put("message", "Incorrect password");
                    data.put("isLoggedIn", false); // Set isLoggedIn to false
                    return new ResponseEntity<>(data, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
        Map<String, Object> data = new HashMap<>();
        data.put("error", "Exception Error");
        data.put("message", e.toString());
        return new ResponseEntity<>(data, HttpStatus.OK);
        }
    }
    

    @PostMapping("/updateUserWithoutPassword")
    public ResponseEntity<Object> updateUserWithoutPW(@RequestBody Account updatedUserWithoutPW) {
        try {
            Map<String, Object> data = new HashMap<>();
            String message = "User " + accountService.updateUserWithoutPW(updatedUserWithoutPW) + " updated!";
            data.put("error", null);
            data.put("message", message);
            return new ResponseEntity<>(data, HttpStatus.OK); // User update successful
        } catch (Exception e) {
            Map<String, Object> datafail = new HashMap<>();
            datafail.put("error", e.toString());
            datafail.put("message", "Check input criteria and try again!");
            return new ResponseEntity<>(datafail, HttpStatus.OK); // User update failed
        }
    }

    @PostMapping("/checkTokenLogin")
    public ResponseEntity<Object> checkTokenLogin(HttpServletRequest request){

        String token = WebUtils.getCookie(request, "jwt").getValue();
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET_KEY))
            .build()
            .verify(token);

        String user = decodedJWT.getClaim("username").toString();
        String username = user.substring(1, user.length() - 1);

        Boolean isLoggedIn = accountService.checkToken(username);

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("isLoggedIn",isLoggedIn);
        data.put("error", null);

         return new ResponseEntity<>(data, HttpStatus.OK);
}
    
}
