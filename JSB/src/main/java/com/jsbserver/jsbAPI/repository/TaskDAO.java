package com.jsbserver.jsbAPI.repository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import com.jsbserver.jsbAPI.entity.Application;
import com.jsbserver.jsbAPI.entity.Account;
import com.jsbserver.jsbAPI.entity.Task; // Import Task entity
import com.jsbserver.jsbAPI.util.HibernateUtil;

import jakarta.persistence.EntityNotFoundException;

@Repository
public class TaskDAO {


    public List<Task> getAllTasks(String app) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Task> taskList = null;

            String sql_statement = "SELECT * from task WHERE Task_app_Acronym = :app";
    
            NativeQuery<Task> query = session.createNativeQuery(sql_statement, Task.class);
            query.setParameter("app", app); // Set the parameter
            taskList = query.list();
    
            if (!taskList.isEmpty()) {
                return taskList;
            } else {
                throw new EntityNotFoundException("No tasks found");
            }
    
        } catch (Exception e) {
            System.out.println(e.getMessage());

            System.out.println("Error occurred, failed to read:\n\t" + e);
        }
    
        return null;
      }

      public Task getOneTask (String taskId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql_statement = "SELECT * from task WHERE Task_id = :taskId";
    
            NativeQuery<Task> query = session.createNativeQuery(sql_statement, Task.class);
            query.setParameter("taskId", taskId); // Set the parameter
            List<Task> taskList = query.list();
    
            if (!taskList.isEmpty()) {
                return taskList.get(0);
            } else {
              return null;
            }
    
        } catch (Exception e) {
            System.out.println("Error occurred, failed to read:\n\t" + e);
        }
    
        return null;
      }


      public String updateTask(Task updatedTask) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Start a transaction
            transaction = session.beginTransaction();
    
            String sqlStatement = "UPDATE task SET task_name = :taskName, task_description = :taskDescription, task_notes = :taskNotes, task_plan = :taskPlan, task_app_Acronym = :taskAppAcronym, task_state = :taskState, task_creator = :taskCreator, task_owner = :taskOwner, task_create_date = :taskCreateDate WHERE task_id = :taskId";
    
            NativeQuery<?> query = session.createNativeQuery(sqlStatement, Task.class)
            .setParameter("taskName", updatedTask.getTask_name())
            .setParameter("taskDescription", updatedTask.getTask_description())
            .setParameter("taskNotes", updatedTask.getTask_notes())
            .setParameter("taskPlan", updatedTask.getTask_plan())
            .setParameter("taskAppAcronym", updatedTask.getTask_app_Acronym())
            .setParameter("taskState", updatedTask.getTask_state())
            .setParameter("taskCreator", updatedTask.getTask_creator())
            .setParameter("taskOwner", updatedTask.getTask_owner())
            .setParameter("taskCreateDate", updatedTask.getTask_create_date())
            .setParameter("taskId", updatedTask.getTask_id());
    
            // Executing the query
            int rowCount = query.executeUpdate();
    
            // Commit transaction
            transaction.commit();
    
            if (rowCount > 0) {
                return updatedTask.getTask_id(); // Return the Task ID if the update was successful
            } else {
                return null; // Return null if no rows were updated (Task not found)
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
    
    public String promoteTask(Task promotedTask) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Start a transaction
            transaction = session.beginTransaction();

            // Init an array for task states
            List<String> setArray = new ArrayList<String>();
            setArray.add("Open");
            setArray.add("To-Do");
            setArray.add("Doing");
            setArray.add("Done");
            setArray.add("Closed");

            promotedTask.setTask_state(setArray.get(setArray.indexOf(promotedTask.getTask_state()) + 1));
            System.out.println(promotedTask.getTask_state());
    
            String sqlStatement = "UPDATE task SET task_name = :taskName, task_description = :taskDescription, task_notes = :taskNotes, task_plan = :taskPlan, task_app_Acronym = :taskAppAcronym, task_state = :taskState, task_creator = :taskCreator, task_owner = :taskOwner, task_create_date = :taskCreateDate WHERE task_id = :taskId";
    
            NativeQuery<?> query = session.createNativeQuery(sqlStatement, Task.class)
            .setParameter("taskName", promotedTask.getTask_name())
            .setParameter("taskDescription", promotedTask.getTask_description())
            .setParameter("taskNotes", promotedTask.getTask_notes())
            .setParameter("taskPlan", promotedTask.getTask_plan())
            .setParameter("taskAppAcronym", promotedTask.getTask_app_Acronym())
            .setParameter("taskState", promotedTask.getTask_state())
            .setParameter("taskCreator", promotedTask.getTask_creator())
            .setParameter("taskOwner", promotedTask.getTask_owner())
            .setParameter("taskCreateDate", promotedTask.getTask_create_date())
            .setParameter("taskId", promotedTask.getTask_id());
    
            // Executing the query
            int rowCount = query.executeUpdate();
    
            // Commit transaction
            transaction.commit();
    
            if (rowCount > 0) {
                return promotedTask.getTask_id(); // Return the Task ID if the update was successful
            } else {
                return null; // Return null if no rows were promoted (Task not found)
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }


    // LOGIC TO BE REFACTORED INTO SERVICE LAYER    
    public String createTask(Task newTask, String note, String date)
        {
            String state = "Open";
            Transaction transaction = null;
            try(Session session = HibernateUtil.getSessionFactory().openSession()){
            //start a transaction
            transaction = session.beginTransaction();

            // Retrieve the current app_r_number from the application table
            String getAppRNumberSQL = "SELECT app_r_number FROM application WHERE app_acronym = :appAcronym";
            NativeQuery<Integer> appRNumberQuery = session.createNativeQuery(getAppRNumberSQL, Integer.class);
            appRNumberQuery.setParameter("appAcronym", newTask.getTask_app_Acronym()); // Assuming app_id is related to app acronym

            // Execute the query to get the current app_r_number
            Integer currentAppRNumber = appRNumberQuery.uniqueResult();

            // Increment the app_r_number by 1
            int newAppRNumber = currentAppRNumber + 1;

            // Combine the incremented app_r_number with the task name to create a new task name
            String newTaskName = newTask.getTask_name() + "_" + newAppRNumber;

            // Update the app_r_number in the application table
            String updateAppRNumberSQL = "UPDATE application SET app_r_number = :newAppRNumber WHERE app_acronym = :appAcronym";
            NativeQuery<Integer> updateAppRNumberQuery = session.createNativeQuery(updateAppRNumberSQL,Integer.class);
            updateAppRNumberQuery.setParameter("newAppRNumber", newAppRNumber);
            updateAppRNumberQuery.setParameter("appAcronym", newTask.getTask_app_Acronym()); // Assuming app_id is related to app acronym

            // Execute the query to update the app_r_number
            updateAppRNumberQuery.executeUpdate();
            String sqlStatement = "INSERT INTO task (task_name, task_description, task_notes, task_plan, task_app_Acronym, task_state, task_creator, task_owner, task_create_date, task_id) " +
            "VALUES (:taskName, :taskDescription, :taskNotes, :taskPlan, :taskAppAcronym, :taskState, :taskCreator, :taskOwner, :taskCreateDate, :taskId)";            // String sql_statement = "INSERT INTO task VALUE(:taskName, :password, :email, :active, :groups)";

            NativeQuery<Task> query = session.createNativeQuery(sqlStatement, Task.class)
            .setParameter("taskName", newTask.getTask_name())
            .setParameter("taskDescription", newTask.getTask_description())
            .setParameter("taskPlan", newTask.getTask_plan())
            .setParameter("taskAppAcronym", newTask.getTask_app_Acronym())
            .setParameter("taskCreator", newTask.getTask_creator())
            .setParameter("taskOwner", newTask.getTask_owner())
            .setParameter("taskId", newTaskName)
            .setParameter("taskCreateDate", date)
            .setParameter("taskNotes", note)
            .setParameter("taskState", state);

            //Executing the query
            query.executeUpdate();

            //commit transaction
            transaction.commit();

            return newTaskName;
        }catch(Exception e){
        System.out.println(e.getMessage());
        if (transaction != null){
            transaction.rollback();
        }
        e.printStackTrace();
        return null;
        }
    }

}