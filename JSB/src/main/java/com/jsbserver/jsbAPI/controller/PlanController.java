package com.jsbserver.jsbAPI.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jsbserver.jsbAPI.entity.Plan;
import com.jsbserver.jsbAPI.service.PlanService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class PlanController {
  @Autowired
  PlanService planService;

  @PostMapping("/createPlan")
  public ResponseEntity<Map<String, Object>> createPlan(@RequestBody Plan plan){
    Map<String, Object> data = new HashMap<>();
    try{
      planService.createPlan(plan);
      data.put("error", null);
      data.put("response", null);
      data.put("message", "Plan successfully created");
      return new ResponseEntity<>(data, HttpStatus.OK);
    }catch(Exception e){
      data.put("error", e.getMessage());
      data.put("response", "There was a error");
      data.put("message", "Error");
      return new ResponseEntity<>(data, HttpStatus.OK);
    }
  }

  @PostMapping("/updatePlan")
  public ResponseEntity<Map<String, Object>> updatePlan(@RequestBody Plan plan){
    Map<String, Object> data = new HashMap<>();
    try{
      planService.updatePlan(plan);
      data.put("error", null);
      data.put("response", null);
      data.put("message", "Plan successfully edited");
      return new ResponseEntity<>(data, HttpStatus.OK);
    }catch(Exception e){
      data.put("error", e.getMessage());
      data.put("response", "There was a error");
      data.put("message", "Error");
      return new ResponseEntity<>(data, HttpStatus.OK);
    }
  }

  @PostMapping("/getAllPlans")
  public ResponseEntity<Map<String, Object>> getAllPlans(@RequestBody ObjectNode app){
    String appName = app.get("app").asText();
    Map<String, Object> data = new HashMap<>();
    try{
      List<Plan> planList = planService.getAllPlans(appName);
      data.put("error", null);
      data.put("plans", planList);
      data.put("message", "Successfully fetched all Plans");
      return new ResponseEntity<>(data, HttpStatus.OK);
    }
    catch(Exception e){
      data.put("error", e.getMessage());
      data.put("response", "There was a error");
      data.put("message", "Error");
      return new ResponseEntity<>(data, HttpStatus.OK);
    }
  }

  @PostMapping("/getOnePlan")
  public ResponseEntity<Map<String, Object>> getOnePlan(@RequestBody Plan plan){
    Map<String, Object> data = new HashMap<>();
    try{
      Plan thePlan = planService.getOnePlan(plan);
      data.put("error", null);
      data.put("plan", thePlan);
      data.put("message", "Successfully fetched Plan " + plan.getPlan_MVP_Name());
      return new ResponseEntity<>(data, HttpStatus.OK);
    }
    catch(Exception e){
      data.put("error", e.getMessage());
      data.put("response", "There was a error");
      data.put("message", "Error");
      return new ResponseEntity<>(data, HttpStatus.OK);
    }
  }
}
