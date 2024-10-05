package com.myweb.webapp.controller;


import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
public class HealthzController {
    @Autowired
    private DataSource dataSource;

    // health check api
    @GetMapping("/healthz")
    public ResponseEntity healthz(HttpServletRequest request, HttpServletResponse response) {
        log.info("Health check Api is called");

        // check if database connection is successful, if not, return error
        try {
            dataSource.getConnection();
            log.info("Successfully connected to MySQL database!");
        } catch (Exception e) {
            log.info("Error: Failed to connect to MySQL database!");
            throw new DbConnectionException();
        }

        // check if request has payload, if yes, return error
        if (request.getContentLength() > 0) {
            log.info("Error: Request includes payload!");
            throw new UnallowedPayloadException();
        }

        //check if the request includes query parameters, if yes, return error
        if (request.getParameterMap().size() > 0) {
            log.info("Error: Request includes parameters!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            return ResponseEntity.badRequest().build(); 
        }

        // check if query parameters are empty, if not, return error
        Map<String, String[]> params = request.getParameterMap();
        if (!params.isEmpty()) {
            // response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new IllegalArgumentException(); 
        }

        // return success response
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("X-Content-Type-Options", "nosniff");
        log.info("Health check Api is healthy");

        return ResponseEntity.ok().build();
    }

}
