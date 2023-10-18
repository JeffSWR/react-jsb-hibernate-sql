package com.jsbserver.jsbAPI.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.jsbserver.jsbAPI.entity.Plan;
import com.jsbserver.jsbAPI.repository.PlanDAO;

@Service
public class PlanService {
  @Autowired
  PlanDAO planDAO;

  public void createPlan(@RequestBody Plan plan){
    planDAO.createPlan(plan);
  }

  public void updatePlan(@RequestBody Plan plan){
    planDAO.updatePlan(plan);
  }

  public List<Plan> getAllPlans(@RequestBody String app){
    return planDAO.getAllPlans(app);
  }

  public Plan getOnePlan (@RequestBody Plan plan){
    return planDAO.getOnePlan(plan);
  }
}
