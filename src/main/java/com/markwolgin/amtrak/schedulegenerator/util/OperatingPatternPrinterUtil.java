package com.markwolgin.amtrak.schedulegenerator.util;

import com.markwolgin.amtrak.schedulegenerator.models.OperatingPattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class OperatingPatternPrinterUtil {

    private static final String DASH = "--";

    public static String generateSmallOperatingPattern(OperatingPattern operatingPattern) {
        return ("%s %s %s %s %s %s %s").formatted(
                operatingPattern.getMonday() ? "Mo" : DASH,
                operatingPattern.getTuesday() ? "Tu" : DASH,
                operatingPattern.getWednesday() ? "We" : DASH,
                operatingPattern.getThursday() ? "Th" : DASH,
                operatingPattern.getFriday() ? "Fr" : DASH,
                operatingPattern.getSaturday() ? "Sa" : DASH,
                operatingPattern.getSunday() ? "Su" : DASH
        );
    }

}
