package com.anzhi.govtech.employeeSalaryManagement.controller;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import com.anzhi.govtech.employeeSalaryManagement.service.EmployeeService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EmployeeControllerTest {
    private String someId = "E100";
    private String someLogin = "GT-HPotter";
    private String someName = "Harry Potter";
    private double someSalary = 1000.0;
    private LocalDate someStartDate = LocalDate.parse("2020-01-08");
    private Long someEid = 1234L;
    private Employee someEmployee = Employee.builder()
            .eid(someEid)
            .id(someId)
            .login(someLogin)
            .name(someName)
            .salary(someSalary)
            .startDate(someStartDate)
            .build();

    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final EmployeeService employeeService = new EmployeeService(employeeRepository);
    private final EmployeeController subject = new EmployeeController(employeeService);

    @Nested
    class GetEmployee {
        @Nested
        class WhenResponseStatus200 {
            @Test
            public void itShouldReturn() throws Exception {
                when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                ResponseEntity<Employee> res = subject.getEmployee(someId);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody()).isEqualTo(someEmployee);
                verify(employeeRepository, times(1)).findEmployeeById(someId);
            }
        }

        @Nested
        class WhenResponseStatus4xx{
            @Test
            public void whenNoSuchEmployee() throws Exception {
                when(employeeRepository.findEmployeeById(someEmployee.getId())).thenReturn(Optional.empty());

                ResponseEntity<HashMap> res = subject.getEmployee(someId);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody().get("message")).isEqualTo("No such employee");
                verify(employeeRepository, times(1)).findEmployeeById(someId);
            }
        }
    }

    @Nested
    class PostEmployee {
        @Nested
        class WhenReturn201 {
            @Test
            public void itShouldReturn() throws Exception {
                ResponseEntity<HashMap> res = subject.postEmployee(someEmployee);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody().get("message")).isEqualTo("Successfully created");
                verify(employeeRepository, times(1)).save(someEmployee);
            }
        }

        @Nested
        class WhenReturn4xx {

            @Nested
            class whenEmployeeIdExist{
                @Test
                public void itShouldReturn400() throws Exception{
                    when(employeeRepository.existsEmployeeById(someEmployee.getId())).thenReturn(true);

                    ResponseEntity<HashMap> res = subject.postEmployee(someEmployee);
                    verification(res, "Employee ID already exists");
                }
            }

            @Nested
            class whenEmployeeIdDoesNotExist{
                @Nested
                class whenEmployeeLoginIsNotUnique {
                    @Test
                    public void itShouldReturn400() throws Exception {
                        when(employeeRepository.existsEmployeeByLogin(someEmployee.getLogin())).thenReturn(true);

                        ResponseEntity<HashMap> res = subject.postEmployee(someEmployee);
                        verification(res, "Employee login not unique");
                    }
                }

                @Nested
                class whenEmployeeLoginIsUnique{
                    @Test
                    public void whenSalaryIsNegative(){
                        Employee negativeSalaryEmployee = someEmployee.withSalary(-100);
                        ResponseEntity<HashMap> res = subject.postEmployee(negativeSalaryEmployee);
                        verification(res, "Invalid salary");
                    }
                }
            }

            private void verification(final ResponseEntity<HashMap> res, String message) {
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody().get("message")).isEqualTo(message);
                verify(employeeRepository,never()).save(someEmployee);
            }
        }
    }

    @Nested
    class GetAllEmployees{

        @Nested
        class WhenReturn200{

            @Test
            public void whenNoQueryParameters(){

            }

        }

    }
}
