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

package msfx.mkt.info;

import msfx.lib.util.Numbers;
import msfx.lib.util.Strings;

import java.math.BigDecimal;

/**
 * Parameter of an indicator.
 *
 * @author Miquel Sas
 */
public class Parameter {

	private static final String NUMBER = "NUMBER";
	private static final String STRING = "STRING";
	private static final String BOOLEAN = "BOOLEAN";

	/**
	 * ID.
	 */
	private final String id;
	/**
	 * Name.
	 */
	private final String name;
	/**
	 * Type, NUMBER, STRING or BOOLEAN.
	 */
	private final String type;
	/**
	 * Number of decimal places when this parameter is a number.
	 */
	private final int decimals;
	/**
	 * List of possible values.
	 */
	private final Object[] possibleValues;

	/**
	 * Value.
	 */
	private Object value;

	/**
	 * Constructor.
	 *
	 * @param id   ID, not null.
	 * @param name Name.
	 * @param type Type: NUMBER, STRING or BOOLEAN.
	 */
	public Parameter(String id, String name, String type) {
		this(id, name, type, (type.equals(NUMBER) ? 0 : -1));
	}
	/**
	 * Constructor.
	 *
	 * @param id             ID, not null.
	 * @param name           Name.
	 * @param type           Type: NUMBER, STRING or BOOLEAN.
	 * @param decimals       Decimal places if it is a number.
	 * @param possibleValues List of possible values.
	 */
	public Parameter(
			String id,
			String name,
			String type,
			int decimals,
			Object... possibleValues) {
		if (id == null) {
			throw new NullPointerException("ID can not be null");
		}
		if (name == null) {
			throw new NullPointerException("Name can not be null");
		}
		if (type == null) {
			throw new NullPointerException("Type can not be null");
		}
		type = type.toUpperCase();
		if (!Strings.in(type, NUMBER, STRING, BOOLEAN)) {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
		if (possibleValues.length > 0) {
			for (int i = 0; i < possibleValues.length; i++) {
				Object value = possibleValues[i];
				validate(type, value, "Possible value (" + i + ")");
			}
		}
		this.id = id;
		this.name = name;
		this.type = type;
		this.decimals = decimals;
		this.possibleValues = possibleValues;
	}

	/**
	 * Set the value.
	 *
	 * @param value
	 */
	public void setValue(Object value) {
		validate(type, value, "Value");
		validatePossibleValues(value);
		this.value = Numbers.getBigDecimal((Number) value, decimals);
	}

	/**
	 * Returns the value if it is a number.
	 *
	 * @return The number.
	 */
	public BigDecimal getNumber() {
		if (!isNumber()) {
			throw new IllegalStateException("Value is not a number");
		}
		return (BigDecimal) value;
	}

	public int getDecimals() {
		return decimals;
	}

	/**
	 * Returns the value if it is a string.
	 *
	 * @return The string.
	 */
	public String getString() {
		if (!isString()) {
			throw new IllegalStateException("Value is not a string");
		}
		return (String) value;
	}

	/**
	 * Returns the value if it is a boolean.
	 *
	 * @return The boolean.
	 */
	public Boolean getBoolean() {
		if (!isBoolean()) {
			throw new IllegalStateException("Value is not a boolean");
		}
		return (Boolean) value;
	}

	/**
	 * Check whether the value is a number.
	 *
	 * @return A boolean.
	 */
	public boolean isNumber() {
		return type.equals(NUMBER);
	}
	/**
	 * Check whether the value is a string.
	 *
	 * @return A boolean.
	 */
	public boolean isString() {
		return type.equals(STRING);
	}
	/**
	 * Check whether the value is a boolean.
	 *
	 * @return A boolean.
	 */
	public boolean isBoolean() {
		return type.equals(BOOLEAN);
	}

	/**
	 * Validate the value.
	 *
	 * @param type   The expected type.
	 * @param value  The value.
	 * @param prefix Error prefix.
	 */
	private void validate(String type, Object value, String prefix) {
		if (value == null) {
			throw new IllegalArgumentException(prefix + " can not be null");
		}
		if (type.equals(NUMBER) && !(value instanceof Number)) {
			throw new IllegalArgumentException(prefix + " must be a number");
		}
		if (type.equals(STRING) && !(value instanceof String)) {
			throw new IllegalArgumentException(prefix + " must be a string");
		}
		if (type.equals(BOOLEAN) && !(value instanceof Boolean)) {
			throw new IllegalArgumentException(prefix + " must be a boolean");
		}
	}
	/**
	 * Validate that the argument value matches the possible values.
	 *
	 * @param value The value to check.
	 */
	private void validatePossibleValues(Object value) {
		if (possibleValues.length == 0) return;
		boolean valid = false;
		for (Object possibleValue : possibleValues) {
			if (value.equals(possibleValue)) {
				valid = true;
				break;
			}
		}
		if (!valid) {
			throw new IllegalArgumentException("Invalid value: " + value);
		}
	}
}
