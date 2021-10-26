package com.anzhi.govtech.employeeSalaryManagement.controller;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import com.anzhi.govtech.employeeSalaryManagement.service.EmployeeService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
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
                verify(employeeRepository, never()).save(someEmployee);
            }
        }
    }

    @Nested
    class GetAllEmployees{
        Double someMinSalary = 0.0;
        Double someMaxSalary = 4000.0;
        String someSort = "id";
        String order = "asc";
        Integer someLimit = 1;
        Integer someOffset = 0;
        Pageable somePageable = PageRequest.of(someOffset, someLimit, Sort.by(someSort).ascending());

        @Nested
        class WhenReturn200{
            @Test
            public void itShouldReturn(){
                when(employeeRepository.advancedSearch(somePageable, someMinSalary, someMaxSalary))
                        .thenReturn(Arrays.asList(someEmployee));
                ResponseEntity<HashMap> res = subject.getAllEmployees(someMinSalary, someMaxSalary, someSort, order, someLimit, someOffset);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody().get("results")).isEqualTo(Arrays.asList(someEmployee));
                verify(employeeRepository, times(1)).advancedSearch(somePageable, someMinSalary, someMaxSalary);
            }
        }
    }

    @Nested
    class DeleteEmployee {
        @Nested
        class whenResponseStatus200 {
            @Test
            public void itShouldReturn() {
                when(employeeRepository.existsEmployeeById(someId)).thenReturn(true);
                ResponseEntity<HashMap> res = subject.deleteEmployee(someId);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody().get("message")).isEqualTo("Successfully deleted");
                verify(employeeRepository, times(1)).existsEmployeeById(someId);
                verify(employeeRepository, times(1)).deleteEmployeeById(someId);
            }
        }

        @Nested
        class whenResponseStatus4xx {
            @Test
            public void whenNoSuchEmployee() {
                when(employeeRepository.existsEmployeeById(someId)).thenReturn(false);
                ResponseEntity<HashMap> res = subject.deleteEmployee(someId);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody().get("message")).isEqualTo("No such employee");
                verify(employeeRepository, times(1)).existsEmployeeById(someId);
                verify(employeeRepository, never()).deleteEmployeeById(someId);
            }
        }
    }

    @Nested
    class UpdateEmployee {
        @Nested
        class whenResponseStatus200 {
            @Test
            public void itShouldReturn() {
                when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                ResponseEntity<HashMap> res = subject.updateEmployee(someId, someEmployee);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody().get("message")).isEqualTo("Successfully updated");
                verify(employeeRepository, times(1)).findEmployeeById(someId);
            }
        }

        @Nested
        class whenResponseStatus4xx {
            @Nested
            class whenEmployeeIdExist {
                @Nested
                class whenEmployeeLoginExistWithDifferentId {
                    @Test
                    public void itShouldReturn400() {
                        when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                        when(employeeRepository.existsEmployeeByLoginAndIdNot(someEmployee.getLogin(), someId))
                                .thenReturn(true);
                        ResponseEntity<HashMap> res = subject.updateEmployee(someId, someEmployee);
                        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                        assertThat(res.getBody().get("message")).isEqualTo("Employee login not unique");
                        verify(employeeRepository, never()).save(any());
                    }
                }

                @Nested
                class whenEmployeeLoginIsUnique {
                    @Nested
                    class whenPositiveEmployeeSalary {
                        @Nested
                        class whenStartDateIsChanged {
                            @Test
                            public void itShouldReturn400() {
                                when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                                Employee someOtherEmployee = someEmployee.withStartDate(LocalDate.parse("2020-08-09"));
                                ResponseEntity<HashMap> res = subject.updateEmployee(someId, someOtherEmployee);
                                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                                assertThat(res.getBody().get("message")).isEqualTo("Invalid start date");
                                verify(employeeRepository, never()).save(any());
                            }
                        }
                    }

                    @Nested
                    class whenNegativeEmployeeSalary {
                        @Test
                        public void itShouldReturn400() {
                            when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                            Employee someOtherEmployee = someEmployee.withSalary(-1.0);
                            ResponseEntity<HashMap> res = subject.updateEmployee(someId, someOtherEmployee);
                            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                            assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                            assertThat(res.getBody().get("message")).isEqualTo("Invalid salary");
                            verify(employeeRepository, never()).save(any());
                        }
                    }
                }
            }

            @Nested
            class whenEmployeeIdDoesNotExist {
                @Test
                public void itShouldReturn400() {
                    when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.empty());
                    ResponseEntity<HashMap> res = subject.updateEmployee(someId, someEmployee);
                    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                    assertThat(res.getBody().get("message")).isEqualTo("No such employee");
                    verify(employeeRepository, never()).save(any());
                }
            }
        }
    }
}
