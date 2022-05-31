package com.wolginm.amtrak.schedulegenerator.view.text;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.PseudoText;
import com.wolginm.amtrak.schedulegenerator.model.Timetable;
import com.wolginm.amtrak.schedulegenerator.model.TimetableEntry;
import com.wolginm.amtrak.schedulegenerator.model.Train;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TextSchedule implements IViewSchedule {

    private final DateTimeFormatter timetable_entry_format 
        = DateTimeFormatter.ofPattern("HH:mm");
    private final String station_horizontial_format
        = "| %s | %s |";


    @Override
    public String buildSchedule(Timetable timetable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.buildWeekdaySchedule(timetable));
        stringBuilder.append(this.buildSaturdaySchedule(timetable));
        stringBuilder.append(this.buildSundaySchedule(timetable));
        return stringBuilder.toString();
        
    }

    @Override
    public String buildWeekdaySchedule(Timetable timetable) {
        LinkedHashMap<String, Train> trainListAsMap = timetable.reorderListToMapForTrain();
        List<String> trainOrder = trainListAsMap
            .keySet()
            .stream()
            .filter(train ->   trainListAsMap.get(train).getDirection() == 1
                            && trainListAsMap.get(train).isWeekday())
            .collect(Collectors.toList());

        return this.buildScheduleGeneral(timetable, trainOrder, trainListAsMap, "Weekday");
    }

    @Override
    public String buildSaturdaySchedule(Timetable timetable) {
        LinkedHashMap<String, Train> trainListAsMap = timetable.reorderListToMapForTrain();
        List<String> trainOrder = trainListAsMap
            .keySet()
            .stream()
            .filter(train ->   trainListAsMap.get(train).getDirection() == 1
                            && trainListAsMap.get(train).isSaturday())
            .collect(Collectors.toList());

        return this.buildScheduleGeneral(timetable, trainOrder, trainListAsMap, "Saturday");
    }

    @Override
    public String buildSundaySchedule(Timetable timetable) {
        LinkedHashMap<String, Train> trainListAsMap = timetable.reorderListToMapForTrain();
        List<String> trainOrder = trainListAsMap
            .keySet()
            .stream()
            .filter(train ->   trainListAsMap.get(train).getDirection() == 1
                            && trainListAsMap.get(train).isSunday())
            .collect(Collectors.toList());
        return this.buildScheduleGeneral(timetable, trainOrder, trainListAsMap, "Sunday");
    }

    private String buildScheduleGeneral(Timetable timetable, List<String> trainOrder, Map<String, Train> trainListAsMap, String... serviceType) {
        StringBuilder stringBuilder = new StringBuilder();
        LinkedHashMap<String, Train> trainListAsMapDupesRemoved = new LinkedHashMap<>();
        List<String> forRemoval = new ArrayList<>();
        for (String train : trainOrder) {
            if (!trainListAsMapDupesRemoved
                    .values()
                    .stream()
                    .map((value) -> value.equals(trainListAsMap.get(train)))
                    .reduce(false, (sum, cur) -> sum || cur)) {
                trainListAsMapDupesRemoved.put(train, trainListAsMap.get(train));
            } else {
                forRemoval.add(train);
            } 
        }
        trainOrder.removeAll(forRemoval);

        stringBuilder.append(this.buildHeader(timetable.getRouteName(), 
            timetable.getStartDate(), timetable.getEndDate(), serviceType));

        String stationInfoHorizontial = this.buildStationTrainInfoHorizontial(trainOrder);

        stringBuilder.append(stationInfoHorizontial);
        stringBuilder.append(String.format("| %s |%s  |\n", " ".repeat(7), "       ".repeat(trainOrder.size()))); 
        for (String station : timetable.getStationsStortedByDirection(0)) {
            stringBuilder.append(this.buildStationTrainHorizontial(station, trainOrder, trainListAsMapDupesRemoved));
        }

        log.info(stringBuilder.toString());
        return stringBuilder.toString();
    }

    private String buildStationTrainInfoHorizontial(List<String> trainOrder) {
        StringBuilder horizontialSlice = new StringBuilder("");

        horizontialSlice.append(String.format("| Station |  "));
        for (String trainNumber : trainOrder) {
            horizontialSlice.append(String.format("%5s  ", trainNumber.substring(5)));
        }

        horizontialSlice.append("|\n");

        return horizontialSlice.toString();
    }

    private String buildStationTrainHorizontial(String station, List<String> trainOrder,
            Map<String, Train> trainListAsMap) {
        StringBuilder horizontialSlice = new StringBuilder("");

        String selectedTime = null;
        horizontialSlice.append(String.format("| %7s |  ", station));
        for (String trainNumber : trainOrder) {
            
            if (trainListAsMap.get(trainNumber).getSchedule().containsKey(station)) {
                selectedTime = this.timetableEntry(trainListAsMap
                    .get(trainNumber)
                    .getSchedule()
                    .get(station));
            } else {
                selectedTime = "-".repeat(5);
            }
            horizontialSlice.append(String.format("%5s  ", selectedTime));
        }

        horizontialSlice.append("|\n");
        return horizontialSlice.toString();
    }

    @Override
    public String timetableEntry(TimetableEntry timetableEntry) {
        return timetableEntry.getDepartureTime().format(this.timetable_entry_format);
    }

    private String buildHeader(String routeName, Date startDate, Date endDate, String... serviceType) {
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
}
