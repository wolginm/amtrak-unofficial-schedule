package com.markwolgin.amtrak.schedulegenerator.view.text;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.PseudoText;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableFrame;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableEntry;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import com.markwolgin.amtrak.schedulegenerator.models.StopTimes;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TextSchedule implements IViewSchedule {

    private final DateTimeFormatter timetable_entry_format 
        = DateTimeFormatter.ofPattern("HH:mm");
    private final String station_horizontial_format
        = "| %s | %s |";

    private final int DEFAULT_MAX_LENGTH = 8;
    private final int DEFAULT_BAR_LENGTH = DEFAULT_MAX_LENGTH + 4;

    /**
     * Builds the general schedule, Weekday, Saturday, and Sunday; in both directions.
     * @param timetable {@link TimetableFrame} holding all {@link TimetableEntry}s.
     * @return          The composite schedule.
     */
    @Override
    public String buildSchedule(TimetableFrame timetable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.buildWeekdaySchedule(timetable, false));
        stringBuilder.append(this.buildWeekdaySchedule(timetable, true));
        stringBuilder.append(this.buildSaturdaySchedule(timetable, false));
        stringBuilder.append(this.buildSaturdaySchedule(timetable, true));
        stringBuilder.append(this.buildSundaySchedule(timetable, false));
        stringBuilder.append(this.buildSundaySchedule(timetable, true));
        return stringBuilder.toString();
        
    }

    @Override
    public String buildWeekdaySchedule(TimetableFrame timetable, final Boolean direction) {
        Map<String, ConsolidatedTrip> trainListAsMap;
        TimetableEntry entry = timetable.getWeekdayTripMap().get(direction);

        trainListAsMap = entry.getPossibleTrips();
        List<String> trainOrder = entry.getTripOrder().navigableKeySet().stream()
                .map(key -> entry.getTripOrder().get(key)).toList();

        return this.buildScheduleGeneral(timetable, trainOrder, trainListAsMap,
                "Weekday %s".formatted(direction ? "North/East" : "South/West"), direction);
    }

    @Override
    public String buildSaturdaySchedule(TimetableFrame timetable, final Boolean direction) {
//        LinkedHashMap<String, ConsolidatedTrip> trainListAsMap = timetable.reorderListToMapForTrain();
//        List<String> trainOrder = trainListAsMap
//            .keySet()
//            .stream()
//            .filter(train ->   trainListAsMap.get(train).getDirectionId() == 1
//                            && trainListAsMap.get(train).getOperatingPattern().getSaturday())
//            .collect(Collectors.toList());

//        return this.buildScheduleGeneral(timetable, trainOrder, trainListAsMap, "Saturday");
        return "";
    }

    @Override
    public String buildSundaySchedule(TimetableFrame timetable, final Boolean direction) {
//        LinkedHashMap<String, ConsolidatedTrip> trainListAsMap = timetable.reorderListToMapForTrain();
//        List<String> trainOrder = trainListAsMap
//            .keySet()
//            .stream()
//            .filter(train ->   trainListAsMap.get(train).getDirectionId() == 1
//                            && trainListAsMap.get(train).getOperatingPattern().getSunday())
//            .collect(Collectors.toList());
//        return this.buildScheduleGeneral(timetable, trainOrder, trainListAsMap, "Sunday");
        return "";
    }

    @Override
    public String timetableEntry(TimetableEntry timetableEntry) {
//        return timetableEntry.get().format(this.timetable_entry_format);
        return "";
    }

    /**
     * Builds a 'Title card' for the text based report.<br>
     * Like the one below,<br>
     * <pre>
     *  █   █                     █                              ███
     *  █  █                      █                             █   █
     *  █ █    ███  █   █   ████ ████   ███  █ ██   ███         █      ███   ████ █   █   ██    ███   ███
     *  ██    █   █  █ █   █      █    █   █ ██  █ █   █         ███  █   █  █  █  █ █     █   █     █   █
     *  █ █   █████  █ █    ████  █    █   █ █   █ █████            █ █████  █     █ █     █   █     █████
     *  █  █  █       █        █  █    █   █ █   █ █            █   █ █      █     █ █     █   █     █
     *  █   █  ████   █    ████   ███   ███  █   █  ████         ███   ████  █      █    █████  ███   ████
     *                █
     *              ██
     *
     * Service Announcements!
     * Starting: 2024-04-05
     *   Ending: 2024-04-05
     * </pre>
     * @param routeName     The Common Name of the Service.
     * @param startDate     Effective start date of the schedule.
     * @param endDate       Last day when this data will be accurate.
     * @param serviceType   Details about the service.
     * @return              The pre-formatted string, to be written or printed.
     */
    private String buildTitleCard(final String routeName,
                                  final LocalDate startDate,
                                  final LocalDate endDate,
                                  final String... serviceType) {
        IRender render = new Render();
		IContextBuilder builder = render.newBuilder();
		builder.width(routeName.length()*30).height(12);
		builder.element(new PseudoText(routeName, false));
		ICanvas canvas = render.render(builder.build());

        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append(canvas.getText());
        if (serviceType != null && serviceType.length == 1) {
            stringBuilder.append(String.format("\n\n%s\n", serviceType[0]));
        } else {
            stringBuilder.append("\n\n");
        }
        stringBuilder.append(String.format("Starting: %s\n  Ending: %s\n", startDate, endDate));

        return stringBuilder.toString();
    }

    private String buildHeader(TimetableEntry timetableEntry,
                               final Boolean direction) {
        StringBuilder header = new StringBuilder("");
        String trainBar = new String("");
        ConsolidatedTrip consolidatedTrip;
        NavigableSet<LocalTime> navigableSet = timetableEntry.getTripOrder().navigableKeySet();

        AtomicInteger maxLength = new AtomicInteger(DEFAULT_MAX_LENGTH);
        timetableEntry.getPossibleTrips().values().stream().forEach(trip -> {
            if (Long.toString(trip.getTripShortName()).length() > maxLength.get()) {
                maxLength.set(Long.toString(trip.getTripShortName()).length());
            }
        });


        int barLength = DEFAULT_BAR_LENGTH;
        String nextElement;
        trainBar = trainBar.concat("| STATION  |");
        // Map of Trip Time -> Trip
        for (LocalTime localTime : navigableSet) {

            consolidatedTrip = timetableEntry.getPossibleTrips().get(timetableEntry.getTripOrder().get(localTime));
            nextElement = (" %-" + maxLength.get() + "s |")
                    .formatted(Long.toString(consolidatedTrip.getTripShortName()));
            trainBar = trainBar.concat(nextElement);
            barLength += nextElement.length();
        }

        header.append("/");
        header.append("=".repeat(barLength-2));
        header.append("\\\n");
        header.append(trainBar);
        header.append("\n");
        header.append("*");
        header.append("-".repeat(barLength-2));
        header.append("*\n");
        return header.toString();
    }

    /**
     * Builds the 'meat and potatoes' of the schedule.  It makes a number of assumptions.<br>
     * 1) We know, or can derive the spacing for each bar.<br>
     * 2) There is a set of stations used, any train as a part of the {@link TimetableEntry} will stop
     *  some or all of the stops.<br>
     * 3) There is a default ordering, by departure time.
     *
     * @param timetableEntry    The {@link TimetableEntry} to build out.
     * @param stationOrder      The order the stations will be listed in.
     * @param direction         The direction the service is oppertating in.
     * @return                  The formatted schedule.
     */
    private String buildScheduleTimeEntries(final TimetableEntry timetableEntry,
                                            final List<String> stationOrder,
                                            final Boolean direction) {
        String aggregate = "";
        StringBuilder current;
        Map<String, StopTimes> stopMap;
        int barLength = DEFAULT_BAR_LENGTH;
        ConsolidatedTrip consolidatedTrip;

        // Flip station order in list (always why?).
            log.debug("Flipping station order.");
            Collections.reverse(stationOrder);


        NavigableSet<LocalTime> navigableSet = timetableEntry.getTripOrder().navigableKeySet();
        Map<String, StringBuilder> rows = new LinkedHashMap<>(timetableEntry.getPossibleTrips().size());

        AtomicInteger maxLength = new AtomicInteger(DEFAULT_MAX_LENGTH);
        timetableEntry.getPossibleTrips().values().stream().forEach(trip -> {
            if (Long.toString(trip.getTripShortName()).length() > maxLength.get()) {
                maxLength.set(Long.toString(trip.getTripShortName()).length());
            }
        });

        for (String station: stationOrder) {
            rows.put(station, new StringBuilder(String.format("| %-" + maxLength.get() + "s |", station.toUpperCase())));
        }

        for (LocalTime localTime : navigableSet) {
            consolidatedTrip = timetableEntry.getPossibleTrips().get(timetableEntry.getTripOrder().get(localTime));
            stopMap = consolidatedTrip.getTripStops().stream().collect(Collectors.toMap(StopTimes::getStopId, s->s));
            String nextTime;

            for (String station: stationOrder) {
                current = rows.get(station);
                nextTime = stopMap.containsKey(station) ? stopMap.get(station).getDepartureTime() : "-".repeat(maxLength.get());
                current.append((" %-" + maxLength.get() + "s |").formatted(nextTime));
            }
        }

        for (String station: stationOrder) {
            aggregate = aggregate.concat(rows.get(station).toString());
            aggregate = aggregate.concat("\n");
            barLength = rows.get(station).toString().length();
        }
        aggregate = aggregate.concat("\\");
        aggregate = aggregate.concat("=".repeat(barLength-2));
        aggregate = aggregate.concat("/\n");
        return aggregate;
    }

    /**
     * A general way to build a schedule for text.
     * @param timetable         {@link TimetableFrame} entire frame object.
     * @param trainOrder        The order the trains will depart in.
     * @param trainListAsMap    Map of service number to the trip.
     * @param announcements     Any text/metadata wanted to be conveyed to the user.
     * @param direction         The direction of travel.
     * @return                  The specific, with title card and rest.
     */
    private String buildScheduleGeneral(final TimetableFrame timetable,
                                        final List<String> trainOrder,
                                        final Map<String, ConsolidatedTrip> trainListAsMap,
                                        final String announcements,
                                        final boolean direction) {

        StringBuilder canvas = new StringBuilder();

        canvas.append(this.buildTitleCard(timetable.getScheduleName(), LocalDate.now(), LocalDate.now(), announcements));
        canvas.append(this.buildHeader(timetable.getWeekdayTripMap().get(direction), false));
        canvas.append(this.buildScheduleTimeEntries(timetable.getWeekdayTripMap().get(direction), timetable.getDefaultStationOrder(), direction));;

        return canvas.toString();
    }
}
