package com.markwolgin.amtrak.schedulegenerator.model;

import java.sql.Date;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedTrip;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Timetable {
    
    private String routeName;
    private Date startDate;
    private Date endDate;
    private Map<String, Map<Integer, Integer>> stations;
    private List<ConsolidatedTrip> services;

    public LinkedHashMap<String, ConsolidatedTrip> reorderListToMapForTrain() {
        LinkedHashMap<String, ConsolidatedTrip> linkedHashMap = new LinkedHashMap<>();

        for (ConsolidatedTrip train : services) {
            linkedHashMap.put(String.valueOf(train.getTripShortName()), train);
        }

        return linkedHashMap;
    }

    public List<String> getStationsStortedByDirection(Integer direction) {
        Map<String, Integer> temporaryArray = new LinkedHashMap<>();
        List<String> reverseList;
        Integer swap;
        for (String station : stations.keySet()) {
            swap = stations.get(station).get(direction);
            temporaryArray.put(station, swap == null ? -1 : swap);
        }
        
        temporaryArray = temporaryArray.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

                reverseList = temporaryArray.keySet().stream().collect(Collectors.toList());
        Collections.reverse(reverseList);

        return reverseList;
    }

}
