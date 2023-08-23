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

package msfx.lib.db.converters;

import msfx.lib.db.Field;
import msfx.lib.db.Value;
import msfx.lib.util.Formats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * String converter for values of type number.
 *
 * @author Miquel Sas
 */
public class NumberStringConverter implements ValueStringConverter {

	/**
	 * Field.
	 */
	private final Field field;
	/**
	 * Locale.
	 */
	private final Locale locale;

	/**
	 * Constructor.
	 *
	 * @param field Field.
	 */
	public NumberStringConverter(Field field) {
		this(field, Locale.getDefault());
	}
	/**
	 * Constructor.
	 *
	 * @param field  Field.
	 * @param locale Locale.
	 */
	public NumberStringConverter(Field field, Locale locale) {
		if (!field.isNumber()) throw new IllegalArgumentException("Field is not a number");
		this.field = field;
		this.locale = locale;
	}
	/**
	 * Return the value given the string.
	 */
	@Override
	public Value fromString(String string) {
		try {
			if (field.isDecimal()) {
				BigDecimal value = Formats.toBigDecimal(string, locale);
				value = value.setScale(field.getDecimals(), RoundingMode.HALF_UP);
				return new Value(value);
			}
			if (field.isDouble()) {
				double value = Formats.toDouble(string, locale);
				return new Value(value);
			}
			if (field.isInteger()) {
				int value = Formats.toInteger(string, locale);
				return new Value(value);
			}
			if (field.isLong()) {
				long value = Formats.toLong(string, locale);
				return new Value(value);
			}
		} catch (java.text.ParseException exc) {
			exc.printStackTrace();
		}
		return null;
	}
	/**
	 * Return the string given the value.
	 */
	@Override
	public String toString(Value value) {
		if (value == null) return toString(field.getDefaultValue());
		Integer decimals = field.getDisplayDecimals();
		if (decimals == null) decimals = 0;
		return Formats.getNumberFormat(decimals, locale).format(value.getDouble());
	}
}