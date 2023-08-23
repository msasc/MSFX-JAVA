/*
 * Copyright (c) 2023 Miquel Sas.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msfx.lib.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Format utils.
 *
 * @author Miquel Sas
 */
public class Formats {

	/** Standard date format. */
	public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	/** Standard time format. */
	public static final DateTimeFormatter FORMAT_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
	/** Standard datetime format. */
	public static final DateTimeFormatter FORMAT_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * Convert from a <i>BigDecimal</i> forcing the scale.
	 *
	 * @param number A number as a <code>java.math.BigDecimal</code>
	 * @param scale  The scale.
	 * @param locale The desired locale.
	 * @return A string.
	 */
	public static String fromBigDecimal(BigDecimal number, int scale, Locale locale) {
		return getNumberFormat(scale, locale).format(number.doubleValue());
	}

	/**
	 * Returns the formatted string representation of a boolean.
	 *
	 * @param bool   The boolean value
	 * @return A string.
	 */
	public static String fromBoolean(boolean bool, String tokenTrue, String tokenFalse) {
		if (bool) return tokenTrue != null ? tokenTrue : "true";
		return tokenFalse != null ? tokenFalse : "false";
	}

	/**
	 * Return the formatted date.
	 *
	 * @param date The local date.
	 * @return The formatted string.
	 */
	public static String fromDate(LocalDate date) {
		return date.format(FORMAT_DATE);
	}

	/**
	 * Return the formatted time.
	 *
	 * @param time The local time.
	 * @return The formatted string.
	 */
	public static String fromTime(LocalTime time) {
		return time.format(FORMAT_TIME);
	}

	/**
	 * Return the formatted date-time.
	 *
	 * @param dateTime The local date-time.
	 * @return The formatted string.
	 */
	public static String fromDateTime(LocalDateTime dateTime) {
		return dateTime.format(FORMAT_DATETIME);
	}

	/**
	 * Return the formatted date-time.
	 *
	 * @param time The time in millis.
	 * @return The formatted string.
	 */
	public static String fromDateTime(long time) {
		LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
		return fromDateTime(dateTime);
	}

	/**
	 * Convert from a <i>Double</i> forcing the scale.
	 *
	 * @param d      The <i>double</i> to convert.
	 * @param scale  The scale.
	 * @param locale The desired locale.
	 * @return A string.
	 */
	public static String fromDouble(double d, int scale, Locale locale) {
		return getNumberFormat(scale, locale).format(d);
	}

	/**
	 * Convert from a <i>double</i>.
	 *
	 * @param d      The <i>double</i> to convert.
	 * @param locale The locale to apply.
	 * @return A string.
	 */
	public static String fromDouble(double d, Locale locale) {
		return NumberFormat.getNumberInstance(locale).format(d);
	}

	/**
	 * Convert from an <i>int</i>.
	 *
	 * @param i      The <i>int</i> to convert.
	 * @param locale The locale to apply.
	 * @return A string.
	 */
	public static String fromInteger(int i, Locale locale) {
		return getNumberFormat(locale).format(i);
	}

	/**
	 * Convert from an <i>long</i>.
	 *
	 * @param l      The <i>long</i> to convert.
	 * @param locale The locale to apply.
	 * @return A string.
	 */
	public static String fromLong(long l, Locale locale) {
		return getNumberFormat(locale).format(l);
	}

	/**
	 * Return the number format.
	 *
	 * @param decimals The number of decimals.
	 * @param locale   The required locale.
	 * @return The number format.
	 */
	public static NumberFormat getNumberFormat(int decimals, Locale locale) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		if (decimals >= 0) {
			numberFormat.setMaximumFractionDigits(decimals);
			numberFormat.setMinimumFractionDigits(decimals);
		}
		return numberFormat;
	}

	/**
	 * Return the number format.
	 *
	 * @param locale The required locale.
	 * @return The number format.
	 */
	public static NumberFormat getNumberFormat(Locale locale) {
		return getNumberFormat(0, locale);
	}

	/**
	 * Convert to <i>BigDecimal</i> from a formatted string.
	 *
	 * @param str    The formatted string to convert.
	 * @param locale The locale to apply.
	 * @return A <i>BigDecimal</i>
	 * @throws ParseException If such exception occurs.
	 */
	public static BigDecimal toBigDecimal(String str, Locale locale) throws ParseException {
		if (str.length() == 0) {
			str = "0";
		}
		return new BigDecimal(NumberFormat.getNumberInstance(locale).parse(str).toString());
	}

	/**
	 * Parse the ISO date.
	 *
	 * @param str The string.
	 * @return The parsed date.
	 * @throws DateTimeParseException If such an error occurs.
	 */
	public static LocalDate toDate(String str) throws DateTimeParseException {
		return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	/**
	 * Parse the ISO time.
	 *
	 * @param str The string.
	 * @return The parsed time.
	 * @throws DateTimeParseException If such an error occurs.
	 */
	public static LocalTime toTime(String str) throws DateTimeParseException {
		return LocalTime.parse(str, DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	/**
	 * Parse the ISO date-time.
	 *
	 * @param str The string.
	 * @return The parsed date-time.
	 * @throws DateTimeParseException If such an error occurs.
	 */
	public static LocalDateTime toDateTime(String str) throws DateTimeParseException {
		return LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * Convert to <i>double</i> from a formatted string.
	 *
	 * @param str The formatted string to convert.
	 * @param loc The locale to apply.
	 * @return The parsed double.
	 * @throws ParseException If an error occurs parsing the string.
	 */
	public static double toDouble(String str, Locale loc) throws ParseException {
		if (str.length() == 0) {
			str = "0";
		}
		return NumberFormat.getNumberInstance(loc).parse(str).doubleValue();
	}

	/**
	 * Convert to <i>int</i> from a formatted string.
	 *
	 * @param str    The formatted string to convert.
	 * @param locale The locale to apply.
	 * @return An <i>int</i>
	 * @throws ParseException If such exception occurs.
	 */
	public static int toInteger(String str, Locale locale) throws ParseException {
		if (str.length() == 0) {
			str = "0";
		}
		return NumberFormat.getNumberInstance(locale).parse(str).intValue();
	}

	/**
	 * Convert to <i>long</i> from a formatted string.
	 *
	 * @return A <i>long</i>
	 * @param str The formatted string to convert.
	 * @param loc The locale to apply.
	 * @throws ParseException If an error occurs parsing the string.
	 */
	public static long toLong(String str, Locale loc) throws ParseException {
		if (str.length() == 0) {
			str = "0";
		}
		return NumberFormat.getNumberInstance(loc).parse(str).longValue();
	}
}
