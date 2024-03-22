package com.markwolgin.amtrak.schedulegenerator.view.text;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.PseudoText;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableFrame;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableEntry;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
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
    public String buildSchedule(TimetableFrame timetable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.buildWeekdaySchedule(timetable));
        stringBuilder.append(this.buildSaturdaySchedule(timetable));
        stringBuilder.append(this.buildSundaySchedule(timetable));
        return stringBuilder.toString();
        
    }

    @Override
    public String buildWeekdaySchedule(TimetableFrame timetable) {
//        LinkedHashMap<String, ConsolidatedTrip> trainListAsMap = timetable.();
//        List<String> trainOrder = trainListAsMap
//            .keySet()
//            .stream()
//            .filter(train ->   trainListAsMap.get(train).getDirectionId() == 1
//                            && (
//                                    trainListAsMap.get(train).getOperatingPattern().getMonday() ||
//                                    trainListAsMap.get(train).getOperatingPattern().getTuesday() ||
//                                    trainListAsMap.get(train).getOperatingPattern().getWednesday() ||
//                                    trainListAsMap.get(train).getOperatingPattern().getThursday() ||
//                                    trainListAsMap.get(train).getOperatingPattern().getFriday()))
//            .collect(Collectors.toList());

//        return this.buildScheduleGeneral(timetable, trainOrder, trainListAsMap, "Weekday");
        return "";
    }

    @Override
    public String buildSaturdaySchedule(TimetableFrame timetable) {
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
    public String buildSundaySchedule(TimetableFrame timetable) {
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
