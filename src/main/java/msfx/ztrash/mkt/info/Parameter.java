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

package msfx.ztrash.mkt.info;

import msfx.lib.util.Numbers;
import msfx.lib.util.Strings;
import msfx.lib.util.funtion.Consumer;
import msfx.lib.util.funtion.Function;

import java.math.BigDecimal;

/**
 * Parameter of an indicator.
 *
 * @author Miquel Sas
 */
public class Parameter {

	public static final String NUMBER = "NUMBER";
	public static final String STRING = "STRING";
	public static final String BOOLEAN = "BOOLEAN";

	/**
	 * ID.
	 */
	private String id;
	/**
	 * Name.
	 */
	private String name;
	/**
	 * Title.
	 */
	private String title;
	/**
	 * Type, NUMBER, STRING or BOOLEAN.
	 */
	private String type = NUMBER;
	/**
	 * Number of decimal places when this parameter is a number.
	 */
	private int decimals = 0;
	/**
	 * List of possible values.
	 */
	private Object[] possibleValues = new Object[0];

	/**
	 * Value.
	 */
	private Object value;
	/**
	 * Value function to be called when the parameter value is set.
	 */
	private Consumer.P1<Object> valueFunction;
	/**
	 * Validator function to be called when the parameter value is set.
	 */
	private Function.P1<Boolean, Object> validatorFunction;

	/**
	 * Constructor.
	 */
	public Parameter() { }

	/**
	 * Return the ID.
	 *
	 * @return The ID.
	 */
	public String getId() {
		return id;
	}
	/**
	 * Set the ID:
	 *
	 * @param id The ID.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * Return the name.
	 *
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Set the name.
	 *
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Return the title.
	 *
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Set the title.
	 *
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * Return the type.
	 *
	 * @return The type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * Set the type, either NUMBER, STRING or BOOLEAN.
	 *
	 * @param type The type.
	 */
	public void setType(String type) {
		type = type.toUpperCase();
		if (!Strings.in(type, NUMBER, STRING, BOOLEAN)) {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
		this.type = type;
	}
	public Object[] getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(Object... possibleValues) {
		if (possibleValues.length > 0) {
			for (int i = 0; i < possibleValues.length; i++) {
				Object value = possibleValues[i];
				validate(type, value, "Possible value (" + i + ")");
			}
		}
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
		if (type.equals("NUMBER")) {
			this.value = Numbers.getBigDecimal((Number) value, decimals);
		} else {
			this.value = value;
		}
		if (valueFunction != null) {
			valueFunction.call(this.value);
		}
	}
	/**
	 * Set the value function to be called when the parameter value is set.
	 *
	 * @param valueFunction The value function or consumer.
	 */
	public void setValueFunction(Consumer.P1<Object> valueFunction) {
		this.valueFunction = valueFunction;
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
	/**
	 * Returns the number of decimals when the value is a number.
	 *
	 * @return The number of decimal places.
	 */
	public int getDecimals() {
		return decimals;
	}
	public void setDecimals(int decimals) {
		this.decimals = decimals;
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
