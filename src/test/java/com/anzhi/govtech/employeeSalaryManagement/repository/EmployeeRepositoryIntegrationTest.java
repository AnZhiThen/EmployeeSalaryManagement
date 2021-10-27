package com.anzhi.govtech.employeeSalaryManagement.repository;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeRepositoryIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee someEmployee1 = Employee.builder()
            .id("e1")
            .login("hpotter")
            .name("Harry Potter")
            .salary(2000.0)
            .startDate(LocalDate.parse("2020-01-01"))
            .build();

    private Employee someEmployee2 = Employee.builder()
            .id("e2")
            .login("rwesley")
            .name("Ron Weasley")
            .salary(5000.0)
            .startDate(LocalDate.parse("2020-01-01"))
            .build();

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AdvancedSearch {
        @BeforeAll
        public void before() {
            employeeRepository.save(someEmployee1);
            employeeRepository.save(someEmployee2);
        }

        @Test
        public void itShouldReturnNothing() {
            List<Employee> employeeList = employeeRepository.advancedSearch(Pageable.unpaged(), 0.0, 0.0);
            assertThat(employeeList.isEmpty()).isTrue();
        }

        @Test
        public void itShouldReturnE1() {
            List<Employee> employeeList = employeeRepository.advancedSearch(Pageable.unpaged(), 0.0, 3000.0);
            assertThat(employeeList.isEmpty()).isFalse();
            assertThat(employeeList.get(0)).isEqualTo(someEmployee1);
        }

        @Test
        public void itShouldReturnE2() {
            List<Employee> employeeList = employeeRepository.advancedSearch(Pageable.unpaged(), 3000.0, 6000.0);
            assertThat(employeeList.isEmpty()).isFalse();
            assertThat(employeeList.get(0)).isEqualTo(someEmployee2);
        }

        @Test
        public void itShouldReturnIdAsc() {
            List<Employee> employeeList = employeeRepository.advancedSearch(PageRequest.of(0, 10, Sort.by("id")), 0.0, 6000.0);
            assertThat(employeeList.isEmpty()).isFalse();
            assertThat(employeeList.get(0)).isEqualTo(someEmployee1);
            assertThat(employeeList.get(1)).isEqualTo(someEmployee2);
        }

        @Test
        public void itShouldReturnIdDesc() {
            List<Employee> employeeList = employeeRepository.advancedSearch(PageRequest.of(0, 10, Sort.by("id").descending()), 0.0, 6000.0);
            assertThat(employeeList.isEmpty()).isFalse();
            assertThat(employeeList.get(0)).isEqualTo(someEmployee2);
            assertThat(employeeList.get(1)).isEqualTo(someEmployee1);
        }

        @Test
        public void itShouldReturnReturnNameAsc() {
            List<Employee> employeeList = employeeRepository.advancedSearch(PageRequest.of(0, 10, Sort.by("name").ascending()), 0.0, 6000.0);
            assertThat(employeeList.isEmpty()).isFalse();
            assertThat(employeeList.get(0)).isEqualTo(someEmployee1);
            assertThat(employeeList.get(1)).isEqualTo(someEmployee2);
        }

        @Test
        public void itShouldReturnReturnNameDesc() {
            List<Employee> employeeList = employeeRepository.advancedSearch(PageRequest.of(0, 10, Sort.by("name").descending()), 0.0, 6000.0);
            assertThat(employeeList.isEmpty()).isFalse();
            assertThat(employeeList.get(0)).isEqualTo(someEmployee2);
            assertThat(employeeList.get(1)).isEqualTo(someEmployee1);
        }
    }
}