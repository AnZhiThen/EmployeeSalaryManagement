package com.anzhi.govtech.employeeSalaryManagement.service;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class EmployeeUploadServiceTest {
    ArrayList<String> someEmployeeLines = new ArrayList(Arrays.asList(
            "e0001, hpotter, Harry Potter, 1234.00, 2011-11-11",
            "e0002, rwesley, Ron Weasley, 19234.50, 2012-11-11",
            "e0003, ssnape, Severus Snape, 4000.0, 2013-11-11"
    ));
    private final Employee someEmployee1 = Employee.builder()
            .id("e0001")
            .login("hpotter")
            .name("Harry Potter")
            .salary(1234.00)
            .startDate(LocalDate.parse("2011-11-11"))
            .build();
    private final Employee someEmployee2 = Employee.builder()
            .id("e0002")
            .login("rwesley")
            .name("Ron Weasley")
            .salary(19234.50)
            .startDate(LocalDate.parse("2012-11-11"))
            .build();
    private final Employee someEmployee3 = Employee.builder()
            .id("e0003")
            .login("ssnape")
            .name("Severus Snape")
            .salary(4000.00)
            .startDate(LocalDate.parse("2013-11-11"))
            .build();
    ArrayList<Employee> someEmployeeList = new ArrayList(Arrays.asList(
            someEmployee1,
            someEmployee2,
            someEmployee3
    ));
    EmployeeRepository repo =  mock(EmployeeRepository.class);
    EmployeeUploadService subject = new EmployeeUploadService(repo);

    @Nested
    class UploadTest {
        @Nested
        class UploadSuccess {
            @Test
            public void itShouldSucceed() throws Exception {
                subject.validateAndProcessList(someEmployeeLines);
                verify(repo, times(1)).saveAll(someEmployeeList);
            }

            @Test
            public void itShouldSucceedWhenLinesAreCommented() throws Exception {
                someEmployeeLines.add("#ThisLineCommented");
                subject.validateAndProcessList(someEmployeeLines);
                verify(repo, times(1)).saveAll(someEmployeeList);
            }
        }

        @Nested
        class UploadFailure {
            @Test
            public void itShouldFailWhenNotExactlyFiveColumns() {
                ArrayList<String> malformedCSV = new ArrayList(
                        Arrays.asList("e0001, hpotter, Harry Potter, 1234.00")
                );
                assertThatThrownBy(() -> subject.validateAndProcessList(malformedCSV))
                        .isInstanceOf(Exception.class).hasMessage("All 5 columns must be filled");
                verify(repo, never()).saveAll(someEmployeeList);
            }

            @Test
            public void itShouldFailWhenNotSalaryIsNegative() {
                ArrayList<String> malformedCSV = new ArrayList(
                        Arrays.asList("e0001, hpotter, Harry Potter, -1234.00, 2013-11-11")
                );
                assertThatThrownBy(() -> subject.validateAndProcessList(malformedCSV))
                        .isInstanceOf(Exception.class).hasMessage("Invalid salary");
                verify(repo, never()).saveAll(someEmployeeList);
            }

            @Test
            public void itShouldFailWhenDateIsInvalid() {
                ArrayList<String> malformedCSV = new ArrayList(
                        Arrays.asList("e0001, hpotter, Harry Potter, 1234.00, 2013-13-13")
                );
                assertThatThrownBy(() -> subject.validateAndProcessList(malformedCSV))
                        .isInstanceOf(Exception.class);
                verify(repo, never()).saveAll(someEmployeeList);
            }

            @Test
            public void itShouldFailWhenIdIsDuplicated() {
                ArrayList<String> malformedCSV = new ArrayList(
                        Arrays.asList(
                                "e0001, hpotter1, Harry Potter1, 4321.00, 2013-11-11",
                                "e0001, hpotter2, Harry Potter2, 1234.00, 2013-11-11"
                        )
                );
                assertThatThrownBy(() -> subject.validateAndProcessList(malformedCSV))
                        .isInstanceOf(Exception.class)
                        .hasMessage("There should no one row with the same employee id");
                verify(repo, never()).saveAll(someEmployeeList);
            }

            @Test
            public void itShouldFailWhenLoginIsDuplicated() {
                ArrayList<String> malformedCSV = new ArrayList(
                        Arrays.asList(
                                "e0001, hpotter1, Harry Potter1, 4321.00, 2013-11-11",
                                "e0002, hpotter1, Harry Potter2, 1234.00, 2013-11-11"
                        )
                );
                assertThatThrownBy(() -> subject.validateAndProcessList(malformedCSV))
                        .isInstanceOf(Exception.class)
                        .hasMessage("There should no one row with the same employee login");
                verify(repo, never()).saveAll(someEmployeeList);
            }
        }
    }
}
