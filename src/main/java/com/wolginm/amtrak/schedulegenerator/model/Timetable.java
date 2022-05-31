package com.wolginm.amtrak.schedulegenerator.model;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Timetable {
    
    private String routeName;
    private Date startDate;
    private Date endDate;
    private Map<String, Map<Integer, Integer>> stations;
    private List<Train> services;

    public LinkedHashMap<String, Train> reorderListToMapForTrain() {
        LinkedHashMap<String, Train> linkedHashMap = new LinkedHashMap<>();

        for (Train train : services) {
            linkedHashMap.put(train.getTrainNumber(), train);
        }

        return linkedHashMap
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors
                .toMap( Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
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
