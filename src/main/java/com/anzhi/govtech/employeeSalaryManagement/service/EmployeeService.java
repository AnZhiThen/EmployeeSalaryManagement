package com.anzhi.govtech.employeeSalaryManagement.service;

import com.anzhi.govtech.employeeSalaryManagement.repository.EmployeeRepository;
import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
        validateEmployee(e);
        if (employeeRepository.existsEmployeeById(e.getId())) {
            throw new Exception("Employee ID already exists");
        }

        if (employeeRepository.existsEmployeeByLogin(e.getLogin())) {
            throw new Exception("Employee login not unique");
        }

        employeeRepository.save(e);
    }

    public void update(String employeeId, final Employee e) throws Exception {
        validateEmployee(e);
        Optional<Employee> currentEmployee = employeeRepository.findEmployeeById(employeeId);
        if (!currentEmployee.isPresent()) {
            throw new Exception("No such employee");
        }

        if (employeeRepository.existsEmployeeByLoginAndIdNot(e.getLogin(), employeeId)) {
            throw new Exception("Employee login not unique");
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

    public List<Employee> getAll(Double minSalary, Double maxSalary, String sort, String order, Integer limit, Integer offset) throws Exception {
        if (minSalary < 0 || maxSalary < 0) {
            throw new Exception("Bad parameters: Min/Max Salary should not be negative");
        }

        if (minSalary > maxSalary) {
            throw new Exception("Bad parameters: Min salary is larger than Max Salary");
        }

        if (limit < 1) {
            throw new Exception("Bad parameters: Limit should not be less than 1");
        }

        if (offset < 0) {
            throw new Exception("Bad parameters: Offset should not be negative");
        }
        Sort sortSettings = order.equals("dsc") ? Sort.by(sort).descending() : Sort.by(sort).ascending();

        Pageable page = PageRequest.of(offset, limit, sortSettings);
        try {
            return employeeRepository.advancedSearch(page, minSalary, maxSalary);
        } catch (Exception ex) {
            throw new Exception("Encountered query exception");
        }
    }

    public void validateEmployee(Employee e) throws Exception {
        if (e.getSalary() < 0) {
            throw new Exception("Invalid salary");
        }

        if (e.getStartDate() == null) {
            throw new Exception("Invalid start date");
        }
    }
}
