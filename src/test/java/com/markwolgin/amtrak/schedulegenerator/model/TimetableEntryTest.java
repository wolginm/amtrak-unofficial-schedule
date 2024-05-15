package com.markwolgin.amtrak.schedulegenerator.model;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import com.markwolgin.amtrak.schedulegenerator.models.OperatingPattern;
import com.markwolgin.amtrak.schedulegenerator.models.StopTimes;
import com.sun.source.tree.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TimetableEntryTest {

    //TripId -> ConsolidatedTrip
    private Map<String, ConsolidatedTrip> tripMap;
    private final OperatingPattern weekday = new OperatingPattern().monday(true).tuesday(true).wednesday(true).thursday(true).friday(true).saturday(false).saturday(false);
    private final OperatingPattern weekend = new OperatingPattern().monday(false).tuesday(false).wednesday(false).thursday(false).friday(false).saturday(true).saturday(true);
    private final OperatingPattern allWeek = new OperatingPattern().monday(true).tuesday(true).wednesday(true).thursday(true).friday(true).saturday(true).saturday(true);
    private final String routeId = "R1";
    private final String serviceId = "S1";
    private final LocalDate effectiveDate = LocalDate.of(2000, 1, 1);
    private final LocalDate noLongerEffectiveDate = LocalDate.of(2999, 12, 31);
    @BeforeEach
    void setUp() {
        tripMap = new HashMap<>();
        tripMap.put("T001", new ConsolidatedTrip()
                .operatingPattern(allWeek)
                .directionId(0)
                .routeId(routeId)
                .serviceId(serviceId)
                .tripId("T001")
                .tripEffectiveOnDate(effectiveDate)
                .tripNoLongerEffectiveOnDate(noLongerEffectiveDate)
                .tripStops(List.of(
                        new StopTimes().stopId("AAA").departureTime("5:40:00").stopSequence(1),
                        new StopTimes().stopId("AAB").departureTime("5:55:00").stopSequence(2),
                        new StopTimes().stopId("AAC").departureTime("6:05:00").stopSequence(3),
                        new StopTimes().stopId("AAD").departureTime("6:20:00").stopSequence(4))));

        tripMap.put("T002", new ConsolidatedTrip()
                .operatingPattern(allWeek)
                .directionId(0)
                .routeId(routeId)
                .serviceId(serviceId)
                .tripId("T002")
                .tripEffectiveOnDate(effectiveDate)
                .tripNoLongerEffectiveOnDate(noLongerEffectiveDate)
                .tripStops(List.of(
                        new StopTimes().stopId("AAB").departureTime("5:58:00").stopSequence(1),
                        new StopTimes().stopId("AAC").departureTime("6:08:00").stopSequence(2),
                        new StopTimes().stopId("AAD").departureTime("6:23:00").stopSequence(3))));

        tripMap.put("T003", new ConsolidatedTrip()
                .operatingPattern(allWeek)
                .directionId(0)
                .routeId(routeId)
                .serviceId(serviceId)
                .tripId("T003")
                .tripEffectiveOnDate(effectiveDate)
                .tripNoLongerEffectiveOnDate(noLongerEffectiveDate)
                .tripStops(List.of(
                        new StopTimes().stopId("AAA").departureTime("5:50:00").stopSequence(1),
                        new StopTimes().stopId("AAC").departureTime("5:59:00").stopSequence(2),
                        new StopTimes().stopId("AAD").departureTime("6:15:00").stopSequence(3))));

            }

            @Test
        void testTripOrderSort() {
            TimetableEntry entry = new TimetableEntry(this.tripMap, null, false, "AAC", "AAD");
            TreeMap<LocalTime, String> tripOrder = entry.getTripOrder();
            Set<LocalTime> sortedTimes = tripOrder.navigableKeySet();
            Assertions.assertEquals(LocalTime.of(5, 59), sortedTimes.toArray()[0]);
            Assertions.assertEquals(LocalTime.of(6, 5), sortedTimes.toArray()[1]);
            Assertions.assertEquals(LocalTime.of(6, 8), sortedTimes.toArray()[2]);
        }
}