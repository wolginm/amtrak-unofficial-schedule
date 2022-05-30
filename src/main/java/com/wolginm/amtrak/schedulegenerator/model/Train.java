package com.wolginm.amtrak.schedulegenerator.model;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Train implements Comparable {
    
    private boolean direction;
    private Map<String, TimetableEntry> schedule;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String trainNumber;

    @Override
    public int compareTo(Object o) {
        Train other = (Train) o;

        return this.getDepartureTime().compareTo(other.getDepartureTime());
    }


}
