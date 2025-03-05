package edu.epam.fop;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Test cases for Java Date-Time formatting")
public class FormatterConfigurerTest {

  @MethodSource
  @DisplayName("Formatting tests")
  @ParameterizedTest(name = "For \"{0}\" configurer {2} must produce \"{3}\"")
  void testFormatting(String name, FormatterConfigurer configurer, TemporalAccessor accessor, String expected) {
    DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
    configurer.configure(builder);
    DateTimeFormatter formatter = builder.toFormatter();
    String actual = assertDoesNotThrow(() -> formatter.format(accessor));
    assertEquals(expected, actual);
  }

  static Stream<Arguments> testFormatting() {
    return Stream.of(
        new TestCase("Slang Based Date", FormatterConfigurerFactory.slangBasedDate())
            .add(date(1990, Month.JUNE, 22), "22 Jun of 90")
            .add(date(1985, Month.JANUARY, 27), "27 Jan of 85")
            .add(date(2007, Month.APRIL, 1), "1 Apr of 07")
            .add(date(1925, Month.NOVEMBER, 27), "27 Nov of 1925")
            .add(date(1930, Month.MARCH, 31), "31 Mar of 1930")
            .add(date(1931, Month.SEPTEMBER, 2), "2 Sep of 31")
            .add(date(2025, Month.DECEMBER, 31), "31 Dec of 25")
            .build(),
        new TestCase("Polite Scheduler", FormatterConfigurerFactory.politeScheduler())
            .add("Asia/Aqtau", FormatterConfigurerTest::isAvailableZone,
                zone -> zoned(date(2022, Month.DECEMBER, 12), hour(13), zone),
                "Scheduled on Monday at 1:00 in the afternoon by West Kazakhstan Time")
            .add(
                local(date(2022, Month.JUNE, 5), hour(11)),
                "Scheduled on Sunday at 11:00 in the morning")
            .add(
                "Asia/Samarkand", FormatterConfigurerTest::isAvailableZone,
                zone -> zoned(date(2022, Month.JANUARY, 22), time(12, 30), zone),
                "Scheduled on Saturday at 12:30 in the afternoon by Uzbekistan Standard Time")
            .add(
                local(date(2021, Month.JULY, 1), hour(0)),
                "Scheduled on Thursday at 12:00 midnight")
            .add(
                "Canada/Yukon", FormatterConfigurerTest::isAvailableZone,
                zone -> zoned(date(2022, Month.OCTOBER, 18), hour(12), zone),
                "Scheduled on Tuesday at 12:00 noon by Mountain Standard Time")
            .build(),
        new TestCase("Scientific Time", FormatterConfigurerFactory.scientificTime())
            .add(time(14, 36, 27, fraction(123, 456, 789)), "14:36:27.123456")
            .add(time(7, 57, 1, fraction(970, 0, 874)), "07:57:01.97")
            .add(time(23, 1, 59), "23:01:59.0")
            .build(),
        new TestCase("Historical Calendar", FormatterConfigurerFactory.historicalCalendar())
            .addAll("ThaiBuddhist", FormatterConfigurerTest::isAvailableChronology,
                chrono -> date -> inChronology(chrono, date), Map.of(
                    year(2022), "2565 of BE (Buddhist Calendar)",
                    year(-632), "90 of BC (Buddhist Calendar)",
                    year(2018), "2561 of BE (Buddhist Calendar)",
                    year(1900), "2443 of BE (Buddhist Calendar)",
                    year(328), "871 of BE (Buddhist Calendar)"
                ))
            .addAll("ISO", FormatterConfigurerTest::isAvailableChronology,
                chrono -> date -> inChronology(chrono, date), Map.of(
                    year(2022), "2022 of Anno Domini (ISO)",
                    year(-632), "633 of Before Christ (ISO)",
                    year(2018), "2018 of Anno Domini (ISO)",
                    year(1900), "1900 of Anno Domini (ISO)",
                    year(328), "328 of Anno Domini (ISO)"
                ))
            .addAll("Minguo", FormatterConfigurerTest::isAvailableChronology,
                chrono -> date -> inChronology(chrono, date), Map.of(
                    year(2022), "111 of Minguo (Minguo Calendar)",
                    year(-632), "2544 of Before R.O.C. (Minguo Calendar)",
                    year(2018), "107 of Minguo (Minguo Calendar)",
                    year(1900), "12 of Before R.O.C. (Minguo Calendar)",
                    year(328), "1584 of Before R.O.C. (Minguo Calendar)"
                ))
            .addAll("Japanese", FormatterConfigurerTest::isAvailableChronology,
                chrono -> date -> inChronology(chrono, date), Map.of(
                    year(2022), "4 of Reiwa (Japanese Calendar)",
                    year(2018), "30 of Heisei (Japanese Calendar)",
                    year(1900), "33 of Meiji (Japanese Calendar)",
                    year(1950), "25 of Shōwa (Japanese Calendar)"
                ))
            .addAll("Hijrah-umalqura", FormatterConfigurerTest::isAvailableChronology,
                chrono -> date -> inChronology(chrono, date), Map.of(
                    year(2022), "1443 of AH (Islamic Calendar (Umm al-Qura))",
                    year(2018), "1439 of AH (Islamic Calendar (Umm al-Qura))",
                    year(1900), "1318 of AH (Islamic Calendar (Umm al-Qura))"
                ))
            .build()
    ).flatMap(Function.identity());
  }
  
  private static boolean isAvailableChronology(String name) {
    try {
      Chronology.of(name);
      return true;
    }
    catch (DateTimeException e) {
      return false;
    }
  }
  
  private static boolean isAvailableZone(String name) {
    return ZoneId.getAvailableZoneIds().contains(name);
  }

  private static ChronoLocalDate inChronology(String name, TemporalAccessor date) {
    return Chronology.of(name).date(date);
  }
  
  private static LocalDate year(int year) {
    return date(year, Month.JULY, 15);
  }

  private static LocalDate date(int year, Month month, int day) {
    return LocalDate.of(year, month, day);
  }

  private static LocalTime hour(int hour) {
    return time(hour, 0);
  }

  private static LocalTime time(int hour, int minute) {
    return time(hour, minute, 0);
  }

  private static LocalTime time(int hour, int minute, int second) {
    return time(hour, minute, second, 0);
  }

  private static LocalTime time(int hour, int minute, int second, int nanos) {
    return LocalTime.of(hour, minute, second, nanos);
  }

  private static int fraction(int millis, int micros, int nanos) {
    return millis * 1_000_000 + micros * 1_000 + nanos;
  }

  private static LocalDateTime local(LocalDate date, LocalTime time) {
    return LocalDateTime.of(date, time);
  }

  private static ZonedDateTime zoned(LocalDate date, LocalTime time, String zone) {
    return ZonedDateTime.of(date, time, ZoneId.of(zone));
  }

  private static class TestCase {

    private final String name;
    private final FormatterConfigurer configurer;

    private final Map<TemporalAccessor, String> args = new HashMap<>();

    TestCase(String name, FormatterConfigurer configurer) {
      this.name = name;
      this.configurer = configurer;
    }
    TestCase add(TemporalAccessor accessor, String expected) {
      args.put(accessor, expected);
      return this;
    }

    <T> TestCase add(T subject, Predicate<T> test, Function<T, TemporalAccessor> accessor, String expected) {
      if (test.test(subject)) {
        args.put(accessor.apply(subject), expected);
      }
      return this;
    }
    
    <T, I> TestCase addAll(T subject, Predicate<T> test, Function<T, Function<I, TemporalAccessor>> fun,
        Map<I, String> inputs) {
      if (test.test(subject)) {
        inputs.forEach((i, exp) -> args.put(fun.apply(subject).apply(i), exp));
      }
      return this;
    }

    Stream<Arguments> build() {
      return args.entrySet().stream()
          .map(e -> arguments(name, configurer, e.getKey(), e.getValue()));
    }

    Stream<Arguments> buildWith(Map<TemporalAccessor, String> args) {
      return args.entrySet().stream()
          .map(e -> arguments(name, configurer, e.getKey(), e.getValue()));
    }
  }

}
