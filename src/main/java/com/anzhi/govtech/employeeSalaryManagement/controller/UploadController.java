package com.anzhi.govtech.employeeSalaryManagement.controller;

import com.anzhi.govtech.employeeSalaryManagement.model.Employee;
import com.anzhi.govtech.employeeSalaryManagement.service.EmployeeService;
import com.anzhi.govtech.employeeSalaryManagement.service.EmployeeUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

@RequestMapping("/users/upload")
@RestController
@RequiredArgsConstructor
public class UploadController {

    private EmployeeUploadService uploadService;

    @Autowired
    public UploadController(final EmployeeUploadService service) {
        this.uploadService = service;
    }

    @PostMapping
    public ResponseEntity uploadEmployeeData(@RequestParam("file") MultipartFile file) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            String line = "";
            ArrayList<String> lines = new ArrayList<>();
            // skip first row for column names
            br.readLine();
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
            uploadService.validateAndProcessList(lines);
            return getResponseEntityWithMessage("All data has been updated/added.", HttpStatus.OK);
        } catch (Exception ex) {
            return getResponseEntityWithMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity getResponseEntityWithMessage(String message, HttpStatus status) {
        HashMap<String, String> body = new HashMap<>();
        body.put("message", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<HashMap>(body, headers, status);
    }
}
