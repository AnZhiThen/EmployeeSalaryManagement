package com.anzhi.govtech.employeeSalaryManagement.service;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeUploadService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeUploadService(final EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public int validateAndProcessList(ArrayList<String> employeeLines) throws Exception {
        ArrayList<Employee> employeeList = new ArrayList<>();
        for (int i = 0; i < employeeLines.size(); i++) {
            String employeeLine = employeeLines.get(i).trim();
            if (employeeLine.charAt(0) == '#') continue;
            employeeList.add(buildEmployee(employeeLine));
        }
        Set<String> idList = employeeList.stream().map(Employee::getId).collect(Collectors.toSet());
        Set<String> loginList = employeeList.stream().map(Employee::getLogin).collect(Collectors.toSet());
        if (idList.size() != employeeList.size()) {
            throw new Exception("There should no one row with the same employee id");
        }
        if (loginList.size() != employeeList.size()) {
            throw new Exception("There should no one row with the same employee login");
        }
        List<Employee> savedEmployeeList = employeeRepository.saveAll(employeeList);
        System.out.println(savedEmployeeList.size());
        return savedEmployeeList.size();
    }

    private Employee buildEmployee(String employeeLine) throws Exception {
        String[] employeeDetails = employeeLine.split(",");
        if (employeeDetails.length != 5) {
            throw new Exception("All 5 columns must be filled");
        }
        String id = employeeDetails[0].trim();
        String login = employeeDetails[1].trim();
        String name = employeeDetails[2].trim();
        double salary = Double.parseDouble(employeeDetails[3].trim());
        if (salary < 0) {
            throw new Exception("Invalid salary");
        }
        LocalDate startDate = LocalDate.parse(employeeDetails[4].trim());
        return Employee.builder()
                .id(id)
                .login(login)
                .name(name)
                .salary(salary)
                .startDate(startDate)
                .build();
    }
}
