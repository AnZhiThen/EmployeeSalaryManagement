package com.anzhi.govtech.employeeSalaryManagement.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import com.anzhi.govtech.employeeSalaryManagement.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

public class EmployeeControllerIntegrationTest {
//    @Autowired
//    MockMvc mockMvc;
//    @MockBean
//    EmployeeService service;
//
//    private String someId = "E100";
//    private String someLogin = "GT-HPotter";
//    private String someName = "Harry Potter";
//    private double someSalary = 1000.0;
//    private LocalDate someStartDate = LocalDate.parse("2020-01-08");
//    private Employee someEmployee = Employee.builder()
//            .id(someId)
//            .login(someLogin)
//            .name(someName)
//            .salary(someSalary)
//            .startDate(someStartDate)
//            .build();
//
//    @Test
//    public void findEmployeeShouldReturn() throws Exception {
//        when(service.read(someId)).thenReturn(someEmployee);
//        this.mockMvc.perform(get("/api/v1/users/"+someId))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string(containsString("Hello, Mock")));;
//    }

//    @Nested
//    class EmployeeRead {
//        @Nested
//        class ValidEmployee {
//            @Test
//            public void findEmployeeShouldReturn() throws Exception {
//                when(service.read(someId)).thenReturn(someEmployee);
//                mockMvc.perform(get("/api/v1/users/"+someId))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(content().string(containsString("No such employee")));
//            }
//        }
//        @Nested
//        class InvalidEmployee {
//            @Test
//            public void findEmployeeShouldReturnNoSuchEmployee() throws Exception {
//                when(service.read(someId)).thenThrow(new Exception("No such employee"));
//                mockMvc.perform(get("/api/v1/users/"+someId))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(content().string(containsString("No such employee")));
//            }
//        }
//    }
}

