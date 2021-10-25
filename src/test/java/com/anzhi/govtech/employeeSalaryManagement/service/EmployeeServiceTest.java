package com.anzhi.govtech.employeeSalaryManagement.service;

import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {
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
    class EmployeeRead {
        @Nested
        class Valid {
            @Test
            public void itShouldReturn() throws Exception {
                when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                Employee e = subject.read(someId);
                assertThat(someEmployee).isEqualTo(e);
            }
        }

        @Nested
        class Invalid {
            @Test
            public void itShouldNotReturn() throws Exception {
                String nonExistentId = "testId";
                when(employeeRepository.findEmployeeById(nonExistentId)).thenReturn(Optional.empty());
                assertThatThrownBy(() -> subject.read(someId))
                        .isInstanceOf(Exception.class).hasMessage("No such employee");
            }
        }

        @AfterEach
        private void verifyRepositoryInteraction() {
            verify(employeeRepository, never()).save(any());
            verify(employeeRepository, times(1)).findEmployeeById(someId);
        }
    }

    @Nested
    class EmployeeUpdate {
        @Nested
        class Valid {
            @Test
            public void itShouldUpdate() throws Exception {
                Employee differentSalaryEmployee = someEmployee.withSalary(4000.00);
                when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.of(someEmployee));
                when(employeeRepository.existsEmployeeByLoginAndIdNot(someEmployee.getLogin(), someId)).thenReturn(false);
                subject.update(someId, differentSalaryEmployee);
                verify(employeeRepository, times(1)).save(differentSalaryEmployee);
            }
        }

        @Nested
        class Invalid {
            public static final String otherId = "otherId";
            @Nested
            class whenEmployeeExists {
                @Nested
                class whenLoginIdNotUnique {
                    @Test
                    public void itShouldNotUpdateWhenLoginUnique() throws Exception {
                        Employee someOtherEmployee = someEmployee.withId(otherId);
                        when(employeeRepository.findEmployeeById(otherId)).thenReturn(Optional.of(someEmployee));
                        when(employeeRepository.existsEmployeeByLoginAndIdNot(someOtherEmployee.getLogin(), otherId)).thenReturn(true);
                        verifyExceptionThrownAndNotSavedToRepo(someOtherEmployee, "Employee login not unique");
                    }
                }

                @Nested
                class whenLoginIdUnique {
                    @Nested
                    class whenSalaryValid {
                        @Nested
                        class whenStartDateInvalid {
                            @Test
                            public void itShouldNotUpdateWhenStartDateIsDifferent() throws Exception {
                                Employee someOtherEmployee = someEmployee.withStartDate(LocalDate.parse("2020-10-10"));
                                when(employeeRepository.findEmployeeById(otherId)).thenReturn(Optional.of(someEmployee));
                                when(employeeRepository.existsEmployeeByLoginAndIdNot(someOtherEmployee.getLogin(), otherId)).thenReturn(false);
                                verifyExceptionThrownAndNotSavedToRepo(someOtherEmployee, "Invalid start date");
                            }
                        }
                    }

                    @Nested
                    class whenSalaryInvalid {
                        @Test
                        public void itShouldNotUpdateWhenSalaryIsNegative() throws Exception {
                            Employee someOtherEmployee = someEmployee.withSalary(-1.00);
                            when(employeeRepository.findEmployeeById(otherId)).thenReturn(Optional.of(someEmployee));
                            when(employeeRepository.existsEmployeeByLoginAndIdNot(someOtherEmployee.getLogin(), otherId)).thenReturn(false);
                            verifyExceptionThrownAndNotSavedToRepo(someOtherEmployee, "Invalid salary");
                        }
                    }
                }
            }

            @Nested
            class whenNoSuchEmployee {
                @Test
                public void itShouldNotUpdateWhenNoSuchEmployee() throws Exception {
                    Employee someOtherEmployee = someEmployee.withId(otherId);
                    when(employeeRepository.findEmployeeById(someId)).thenReturn(Optional.empty());
                    verifyExceptionThrownAndNotSavedToRepo(someOtherEmployee, "No such employee");
                }
            }

            private void verifyExceptionThrownAndNotSavedToRepo(Employee e, String exceptionMessage) {
                assertThatThrownBy(() -> subject.update(otherId, e))
                        .isInstanceOf(Exception.class)
                        .hasMessage(exceptionMessage);
                verify(employeeRepository, never()).save(any());
            }
        }
    }

    @Nested
    class EmployeeDelete {
        @Nested
        class ValidEmployee {
            @Test
            public void itShouldPass() throws Exception {
                when(employeeRepository.existsEmployeeById(someEmployee.getId()))
                        .thenReturn(true);
                subject.delete(someId);
                verify(employeeRepository, times(1)).deleteEmployeeById(someEmployee.getId());
            }
        }

        @Nested
        class invalidEmployee {
            @Test
            public void itShouldPass() throws Exception {
                String someOtherId = "someOtherId";
                when(employeeRepository.existsEmployeeById(someOtherId))
                        .thenReturn(false);
                assertThatThrownBy(() -> subject.delete(someId))
                        .isInstanceOf(Exception.class).hasMessage("No such employee");
                verify(employeeRepository, never()).deleteById(any());
            }
        }
    }

    @Nested
    class GetAllEmployees{
        Double someMinSalary = 0.0;
        Double someMaxSalary = 4000.0;
        String someSort = "id";
        String someOrder = "asc";
        Integer someLimit = 1;
        Integer someOffset = 0;

        @Nested
        class ValidParameters {

            @Test
            public void whenItShouldReturn() throws Exception {
                when(employeeRepository.advancedSearch(PageRequest.of(someOffset, someLimit), someMinSalary, someMaxSalary))
                        .thenReturn(Arrays.asList(someEmployee));
                List<Employee> employeeList = subject.getAll(someMinSalary, someMaxSalary, someSort, someOrder, someLimit, someOffset);
                verify(employeeRepository, times(1)).advancedSearch(PageRequest.of(someOffset, someLimit, Sort.by(someSort).ascending())
                        , someMinSalary, someMaxSalary);
            }
        }

        @Nested
        class InvalidParameters {
            @Test
            public void shouldFailWhenInvalidMinSalary() throws Exception {
                assertThatThrownBy(() -> subject.getAll(-1.0, someMaxSalary, someSort, someOrder, someLimit, someOffset))
                        .isInstanceOf(Exception.class).hasMessage("Bad parameters: Min/Max Salary should not be negative");
                verify(employeeRepository, never()).advancedSearch(any(), any(), any());
            }

            @Test
            public void shouldFailWhenInvalidMaxSalary() throws Exception {
                assertThatThrownBy(() -> subject.getAll(someMinSalary, -1.0, someSort, someOrder, someLimit, someOffset))
                        .isInstanceOf(Exception.class).hasMessage("Bad parameters: Min/Max Salary should not be negative");
                verify(employeeRepository, never()).advancedSearch(any(), any(), any());
            }

            @Test
            public void shouldFailWhenMaxIsSmallerThanMin() throws Exception {
                assertThatThrownBy(() -> subject.getAll(someMinSalary + 1, someMinSalary, someSort, someOrder, someLimit, someOffset))
                        .isInstanceOf(Exception.class).hasMessage("Bad parameters: Min salary is larger than Max Salary");
                verify(employeeRepository, never()).advancedSearch(any(), any(), any());
            }

            @Test
            public void shouldFailWhenLimitNegative() throws Exception {
                assertThatThrownBy(() -> subject.getAll(someMinSalary, someMaxSalary, someSort, someOrder, -1, someOffset))
                        .isInstanceOf(Exception.class).hasMessage("Bad parameters: Limit should not be less than 1");
                verify(employeeRepository, never()).advancedSearch(any(), any(), any());
            }

            @Test
            public void shouldFailWhenOffsetNegative() throws Exception {
                assertThatThrownBy(() -> subject.getAll(someMinSalary, someMaxSalary, someSort, someOrder, someLimit, -1))
                        .isInstanceOf(Exception.class).hasMessage("Bad parameters: Offset should not be negative");
                verify(employeeRepository, never()).advancedSearch(any(), any(), any());
            }
        }
    }
}