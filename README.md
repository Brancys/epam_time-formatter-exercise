# Time Formatter

The goal of this task is to practice defining custom Date-Time formatter especially using `DateTimeFormatterBuilder`.

Duration: _1 hour_

## Description

You are already provided with one interface and one factory class - `FormatterConfigurer`
and `FormatterConfigurerFactory`. In the factory you must implement methods which return an instance of this interface.

`FormatterConfigurer` is a simple functional interface, which accepts `DateTimeFormatterBuilder` as a parameter and
configures it for the specified purposes.

Each method of `FormatterConfigurerFactory` specifies how the passed `DateTimeFormatterBuilder` must be configured
in order to meet the requirements. The requirements for each method is described below.

## Requirements

#### slangBasedDate

The builder configured by the `slangBasedDate` method must return a `String` in the following format:
```
[day] [short month] of [reduced year]
```

* `day` is an integer in range **1-31** which represents a day of month.
* `short month` is a month abbreviation (e.g. January - Jan).
* `reduced year` is a year, but
  * it must contain only **2** symbols if a year is in range **1931-2030** (e.g. **2012** => **12** and **1990** => **90**)
  * it must contain **4** symbols if a year is not within this range (e.g. **1912** => **1912** and **2031** => **2031**)

#### politeScheduler

The builder configured by the `politeScheduler` method must return a `String` in the following formats:
```
Scheduled on [day of week] at [short time] [day period]
OR
Scheduled on [day of week] at [short time] [day period] by [time zone]
```

* `day of week` is a full name of day of week (Monday, Tuesday, etc.)
* `short time` is an hour and time separated by colon (`:`), but an hour is represented in short format (e.g. **13** => **1**, **18** => **6**)
* `day period` is a name for period of day such as noon, midnight, etc.
* `time zone` is a full name of time zone (e.g. `Mountain Standard Time`)

Please keep in mind that time zone is optional, so if a date does not have any time zone, then it must be omitted.

#### scientificTime

The builder configured by the `scientificTime` method must return a `String` in the following format:

```
[time].[second fraction]
```

* `time` is a simply hours, minutes, and seconds separated by colon.
Each of them must be represented by **2** symbols exactly, i.e. that **2** hours must be formatted as `02`.
* `second fraction` is a micro-seconds which must contain from **1** up to **6** symbols,
e.g. **123456** => `123456`, **123000** => `123`, and **0** => `0`

#### historicalCalendar

The builder configured by the `historicalCalendar` method must return a `String` in the following format:

```
[year of era] of [era] ([chronology])
```

* `year of era` is a year which is tied to the certain era, e.g. **145 AD** is **145**, but **145 BC** is **-146**
* `era` is a full name of the date era, e.g. `Before Christ` or `Anno Domini`
* `chronology` is a name of calendar which is used for this date

Java supports multiple chronologies, such as ISO, Japanese, Minguo, etc.
`java.time.Chronology` is used to describe them. They have different eras,
different start date (e.g. Japanese and Hijrah do not support dates before certain point).
In the tests you may see how the same date in ISO system might be represented
by these chronologies. And you will implement correct formatting rules for that.