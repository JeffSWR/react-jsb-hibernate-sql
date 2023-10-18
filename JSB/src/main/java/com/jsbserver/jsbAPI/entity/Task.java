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
@Table(name = "task")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @Column(name = "task_id", nullable = false)
    private String task_id; // Primary Key

    @Column(name = "task_name", nullable = false, length = 255)
    private String task_name;

    @Column(name = "task_description", columnDefinition = "longtext")
    private String task_description;

    @Column(name = "task_notes", columnDefinition = "longtext")
    private String task_notes;

    @Column(name = "task_plan", length = 255)
    private String task_plan;

    @Column(name = "task_app_Acronym", length = 255, nullable = false)
    private String task_app_Acronym;

    @Column(name = "task_state", length = 255)
    private String task_state;

    @Column(name = "task_creator", length = 255, nullable = false)
    private String task_creator;

    @Column(name = "task_owner", length = 255, nullable = false)
    private String task_owner;

    @Column(name = "task_create_date", length = 255, nullable = false)
    private String task_create_date;

    // Add any other fields and methods as needed
}