/*
 * Copyright (c) 2021-2024 Miquel Sas.
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
package msfx.ztrash.db;

import msfx.ztrash.json.JSONTypes;

/**
 * Data types supported.
 *
 * @author Miquel Sas
 */
public enum Types {

	/**
	 * Java type is {@link Boolean}, supported by a VARCHAR of length 1, with T/F values.
	 */
	BOOLEAN,

	/**
	 * Java type is {@link java.math.BigDecimal}.
	 */
	DECIMAL,
	/**
	 * Java type is {@link Double}.
	 */
	DOUBLE,
	/**
	 * Java type is {@link Integer}.
	 */
	INTEGER,
	/**
	 * Java type is {@link Long}
	 */
	LONG,

	/**
	 * Java type is {@link java.time.LocalDate}. Normalized format is <i>YYYY-MM-DD</i>.
	 */
	DATE,
	/**
	 * Java type is {@link java.time.LocalTime}. Normalized format is <i>hh:mm:ss.nnnnnnnnn</i>.
	 */
	TIME,
	/**
	 * Java type is {@link java.time.LocalDateTime}. Normalized format is
	 * <i>YYYY-MM-DDThh:mm:ss.nnnnnnnnn</i>.
	 */
	TIMESTAMP,

	/**
	 * Java type is {@link String}. With a maximum length defined or not, whether it will be backed
	 * in the database by a simple <i>VARCHAR</i> or a <i>CLOB</i> will depend on the database, the
	 * supported maximum length for varying chars and the required length of the field.
	 */
	STRING,

	/**
	 * Binary, java type is <i>byte[]</i>.
	 */
	BINARY,

	/**
	 * Array type, java type is <i>Value[]</i>. Supported by a JSON object with an
	 * array of values.
	 */
	ARRAY,
	/**
	 * JSON object, java type is <i>JSONObject</i>. Modern relational databases use
	 * to support JSON objects.
	 */
	OBJECT;

	/**
	 * Return a boolean indicating whether the type is BOOLEAN.
	 *
	 * @return A boolean.
	 */
	public boolean isBoolean() {
		return this == Types.BOOLEAN;
	}
	/**
	 * Return a boolean indicating whether the type is DECIMAL.
	 *
	 * @return A boolean.
	 */
	public boolean isDecimal() {
		return this == Types.DECIMAL;
	}
	/**
	 * Return a boolean indicating whether the type is DOUBLE.
	 *
	 * @return A boolean.
	 */
	public boolean isDouble() {
		return this == Types.DOUBLE;
	}
	/**
	 * Return a boolean indicating whether the type is INTEGER.
	 *
	 * @return A boolean.
	 */
	public boolean isInteger() {
		return this == Types.INTEGER;
	}
	/**
	 * Return a boolean indicating whether the type is LONG.
	 *
	 * @return A boolean.
	 */
	public boolean isLong() {
		return this == Types.LONG;
	}
	/**
	 * Return a boolean indicating whether the type is a number.
	 *
	 * @return A boolean.
	 */
	public boolean isNumber() {
		return isDecimal() || isDouble() || isInteger() || isLong();
	}

	/**
	 * Return a boolean indicating whether the type is DATE.
	 *
	 * @return A boolean.
	 */
	public boolean isDate() {
		return this == Types.DATE;
	}
	/**
	 * Return a boolean indicating whether the type is TIME.
	 *
	 * @return A boolean.
	 */
	public boolean isTime() {
		return this == Types.TIME;
	}
	/**
	 * Return a boolean indicating whether the type is TIMESTAMP.
	 *
	 * @return A boolean.
	 */
	public boolean isTimestamp() {
		return this == Types.TIMESTAMP;
	}

	/**
	 * Return a boolean indicating whether the type is STRING.
	 *
	 * @return A boolean.
	 */
	public boolean isString() {
		return this == Types.STRING;
	}

	/**
	 * Return a boolean indicating whether the type is BINARY.
	 *
	 * @return A boolean.
	 */
	public boolean isBinary() {
		return this == Types.BINARY;
	}

	/**
	 * Return a boolean indicating whether the type an ARRAY.
	 *
	 * @return A boolean.
	 */
	public boolean isArray() {
		return this == Types.ARRAY;
	}
	/**
	 * Return a boolean indicating whether the type is OBJECT.
	 *
	 * @return A boolean.
	 */
	public boolean isObject() {
		return this == Types.OBJECT;
	}

	/**
	 * Check whether the argument type is comparable to this type.
	 *
	 * @param type The type to check.
	 * @return A boolean.
	 */
	public boolean isComparable(Types type) {
		if (type == null) throw new NullPointerException("Type can not be null");
		if (isNumber() && type.isNumber()) return true;
		if (this == type) return true;
		return false;
	}

	/**
	 * Returns the corresponding JSON type.
	 *
	 * @return The JSON type.
	 */
	public JSONTypes toJSONTypes() {
		if (this == BOOLEAN) return JSONTypes.BOOLEAN;
		if (this == DECIMAL) return JSONTypes.NUMBER;
		if (this == DOUBLE) return JSONTypes.NUMBER;
		if (this == INTEGER) return JSONTypes.NUMBER;
		if (this == LONG) return JSONTypes.NUMBER;
		if (this == DATE) return JSONTypes.DATE;
		if (this == TIME) return JSONTypes.TIME;
		if (this == TIMESTAMP) return JSONTypes.TIMESTAMP;
		if (this == STRING) return JSONTypes.STRING;
		if (this == BINARY) return JSONTypes.BINARY;
		if (this == ARRAY) return JSONTypes.ARRAY;
		if (this == OBJECT) return JSONTypes.OBJECT;
		throw new IllegalStateException();
	}
}
