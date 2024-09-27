package com.myweb.webapp.controller;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.myweb.webapp.exceptions.DbConnectionException;
import com.myweb.webapp.exceptions.UnallowedPayloadException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequestMapping("/")
public class ApiController {
    @Autowired
    private DataSource dataSource;

    // health check api
    @GetMapping("/healthz")
    public ResponseEntity healthz(HttpServletRequest request, HttpServletResponse response) {
        log.info("Health check Api is called");

        // check if request has payload, if yes, return error
        if (request.getContentLength() > 0) {
            log.info("Error: Request includes payload!");
            throw new UnallowedPayloadException();
        }

        // check if database connection is successful, if not, return error
        try {
            dataSource.getConnection();
            log.info("Successfully connected to MySQL database!");
        } catch (Exception e) {
            log.info("Error: Failed to connect to MySQL database!");
            throw new DbConnectionException();
        }

        // return success response
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("X-Content-Type-Options", "nosniff");
        log.info("Health check Api is healthy");

        return ResponseEntity.ok().build();
    }

}
