package strikt.assertions

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import strikt.api.expect
import strikt.fails
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.MonthDay
import java.time.OffsetTime
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.chrono.JapaneseDate
import java.time.temporal.TemporalAccessor

@DisplayName("assertions on temporal types")
internal class TemporalAssertions {

  companion object {
    val zone: ZoneId = ZoneId.systemDefault()
    val now: Instant = Instant.now()
    val local: ZonedDateTime = now.atZone(zone)
    val today: LocalDate = local.toLocalDate()
    val rightNow: LocalTime = local.toLocalTime()
  }

  @Nested
  @DisplayName("isBefore assertion")
  inner class IsBefore {
    @TestFactory
    fun `passes if subject value is before the expected`() =
      listOf<Pair<TemporalAccessor, TemporalAccessor>>(
        Pair(now, now.plusMillis(1)),
        Pair(now.atOffset(ZoneOffset.MIN).toInstant(), now.plusMillis(1)),
        Pair(now.atOffset(ZoneOffset.UTC).toInstant(), now.plusMillis(1)),
        Pair(now.atOffset(ZoneOffset.MAX).toInstant(), now.plusMillis(1)),
        Pair(now, now.plusMillis(1).atOffset(ZoneOffset.MIN)),
        Pair(now, now.plusMillis(1).atOffset(ZoneOffset.UTC)),
        Pair(now, now.plusMillis(1).atOffset(ZoneOffset.MAX)),
        Pair(today, today.plusDays(1)),
        Pair(JapaneseDate.from(today), today.plusDays(1)),
        Pair(rightNow, rightNow.plusSeconds(1)), // TODO: potential failure on day boundary
        Pair(rightNow, now.plusSeconds(1).atZone(zone)), // TODO: potential failure on day boundary
        Pair(MonthDay.from(today), MonthDay.from(today.plusDays(1))),
        Pair(MonthDay.from(today), today.plusDays(1)),
        Pair(OffsetTime.from(local), local.plusSeconds(1)), // TODO: potential failure on day boundary
        Pair(Year.from(today), today.plusYears(1)),
        Pair(YearMonth.from(today), today.plusMonths(1))
      ).map { (actual, expected) ->
        dynamicTest("passes asserting $actual (${actual.javaClass.simpleName}) is before $expected (${expected.javaClass.simpleName})") {
          expect(actual).isBefore(expected)
        }
      }

    @TestFactory
    fun `fails if the subject value is the same or later than the expected`(): List<DynamicTest> {
      return listOf<Pair<TemporalAccessor, TemporalAccessor>>(
        Pair(now, now),
        Pair(now.atOffset(ZoneOffset.MIN).toInstant(), now),
        Pair(now.atOffset(ZoneOffset.UTC).toInstant(), now),
        Pair(now.atOffset(ZoneOffset.MAX).toInstant(), now),
        Pair(now, now.atOffset(ZoneOffset.MIN)),
        Pair(now, now.atOffset(ZoneOffset.UTC)),
        Pair(now, now.atOffset(ZoneOffset.MAX)),
        Pair(now, now.minusMillis(1)),
        Pair(today, today),
        Pair(today, today.minusDays(1)),
        Pair(JapaneseDate.from(today), today.minusDays(1)),
        Pair(rightNow, rightNow),
        Pair(rightNow, rightNow.minusSeconds(1)), // TODO: potential failure on day boundary
        Pair(rightNow, now.atZone(zone)),
        Pair(rightNow, now.minusSeconds(1).atZone(zone)), // TODO: potential failure on day boundary
        Pair(MonthDay.from(today), MonthDay.from(today)),
        Pair(MonthDay.from(today), MonthDay.from(today.minusDays(1))),
        Pair(MonthDay.from(today), today),
        Pair(MonthDay.from(today), today.minusDays(1)),
        Pair(OffsetTime.from(local), local),
        Pair(OffsetTime.from(local), local.minusSeconds(1)), // TODO: potential failure on day boundary
        Pair(Year.from(today), today),
        Pair(Year.from(today), today.minusYears(1)),
        Pair(YearMonth.from(today), today),
        Pair(YearMonth.from(today), today.minusMonths(1))
      ).map { (actual, expected) ->
        dynamicTest("fails asserting $actual (${actual.javaClass.simpleName}) is before $expected (${expected.javaClass.simpleName})") {
          fails {
            expect(actual).isBefore(expected)
          }
        }
      }
    }

    @TestFactory
    fun `throws an exception if expected value can't be converted to the subject type`() =
      listOf<Pair<Instant, TemporalAccessor>>(
        Pair(now, LocalDate.of(2008, 10, 2))
      ).map { (actual, expected) ->
        dynamicTest("fails asserting $actual is before $expected") {
          assertThrows<DateTimeException> {
            expect(actual).isBefore(expected)
          }
        }
      }
  }

  @Nested
  @DisplayName("isAfter assertion")
  inner class IsAfter {
    @TestFactory
    fun `passes if subject value is after the expected`() =
      listOf<Pair<TemporalAccessor, TemporalAccessor>>(
        Pair(now, now.minusMillis(1)),
        Pair(now.atOffset(ZoneOffset.MIN).toInstant(), now.minusMillis(1)),
        Pair(now.atOffset(ZoneOffset.UTC).toInstant(), now.minusMillis(1)),
        Pair(now.atOffset(ZoneOffset.MAX).toInstant(), now.minusMillis(1)),
        Pair(now, now.minusMillis(1).atOffset(ZoneOffset.MIN)),
        Pair(now, now.minusMillis(1).atOffset(ZoneOffset.UTC)),
        Pair(now, now.minusMillis(1).atOffset(ZoneOffset.MAX)),
        Pair(today, today.minusDays(1)),
        Pair(JapaneseDate.from(today), today.minusDays(1)),
        Pair(rightNow, rightNow.minusSeconds(1)), // TODO: potential failure on day boundary
        Pair(rightNow, now.minusSeconds(1).atZone(zone)), // TODO: potential failure on day boundary
        Pair(MonthDay.from(today), MonthDay.from(today.minusDays(1))),
        Pair(MonthDay.from(today), today.minusDays(1)),
        Pair(OffsetTime.from(local), local.minusSeconds(1)), // TODO: potential failure on day boundary
        Pair(Year.from(today), today.minusYears(1)),
        Pair(YearMonth.from(today), today.minusMonths(1))
      ).map { (actual, expected) ->
        dynamicTest("passes asserting $actual (${actual.javaClass.simpleName}) is before $expected (${expected.javaClass.simpleName})") {
          expect(actual).isAfter(expected)
        }
      }

    @TestFactory
    fun `fails if the subject value is the same or earlier than the expected`(): List<DynamicTest> {
      return listOf<Pair<TemporalAccessor, TemporalAccessor>>(
        Pair(now, now),
        Pair(now.atOffset(ZoneOffset.MIN).toInstant(), now),
        Pair(now.atOffset(ZoneOffset.UTC).toInstant(), now),
        Pair(now.atOffset(ZoneOffset.MAX).toInstant(), now),
        Pair(now, now.atOffset(ZoneOffset.MIN)),
        Pair(now, now.atOffset(ZoneOffset.UTC)),
        Pair(now, now.atOffset(ZoneOffset.MAX)),
        Pair(now, now.plusMillis(1)),
        Pair(today, today),
        Pair(today, today.plusDays(1)),
        Pair(JapaneseDate.from(today), today.plusDays(1)),
        Pair(rightNow, rightNow),
        Pair(rightNow, rightNow.plusSeconds(1)), // TODO: potential failure on day boundary
        Pair(rightNow, now.atZone(zone)),
        Pair(rightNow, now.plusSeconds(1).atZone(zone)), // TODO: potential failure on day boundary
        Pair(MonthDay.from(today), MonthDay.from(today)),
        Pair(MonthDay.from(today), MonthDay.from(today.plusDays(1))),
        Pair(MonthDay.from(today), today),
        Pair(MonthDay.from(today), today.plusDays(1)),
        Pair(OffsetTime.from(local), local),
        Pair(OffsetTime.from(local), local.plusSeconds(1)), // TODO: potential failure on day boundary
        Pair(Year.from(today), today),
        Pair(Year.from(today), today.plusYears(1)),
        Pair(YearMonth.from(today), today),
        Pair(YearMonth.from(today), today.plusMonths(1))
      ).map { (actual, expected) ->
        dynamicTest("fails asserting $actual (${actual.javaClass.simpleName}) is before $expected (${expected.javaClass.simpleName})") {
          fails {
            expect(actual).isAfter(expected)
          }
        }
      }
    }

    @TestFactory
    fun `throws an exception if expected value can't be converted to the subject type`() =
      listOf<Pair<Instant, TemporalAccessor>>(
        Pair(now, LocalDate.of(2008, 10, 2))
      ).map { (actual, expected) ->
        dynamicTest("fails asserting $actual is before $expected") {
          assertThrows<DateTimeException> {
            expect(actual).isBefore(expected)
          }
        }
      }
  }
}
