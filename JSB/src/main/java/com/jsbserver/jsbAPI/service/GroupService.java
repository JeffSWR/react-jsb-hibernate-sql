package com.jsbserver.jsbAPI.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.jsbserver.jsbAPI.entity.Group;
import com.jsbserver.jsbAPI.repository.GroupDAO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GroupService {

    @Autowired
    GroupDAO groupDAO;

    public String createGroup(@RequestBody Group newUser) { 
        return groupDAO.createGroup(newUser);
    } 

    public List<Group> getAllGroups() {
        return groupDAO.getAllGroup(); 
    } 
    
}
