package com.jsbserver.jsbAPI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plan") // Specify the table name
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    @Id
    @Column(name = "plan_MVP_Name")
    private String plan_MVP_Name; // Primary Key

    @Column(name = "plan_start_date", nullable = true)
    private String plan_start_date;

    @Column(name = "plan_end_date", nullable = true)
    private String plan_end_date;

    @Id
    @Column(name = "plan_app_Acronym")
    private String plan_app_Acronym;

    // Add any other fields and methods as needed
}