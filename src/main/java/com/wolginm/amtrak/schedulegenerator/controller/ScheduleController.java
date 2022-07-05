package com.wolginm.amtrak.schedulegenerator.controller;

import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wolginm.amtrak.schedulegenerator.service.ScheduleGeneratorService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/api/schedule")
public class ScheduleController {

    private ScheduleGeneratorService scheduleGeneratorService;

    public ScheduleController(ScheduleGeneratorService scheduleGeneratorService) {
        this.scheduleGeneratorService = scheduleGeneratorService;
    }

    @GetMapping("keystone")
    public ResponseEntity<String> getKeystone() throws JsonProcessingException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        URI uri = new URI("http://localhost:8080");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Status", "200");
        headers.set("Content-Type", "application/json");

        return ResponseEntity
            .created(uri)
            .headers(headers)
            .body(objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(this.scheduleGeneratorService.getRoute(94)));
    }

}
