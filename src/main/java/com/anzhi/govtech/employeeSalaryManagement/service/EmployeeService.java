package com.anzhi.govtech.employeeSalaryManagement.service;

import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    public void create(final Employee e) throws Exception {
        if (employeeRepository.existsEmployeeById(e.getId())) {
            throw new Exception("Employee ID already exists");
        }

        if (employeeRepository.existsEmployeeByLogin(e.getLogin())) {
            throw new Exception("Employee login not unique");
        }

        if (e.getSalary() < 0) {
            throw new Exception("Invalid salary");
        }
        employeeRepository.save(e);
    }

    public void update(final Employee e) {
        employeeRepository.save(e);
    }
}
