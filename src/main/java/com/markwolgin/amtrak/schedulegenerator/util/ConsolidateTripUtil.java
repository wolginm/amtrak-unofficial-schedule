package com.markwolgin.amtrak.schedulegenerator.util;

import com.markwolgin.amtrak.schedulegenerator.model.sets.Pair;
import com.markwolgin.amtrak.schedulegenerator.model.sets.Range;
import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import com.markwolgin.amtrak.schedulegenerator.models.OperatingPattern;
import com.markwolgin.amtrak.schedulegenerator.models.StopTimes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * To consolidate {@link com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip}s that are similar in form.
 */
@Slf4j
@Component
public class ConsolidateTripUtil {

    //TODO NEED TO UNIT TEST THE HOLY HELL OUT OF THIS MESSY MESS!  CHECK NOTABILITY NOTES FROM JUN 10 FOR DETAILS ON WHAT WAS
    //  IN THIS CRACKED HEAD!!

    /**
     * Will aggregate all trips into the fewest common sets.
     * Assumes that only one type of route is selected.
     * @param consolidatedTripList  All trips in a route.
     * @return                      Map of
     */
    public List<ConsolidatedTrip> aggregateSimilarConsolidatedTrips(final List<ConsolidatedTrip> consolidatedTripList) {

        Map<Integer, List<ConsolidatedTrip>> aggregatedTripListByCustomHash = buildTripsListByHash(consolidatedTripList);

        List<ConsolidatedTrip> aggregate = new LinkedList<>();
        for (Map.Entry<Integer, List<ConsolidatedTrip>> entry: aggregatedTripListByCustomHash.entrySet()) {
            aggregate.addAll(this.createSuperConsolidatedTrip(entry.getValue()));
        }

        return aggregate;
    }

    /**
     * Will reduce the list to the smallest number of compliant entries.
     * @param collapsable   A list of trips with the same stops and direction.
     * @return              A list of size [0, n] | n is the size of {@code collapsable}.
     */
    protected List<ConsolidatedTrip> createSuperConsolidatedTrip(List<ConsolidatedTrip> collapsable) {
        Map<ConsolidatedTrip, Range<LocalDate>> timeRanges = new HashMap<>(collapsable.size());

        boolean domainCheck;
        int lower, upper;

        LocalDate offsetDate;
        Range<LocalDate> range;
        boolean needsANewTrip;

        Collections.sort(collapsable, new Comparator<ConsolidatedTrip>() {
            @Override
            public int compare(ConsolidatedTrip consolidatedTrip, ConsolidatedTrip t1) {
                if (consolidatedTrip.getTripEffectiveOnDate().isBefore(t1.getTripEffectiveOnDate())) {
                    return -1;
                } else if (consolidatedTrip.getTripEffectiveOnDate().isAfter(t1.getTripEffectiveOnDate())) {
                    return 1;
                }
                return 0;
            }
        });

        for (ConsolidatedTrip trip: collapsable) {
            // Will run once!
            if (timeRanges.isEmpty()) timeRanges.put(trip,
                    new Range<>(trip.getTripEffectiveOnDate(), trip.getTripNoLongerEffectiveOnDate()));
            range = timeRanges.get(trip);
            // Every other time.
            lower = upper = 0;
            needsANewTrip = false;

            for (Map.Entry<ConsolidatedTrip, Range<LocalDate>> entry: timeRanges.entrySet()) {
                lower = entry.getValue().inDomain(trip.getTripEffectiveOnDate());
                upper = entry.getValue().inDomain(trip.getTripNoLongerEffectiveOnDate());
                domainCheck = lower >= 0 && upper <= 0;

                //todo - need to add operating pattern to the check.
                if (domainCheck) {
                    needsANewTrip = true;
                }

                else if (!domainCheck) {
                    offsetDate = trip.getTripEffectiveOnDate().minusDays(7);

                    if (offsetDate.isEqual(entry.getValue().getSecond())
                            || offsetDate.isBefore(entry.getValue().getSecond())) {


                        //Todo: Maybe find a way to place a calander date on a calenader, apply the opperating pattern,
                        // and see if any days inbetween will be missed.  If so, add new trip.
//                        long dayDelta = ChronoUnit.DAYS.between(trip.getTripNoLongerEffectiveOnDate(), entry.getValue().getFirst());
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.set(trip.getTripNoLongerEffectiveOnDate().getYear(),
//                                trip.getTripNoLongerEffectiveOnDate().getMonthValue(), trip.getTripNoLongerEffectiveOnDate().getDayOfMonth());

                        entry.getValue().setSecond(trip.getTripNoLongerEffectiveOnDate());
                    } else {
                        needsANewTrip = true;
                    }
                }
            }
            if (needsANewTrip) {
                timeRanges.put(trip, new Range<>(trip.getTripEffectiveOnDate(), trip.getTripNoLongerEffectiveOnDate()));
            }
        }

        return timeRanges.entrySet().stream().map(entry -> {
            entry.getKey().setTripEffectiveOnDate(entry.getValue().getFirst());
            entry.getKey().setTripNoLongerEffectiveOnDate(entry.getValue().getSecond());
            entry.getKey().setTripId(String.valueOf(entry.getKey().hashCode()).substring(0, 6));
            return entry.getKey();
        }).toList();
    }

    /**
     * Will take a list of trips and sort them into a map by the custom hash
     * {@link ConsolidateTripUtil#calculateCustomHashCode(ConsolidatedTrip)}
     * @param consolidatedTripList  Input trips.
     * @return                      Map of all similar trips.
     */
    protected Map<Integer, List<ConsolidatedTrip>> buildTripsListByHash(List<ConsolidatedTrip> consolidatedTripList) {
        Map<Integer, List<ConsolidatedTrip>> aggregatedTripListByCustomHash = new TreeMap<>();
        Integer swapHash;
        for (ConsolidatedTrip tripOfTheDay: consolidatedTripList) {
            swapHash = this.calculateCustomHashCode(tripOfTheDay);
            if (!aggregatedTripListByCustomHash.containsKey(swapHash)) {
                aggregatedTripListByCustomHash.put(swapHash, new LinkedList<>(List.of(tripOfTheDay)));
            } else {
                aggregatedTripListByCustomHash.get(swapHash).add(tripOfTheDay);
            }
        }
        return aggregatedTripListByCustomHash;
    }

    protected Integer calculateCustomHashCode(final ConsolidatedTrip consolidatedTrip) {
        Integer integerHashCode = 0;
        for (StopTimes stopTimes: consolidatedTrip.getTripStops()) {
            integerHashCode += stopTimes.getStopId().hashCode() + stopTimes.getDepartureTime().hashCode()
                    + stopTimes.getArrivalTime().hashCode() + stopTimes.getStopSequence().hashCode()
                    + stopTimes.getPickupType() + stopTimes.getDropOffType();
        }

        return Objects.hash(consolidatedTrip.getDirectionId(), consolidatedTrip.getRouteId(), integerHashCode);
    }

     /**
     * Returns an operating pattern of all the similar trips.
     *  For example, if we had two substantively similar trips, minus metadata, we
     *  will merge them and treat them as the same trip with a more expansive patter
     *  for the purposes of data visualization.
     * @return An aggregate of all operating patterns.
     */
     protected OperatingPattern reduceOpertatingPattern(List<ConsolidatedTrip> consolidatedTripSubList) {
        OperatingPattern ops = new OperatingPattern().monday(false).tuesday(false).wednesday(false)
                .thursday(false).friday(false).saturday(false).sunday(false);
        for (ConsolidatedTrip trip: consolidatedTripSubList) {
            ops.setMonday(ops.getMonday() || trip.getOperatingPattern().getMonday());
            ops.setTuesday(ops.getTuesday() || trip.getOperatingPattern().getTuesday());
            ops.setWednesday(ops.getWednesday() || trip.getOperatingPattern().getWednesday());
            ops.setThursday(ops.getThursday() || trip.getOperatingPattern().getThursday());
            ops.setFriday(ops.getFriday() || trip.getOperatingPattern().getFriday());
            ops.setSaturday(ops.getSaturday() || trip.getOperatingPattern().getSaturday());
            ops.setSunday(ops.getSunday() || trip.getOperatingPattern().getSunday());
        }
        return ops;
    }



}
