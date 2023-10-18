package com.jsbserver.jsbAPI.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import com.jsbserver.jsbAPI.entity.Account;
import com.jsbserver.jsbAPI.util.HibernateUtil;

import jakarta.persistence.EntityNotFoundException;

@Repository
public class AccountDAO {
  
  public List<Account> getAllAccount(){
    List<Account> accountList = null;
    try(Session session = HibernateUtil.getSessionFactory().openSession()){

      String sql_statement = "SELECT * FROM accounts";

      //get a account list
      NativeQuery<Account> query = session.createNativeQuery(sql_statement, Account.class);
      accountList = query.list();

      if(accountList.isEmpty()){
        throw new EntityNotFoundException("No accounts found");
      }

      return accountList;
    }
    catch(Exception e){
      System.out.println("Error occurred, failed to read:\n\t" + e); 
 
      return null; 
    }
  }

  public List<String> getDonePermitUsers(String group){
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        String sql_statement = "SELECT email FROM accounts WHERE `groups` LIKE :group";

        NativeQuery<String> query = session.createNativeQuery(sql_statement, String.class);
        query.setParameter("group", "%," + group + ",%"); // Set the parameter
        System.out.println(query.list());
        List<String> emailList = query.list();

        if (!emailList.isEmpty()) {
            return emailList;
        } else {
          return null;
        }

    } catch (Exception e) {
        System.out.println("Error occurred, failed to read:\n\t" + e);
    }

    return null;
  }

  public Account getOneUser(String username) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        String sql_statement = "SELECT * from accounts WHERE username = :username";

        NativeQuery<Account> query = session.createNativeQuery(sql_statement, Account.class);
        query.setParameter("username", username); // Set the parameter
        List<Account> accountList = query.list();

        if (!accountList.isEmpty()) {
            return accountList.get(0);
        } else {
          return null;
        }

    } catch (Exception e) {
        System.out.println("Error occurred, failed to read:\n\t" + e);
    }

    return null;
  }

  //Check for

  public String createUser(Account newUser)
  {
    Transaction transaction = null;
    try(Session session = HibernateUtil.getSessionFactory().openSession()){
      //start a transaction
      transaction = session.beginTransaction();

      String sql_statement = "INSERT INTO accounts VALUE(:username, :password, :email, :active, :groups)";

      NativeQuery<Account> query = session.createNativeQuery(sql_statement, Account.class)
      .setParameter("username", newUser.getUsername())
      .setParameter("password", newUser.getPassword())
      .setParameter("email", newUser.getEmail())
      .setParameter("active", newUser.isActive())
      .setParameter("groups", newUser.getGroups());

      //Executing the query
      query.executeUpdate();

      //commit transaction
      transaction.commit();

      return newUser.getUsername();
    }catch(Exception e){
      if (transaction != null){
        transaction.rollback();
      }
      e.printStackTrace();
      return null;
    }
  }

  public String updateUser(Account updateUser) {
    Transaction transaction = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        // Start a transaction
        transaction = session.beginTransaction();

        String sqlStatement = "UPDATE accounts SET password = :password, email = :email, active = :active, `groups` = :groups WHERE username = :username";

        NativeQuery<?> query = session.createNativeQuery(sqlStatement,Account.class)
                .setParameter("password", updateUser.getPassword())
                .setParameter("email", updateUser.getEmail())
                .setParameter("active", updateUser.isActive())
                .setParameter("groups", updateUser.getGroups())
                .setParameter("username", updateUser.getUsername());

        // Executing the query
        int rowCount = query.executeUpdate();

        // Commit transaction
        transaction.commit();

        if (rowCount > 0) {
            return updateUser.getUsername(); // Return the username if the update was successful
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
  
  public String updateUserWithoutPW(Account updateUser) {
    Transaction transaction = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        // Start a transaction
        transaction = session.beginTransaction();

        String sqlStatement = "UPDATE accounts SET email = :email, active = :active, `groups` = :groups WHERE username = :username";

        NativeQuery<?> query = session.createNativeQuery(sqlStatement,Account.class)
                .setParameter("email", updateUser.getEmail())
                .setParameter("active", updateUser.isActive())
                .setParameter("groups", updateUser.getGroups())
                .setParameter("username", updateUser.getUsername());

        // Executing the query
        int rowCount = query.executeUpdate();

        // Commit transaction
        transaction.commit();

        if (rowCount > 0) {
            return updateUser.getUsername(); // Return the username if the update was successful
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

  public Boolean checkToken(String username){
        Boolean token = false;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        String sql_statement = "SELECT * from accounts WHERE username = :username and active = 1";

        NativeQuery<Account> query = session.createNativeQuery(sql_statement, Account.class);
        query.setParameter("username", username); // Set the parameter
        List<Account> accountList = query.list();

        if (!accountList.isEmpty()) {
            token = true;
        } else {
          token = false;
        }
      
    } catch (Exception e) {
        System.out.println("Error occurred, failed to read:\n\t" + e);
    }
    return token;
  }
}
