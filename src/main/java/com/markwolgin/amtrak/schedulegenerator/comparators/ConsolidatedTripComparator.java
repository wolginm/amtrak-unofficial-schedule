package com.markwolgin.amtrak.schedulegenerator.comparators;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import com.markwolgin.amtrak.schedulegenerator.models.StopTimes;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;

public class ConsolidatedTripComparator implements Comparator<ConsolidatedTrip> {
    /**
     * @param alpha First trip to check.
     * @param beta  Second trip to check.
     * @return 0 if the same, -1 otherwise.
     */
    @Override
    public int compare(ConsolidatedTrip alpha, ConsolidatedTrip beta) {
        HashSet<StopTimes> alphaConsTripStopTimes = new HashSet<>(alpha.getTripStops());
        boolean areAllStopsTheSame = alphaConsTripStopTimes.containsAll(beta.getTripStops());
        boolean sameOperatingDirection = Objects.equals(alpha.getDirectionId(), beta.getDirectionId());
        boolean sameTrainRoute = Objects.equals(alpha.getRouteId(), beta.getRouteId());

        return areAllStopsTheSame && sameTrainRoute && sameOperatingDirection ? 0 : -1;
    }
}
