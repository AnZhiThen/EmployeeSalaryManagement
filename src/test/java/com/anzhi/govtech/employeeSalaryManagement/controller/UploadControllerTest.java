package com.anzhi.govtech.employeeSalaryManagement.controller;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import com.anzhi.govtech.employeeSalaryManagement.service.EmployeeUploadService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UploadControllerTest {
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
    private final ArrayList<Employee> someEmployeeList = new ArrayList(Arrays.asList(
            someEmployee1,
            someEmployee2,
            someEmployee3
    ));
    private final String someCSV = "id,login,name,salary,startDate\n" +
            "e0001,hpotter,Harry Potter,1234.00,2011-11-11\n" +
            "e0002,rwesley,Ron Weasley,19234.50,2012-11-11\n" +
            "e0003,ssnape,Severus Snape,4000.0,2013-11-11";
    EmployeeRepository repo =  mock(EmployeeRepository.class);
    EmployeeUploadService service = new EmployeeUploadService(repo);
    UploadController subject = new UploadController(service);

    @Nested
    class Upload {
        @Nested
        class WhenItReturn2xx {
            @Test
            public void itShouldReturn200() {
                MultipartFile multipartFile = new MockMultipartFile("file", someCSV.getBytes());
                ResponseEntity res = subject.uploadEmployeeData(multipartFile);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
                verify(repo, times(1)).saveAll(someEmployeeList);
            }
        }

        @Nested
        class WhenItReturn400 {
            @Test
            public void itShouldReturn400() {
                String someOtherCSV = someCSV + "\ne0001,hpotter,Harry Potter,1234.00,2011-11-11";
                MultipartFile multipartFile = new MockMultipartFile("file", someOtherCSV.getBytes());
                ResponseEntity<HashMap> res = subject.uploadEmployeeData(multipartFile);
                assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(res.getBody().get("message")).isEqualTo("There should no one row with the same employee id");
                verify(repo, never()).saveAll(someEmployeeList);
            }
        }
    }
}
