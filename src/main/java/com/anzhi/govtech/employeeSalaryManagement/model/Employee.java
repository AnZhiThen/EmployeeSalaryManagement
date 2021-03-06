package com.anzhi.govtech.employeeSalaryManagement.model;

import com.anzhi.govtech.employeeSalaryManagement.CustomDateValidator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.With;

import javax.persistence.*;
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
    @NonNull
    @Id
    private String id;

    @NonNull
    @Column(unique = true)
    private String login;

    @NonNull
    private String name;

    @Min(value = 0)
    private double salary;

    @JsonDeserialize(using = CustomDateValidator.class)
    private LocalDate startDate;
}
