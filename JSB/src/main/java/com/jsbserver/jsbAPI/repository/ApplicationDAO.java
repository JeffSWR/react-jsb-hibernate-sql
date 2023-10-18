package com.jsbserver.jsbAPI.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import com.jsbserver.jsbAPI.entity.Application;
import com.jsbserver.jsbAPI.util.HibernateUtil;

import jakarta.persistence.EntityNotFoundException;

@Repository
public class ApplicationDAO {
  
  public List<Application> getAllApplications(){
    List<Application> applicationsList = null;
    try(Session session = HibernateUtil.getSessionFactory().openSession()){

      String sql_statement = "SELECT * FROM application";

      //get a account list
      NativeQuery<Application> query = session.createNativeQuery(sql_statement, Application.class);
      applicationsList = query.list();
      System.out.print(applicationsList);

      if(applicationsList.isEmpty()){
        throw new EntityNotFoundException("No applications found");
      }

      return applicationsList;
    }
    catch(Exception e){
      System.out.println("Error occurred, failed to read:\n\t" + e); 
 
      return null; 
    }
  }

  public Application getApplication(String appAcronym) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        String sql_statement = "SELECT * from application WHERE app_Acronym = :appAcronym";

        NativeQuery<Application> query = session.createNativeQuery(sql_statement, Application.class);
        query.setParameter("appAcronym", appAcronym); // Set the parameter
        List<Application> applicationsList = query.list();

        if (!applicationsList.isEmpty()) {
            return applicationsList.get(0);
        } else {
            return null;
        }

    } catch (Exception e) {
        System.out.println("Error occurred, failed to read:\n\t" + e);
    }

    return null;
  }

  //Check for

  public String createApplication(Application newApplication)
  {
    Transaction transaction = null;
    try(Session session = HibernateUtil.getSessionFactory().openSession()){
      //start a transaction
      transaction = session.beginTransaction();

      String sql_statement = "INSERT INTO application VALUE(:app_Acronym, :app_Description, :app_Rnumber, :app_startDate, :app_endDate, :app_permit_Open, :app_permit_toDoList, :app_permit_Doing, :app_permit_Done, :app_permit_create)";

      NativeQuery<Application> query = session.createNativeQuery(sql_statement, Application.class)
      .setParameter("app_Acronym", newApplication.getApp_Acronym())
      .setParameter("app_Rnumber", newApplication.getApp_Rnumber())
      .setParameter("app_Description", newApplication.getApp_Description())
      .setParameter("app_startDate", newApplication.getApp_startDate())
      .setParameter("app_endDate", newApplication.getApp_endDate())
      .setParameter("app_permit_Open", newApplication.getApp_permit_Open())
      .setParameter("app_permit_toDoList", newApplication.getApp_permit_toDoList())
      .setParameter("app_permit_Doing", newApplication.getApp_permit_Doing())
      .setParameter("app_permit_Done", newApplication.getApp_permit_Done())
      .setParameter("app_permit_create", newApplication.getApp_permit_create());

      //Executing the query
      query.executeUpdate();

      //commit transaction
      transaction.commit();

      return newApplication.getApp_Acronym();
    }catch(Exception e){
      if (transaction != null){
        transaction.rollback();
      }
      e.printStackTrace();
      return null;
    }
  }

  public String updateApplication(Application updatedApplication) {
    Transaction transaction = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        // Start a transaction
        transaction = session.beginTransaction();

        String sqlStatement = "UPDATE application SET app_acronym = :app_Acronym, app_r_number = :app_Rnumber, app_description = :app_Description, app_start_date = :app_startDate, app_end_date = :app_endDate, app_permit_open = :app_permit_Open, app_permit_to_do_list = :app_permit_toDoList, app_permit_doing = :app_permit_Doing, app_permit_done = :app_permit_Done, app_permit_create = :app_permit_create WHERE app_acronym = :app_Acronym";

        NativeQuery<?> query = session.createNativeQuery(sqlStatement,Application.class)
        .setParameter("app_Acronym", updatedApplication.getApp_Acronym())
        .setParameter("app_Rnumber", updatedApplication.getApp_Rnumber())
        .setParameter("app_Description", updatedApplication.getApp_Description())
        .setParameter("app_startDate", updatedApplication.getApp_startDate())
        .setParameter("app_endDate", updatedApplication.getApp_endDate())
        .setParameter("app_permit_Open", updatedApplication.getApp_permit_Open())
        .setParameter("app_permit_toDoList", updatedApplication.getApp_permit_toDoList())
        .setParameter("app_permit_Doing", updatedApplication.getApp_permit_Doing())
        .setParameter("app_permit_Done", updatedApplication.getApp_permit_Done())
        .setParameter("app_permit_create", updatedApplication.getApp_permit_create());

        // Executing the query
        int rowCount = query.executeUpdate();

        // Commit transaction
        transaction.commit();

        if (rowCount > 0) {
            return updatedApplication.getApp_Acronym(); // Return the username if the update was successful
        } else {
            return null; // Return null if no rows were updated (user not found)
        }
    } catch (Exception e) {
        if (transaction != null) {
            transaction.rollback();
        }
        e.printStackTrace();
        return null;
    }
  }
      
}
