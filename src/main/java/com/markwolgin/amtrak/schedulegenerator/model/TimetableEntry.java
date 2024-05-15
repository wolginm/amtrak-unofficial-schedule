package com.markwolgin.amtrak.schedulegenerator.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import com.markwolgin.amtrak.schedulegenerator.models.StopTimes;
import lombok.Data;

@Data
public class TimetableEntry {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");

    /**
     * All trips valid or the day.
     */
    private final Map<String, ConsolidatedTrip> possibleTrips;
    /**
     * The order the trips will appear horizontally.
     */
    private final TreeMap<LocalTime, String> tripOrder;
    private final boolean metaDirectionId;

    /**
     * We will assume that all trips needing filtering have been filtered.
     * @param possibleTrips     All valid trips.
     * @param completeTripList  The order of the trips on the day.
     * @param directionId       The direction of travel.
     * @param zeroIndex         The starting station in the zero direction.
     * @param oneIndex          The starting station in the one direction.
     */
    public TimetableEntry(final Map<String, ConsolidatedTrip> possibleTrips,
                          final List<String> completeTripList,
                          final boolean directionId,
                          final String zeroIndex,
                          final String oneIndex) {
        this.possibleTrips = possibleTrips;
        this.metaDirectionId = directionId;
        this.tripOrder = new TreeMap<>();

        for (ConsolidatedTrip consTrip: possibleTrips.values()) {
            String departureTime = "";
            for (StopTimes stop: consTrip.getTripStops()) {
                if ((!directionId && stop.getStopId().equals(zeroIndex)) || (directionId && stop.getStopId().equals(oneIndex))) {
                    departureTime = stop.getDepartureTime();
                    break;
                }
            }
            if (!directionId && departureTime.isEmpty()) {
                departureTime = consTrip.getTripStops().get(0).getDepartureTime();
            } else if (directionId && departureTime.isEmpty()) {
                departureTime = consTrip.getTripStops().get(consTrip.getTripStops().size() - 1).getDepartureTime();
            }
            this.tripOrder.put(LocalTime.parse(departureTime, formatter), consTrip.getTripId());
        }
    }

}
