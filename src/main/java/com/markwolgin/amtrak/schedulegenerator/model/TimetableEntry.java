package com.markwolgin.amtrak.schedulegenerator.model;

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

    @Override
    public boolean equals(Object o) {
        TimetableEntry other = (TimetableEntry) o;

        return o == null ? false 
            :  this.getArrivalTime().compareTo(other.getArrivalTime()) == 0
            && this.getDepartureTime().compareTo(other.getDepartureTime()) == 0
            && this.getStopId().equalsIgnoreCase(other.getStopId())
            && this.getStopName().equalsIgnoreCase(other.getStopName())
            && this.getStopOrder() == other.getStopOrder();
    }
}
