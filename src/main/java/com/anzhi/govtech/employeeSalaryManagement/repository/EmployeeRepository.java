package com.anzhi.govtech.employeeSalaryManagement.repository;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    @Query
    Boolean existsEmployeeByLogin(String login);

    @Query
    Boolean existsEmployeeById(String id);

    @Query
    Optional<Employee> findEmployeeById(String id);

    @Query
    @Transactional
    void deleteEmployeeById(String id);

    @Query
    Boolean existsEmployeeByLoginAndIdNot(String login, String id);

    @Query(value = "Select e from Employee e " +
            "where e.salary >= :minSalary and e.salary <= :maxSalary")
    List<Employee> advancedSearch(Pageable p, Double minSalary, Double maxSalary);
}
