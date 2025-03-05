package edu.epam.fop;

import java.time.format.DateTimeFormatterBuilder;

@FunctionalInterface
public interface FormatterConfigurer {

  void configure(DateTimeFormatterBuilder builder);
}
