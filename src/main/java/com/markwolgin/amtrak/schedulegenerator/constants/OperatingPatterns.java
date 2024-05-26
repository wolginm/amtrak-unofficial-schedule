package com.markwolgin.amtrak.schedulegenerator.constants;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import com.markwolgin.amtrak.schedulegenerator.models.OperatingPattern;

import java.util.ArrayList;
import java.util.List;

public class OperatingPatterns {

    private final List<ConsolidatedTrip> consolidatedTrips;

    public OperatingPatterns() {
        this.consolidatedTrips = new ArrayList<>();
    }

    public OperatingPattern getConsolidatedOperatingPattern() {
        OperatingPattern ops = new OperatingPattern();
        for (ConsolidatedTrip trip: this.consolidatedTrips) {
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
