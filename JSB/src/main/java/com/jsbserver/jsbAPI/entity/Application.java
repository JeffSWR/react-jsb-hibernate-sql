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
@Table(name = "application") // Specify the table name
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    @Id
    @Column(name = "app_acronym")
    private String app_Acronym; // Primary Key

    @Column(columnDefinition = "longtext", nullable = true, name = "app_description")
    private String app_Description;

    @Column(nullable = false, name = "app_r_number")
    private Long app_Rnumber;

    @Column(nullable = true, name = "app_start_date")
    private String app_startDate;

    @Column(nullable = true, name = "app_end_date")
    private String app_endDate;

    @Column(nullable = true, name = "app_permit_open")
    private String app_permit_Open;

    @Column(nullable = true, name = "app_permit_to_do_list")
    private String app_permit_toDoList;

    @Column(nullable = true, name = "app_permit_doing")
    private String app_permit_Doing;

    @Column(nullable = true, name = "app_permit_done")
    private String app_permit_Done;

    @Column(nullable = true, name = "app_permit_create")
    private String app_permit_create;

    // Add any other fields and methods as needed
}