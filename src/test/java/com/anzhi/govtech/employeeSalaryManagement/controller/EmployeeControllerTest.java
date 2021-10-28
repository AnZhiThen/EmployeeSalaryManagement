package com.anzhi.govtech.employeeSalaryManagement.controller;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import com.anzhi.govtech.employeeSalaryManagement.service.EmployeeService;
import org.hibernate.QueryException;
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
    private final String someId = "E100";
    private final String someLogin = "GT-HPotter";
    private final String someName = "Harry Potter";
    private final double someSalary = 1000.0;
    private final LocalDate someStartDate = LocalDate.parse("2020-01-08");
    private final Employee someEmployee = Employee.builder()
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
            public void itShouldReturn() {
                when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                ResponseEntity<Employee> res = subject.getEmployee(someId);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody()).isEqualTo(someEmployee);
                verify(employeeRepository, times(1)).findEmployeeById(someId);
            }
        }

        @Nested
        class WhenResponseStatus4xx {
            @Nested
            class WhenNoSuchEmployee {
                @Test
                public void itShouldReturn400() {
                    when(employeeRepository.findEmployeeById(someEmployee.getId())).thenReturn(Optional.empty());
                    ResponseEntity<HashMap> res = subject.getEmployee(someId);
                    verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "No such employee");
                    verify(employeeRepository, times(1)).findEmployeeById(someId);
                }
            }
        }
    }

    @Nested
    class PostEmployee {
        @Nested
        class WhenReturn201 {
            @Test
            public void itShouldReturn() {
                ResponseEntity<HashMap> res = subject.postEmployee(someEmployee);
                verifyResponseEntityWithMessage(res, HttpStatus.CREATED, "Successfully created");
                verify(employeeRepository, times(1)).save(someEmployee);
            }
        }

        @Nested
        class WhenReturn4xx {
            @Nested
            class WhenEmployeeIdExist {
                @Test
                public void itShouldReturn400() {
                    when(employeeRepository.existsEmployeeById(someEmployee.getId())).thenReturn(true);
                    ResponseEntity<HashMap> res = subject.postEmployee(someEmployee);
                    verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Employee ID already exists");
                    verify(employeeRepository, never()).save(someEmployee);
                }
            }

            @Nested
            class WhenEmployeeIdDoesNotExist {
                @Nested
                class WhenEmployeeLoginIsNotUnique {
                    @Test
                    public void itShouldReturn400() {
                        when(employeeRepository.existsEmployeeByLogin(someEmployee.getLogin())).thenReturn(true);
                        ResponseEntity<HashMap> res = subject.postEmployee(someEmployee);
                        verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Employee login not unique");
                        verify(employeeRepository, never()).save(someEmployee);
                    }
                }

                @Nested
                class WhenEmployeeLoginIsUnique {
                    @Nested
                    class WhenSalaryIsNegative {
                        @Test
                        public void itShouldReturn400() {
                            Employee negativeSalaryEmployee = someEmployee.withSalary(-100);
                            ResponseEntity<HashMap> res = subject.postEmployee(negativeSalaryEmployee);
                            verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Invalid salary");
                            verify(employeeRepository, never()).save(someEmployee);
                        }
                    }
                }
            }
        }
    }

    @Nested
    class GetAllEmployees {
        Double someMinSalary = 0.0;
        Double someMaxSalary = 4000.0;
        String someSort = "id";
        String order = "asc";
        Integer someLimit = 1;
        Integer someOffset = 0;
        Pageable somePageable = PageRequest.of(someOffset, someLimit, Sort.by(someSort).ascending());

        @Nested
        class WhenReturn200 {
            @Test
            public void itShouldReturn() {
                when(employeeRepository.advancedSearch(somePageable, someMinSalary, someMaxSalary))
                        .thenReturn(Arrays.asList(someEmployee));
                ResponseEntity<HashMap> res = subject.getAllEmployees(someMinSalary, someMaxSalary, someSort, order, someLimit, someOffset);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody().get("results")).isEqualTo(Arrays.asList(someEmployee));
                verify(employeeRepository, times(1)).advancedSearch(somePageable, someMinSalary, someMaxSalary);
            }
        }

        @Nested
        class WhenReturn4xx {
            @Nested
            class WhenMinMaxSalaryIsPositive {
                @Nested
                class WhenLimitIsPositive {
                    @Nested
                    class WhenOffsetIsPositive {
                        @Test
                        public void itShouldFailWhenSortIsNotEmployeeFieldNames() {
                            Sort sortSettings = Sort.by("someField").ascending();
                            Pageable page = PageRequest.of(someOffset, someLimit, sortSettings);
                            when(employeeRepository.advancedSearch(page, someMinSalary, someMaxSalary))
                                    .thenThrow(new QueryException("Encountered query exception"));
                            ResponseEntity<HashMap> res = subject.getAllEmployees(someMinSalary, someMaxSalary, "someField", order, someLimit, someOffset);
                            verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Encountered query exception");
                            verify(employeeRepository, times(1)).advancedSearch(any(), any(), any());
                        }
                    }
                    @Nested
                    class WhenOffSetIsNegative {
                        @Test
                        public void itShouldFailWhenOffsetIsNegative() {
                            ResponseEntity<HashMap> res = subject.getAllEmployees(someMinSalary, someMaxSalary, someSort, order, someLimit, -1);
                            verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Bad parameters: Offset should not be negative");
                            verify(employeeRepository, never()).advancedSearch(any(), any(), any());
                        }
                    }
                }

                @Nested
                class WhenLimitIsNegative {
                    @Test
                    public void itShouldFailWhenLimitIsNegative() {
                        ResponseEntity<HashMap> res = subject.getAllEmployees(someMinSalary, someMaxSalary, someSort, order, 0, someOffset);
                        verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Bad parameters: Limit should not be less than 1");
                        verify(employeeRepository, never()).advancedSearch(any(), any(), any());
                    }
                }
            }

            @Nested
            class WhenMinMaxSalaryIsNegative {
                @Test
                public void itShouldFailWhenMinSalaryIsNegative() {
                    ResponseEntity<HashMap> res = subject.getAllEmployees(-1.00, someMaxSalary, someSort, order, someLimit, someOffset);
                    verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Bad parameters: Min/Max Salary should not be negative");
                    verify(employeeRepository, never()).advancedSearch(any(), any(), any());
                }

                @Test
                public void itShouldFailWhenMaxSalaryIsNegative() {
                    ResponseEntity<HashMap> res = subject.getAllEmployees(someMinSalary, -someMaxSalary, someSort, order, someLimit, someOffset);
                    verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Bad parameters: Min/Max Salary should not be negative");
                    verify(employeeRepository, never()).advancedSearch(any(), any(), any());
                }
            }
        }
    }

    @Nested
    class DeleteEmployee {
        @Nested
        class WhenResponseStatus200 {
            @Test
            public void itShouldReturn() {
                when(employeeRepository.existsEmployeeById(someId)).thenReturn(true);
                ResponseEntity<HashMap> res = subject.deleteEmployee(someId);
                verifyResponseEntityWithMessage(res, HttpStatus.OK, "Successfully deleted");
                verify(employeeRepository, times(1)).existsEmployeeById(someId);
                verify(employeeRepository, times(1)).deleteEmployeeById(someId);
            }
        }

        @Nested
        class WhenResponseStatus4xx {
            @Nested
            class WhenNoSuchEmployee {
                @Test
                public void itShouldReturn400() {
                    when(employeeRepository.existsEmployeeById(someId)).thenReturn(false);
                    ResponseEntity<HashMap> res = subject.deleteEmployee(someId);
                    verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "No such employee");
                    verify(employeeRepository, times(1)).existsEmployeeById(someId);
                    verify(employeeRepository, never()).deleteEmployeeById(someId);
                }
            }
        }
    }

    @Nested
    class UpdateEmployee {
        @Nested
        class WhenResponseStatus200 {
            @Test
            public void itShouldReturn() {
                when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                ResponseEntity<HashMap> res = subject.updateEmployee(someId, someEmployee);
                verifyResponseEntityWithMessage(res, HttpStatus.OK, "Successfully updated");
                verify(employeeRepository, times(1)).findEmployeeById(someId);
            }
        }

        @Nested
        class WhenResponseStatus4xx {
            @Nested
            class WhenEmployeeIdExist {
                @Nested
                class WhenEmployeeLoginExistWithDifferentId {
                    @Test
                    public void itShouldReturn400() {
                        when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                        when(employeeRepository.existsEmployeeByLoginAndIdNot(someEmployee.getLogin(), someId))
                                .thenReturn(true);
                        ResponseEntity<HashMap> res = subject.updateEmployee(someId, someEmployee);
                        verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Employee login not unique");
                        verify(employeeRepository, never()).save(any());
                    }
                }

                @Nested
                class WhenEmployeeLoginIsUnique {
                    @Nested
                    class WhenPositiveEmployeeSalary {
                        @Nested
                        class WhenStartDateIsChanged {
                            @Test
                            public void itShouldReturn400() {
                                when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                                Employee someOtherEmployee = someEmployee.withStartDate(LocalDate.parse("2020-08-09"));
                                ResponseEntity<HashMap> res = subject.updateEmployee(someId, someOtherEmployee);
                                verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Invalid start date");
                                verify(employeeRepository, never()).save(any());
                            }
                        }
                    }

                    @Nested
                    class WhenNegativeEmployeeSalary {
                        @Test
                        public void itShouldReturn400() {
                            when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                            Employee someOtherEmployee = someEmployee.withSalary(-1.0);
                            ResponseEntity<HashMap> res = subject.updateEmployee(someId, someOtherEmployee);
                            verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "Invalid salary");
                            verify(employeeRepository, never()).save(any());
                        }
                    }
                }
            }

            @Nested
            class WhenEmployeeIdDoesNotExist {
                @Test
                public void itShouldReturn400() {
                    when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.empty());
                    ResponseEntity<HashMap> res = subject.updateEmployee(someId, someEmployee);
                    verifyResponseEntityWithMessage(res, HttpStatus.BAD_REQUEST, "No such employee");
                    verify(employeeRepository, never()).save(any());
                }
            }
        }
    }

    private void verifyResponseEntityWithMessage(ResponseEntity<HashMap> res, HttpStatus status, String message) {
        assertThat(res.getStatusCode()).isEqualTo(status);
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(res.getBody().get("message")).isEqualTo(message);
    }
}
