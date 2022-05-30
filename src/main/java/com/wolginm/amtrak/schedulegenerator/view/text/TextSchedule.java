package com.wolginm.amtrak.schedulegenerator.view.text;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.wolginm.amtrak.schedulegenerator.model.Timetable;
import com.wolginm.amtrak.schedulegenerator.model.TimetableEntry;
import com.wolginm.amtrak.schedulegenerator.model.Train;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TextSchedule implements IViewSchedule {

    private final DateTimeFormatter timetable_entry_format 
        = DateTimeFormatter.ofPattern("hh:mm");
    private final String station_horizontial_format
        = "| %s | %s |";
    
    @Override
    public String buildSchedule(Timetable timetable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.buildHeader(timetable.getRouteName()));
        LinkedHashMap<String, Train> trainListAsMap = timetable.reorderListToMapForTrain();
        List<String> trainOrder = trainListAsMap.keySet().stream().filter(train -> trainListAsMap.get(train).isDirection()).collect(Collectors.toList());

        String stationInfoHorizontial = this.buildStationTrainInfoHorizontial(trainOrder);

        stringBuilder.append(stationInfoHorizontial);
        stringBuilder.append(String.format("| %s |%s  |\n", " ".repeat(7), "       ".repeat(trainOrder.size()))); 
        for (String station : timetable.getStations()) {
            stringBuilder.append(this.buildStationTrainHorizontial(station, trainOrder, trainListAsMap));
        }

        log.info(stringBuilder.toString());
        return null;
        
    }

    private String buildStationTrainInfoHorizontial(List<String> trainOrder) {
        StringBuilder horizontialSlice = new StringBuilder("");

        horizontialSlice.append(String.format("| Station |  "));
        for (String trainNumber : trainOrder) {
            horizontialSlice.append(String.format("%5s  ", trainNumber.substring(5)));
        }

        horizontialSlice.append("|\n");
        // log.info(horizontialSlice.toString());

        return horizontialSlice.toString();
    }

    private String buildStationTrainHorizontial(String station, List<String> trainOrder,
            LinkedHashMap<String, Train> trainListAsMap) {
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
        // log.info(horizontialSlice.toString());

        return horizontialSlice.toString();
    }

    @Override
    public String timetableEntry(TimetableEntry timetableEntry) {
        return timetableEntry.getDepartureTime().format(this.timetable_entry_format);
    }

    private String buildHeader(String routeName) {
        return routeName + "\n\n";
    }
}
