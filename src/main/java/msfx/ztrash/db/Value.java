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

import msfx.ztrash.json.JSONArray;
import msfx.ztrash.json.JSONEntry;
import msfx.ztrash.json.JSONObject;
import msfx.lib.util.Numbers;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Objects;

/**
 * A value contains a reference to one of the supported types and its value.
 * <p>
 * Boolean, numeric and string values can not be null.
 *
 * @author Miquel Sas
 */
public class Value implements Comparable<Object> {

	/**
	 * Compare when one or both arguments is null.
	 *
	 * @param null_a Argument a.
	 * @param null_b Argument b.
	 * @return Comparison integer.
	 */
	public static int compareNulls(boolean null_a, boolean null_b) {
		if (!null_a && null_b) return -1;
		if (null_a && !null_b) return 1;
		if (null_a && null_b) return 0;
		throw new IllegalArgumentException("At least one must be null");
	}

	/**
	 * Compare two arrays of bytes for order.
	 *
	 * @param b1 the first byte array.
	 * @param b2 the second byte array.
	 * @return The comparison integer -1, 0 or 1.
	 */
	public static int compare(byte[] b1, byte[] b2) {
		if (b1 == null && b2 == null) return 0;
		if (b1 != null && b2 == null) return 1;
		if (b1 == null && b2 != null) return -1;
		int size = Math.max(b1.length, b2.length);
		for (int i = 0; i < size; i++) {
			if (i >= b1.length) return -1;
			if (i >= b2.length) return 1;
			int compare = Byte.compare(b1[i], b2[i]);
			if (compare != 0) return compare;
		}
		return 0;
	}

	/**
	 * Compare two arrays of values for order.
	 *
	 * @param a1 Value array 1.
	 * @param a2 Value array 2.
	 * @return The comparison integer -1, 0 or 1.
	 */
	public static int compare(Value[] a1, Value[] a2) {
		if (a1 == null && a2 == null) return 0;
		if (a1 != null && a2 == null) return 1;
		if (a1 == null && a2 != null) return -1;
		int size = Math.max(a1.length, a2.length);
		for (int i = 0; i < size; i++) {
			if (i >= a1.length)
				return -1;
			if (i >= a2.length)
				return 1;
			int compare = a1[i].compareTo(a2[i]);
			if (compare != 0)
				return compare;
		}
		return 0;
	}

	/**
	 * Return a {@link JSONArray} that maps the array of values.
	 *
	 * @param value_array The array o values.
	 * @return The {@link JSONArray}.
	 */
	public static JSONArray toJSONArray(Value[] value_array) {
		JSONArray json_array = new JSONArray();
		for (Value value : value_array) {
			json_array.add(value.toJSONEntry());
		}
		return json_array;
	}
	/**
	 * Return a {@link JSONObject} that maps the argument value array.
	 *
	 * @param value_array The array o values.
	 * @return The {@link JSONObject}.
	 */
	public static JSONObject toJSONObject(Value[] value_array) {
		JSONObject json_obj = new JSONObject();
		json_obj.put("value_array", toJSONArray(value_array));
		return json_obj;
	}
	/**
	 * Convert a {@link JSONEntry} to a {@link Value}.
	 *
	 * @param entry The {@link JSONEntry}.
	 * @return The corresponding {@link Value}.
	 */
	public static Value toValue(JSONEntry entry) {
		switch (entry.getType()) {
		case NULL:
			return new Value((String) null);
		case BOOLEAN:
			return new Value(entry.getBoolean());
		case DATE:
			return new Value(entry.getDate());
		case TIME:
			return new Value(entry.getTime());
		case TIMESTAMP:
			return new Value(entry.getTimestamp());
		case NUMBER:
			return new Value(entry.getNumber());
		case BINARY:
			return new Value(entry.getBinary());
		case STRING:
			return new Value(entry.getString());
		case OBJECT:
			return new Value(entry.getObject());
		case ARRAY:
			return new Value(toValueArray(entry.getArray()));
		}
		throw new IllegalStateException("Unreachable code");
	}
	/**
	 * Convert the argument <i>JSONArray</i> to a <i>Value[]</i>.
	 *
	 * @param json_array The <i>JSONArray</i> to convert.
	 * @return The corresponding <i>Value[]</i>.
	 */
	public static Value[] toValueArray(JSONArray json_array) {
		if (json_array == null) {
			return new Value[] {};
		}
		Value[] value_array = new Value[json_array.size()];
		for (int i = 0; i < json_array.size(); i++) {
			value_array[i] = toValue(json_array.get(i));
		}
		return value_array;
	}
	/**
	 * Returns a value array from a <i>JSONObject</i> that contains a
	 * <i>JSONArray</i> that maps the value array.
	 *
	 * @param json_obj The source <i>JSONObject</i>.
	 * @return The value array.
	 */
	public static Value[] toValueArray(JSONObject json_obj) {
		JSONArray json_arr = json_obj.get("value_array").getArray();
		Value[] values = Value.toValueArray(json_arr);
		return values;
	}

	/**
	 * Internal value.
	 */
	private final Object value;
	/**
	 * The type, registered to track it for null values.
	 */
	private final Types type;

	/**
	 * Constructor of a BOOLEAN value.
	 *
	 * @param value The value.
	 */
	public Value(Boolean value) {
		if (value == null) value = false;
		this.type = Types.BOOLEAN;
		this.value = value;
	}
	/**
	 * Constructor of a DECIMAL value.
	 *
	 * @param value The value.
	 */
	public Value(BigDecimal value) {
		if (value == null) value = BigDecimal.valueOf(0);
		this.type = Types.DECIMAL;
		this.value = value;
	}
	/**
	 * Constructor of a DOUBLE value.
	 *
	 * @param value The value.
	 */
	public Value(Double value) {
		if (value == null) value = 0.0;
		this.type = Types.DOUBLE;
		this.value = value;
	}
	/**
	 * Constructor of a INTEGER value.
	 *
	 * @param value The value.
	 */
	public Value(Integer value) {
		if (value == null) value = 0;
		this.type = Types.INTEGER;
		this.value = value;
	}
	/**
	 * Constructor of a LONG value.
	 *
	 * @param value The value.
	 */
	public Value(Long value) {
		if (value == null) value = 0L;
		this.type = Types.LONG;
		this.value = value;
	}
	/**
	 * Constructor of a DATE value.
	 *
	 * @param value The value.
	 */
	public Value(LocalDate value) {
		this.type = Types.DATE;
		this.value = value;
	}
	/**
	 * Constructor of a TIME value.
	 *
	 * @param value The value.
	 */
	public Value(LocalTime value) {
		this.type = Types.TIME;
		this.value = value;
	}
	/**
	 * Constructor of a TIMESTAMP value.
	 *
	 * @param value The value.
	 */
	public Value(LocalDateTime value) {
		this.type = Types.TIMESTAMP;
		this.value = value;
	}
	/**
	 * Constructor of a DATE value.
	 *
	 * @param value The value.
	 */
	public Value(Date value) {
		this.type = Types.DATE;
		Instant instant = Instant.ofEpochMilli(value.getTime());
		this.value = instant.atZone(ZoneId.systemDefault()).toLocalDate();
	}
	/**
	 * Constructor of a TIME value.
	 *
	 * @param value The value.
	 */
	public Value(Time value) {
		this.type = Types.TIME;
		Instant instant = Instant.ofEpochMilli(value.getTime());
		this.value = instant.atZone(ZoneId.systemDefault()).toLocalTime();
	}
	/**
	 * Constructor of a TIMESTAMP value.
	 *
	 * @param value The value.
	 */
	public Value(Timestamp value) {
		this.type = Types.TIMESTAMP;
		Instant instant = Instant.ofEpochMilli(value.getTime());
		this.value = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	/**
	 * Constructor of a STRING value.
	 *
	 * @param value The value.
	 */
	public Value(String value) {
		if (value == null) value = "";
		this.type = Types.STRING;
		this.value = value;
	}
	/**
	 * Constructor of a BINARY value.
	 *
	 * @param value The value.
	 */
	public Value(byte[] value) {
		this.type = Types.BINARY;
		this.value = value;
	}
	/**
	 * Constructor of an ARRAY value.
	 *
	 * @param value The value.
	 */
	public Value(Value[] value) {
		this.type = Types.ARRAY;
		this.value = value;
	}
	/**
	 * Constructor of a OBJECT value.
	 *
	 * @param value The value.
	 */
	public Value(JSONObject value) {
		this.type = Types.OBJECT;
		this.value = value;
	}

	/**
	 * Return the {@link Boolean} value.
	 *
	 * @return The {@link Boolean} value or null.
	 * @throws ClassCastException if the type is not a <i>BOOLEAN</i>.
	 */
	public Boolean getBoolean() {
		if (type != Types.BOOLEAN) throw new ClassCastException("Type is not a BOOLEAN");
		return (Boolean) value;
	}

	/**
	 * Return the {@link BigDecimal value.
	 * <p>
	 * @return The  {@link BigDecimal value or null.
	 * @throws ClassCastException if the type is not numeric.
	 */
	public BigDecimal getBigDecimal() {
		if (!type.isNumber()) throw new ClassCastException("Type is not numeric");
		if (value == null) return null;
		if (type == Types.DECIMAL) return (BigDecimal) value;
		return BigDecimal.valueOf(((Number) value).doubleValue());
	}
	/**
	 * Return the {@link Double value.
	 * <p>
	 * @return The  {@link Double value or null.
	 * @throws ClassCastException if the type is not numeric.
	 */
	public Double getDouble() {
		if (!type.isNumber()) throw new ClassCastException("Type is not numeric");
		if (value == null) return null;
		if (type == Types.DOUBLE) return (Double) value;
		return ((Number) value).doubleValue();
	}
	/**
	 * Return the {@link Integer value.
	 * <p>
	 * @return The  {@link Integer value or null.
	 * @throws ClassCastException if the type is not numeric.
	 */
	public Integer getInteger() {
		if (!type.isNumber()) throw new ClassCastException("Type is not numeric");
		if (value == null) return null;
		if (type == Types.INTEGER) return (Integer) value;
		return ((Number) value).intValue();
	}
	/**
	 * Return the {@link Long value.
	 * <p>
	 * @return The  {@link Long value or null.
	 * @throws ClassCastException if the type is not numeric.
	 */
	public Long getLong() {
		if (!type.isNumber()) throw new ClassCastException("Type is not numeric");
		if (value == null) return null;
		if (type == Types.LONG) return (Long) value;
		return ((Number) value).longValue();
	}
	/**
	 * Return the {@link Number value.
	 * <p>
	 * @return The  {@link Number value or null.
	 * @throws ClassCastException if the type is not numeric.
	 */
	public Number getNumber() {
		if (!type.isNumber()) throw new ClassCastException("Type is not numeric");
		return ((Number) value);
	}

	/**
	 * Return the {@link LocalDate value.
	 * <p>
	 * @return The  {@link LocalDate value or null.
	 * @throws ClassCastException if the type is not a <i>DATE</i>.
	 */
	public LocalDate getDate() {
		if (type != Types.DATE) throw new ClassCastException("Type is not a DATE");
		return (LocalDate) value;
	}
	/**
	 * Return the {@link LocalTime value.
	 * <p>
	 * @return The  {@link LocalTime value or null.
	 * @throws ClassCastException if the type is not a <i>TIME</i>.
	 */
	public LocalTime getTime() {
		if (type != Types.TIME) throw new ClassCastException("Type is not a TIME");
		return (LocalTime) value;
	}
	/**
	 * Return the {@link LocalDateTime value.
	 * <p>
	 * @return The  {@link LocalDateTime value or null.
	 * @throws ClassCastException if the type is not a <i>TIMESTAMP</i>.
	 */
	public LocalDateTime getTimestamp() {
		if (type != Types.TIMESTAMP) throw new ClassCastException("Type is not a TIMESTAMP");
		return (LocalDateTime) value;
	}

	/**
	 * Return the ´@link String value.
	 *
	 * @return The e {@link String} value or null.
	 * @throws ClassCastException if the type is not a <i>STRING</i>.
	 */
	public String getString() {
		if (type != Types.STRING) throw new ClassCastException("Type is not a STRING");
		return (String) value;
	}

	/**
	 * Return the <i>byte[]</i> value.
	 *
	 * @return The <i>byte[]</i> value or null.
	 * @throws ClassCastException if the type is not a <i>BINARY</i>.
	 */
	public byte[] getBinary() {
		if (type != Types.BINARY) throw new ClassCastException("Type is not a BINARY");
		return (byte[]) value;
	}

	/**
	 * Return the <i>Value[]</i> value.
	 *
	 * @return The <i>Value[]</i> value or null.
	 * @throws ClassCastException if the type is not an <i>ARRAY</i>.
	 */
	public Value[] getArray() {
		if (type != Types.ARRAY) throw new ClassCastException("Type is not a ARRAY");
		return (Value[]) value;
	}

	/**
	 * Return the {@link JSONObject value.
	 * <p>
	 * @return The  {@link JSONObject value or null.
	 * @throws ClassCastException if the type is not a <i>JSONOBJECT</i>.
	 */
	public JSONObject getObject() {
		if (type != Types.OBJECT) throw new ClassCastException("Type is not a OBJECT");
		return (JSONObject) value;
	}

	/**
	 * Return the type.
	 *
	 * @return The type.
	 */
	public Types getType() {
		return type;
	}

	/**
	 * Check whether the value is null.
	 *
	 * @return A boolean.
	 */
	public boolean isNull() {
		return value == null;
	}
	/**
	 * Return a boolean indicating whether the type is BOOLEAN.
	 *
	 * @return A boolean.
	 */
	public boolean isBoolean() {
		return type.isBoolean();
	}
	/**
	 * Return a boolean indicating whether the type is DECIMAL.
	 *
	 * @return A boolean.
	 */
	public boolean isDecimal() {
		return type.isDecimal();
	}
	/**
	 * Return a boolean indicating whether the type is DOUBLE.
	 *
	 * @return A boolean.
	 */
	public boolean isDouble() {
		return type.isDouble();
	}
	/**
	 * Return a boolean indicating whether the type is INTEGER.
	 *
	 * @return A boolean.
	 */
	public boolean isInteger() {
		return type.isInteger();
	}
	/**
	 * Return a boolean indicating whether the type is LONG.
	 *
	 * @return A boolean.
	 */
	public boolean isLong() {
		return type.isLong();
	}
	/**
	 * Return a boolean indicating whether the type is a number.
	 *
	 * @return A boolean.
	 */
	public boolean isNumber() {
		return type.isNumber();
	}
	/**
	 * Return a boolean indicating whether the type is DATE.
	 *
	 * @return A boolean.
	 */
	public boolean isDate() {
		return type.isDate();
	}
	/**
	 * Return a boolean indicating whether the type is TIME.
	 *
	 * @return A boolean.
	 */
	public boolean isTime() {
		return type.isTime();
	}
	/**
	 * Return a boolean indicating whether the type is TIMESTAMP.
	 *
	 * @return A boolean.
	 */
	public boolean isTimestamp() {
		return type.isTimestamp();
	}
	/**
	 * Return a boolean indicating whether the type is STRING.
	 *
	 * @return A boolean.
	 */
	public boolean isString() {
		return type.isString();
	}
	/**
	 * Return a boolean indicating whether the type is BINARY.
	 *
	 * @return A boolean.
	 */
	public boolean isBinary() {
		return type.isBinary();
	}
	/**
	 * Return a boolean indicating whether the type an ARRAY.
	 *
	 * @return A boolean.
	 */
	public boolean isArray() {
		return type.isArray();
	}
	/**
	 * Return a boolean indicating whether the type is OBJECT.
	 *
	 * @return A boolean.
	 */
	public boolean isObject() {
		return type.isObject();
	}

	/**
	 * Compare.
	 *
	 * @param o The object to be compared.
	 * @return A comparison integer,-1, 0 or 1.
	 */
	@Override
	public int compareTo(Object o) {

		/* No null compare. */
		if (o == null) throw new NullPointerException();

		/* Value. */
		if (o instanceof Value v) {
			if (type.isNumber() && v.type.isNumber()) {
				return Numbers.compare(getNumber(), v.getNumber());
			}
			if (type.isBoolean() && v.type.isBoolean()) {
				return getBoolean().compareTo(v.getBoolean());
			}
			if (type.isDate() && v.type.isDate()) {
				if (!isNull() && !v.isNull()) return getDate().compareTo(v.getDate());
				else return compareNulls(isNull(), v.isNull());
			}
			if (type.isTime() && v.type.isTime()) {
				if (!isNull() && !v.isNull()) return getTime().compareTo(v.getTime());
				else return compareNulls(isNull(), v.isNull());
			}
			if (type.isTimestamp() && v.type.isTimestamp()) {
				if (!isNull() && !v.isNull()) return getTimestamp().compareTo(v.getTimestamp());
				else return compareNulls(isNull(), v.isNull());
			}
			if (type.isString() && v.type.isString()) {
				if (!isNull() && !v.isNull()) return getString().compareTo(v.getString());
				else return compareNulls(isNull(), v.isNull());
			}
			if (type.isBinary() && v.type.isBinary()) {
				if (!isNull() && !v.isNull()) return compare(getBinary(), v.getBinary());
				else return compareNulls(isNull(), v.isNull());
			}
			if (type.isArray() && v.type.isArray()) {
				if (!isNull() && !v.isNull()) return compare(getArray(), v.getArray());
				else return compareNulls(isNull(), v.isNull());
			}
			if (type.isObject() && v.type.isObject()) {
				if (!isNull() && !v.isNull()) return toString().compareTo(v.toString());
				else return compareNulls(isNull(), v.isNull());
			}
			throw new IllegalStateException("Never should happen");
		}

		/* Booleans. */
		if (type.isBoolean() && (o instanceof Boolean b)) {
			return getBoolean().compareTo(b);
		}

		/* Numbers. */
		if (type.isNumber() && (o instanceof Number n)) {
			return Numbers.compare(getNumber(), n);
		}

		/* Date. */
		if (type.isDate() && (o instanceof LocalDate d)) {
			if (isNull()) return -1;
			return getDate().compareTo(d);
		}
		/* Time. */
		if (type.isTime() && (o instanceof LocalTime t)) {
			if (isNull()) return -1;
			return getTime().compareTo(t);
		}
		/* Timestamp. */
		if (type.isTimestamp() && (o instanceof LocalDateTime t)) {
			if (isNull()) return -1;
			return getTimestamp().compareTo(t);
		}

		/* String. */
		if (type.isString() && (o instanceof String s)) {
			if (isNull()) return -1;
			return getString().compareTo(s);
		}

		/* Binary. */
		if (type.isBinary() && (o instanceof byte[] b)) {
			if (isNull()) return -1;
			return compare(getBinary(), b);
		}

		/* Value[]. */
		if (type.isArray() && (o instanceof Value[] a)) {
			if (isNull()) return -1;
			return compare(getArray(), a);
		}

		/* JSONObject. */
		if (type.isObject() && (o instanceof JSONObject j)) {
			return getObject().toString().compareTo(j.toString());
		}

		throw new IllegalStateException("Not comparable types");
	}

	/**
	 * Check equality.
	 *
	 * @param o The object to check.
	 * @return A boolean indicating whether the argument object is equal to this object.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Value v){
			return compareTo(v) == 0;
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	/**
	 * Returns a proper JSON entry.
	 *
	 * @return A JSON entry for this value.
	 */
	public JSONEntry toJSONEntry() {
		return new JSONEntry(type.toJSONTypes(), value);
	}

	@Override
	public String toString() {
		if (value == null) return "null";
		return value.toString();
	}
}
