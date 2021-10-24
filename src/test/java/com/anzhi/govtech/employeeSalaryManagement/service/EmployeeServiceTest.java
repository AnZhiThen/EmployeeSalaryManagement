package com.anzhi.govtech.employeeSalaryManagement.service;

import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {
    private String someId = "E100";
    private String someLogin = "GT-HPotter";
    private String someName = "Harry Potter";
    private double someSalary = 1000.0;
    private LocalDate someStartDate = LocalDate.parse("2020-01-08");
    private final long someEmployeeId = 100L;
    private Employee someEmployee = Employee.builder()
            .eid(someEmployeeId)
            .id(someId)
            .login(someLogin)
            .name(someName)
            .salary(someSalary)
            .startDate(someStartDate)
            .build();

    final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);

    private final EmployeeService subject = new EmployeeService(employeeRepository);

    @Nested
    class EmployeeCreate {
        @Nested
        class ValidEmployee {
            @Test
            public void itShouldPass() throws Exception {
                subject.create(someEmployee);

                verify(employeeRepository, times(1)).save(someEmployee);
            }
        }

        @Nested
        class InvalidEmployee{
            @Test
            public void itShouldFailWhenDuplicatedLogin() throws Exception {
                when(employeeRepository.existsEmployeeByLogin(someEmployee.getLogin()))
                        .thenReturn(true);

                invalidEmployeeVerification(someEmployee, "Employee login not unique");
            }
            @Test
            public void itShouldFailWhenDuplicatedId() throws Exception {
                 when(employeeRepository.existsEmployeeById(someEmployee.getId()))
                        .thenReturn(true);

                invalidEmployeeVerification(someEmployee, "Employee ID already exists");
            }
            @Test
            public void itShouldFailWhenNegativeSalary(){
                Employee negativeSalaryEmployee = someEmployee.withSalary(-100.0);

                invalidEmployeeVerification(negativeSalaryEmployee, "Invalid salary");
            }

            private void invalidEmployeeVerification(final Employee employee, final String errorMessage) {
                assertThatThrownBy(() -> subject.create(employee))
                        .isInstanceOf(Exception.class).hasMessage(errorMessage);
                verify(employeeRepository, never()).save(employee);
            }
        }
    }

    @Nested
    class EmployeeUpdate {
        @Test
        public void itShouldPass() {
            Employee e = Employee.builder()
                    .eid(100L)
                    .id(someId)
                    .login(someLogin)
                    .name(someName)
                    .salary(someSalary)
                    .startDate(someStartDate)
                    .build();
            subject.update(e);

            verify(employeeRepository).save(e);
        }
    }
}