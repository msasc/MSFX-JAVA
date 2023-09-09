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

package msfx.mkt;

import msfx.lib.util.Numbers;
import msfx.lib.util.Strings;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Timed data.
 *
 * @author Miquel Sas
 */
public class Data {

	/**
	 * Open index in a price data.
	 */
	public static final int OPEN = 0;
	/**
	 * High index in a price data.
	 */
	public static final int HIGH = 1;
	/**
	 * Low index in a price data.
	 */
	public static final int LOW = 2;
	/**
	 * Close index in a price data.
	 */
	public static final int CLOSE = 3;
	/**
	 * Volume index in a price data.
	 */
	public static final int VOLUME = 4;
	/**
	 * Data price (OHLCV) size.
	 */
	public static final int DATA_PRICE_SIZE = 5;

	/**
	 * Data, any number of double values.
	 */
	private final double[] values;
	/**
	 * Start time in epoch seconds.
	 */
	private final int time;

	/**
	 * Constructor.
	 *
	 * @param time Start time in epoch seconds.
	 * @param values Array of data values.
	 */
	public Data(int time, double... values) {
		this.time = time;
		this.values = values;
	}

	/**
	 * Returns the start in epoch seconds.
	 *
	 * @return The start time in epoch seconds.
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Returns the array of values.
	 *
	 * @return The array of values.
	 */
	public double[] getValues() {
		return values;
	}
	/**
	 * Returns the value at the given index.
	 *
	 * @param index The index.
	 * @return The value.
	 */
	public double getValue(int index) {
		return values[index];
	}

	/**
	 * Returns the number of values.
	 *
	 * @return The number of values.
	 */
	public int size() {
		return values.length;
	}

	/**
	 * Returns a string representation applying the number of decimals to the values.
	 *
	 * @return The string representation.
	 */
	public String toString() {
		return toString(2);
	}
	/**
	 * Returns a string representation applying the number of decimals to the values.
	 *
	 * @param decimals Number of decimal places.
	 * @return The string representation.
	 */
	public String toString(int decimals) {

		StringBuilder b = new StringBuilder();

		/*
		 * Time part, adjust to minutes.
		 */
//		LocalDateTime dateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
//		b.append(Strings.leftPad(dateTime.getYear(), 4, "0"));
//		b.append("-");
//		b.append(Strings.leftPad(dateTime.getMonthValue(), 2, "0"));
//		b.append("-");
//		b.append(Strings.leftPad(dateTime.getDayOfMonth(), 2, "0"));
//		b.append(" ");
//		b.append(Strings.leftPad(dateTime.getHour(), 2, "0"));
//		b.append(":");
//		b.append(Strings.leftPad(dateTime.getMinute(), 2, "0"));
		b.append(time);

		/*
		 * Values.
		 */
		for (double value : values) {
			b.append(", ");
			b.append(Numbers.round(value, decimals));
		}

		return b.toString();
	}

}
