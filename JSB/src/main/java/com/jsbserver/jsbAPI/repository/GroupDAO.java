package com.jsbserver.jsbAPI.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;


import com.jsbserver.jsbAPI.entity.Group;
import com.jsbserver.jsbAPI.util.HibernateUtil;

import jakarta.persistence.EntityNotFoundException;

@Repository
public class GroupDAO {


  public List<Group> getAllGroup(){
    List<Group> groupList = null;
    try(Session session = HibernateUtil.getSessionFactory().openSession()){

      String sql_statement = "SELECT * FROM `groups`";

      NativeQuery<Group> query = session.createNativeQuery(sql_statement, Group.class);
      groupList = query.list();

      if(groupList.isEmpty()){
        throw new EntityNotFoundException("No group found");
      }

      return groupList;
    }
    catch(Exception e){
      System.out.println("Error occurred, failed to read:\n\t" + e); 
 
      return null; 
    }
  }
  
  public String createGroup(Group newGrp)
  {
    Transaction transaction = null;
    try(Session session = HibernateUtil.getSessionFactory().openSession()){
      //start a transaction
      transaction = session.beginTransaction();

      String sql_statement = "INSERT INTO  `groups` VALUE(:group_name)";

      NativeQuery<Group> query = session.createNativeQuery(sql_statement, Group.class)
      .setParameter("group_name", newGrp.getGroup_name());


      //Executing the query
      query.executeUpdate();

      //commit transaction
      transaction.commit();

      return newGrp.getGroup_name();
    }catch(Exception e){
      if (transaction != null){
        transaction.rollback();
      }
      e.printStackTrace();
      return null;
    }
  }

}
