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
 * Periods of data.
 *
 * @author Miquel Sas
 */
public enum Period {

	ONE_MINUTE(Unit.MINUTE, 1),
	THREE_MINUTES(Unit.MINUTE, 3),
	FIVE_MINUTES(Unit.MINUTE, 5),
	FIFTEEN_MINUTES(Unit.MINUTE, 15),
	THIRTY_MINUTES(Unit.MINUTE, 30),

	ONE_HOUR(Unit.HOUR, 1),
	FOUR_HOURS(Unit.HOUR, 4),
	SIX_HOURS(Unit.HOUR, 6),
	TWELVE_HOURS(Unit.HOUR, 12),

	DAY(Unit.DAY, 1),
	WEEK(Unit.WEEK, 1),
	MONTH(Unit.MONTH, 1);

	/**
	 * Unit.
	 */
	private final Unit unit;
	/**
	 * The number of units or size.
	 */
	private final int size;
	/**
	 * Constructor assigning unit and size.
	 *
	 * @param unit The unit.
	 * @param size The size or number of units.
	 */
	Period(Unit unit, int size) {
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
	 * Check whether this period is intraday.
	 *
	 * @return A boolean.
	 */
	public boolean isIntraDay() {
		if (this == ONE_MINUTE) return true;
		if (this == THREE_MINUTES) return true;
		if (this == FIVE_MINUTES) return true;
		if (this == FIFTEEN_MINUTES) return true;
		if (this == THIRTY_MINUTES) return true;
		if (this == ONE_HOUR) return true;
		if (this == FOUR_HOURS) return true;
		if (this == SIX_HOURS) return true;
		if (this == TWELVE_HOURS) return true;
		return false;
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

	/**
	 * Returns the proper period given a unit and a size.
	 *
	 * @param unit The unit.
	 * @param size The size.
	 * @return
	 */
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
}
