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
package msfx.lib.db.md;

import msfx.ztrash.json.JSONTypes;

import java.util.Set;

/**
 * Data types supported.
 * @author Miquel Sas
 */
public enum Type {
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
	 * Java type is {@link java.time.LocalDate}. The Normalized format is <i>YYYY-MM-DD</i>.
	 */
	DATE,
	/**
	 * Java type is {@link java.time.LocalTime}. The normalized format is <i>hh:mm:ss.nnnnnnnnn</i>.
	 */
	TIME,
	/**
	 * Java type is {@link java.time.LocalDateTime}. The normalized format is
	 * <i>YYYY-MM-DDThh:mm:ss.nnnnnnnnn</i>.
	 */
	TIMESTAMP,
	/**
	 *
	 */
	VARCHAR,
	/**
	 *
	 */
	CLOB,
	/**
	 * Binary, java type is <i>byte[]</i>.
	 */
	VARBINARY,
	/**
	 * Binary, java type is <i>byte[]</i>.
	 */
	BLOB;

	/**
	 * Return a boolean indicating whether the type is BOOLEAN.
	 * @return A boolean.
	 */
	public boolean isBoolean() {
		return this == BOOLEAN;
	}
	/**
	 * Return a boolean indicating whether the type is DECIMAL.
	 * @return A boolean.
	 */
	public boolean isDecimal() {
		return this == DECIMAL;
	}
	/**
	 * Return a boolean indicating whether the type is DOUBLE.
	 * @return A boolean.
	 */
	public boolean isDouble() {
		return this == DOUBLE;
	}
	/**
	 * Return a boolean indicating whether the type is INTEGER.
	 * @return A boolean.
	 */
	public boolean isInteger() {
		return this == INTEGER;
	}
	/**
	 * Return a boolean indicating whether the type is LONG.
	 * @return A boolean.
	 */
	public boolean isLong() {
		return this == LONG;
	}
	/**
	 * Return a boolean indicating whether the type is a number.
	 * @return A boolean.
	 */
	public boolean isNumber() {
		return Set.of(DECIMAL, DOUBLE, INTEGER, LONG).contains(this);
	}

	/**
	 * Return a boolean indicating whether the type is DATE.
	 * @return A boolean.
	 */
	public boolean isDate() {
		return this == DATE;
	}
	/**
	 * Return a boolean indicating whether the type is TIME.
	 * @return A boolean.
	 */
	public boolean isTime() {
		return this == TIME;
	}
	/**
	 * Return a boolean indicating whether the type is TIMESTAMP.
	 * @return A boolean.
	 */
	public boolean isTimestamp() {
		return this == TIMESTAMP;
	}

	/**
	 * Return a boolean indicating whether the type is a string.
	 * @return A boolean.
	 */
	public boolean isString() {
		return Set.of(Type.VARCHAR, Type.CLOB).contains(this);
	}
	/**
	 * Return a boolean indicating whether the type is VARCHAR.
	 * @return A boolean.
	 */
	public boolean isVARCHAR() {
		return this == VARCHAR;
	}
	/**
	 * Return a boolean indicating whether the type is VARCHAR.
	 * @return A boolean.
	 */
	public boolean isCLOB() {
		return this == CLOB;
	}

	/**
	 * Return a boolean indicating whether the type is BINARY.
	 * @return A boolean.
	 */
	public boolean isBinary() {
		return Set.of(VARBINARY, BLOB).contains(this);
	}
	/**
	 * Return a boolean indicating whether the type is VARBINARY.
	 * @return A boolean.
	 */
	public boolean isVARBINARY() {
		return this == VARBINARY;
	}
	/**
	 * Return a boolean indicating whether the type is VARCHAR.
	 * @return A boolean.
	 */
	public boolean isBLOB() {
		return this == CLOB;
	}

	/**
	 * Check whether the argument type is comparable to this type.
	 * @param type The type to check.
	 * @return A boolean.
	 */
	public boolean isComparable(Type type) {
		if (type == null) throw new NullPointerException("Type can not be null");
		if (isNumber() && type.isNumber()) return true;
		if (this == type) return true;
		return false;
	}

}
