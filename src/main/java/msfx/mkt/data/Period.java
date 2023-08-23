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

package msfx.mkt.data;

/**
 * Periods of data, like 5 minutes, 30 minutes, 1 hour, 4 hours, etc.
 *
 * @author Miquel Sas
 */
public final class Period {

	/**
	 * Unit.
	 */
	private final Unit unit;
	/**
	 * The number of units or size.
	 */
	private final int size;

	/**
	 * Private constructor assigning unit and size.
	 *
	 * @param unit The unit.
	 * @param size The size or number of units.
	 */
	public Period(Unit unit, int size) {
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
}
