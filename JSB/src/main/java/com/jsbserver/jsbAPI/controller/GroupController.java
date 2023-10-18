package com.jsbserver.jsbAPI.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.jsbserver.jsbAPI.entity.Group;
import com.jsbserver.jsbAPI.service.GroupService;


@RequestMapping("/admin")
@RestController
public class GroupController{
    @Autowired
    GroupService groupService;


    @PostMapping("/createGroup") 
    public ResponseEntity<Object> createGroup(@RequestBody Group newGrp) { 
        try { 
            Map<String, Object> data = new HashMap<>();
            String message = "Group " + groupService.createGroup(newGrp) + " created!";
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

    @PostMapping("/getAllGroups") 
    public ResponseEntity<Object> getAllGroups() { 
        Map<String, Object> data = new HashMap<>();
        data.put("error", null);
        List<Group> grpList = groupService.getAllGroups();
        List<String> groupNames = grpList.stream()
            .map(Group::getGroup_name) // Extract group names
            .collect(Collectors.toList());

        data.put("message", groupNames);

      return new ResponseEntity<>(data, HttpStatus.OK); 
  } 

    @GetMapping("/checkTokenLogin") 
    public ResponseEntity<Object> checkTokenLogin() { 
        Map<String, Object> data = new HashMap<>();
        data.put("error", null);
        data.put("message", "json msg");

      return new ResponseEntity<>(data, HttpStatus.OK); 
  } 

}
