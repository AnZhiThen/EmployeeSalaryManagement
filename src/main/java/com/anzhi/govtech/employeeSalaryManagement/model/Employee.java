package com.anzhi.govtech.employeeSalaryManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.With;
import org.hibernate.annotations.Generated;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@With
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EMPLOYEE")
public class Employee {

    @Id
    @NonNull
    @GeneratedValue(strategy= GenerationType.AUTO)
    @JsonIgnore
    private Long eid;

    @NonNull
    private String id;

    @NonNull
    private String login;

    @NonNull
    private String name;

    @Min(value = 0)
    private double salary;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;
}
