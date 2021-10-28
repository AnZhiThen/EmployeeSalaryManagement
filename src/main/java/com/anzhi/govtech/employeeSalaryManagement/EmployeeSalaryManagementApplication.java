package com.anzhi.govtech.employeeSalaryManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class EmployeeSalaryManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeSalaryManagementApplication.class, args);
	}

}
