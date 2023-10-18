package com.jsbserver.jsbAPI.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import com.jsbserver.jsbAPI.entity.Plan;
import com.jsbserver.jsbAPI.util.HibernateUtil;

@Repository
public class PlanDAO {
  public void createPlan(Plan plan){
    Transaction transaction = null;
    try(Session session = HibernateUtil.getSessionFactory().openSession()){
      //start a transaction
      transaction = session.beginTransaction();

      String sql_statement = "INSERT INTO plan(plan_MVP_Name, plan_start_date, plan_end_date, plan_app_Acronym) VALUE(:Plan_MVP_Name, :plan_start_date, :plan_end_date, :Plan_App_Acronym)";

      NativeQuery<Plan> query = session.createNativeQuery(sql_statement, Plan.class)
      .setParameter("Plan_MVP_Name", plan.getPlan_MVP_Name())
      .setParameter("plan_start_date", plan.getPlan_start_date())
      .setParameter("plan_end_date", plan.getPlan_end_date())
      .setParameter("Plan_App_Acronym", plan.getPlan_app_Acronym());

      //Executing the query
      query.executeUpdate();

      //commit transaction
      transaction.commit();
    }catch(Exception e){
      if (transaction != null){
        transaction.rollback();
      }
      e.printStackTrace();
      throw e;
    }
  }

  public void updatePlan(Plan plan){
    Transaction transaction = null;
    try(Session session = HibernateUtil.getSessionFactory().openSession()){
      //Start a transaction
      transaction = session.beginTransaction();

      String sql_statement = "UPDATE plan SET plan_start_date = :plan_start_date, plan_end_date = :plan_end_date WHERE plan_MVP_name = :plan_MVP_name AND plan_app_Acronym = :plan_app_Acronym";

      NativeQuery<Plan> query = session.createNativeQuery(sql_statement, Plan.class)
      .setParameter("plan_start_date", plan.getPlan_start_date())
      .setParameter("plan_end_date", plan.getPlan_end_date())
      .setParameter("plan_MVP_name", plan.getPlan_MVP_Name())
      .setParameter("plan_app_Acronym", plan.getPlan_app_Acronym());

      //Executing the query
      query.executeUpdate();

      //commit transaction
      transaction.commit();
    }catch(Exception e){
      if (transaction != null){
        transaction.rollback();
      }
      e.printStackTrace();
      throw e;
    }
  }

  public List<Plan> getAllPlans(String app){
    try(Session session = HibernateUtil.getSessionFactory().openSession()){
      String sql_statement = "SELECT * FROM plan WHERE plan_app_Acronym = :plan_app_Acronym";

      NativeQuery<Plan> query = session.createNativeQuery(sql_statement, Plan.class)
      .setParameter("plan_app_Acronym", app);

      //Executing the query
      List<Plan> planList = query.list();
      
      return planList;
    }catch(Exception e){
      e.printStackTrace();
      throw e;
    }
  }

  public Plan getOnePlan(Plan plan){
    try(Session session = HibernateUtil.getSessionFactory().openSession()){
      String sql_statement = "SELECT * FROM plan WHERE plan_MVP_name = :plan_MVP_name AND plan_app_Acronym = :plan_app_Acronym";

      NativeQuery<Plan> query = session.createNativeQuery(sql_statement, Plan.class)
      .setParameter("plan_MVP_name", plan.getPlan_MVP_Name())
      .setParameter("plan_app_Acronym", plan.getPlan_app_Acronym());

      //Executing the query
      List<Plan> planList = query.list();
      
      return planList.get(0);
    }catch(Exception e){
      e.printStackTrace();
      throw e;
    }
  }
}
