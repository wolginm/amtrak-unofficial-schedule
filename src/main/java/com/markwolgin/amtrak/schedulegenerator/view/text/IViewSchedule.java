package com.markwolgin.amtrak.schedulegenerator.view.text;

import com.markwolgin.amtrak.schedulegenerator.model.TimetableFrame;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableEntry;

public interface IViewSchedule {
    
    String buildSchedule(TimetableFrame timetable);
    String buildWeekdaySchedule(TimetableFrame timetable);
    String buildSaturdaySchedule(TimetableFrame timetable);
    String buildSundaySchedule(TimetableFrame timetable);

    String timetableEntry(TimetableEntry timetableEntry);
}
