package com.anzhi.govtech.employeeSalaryManagement.repository;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query
    Boolean existsEmployeeByLogin(String login);

    @Query
    Boolean existsEmployeeById(String id);

    @Query
    Optional<Employee> findEmployeeById(String id);

    @Query
    void deleteEmployeeById(String id);

    @Query
    Boolean existsEmployeeByLoginAndIdNot(String login, String id);

//    @Query(value = "Select e from Employee e " +
//            "where e.salary >= :minSalary and e.salary <= :maxSalary")
//    Page<Employee> advancedSearch(double minSalary, double maxSalary);
}
