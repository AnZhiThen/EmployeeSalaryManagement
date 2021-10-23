package com.anzhi.govtech.employeeSalaryManagement.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;

import java.time.LocalDate;
import java.util.Date;

@Data
@Getter
@Setter
@Builder
@With
public class Employee {

    @Id
    @NonNull
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
