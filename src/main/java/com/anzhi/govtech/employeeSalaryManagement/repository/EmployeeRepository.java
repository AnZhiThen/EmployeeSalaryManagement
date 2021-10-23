package com.anzhi.govtech.employeeSalaryManagement.repository;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    @Query
    Boolean existsEmployeeByLogin(String login);

    @Query
    Boolean existsEmployeeById(String id);
}
