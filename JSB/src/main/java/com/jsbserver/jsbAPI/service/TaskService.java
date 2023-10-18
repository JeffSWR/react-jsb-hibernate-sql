package com.jsbserver.jsbAPI.service;
import com.jsbserver.jsbAPI.entity.Task;
import com.jsbserver.jsbAPI.entity.Application;
import com.jsbserver.jsbAPI.repository.TaskDAO;
import com.jsbserver.jsbAPI.repository.AccountDAO;
import com.jsbserver.jsbAPI.repository.ApplicationDAO;
import java.lang.reflect.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; 
import org.springframework.web.bind.annotation.RequestBody; 

@Service
public class TaskService {

    @Autowired
    TaskDAO taskDAO;
    @Autowired
    ApplicationDAO appDAO;
    @Autowired
    AccountDAO accDAO;
    @Autowired
    EmailServiceImpl emailServiceImpl;

    public List<Task> getAllTasks(String app) {
        return taskDAO.getAllTasks(app);
    }

    public Task getOneTask(String taskId) {
        return taskDAO.getOneTask(taskId);
    }

    public String updateTask(Task updatedTask, String new_note) { 

        updatedTask.setTask_notes(generateUpdatedTaskNotes(updatedTask,new_note));
        return taskDAO.updateTask(updatedTask);
    }
    
    public String promoteTask(Task promotedTask, String new_note){
        promotedTask.setTask_notes(generatePromotedTaskNotes(promotedTask, new_note));
        if(promotedTask.getTask_state().equals("Doing")){
            Application app =  appDAO.getApplication(promotedTask.getTask_app_Acronym());
            String app_Permit_Done = app.getApp_permit_Done();
            List<String> emails = accDAO.getDonePermitUsers(app_Permit_Done);
            emailServiceImpl.sendEmail(promotedTask.getTask_id() ,emails);
        }
        return taskDAO.promoteTask(promotedTask);
    }

    public String demoteTask(Task updatedTask, String new_note) { 

        updatedTask.setTask_notes(generateDemotedTaskNotes(updatedTask,new_note));
        return taskDAO.updateTask(updatedTask);
    } 

    public String getCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    public String generateUpdatedTaskNotes(Task taskObject, String new_note) {
        String date = getCurrentDateTime();
    
        // Store the old notes in a temporary variable
        String oldNotes = taskObject.getTask_notes();

        // Construct the updated notes
            String updatedNotes =
            "Updated by " + taskObject.getTask_owner() + " on " + date + "\n" +
            "Current task state: " + taskObject.getTask_state() + "\n" +
            "Notes added:\n" + new_note + "\n" +
            "------------------------------------------------\n" +
            oldNotes;

        return updatedNotes;
    }

     public String generatePromotedTaskNotes(Task taskObject, String new_note) {
        String date = getCurrentDateTime();
    
        // Store the old notes in a temporary variable
        String oldNotes = taskObject.getTask_notes();

        // Construct the updated notes
            String updatedNotes =
            "Promoted by " + taskObject.getTask_owner() + " on " + date + "\n" +
            "Current task state: " + taskObject.getTask_state() + "\n" +
            "Notes added:\n" + new_note + "\n" +
            "------------------------------------------------\n" +
            oldNotes;

        return updatedNotes;
    }

     public String generateDemotedTaskNotes(Task taskObject, String new_note) {
        String date = getCurrentDateTime();
    
        // Store the old notes in a temporary variable
        String oldNotes = taskObject.getTask_notes();

//         // Get the new notes from the taskObject
//         // String newNotes = taskObject.getNewNote();
        
//         // If newNotes is empty, set it to a default value
//         // if (newNotes == null || newNotes.isEmpty()) {
//         //     newNotes = "---";
//         // }

        // Construct the updated notes
            String updatedNotes =
            "Demoted by " + taskObject.getTask_owner() + " on " + date + "\n" +
            "Current task state: " + taskObject.getTask_state() + "\n" +
            "Notes added:\n" + new_note + "\n" +
            "------------------------------------------------\n" +
            oldNotes;

        return updatedNotes;
    }
    public String createTask(Task newTask) { 
        String date = getCurrentDateTime();
        String note = "Created by "+ newTask.getTask_owner() + " on " + date + "\n";
        return taskDAO.createTask(newTask, note, date);
    }  
}