package com.jsbserver.jsbAPI.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.PostMapping; 
// import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jsbserver.jsbAPI.entity.Account;
import com.jsbserver.jsbAPI.entity.Task;
import com.jsbserver.jsbAPI.entity.Group;

import com.jsbserver.jsbAPI.security.SecurityConstants;
import com.jsbserver.jsbAPI.service.AccountService;
import com.jsbserver.jsbAPI.service.TaskService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor; 

@RestController
public class TaskController {

    @Autowired
    TaskService taskService;

    @PostMapping("/getAllTasks")
    public ResponseEntity<Object> getAllTasks(@RequestBody Map<String, String> requestBody) {
    try {
        String app = requestBody.get("app");
        List<Task> task = taskService.getAllTasks(app);

    if (task != null) {
        Map<String, Object> data = new HashMap<>();
        data.put("error", null);
        List<Task> taskList = taskService.getAllTasks(app);
        data.put("message", taskList);
        return new ResponseEntity<>(data, HttpStatus.OK); // Task retrieval successful
    } else {
        ArrayList<Task> list = new ArrayList<Task>();
        Map<String, Object> data= new HashMap<>();
        data.put("error", "No tasks found");
        data.put("message",list);
        return new ResponseEntity<>(data, HttpStatus.OK); // Task retrieval failed
    }
    } catch (Exception e) {
        System.out.println(e.getMessage());
        Map<String, Object> datafail = new HashMap<>();
        datafail.put("error", e.toString());
        datafail.put("message", "check input criteria and try again!");
        return new ResponseEntity<>(datafail, HttpStatus.OK); // Internal server error
        }
    }

    @PostMapping("/getOneTask")
    public ResponseEntity<Object> getOneTask(@RequestBody Map<String, String> requestBody) {
    try {
        String taskId = requestBody.get("task_id");
        Task task = taskService.getOneTask(taskId);

        if (task != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("err", null);

            Map<String, Object> taskDetails = new HashMap<>();
            taskDetails.put("task_name", task.getTask_name());
            taskDetails.put("task_description", task.getTask_description());
            taskDetails.put("task_notes", task.getTask_notes());
            taskDetails.put("task_plan", task.getTask_plan());
            taskDetails.put("task_app_Acronym", task.getTask_app_Acronym());
            taskDetails.put("task_state", task.getTask_state());
            taskDetails.put("task_creator", task.getTask_creator());
            taskDetails.put("task_owner", task.getTask_owner());
            taskDetails.put("task_create_date", task.getTask_create_date());
            taskDetails.put("task_id", task.getTask_id()); // Assuming you want to include taskId

            data.put("message", taskDetails);

            return new ResponseEntity<>(data, HttpStatus.OK); // Task retrieval successful
            } else {
                Map<String, Object> datafail = new HashMap<>();
                datafail.put("err", "Task not found");
                datafail.put("message", null);
                return new ResponseEntity<>(datafail, HttpStatus.OK); // Task retrieval failed
            }
        } catch (Exception e) {
            Map<String, Object> datafail = new HashMap<>();
            datafail.put("err", e.toString());
            datafail.put("message", "Check input criteria and try again!");
            return new ResponseEntity<>(datafail, HttpStatus.OK); // Internal server error
        }
    }


    @PostMapping("/updateTask")
    public ResponseEntity<Object> updateTask(@RequestBody ObjectNode updatedTask) {
        try {
        Task new_task = new Task();
        new_task.setTask_app_Acronym(updatedTask.get("task_app_Acronym").asText());
        new_task.setTask_create_date(updatedTask.get("task_create_date").asText());
        new_task.setTask_creator(updatedTask.get("task_creator").asText());
        new_task.setTask_description(updatedTask.get("task_description").asText());
        new_task.setTask_id(updatedTask.get("task_id").asText());
        new_task.setTask_name(updatedTask.get("task_name").asText());
        new_task.setTask_notes(updatedTask.get("task_notes").asText());
        new_task.setTask_owner(updatedTask.get("task_owner").asText());
        new_task.setTask_plan(updatedTask.get("task_plan").asText());
        new_task.setTask_state(updatedTask.get("task_state").asText());
        String new_note = updatedTask.get("newNotes").asText();

        Map<String, Object> data = new HashMap<>();
      
            taskService.updateTask(new_task,new_note);
            System.out.println(new_task.getTask_id());
            String message = "Task " + new_task.getTask_id() + " updated!";
            data.put("error", null);
            data.put("message", message);
            return new ResponseEntity<>(data, HttpStatus.OK); // User update successful
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Map<String, Object> datafail = new HashMap<>();
            datafail.put("error", e.toString());
            datafail.put("message", "Check input criteria and try again!");
            return new ResponseEntity<>(datafail, HttpStatus.OK); // User update failed
        }
    }

    @PostMapping("/promoteTask")
    public ResponseEntity<Object> promoteTask(@RequestBody ObjectNode promotedTask){
        try {
        Task new_task = new Task();
        new_task.setTask_app_Acronym(promotedTask.get("task_app_Acronym").asText());
        new_task.setTask_create_date(promotedTask.get("task_create_date").asText());
        new_task.setTask_creator(promotedTask.get("task_creator").asText());
        new_task.setTask_description(promotedTask.get("task_description").asText());
        new_task.setTask_id(promotedTask.get("task_id").asText());
        new_task.setTask_name(promotedTask.get("task_name").asText());
        new_task.setTask_notes(promotedTask.get("task_notes").asText());
        new_task.setTask_owner(promotedTask.get("task_owner").asText());
        new_task.setTask_plan(promotedTask.get("task_plan").asText());
        new_task.setTask_state(promotedTask.get("task_state").asText());
        String new_note = promotedTask.get("newNotes").asText();

        Map<String, Object> data = new HashMap<>();
      
            taskService.promoteTask(new_task,new_note);
        System.out.println(new_task.getTask_id());
            String message = "Task " + new_task.getTask_id() + " updated!";
            data.put("error", null);
            data.put("message", message);
            return new ResponseEntity<>(data, HttpStatus.OK); // User update successful
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Map<String, Object> datafail = new HashMap<>();
            datafail.put("error", e.toString());
            datafail.put("message", "Check input criteria and try again!");
            return new ResponseEntity<>(datafail, HttpStatus.OK); // User update failed
        }
    }
    
     @PostMapping("/demoteTask")
    public ResponseEntity<Object> demoteTask(@RequestBody ObjectNode updatedTask) {

        ArrayList<String> statesArray = new ArrayList<String>();
        statesArray.add("Open");
        statesArray.add("To-Do");
        statesArray.add("Doing");
        statesArray.add("Done");
        statesArray.add("Closed");

        String currentState = updatedTask.get("task_state").asText();
        int currentStateIndex = statesArray.indexOf(currentState);
        String newState = statesArray.get(currentStateIndex-1);

        try {
        Task new_task = new Task();
        new_task.setTask_app_Acronym(updatedTask.get("task_app_Acronym").asText());
        new_task.setTask_create_date(updatedTask.get("task_create_date").asText());
        new_task.setTask_creator(updatedTask.get("task_creator").asText());
        new_task.setTask_description(updatedTask.get("task_description").asText());
        new_task.setTask_id(updatedTask.get("task_id").asText());
        new_task.setTask_name(updatedTask.get("task_name").asText());
        new_task.setTask_notes(updatedTask.get("task_notes").asText());
        new_task.setTask_owner(updatedTask.get("task_owner").asText());
        new_task.setTask_plan(updatedTask.get("task_plan").asText());
        new_task.setTask_state(newState);
        String new_note = updatedTask.get("newNotes").asText();

        Map<String, Object> data = new HashMap<>();
      
            taskService.demoteTask(new_task,new_note);

            System.out.println(new_task.getTask_id());
            String message = "Task " + new_task.getTask_id() + " updated!";
            data.put("error", null);
            data.put("message", message);
            return new ResponseEntity<>(data, HttpStatus.OK); // User update successful
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Map<String, Object> datafail = new HashMap<>();
            datafail.put("error", e.toString());
            datafail.put("message", "Check input criteria and try again!");
            return new ResponseEntity<>(datafail, HttpStatus.OK); // User update failed
        }
    }

    @PostMapping("/createTask") 
    public ResponseEntity<Object> createTask(@RequestBody Task newTask) { 
        try {
            System.out.println(newTask.toString());
            Map<String, Object> data = new HashMap<>();
            String message = "Task " + taskService.createTask(newTask) + " created!";
            data.put("error", null);
            data.put("message", message);
            return new ResponseEntity<>(data, HttpStatus.OK); //Task creation successful
        } catch (Exception e) { 
            Map<String, Object> datafail = new HashMap<>();
            datafail.put("error", e.toString());
            datafail.put("message", "check input criteria and try again!");
            return new ResponseEntity<>(datafail, HttpStatus.OK); //Task creation failed 
        } 
    } 
    
}