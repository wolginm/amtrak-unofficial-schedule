package com.markwolgin.amtrak.schedulegenerator.view.text;

import com.markwolgin.amtrak.schedulegenerator.model.TimetableFrame;
import com.markwolgin.amtrak.schedulegenerator.model.TimetableEntry;

public interface IViewSchedule {
    
    String buildSchedule(TimetableFrame timetable);

    String buildWeekdaySchedule(TimetableFrame timetable, Boolean direction);

    String buildSaturdaySchedule(TimetableFrame timetable, Boolean direction);
    String buildSundaySchedule(TimetableFrame timetable, Boolean direction);

    String timetableEntry(TimetableEntry timetableEntry);
}
