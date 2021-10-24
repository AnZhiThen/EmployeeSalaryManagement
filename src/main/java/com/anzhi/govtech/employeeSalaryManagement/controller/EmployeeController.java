package com.anzhi.govtech.employeeSalaryManagement.controller;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import com.anzhi.govtech.employeeSalaryManagement.service.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(final EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity getEmployee(@PathVariable("id") String id) {
        try {
            Employee employee = employeeService.read(id);
            return constructResponseEntity(employee, HttpStatus.OK);
        } catch(Exception ex) {
            return getResponseEntityWithMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity postEmployee(@RequestBody Employee e) {
        try {
            employeeService.create(e);
            return getResponseEntityWithMessage("Successfully created", HttpStatus.CREATED);
        } catch (Exception ex) {
            return getResponseEntityWithMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Employee> constructResponseEntity(Employee employee, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<Employee>(employee, headers, status);
    }

    private ResponseEntity getResponseEntityWithMessage(String message, HttpStatus status) {
        HashMap<String, String> body = new HashMap<>();
        body.put("message", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<HashMap>(body, headers, status);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(){
        return null;
    }
}
