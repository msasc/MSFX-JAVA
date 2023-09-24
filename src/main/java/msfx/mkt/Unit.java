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

import msfx.lib.res.StringRes;

import java.util.Locale;

/**
 * Units used to define periods of aggregate incoming quotes.
 *
 * @author Miquel Sas
 */
public enum Unit {

	MINUTE("m"),
	HOUR("h"),
	DAY("D"),
	WEEK("W"),
	MONTH("M");

	/**
	 * One char id.
	 */
	private final String id;

	/**
	 * Constructor.
	 *
	 * @param id One char id.
	 */
	private Unit(String id) {
		this.id = id;
	}

	/**
	 * Returns the two char id.
	 *
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the short description.
	 *
	 * @param loc The locale.
	 * @return The short description.
	 */
	public String getShortName(Locale loc) {
		switch (this) {
		case MINUTE:
			return StringRes.get("unitMinute", "Min", loc);
		case HOUR:
			return StringRes.get("unitSecond", "Hour", loc);
		case DAY:
			return StringRes.get("unitSecond", "Day", loc);
		case WEEK:
			return StringRes.get("unitSecond", "Week", loc);
		case MONTH:
			return StringRes.get("unitSecond", "Month", loc);
		default:
			throw new IllegalArgumentException();
		}
	}
}
