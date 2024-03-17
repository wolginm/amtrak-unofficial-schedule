package com.markwolgin.amtrak.schedulegenerator.model;

import java.util.List;
import java.util.Map;

import com.markwolgin.amtrak.schedulegenerator.models.Stops;
import lombok.Data;

/**
 * @param scheduleName          The Colloquial Name of the Service.
 * @param routeId               Unique ID of the Service.
 * @param weekdayTripMap        The map of all trips that fall on weekdays, by direction.
 * @param saturdayTripMap       The map of all trips that fall on Saturday, by direction.
 * @param sundayTripMap         The map of all trips that fall on Sunday, by direction.
 * @param defaultStationOrder   Station Stop Order List.
 * @param mapOfAllPossibleStops Every stop that can appear in the service.
 */
@Data
public record TimetableFrame(String scheduleName, String routeId, Map<Boolean, TimetableEntry> weekdayTripMap,
                             Map<Boolean, TimetableEntry> saturdayTripMap, Map<Boolean, TimetableEntry> sundayTripMap,
                             List<String> defaultStationOrder, Map<String, Stops> mapOfAllPossibleStops) {

}
