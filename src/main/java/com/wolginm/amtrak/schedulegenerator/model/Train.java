package com.wolginm.amtrak.schedulegenerator.model;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class Train implements Comparable<Train> {
    
    private int direction;
    private boolean weekday;
    private boolean saturday;
    private boolean sunday;
    private Map<String, TimetableEntry> schedule;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Date dayScheduleStarts;
    private Date dayScheduleEnds;
    private String trainNumber;

    @Override
    public int compareTo(Train o) {
        Train other = o;

        return this.getDepartureTime().compareTo(other.getDepartureTime());
    }

    @Override
    public String toString() {
        return String.format("Train: %s / %s", trainNumber, direction);
    }

    @Override
    public boolean equals(@NonNull Object o) {
        Train other = (Train) o;

        boolean compareMaps = true;
        try {
            for (String station : schedule.keySet()) {
                compareMaps &= this.schedule.get(station).equals(other.getSchedule().get(station));
            }
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            compareMaps = false;
        }

        return this.direction == other.direction
            && this.weekday == other.weekday
            && this.saturday == other.saturday
            && this.sunday == other.sunday
            && compareMaps;
    }
}
