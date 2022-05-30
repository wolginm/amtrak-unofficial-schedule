package com.wolginm.amtrak.schedulegenerator.model;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TimetableEntry {
    
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private String stopId;
    private String stopName;
    private int stopOrder;
}
