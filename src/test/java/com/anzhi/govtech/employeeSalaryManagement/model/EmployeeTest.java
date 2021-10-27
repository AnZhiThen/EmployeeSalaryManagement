package com.anzhi.govtech.employeeSalaryManagement.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeTest {

    private String someId = "E100";
    private String someLogin = "GT-HPotter";
    private String someName = "Harry Potter";
    private double someSalary = 1000.0;
    private LocalDate someStartDate = LocalDate.parse("2020-01-08");
    private Long someEid = 1234L;
    private Employee expectedEmployee = Employee.builder()
            .id(someId)
            .login(someLogin)
            .name(someName)
            .salary(someSalary)
            .startDate(someStartDate)
            .build();

    @Test
    public void testValidEmployee() {
        Employee e = Employee.builder()
                .id(someId)
                .login(someLogin)
                .name(someName)
                .salary(someSalary)
                .startDate(someStartDate)
                .build();
        assertEquals(e, expectedEmployee);
    }
}
