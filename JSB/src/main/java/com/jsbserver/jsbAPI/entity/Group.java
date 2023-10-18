package com.jsbserver.jsbAPI.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "`groups`") // Use backticks to specify the table name
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group {
      @Id
      private String group_name;

}
