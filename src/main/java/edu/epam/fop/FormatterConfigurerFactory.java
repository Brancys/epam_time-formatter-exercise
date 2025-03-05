package edu.epam.fop;

import java.time.format.*;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;

public class FormatterConfigurerFactory {

  private FormatterConfigurerFactory() {
  }

  public static FormatterConfigurer slangBasedDate() {
    return builder -> builder
        .appendValue(ChronoField.DAY_OF_MONTH) 
        .appendLiteral(' ') 
        .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT) 
        .appendLiteral(" of ") 
        .appendValueReduced(ChronoField.YEAR, 2, 4, 1931) // 
        .toFormatter(Locale.ENGLISH); 
  }

  public static FormatterConfigurer politeScheduler() {
    return builder -> builder
        .appendLiteral("Scheduled on ")
        .appendText(ChronoField.DAY_OF_WEEK, TextStyle.FULL) // 
        .appendLiteral(" at ")
        .appendValue(ChronoField.CLOCK_HOUR_OF_AMPM) // 
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2) // 
        .appendLiteral(' ')
        .optionalStart()
        .appendText(ChronoField.AMPM_OF_DAY, Map.of(
            0L, "in the morning", // 
            1L, "in the afternoon" // 
        ))
        .optionalEnd()
        .optionalStart()
        .appendLiteral(" by ")
        .appendZoneText(TextStyle.FULL) // 
        .optionalEnd()
        .toFormatter(Locale.ENGLISH);
  }

  public static FormatterConfigurer scientificTime() {
    return builder -> builder
        .appendValue(ChronoField.HOUR_OF_DAY, 2) 
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .appendLiteral('.')
        .appendFraction(ChronoField.MICRO_OF_SECOND, 1, 6, false) 
        .toFormatter(Locale.ENGLISH);
  }

  public static FormatterConfigurer historicalCalendar() {
    return builder -> builder
        .appendValue(ChronoField.YEAR_OF_ERA)
        .appendLiteral(" of ")
        .appendText(ChronoField.ERA, TextStyle.FULL)
        .appendLiteral(" (")
        .appendChronologyText(TextStyle.FULL)
        .appendLiteral(")")
        .toFormatter(Locale.ENGLISH); // 
  }

}
