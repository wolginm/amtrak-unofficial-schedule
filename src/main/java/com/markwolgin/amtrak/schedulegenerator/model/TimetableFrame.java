package com.markwolgin.amtrak.schedulegenerator.model;

import java.util.List;
import java.util.Map;

import com.markwolgin.amtrak.schedulegenerator.models.Stops;
import lombok.Data;

@Data
public class TimetableFrame {

    /**
     * The Colloquial Name of the Service.
     */
    private final String scheduleName;
    /**
     * Unique ID of the Service.
     */
    private final String routeId;
    /**
     * The map of all trips that fall on weekdays, by direction.
     */
    private final Map<Boolean, TimetableEntry> weekdayTripMap;
    /**
     * The map of all trips that fall on Saturday, by direction.
     */
    private final Map<Boolean, TimetableEntry> saturdayTripMap;
    /**
     * The map of all trips that fall on Sunday, by direction.
     */
    private final Map<Boolean, TimetableEntry> sundayTripMap;
    /**
     * Station Stop Order List.
     */
    private final List<String> defaultStationOrder;
    /**
     * Every stop that can appear in the service.
     */
    private final Map<String, Stops> mapOfAllPossibleStops;

}
