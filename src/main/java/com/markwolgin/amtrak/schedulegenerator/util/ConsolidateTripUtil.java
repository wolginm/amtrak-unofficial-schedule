package com.markwolgin.amtrak.schedulegenerator.util;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import com.markwolgin.amtrak.schedulegenerator.models.OperatingPattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * To consolidate {@link com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip}s that are similar in form.
 */
@Slf4j
@Component
public class ConsolidateTripUtil {

    public List<ConsolidatedTrip> aggregateSimilarConsolidatedTrips(final List<ConsolidatedTrip> consolidatedTripList) {

        Map<Integer, List<ConsolidatedTrip>> sortedConsolidatedTripsByHash = this.sortConsolidatedTripsByHash(consolidatedTripList);

        return null;
    }

    protected Map<Integer, List<ConsolidatedTrip>> sortConsolidatedTripsByHash(List<ConsolidatedTrip> consolidatedTripList) {
        return null;
    }

     /**
     * Returns an operating pattern of all the similar trips.
     *  For example, if we had two substantively similar trips, minus metadata, we
     *  will merge them and treat them as the same trip with a more expansive patter
     *  for the purposes of data visualization.
     * @return An aggregate of all operating patterns.
     */
     protected OperatingPattern reduceOpertatingPattern(List<ConsolidatedTrip> consolidatedTripSubList) {
        OperatingPattern ops = new OperatingPattern();
        for (ConsolidatedTrip trip: consolidatedTripSubList) {
            ops.setMonday(ops.getMonday() || trip.getOperatingPattern().getMonday());
            ops.setMonday(ops.getTuesday() || trip.getOperatingPattern().getTuesday());
            ops.setMonday(ops.getWednesday() || trip.getOperatingPattern().getWednesday());
            ops.setMonday(ops.getThursday() || trip.getOperatingPattern().getThursday());
            ops.setMonday(ops.getFriday() || trip.getOperatingPattern().getFriday());
            ops.setMonday(ops.getSaturday() || trip.getOperatingPattern().getSaturday());
            ops.setMonday(ops.getSunday() || trip.getOperatingPattern().getSunday());
        }
        return ops;
    }

}
