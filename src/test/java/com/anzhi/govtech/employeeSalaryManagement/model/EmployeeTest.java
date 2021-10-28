package com.anzhi.govtech.employeeSalaryManagement.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeTest {

    private final String someId = "E100";
    private final String someLogin = "GT-HPotter";
    private final String someName = "Harry Potter";
    private final double someSalary = 1000.0;
    private final LocalDate someStartDate = LocalDate.parse("2020-01-08");
    private final Employee expectedEmployee = Employee.builder()
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
