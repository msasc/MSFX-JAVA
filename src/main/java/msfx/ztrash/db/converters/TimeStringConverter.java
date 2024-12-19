/*
 * Copyright (c) 2023-2024 Miquel Sas.
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

package msfx.ztrash.db.converters;

import msfx.ztrash.db.Value;
import msfx.lib.util.Strings;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * String converter for time values expressed as a long.
 *
 * @author Miquel Sas
 */
public class TimeStringConverter implements ValueStringConverter {

	public static final String PATTERN_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String PATTERN_SECOND = "yyyy-MM-dd HH:mm:ss";
	public static final String PATTERN_MINUTE = "yyyy-MM-dd HH:mm";
	public static final String PATTERN_HOUR = "yyyy-MM-dd HH";
	public static final String PATTERN_DAY = "yyyy-MM-dd";
	public static final String PATTERN_WEEK = "yyyy-MM-dd";
	public static final String PATTERN_MONTH = "yyyy-MM";
	public static final String PATTERN_YEAR = "yyyy";

	/** Date format. */
	private final SimpleDateFormat format;

	/**
	 * Constructor.
	 *
	 * @param pattern The pattern.
	 */
	public TimeStringConverter(String pattern) {
		if (pattern == null) throw new NullPointerException();
		String[] patterns = new String[] {
				PATTERN_MILLIS,
				PATTERN_SECOND,
				PATTERN_MINUTE,
				PATTERN_HOUR,
				PATTERN_DAY,
				PATTERN_WEEK,
				PATTERN_MONTH,
				PATTERN_YEAR
		};
		if (!Strings.in(pattern, patterns)) {
			throw new IllegalArgumentException("Invalid pattern " + pattern);
		}
		format = new SimpleDateFormat(pattern);
	}
	/**
	 * Return the value given the string.
	 */
	@Override
	public Value fromString(String string) {
		long time = -1;
		try {
			time = format.parse(string).getTime();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return new Value(time);
	}
	/**
	 * Return the string given the value.
	 */
	@Override
	public String toString(Value value) {
		return format.format(new Timestamp(value.getLong()));
	}
}
