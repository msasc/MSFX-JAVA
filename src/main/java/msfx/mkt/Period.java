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

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Periods of data, like 5 minutes, 30 minutes, 1 hour, 4 hours, etc.
 *
 * @author Miquel Sas
 */
public final class Period {

	public static final Period ONE_MINUTE = new Period(Unit.MINUTE, 1);
	public static final Period THREE_MINUTES = new Period(Unit.MINUTE, 3);
	public static final Period FIVE_MINUTES = new Period(Unit.MINUTE, 5);
	public static final Period FIFTEEN_MINUTES = new Period(Unit.MINUTE, 15);
	public static final Period THIRTY_MINUTES = new Period(Unit.MINUTE, 30);

	public static final Period ONE_HOUR = new Period(Unit.HOUR, 1);
	public static final Period FOUR_HOURS = new Period(Unit.HOUR, 4);
	public static final Period SIX_HOURS = new Period(Unit.HOUR, 6);
	public static final Period TWELVE_HOURS = new Period(Unit.HOUR, 12);

	public static final Period DAY = new Period(Unit.DAY, 1);
	public static final Period WEEK = new Period(Unit.WEEK, 1);
	public static final Period MONTH = new Period(Unit.MONTH, 1);

	public static Period getPeriod(Unit unit, int size) {
		if (unit == Unit.MINUTE && size == 1) return ONE_MINUTE;
		if (unit == Unit.MINUTE && size == 3) return THREE_MINUTES;
		if (unit == Unit.MINUTE && size == 5) return FIVE_MINUTES;
		if (unit == Unit.MINUTE && size == 15) return FIFTEEN_MINUTES;
		if (unit == Unit.MINUTE && size == 30) return THIRTY_MINUTES;
		if (unit == Unit.HOUR && size == 1) return ONE_HOUR;
		if (unit == Unit.HOUR && size == 4) return FOUR_HOURS;
		if (unit == Unit.HOUR && size == 6) return SIX_HOURS;
		if (unit == Unit.HOUR && size == 12) return TWELVE_HOURS;
		if (unit == Unit.DAY && size == 1) return DAY;
		if (unit == Unit.DAY && size == 7) return WEEK;
		if (unit == Unit.DAY && size == 30) return MONTH;
		if (unit == Unit.WEEK && size == 1) return WEEK;
		if (unit == Unit.WEEK && size == 4) return MONTH;
		if (unit == Unit.MONTH && size == 1) return MONTH;
		throw new IllegalArgumentException("Invalid unit and/or size: " + unit + " " + size);
	}

	/**
	 * Unit.
	 */
	private final Unit unit;
	/**
	 * The number of units or size.
	 */
	private final int size;

	/**
	 * Public constructor assigning unit and size.
	 *
	 * @param unit The unit.
	 * @param size The size or number of units.
	 */
	private Period(Unit unit, int size) {
		if (unit == null) {
			throw new NullPointerException("Unit can not be null");
		}
		if (size <= 0) {
			throw new IllegalArgumentException("Invalid size: " + size);
		}
		this.unit = unit;
		this.size = size;
	}

	/**
	 * Returns the unit.
	 *
	 * @return The unit.
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * Returns the size or number of units.
	 *
	 * @return The size or number of units.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Add the given number of periods, positive or negative, to the argument date-time.
	 *
	 * @param time    The date-time.
	 * @param periods The number of periods to add.
	 * @return The new date-time.
	 */
	public LocalDateTime add(LocalDateTime time, int periods) {
		if (unit == Unit.MINUTE) {
			return time.plusMinutes(periods * size);
		}
		if (unit == Unit.HOUR) {
			return time.plusHours(periods * size);
		}
		if (unit == Unit.DAY) {
			return time.plusDays(periods * size);
		}
		if (unit == Unit.WEEK) {
			return time.plusDays(periods * size * 7);
		}
		if (unit == Unit.MONTH) {
			return time.plusMonths(periods * size);
		}
		throw new IllegalStateException();
	}

	/**
	 * Returns a string representation.
	 *
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getSize());
		b.append(" ");
		b.append(getUnit().getShortName(Locale.getDefault()));
		if (getSize() > 1) {
			b.append("s");
		}
		return b.toString();
	}
}
