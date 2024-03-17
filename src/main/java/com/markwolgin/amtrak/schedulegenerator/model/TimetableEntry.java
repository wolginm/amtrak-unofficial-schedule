package com.markwolgin.amtrak.schedulegenerator.model;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import lombok.Data;

@Data
public class TimetableEntry {

    /**
     * All trips valid or the day.
     */
    private final Map<String, ConsolidatedTrip> possibleTrips;
    /**
     * The order the trips will appear horizontally.
     */
    private final Map<LocalTime, String> tripOrder;
    private final boolean metaDirectionId;

    /**
     * We will assume that all trips needing filtering have been filtered.
     * @param possibleTrips     All valid trips.
     * @param completeTripList  The order of the trips on the day.
     * @param directionId       The direction of travel.
     */
    public TimetableEntry(final Map<String, ConsolidatedTrip> possibleTrips,
                          final List<String> completeTripList,
                          final boolean directionId) {
        this.possibleTrips = possibleTrips;
        this.metaDirectionId = directionId;
        this.tripOrder = new TreeMap<>();

        for (ConsolidatedTrip consTrip: possibleTrips.values()) {
            this.tripOrder.put(LocalTime.parse(consTrip.getTripStops().get(0).getDepartureTime()), consTrip.getTripId());
        }
    }

}
