package com.anzhi.govtech.employeeSalaryManagement.service;

import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(final EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

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

    public void update(String employeeId, final Employee e) throws Exception {
        Optional<Employee> currentEmployee = employeeRepository.findEmployeeById(employeeId);
        if (!currentEmployee.isPresent()) {
            throw new Exception("No such employee");
        }

        if (employeeRepository.existsEmployeeByLoginAndIdNot(e.getLogin(), employeeId)) {
            throw new Exception("Employee login not unique");
        }

        if (e.getSalary() < 0) {
            throw new Exception("Invalid salary");
        }

        if (!currentEmployee.get().getStartDate().isEqual(e.getStartDate())) {
            throw new Exception("Invalid start date");
        }

        employeeRepository.save(e);
    }

    public Employee read(String employeeId) throws Exception {
        Optional<Employee> e = employeeRepository.findEmployeeById(employeeId);
        return e.orElseThrow(() -> new Exception("No such employee"));
    }

    public void delete(String employeeId) throws Exception {
        if (!employeeRepository.existsEmployeeById(employeeId)) {
            throw new Exception("No such employee");
        }

        employeeRepository.deleteEmployeeById(employeeId);
    }

    public List<Employee> getAll() {
        List<Employee> result = new ArrayList<>();
        employeeRepository.findAll().forEach(result::add);
        return result;
    }
}
