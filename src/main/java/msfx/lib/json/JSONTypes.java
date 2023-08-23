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

package msfx.lib.json;

/**
 * JSON types, either standard and extended.
 *
 * @author Miquel Sas
 */
public enum JSONTypes {

	/**
	 * Standard JSON object.
	 */
	OBJECT,
	/**
	 * Standard JSON array.
	 */
	ARRAY,
	/**
	 * Standard JSON string.
	 */
	STRING,
	/**
	 * Standard JSON number.
	 */
	NUMBER,
	/**
	 * Standard JSON boolean, true or false.
	 */
	BOOLEAN,
	/**
	 * Standard JSON null.
	 */
	NULL,

	/**
	 * Extended type binary.
	 */
	BINARY("%bn%"),
	/**
	 * Extended type date.
	 */
	DATE("%dt%"),
	/**
	 * Extended type time.
	 */
	TIME("%tm%"),
	/**
	 * Extended type timestamp.
	 */
	TIMESTAMP("%ts%");

	/**
	 * Key to store extended types.
	 */
	private String key = null;

	/**
	 * Constructor without extended key.
	 */
	JSONTypes() { }
	/**
	 * Constructor assigning the key for extended types.
	 *
	 * @param key The key for extended types.
	 */
	JSONTypes(String key) {
		this.key = key;
	}

	/**
	 * Returns the key for extended types, null otherwise.
	 *
	 * @return The key.
	 */
	public String getKey() {
		return key;
	}
	/**
	 * Check whether this type is an extended type.
	 *
	 * @return A boolean.
	 */
	public boolean isExtended() {
		return key != null;
	}

	/**
	 * Extended types.
	 */
	public static final JSONTypes[] EXTENDED_TYPES = new JSONTypes[] {
			BINARY, DATE, TIME, TIMESTAMP
	};
}
