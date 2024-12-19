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

package msfx.ztrash.json;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;

/**
 * An entry of a JSON document or list. Packs the value and the type.
 *
 * @author Miquel Sas
 */
public class JSONEntry {

	/**
	 * The JSON type.
	 */
	private JSONTypes type = null;
	/**
	 * The value.
	 */
	private Object value = null;

	/**
	 * Package private constructor used in the parser.
	 *
	 * @param type  The {@link JSONTypes}.
	 * @param value The value.
	 */
	public JSONEntry(JSONTypes type, Object value) {
		setValue(type, value);
	}

	/**
	 * Constructor of a NULL entry.
	 */
	public JSONEntry() {
		setValue(JSONTypes.NULL, null);
	}
	/**
	 * Constructor of a {@link JSONObject} entry.
	 *
	 * @param value The not null {@link JSONObject} value.
	 */
	public JSONEntry(JSONObject value) {
		setValue(JSONTypes.OBJECT, value);
	}
	/**
	 * Constructor of a {@link JSONArray} entry.
	 *
	 * @param value The not null {@link JSONArray} value.
	 */
	public JSONEntry(JSONArray value) {
		setValue(JSONTypes.ARRAY, value);
	}
	/**
	 * Constructor of a {@link String} entry.
	 *
	 * @param value The not null {@link String} value.
	 */
	public JSONEntry(String value) {
		setValue(JSONTypes.STRING, value);
	}
	/**
	 * Constructor of a {@link Number} entry.
	 *
	 * @param value The not null {@link Number} value.
	 */
	public JSONEntry(Number value) {
		setValue(JSONTypes.NUMBER, value);
	}
	/**
	 * Constructor of a {@link Boolean} entry.
	 *
	 * @param value The not null {@link Boolean} value.
	 */
	public JSONEntry(Boolean value) {
		setValue(JSONTypes.BOOLEAN, value);
	}
	/**
	 * Constructor of a byte[] entry.
	 *
	 * @param value The not null byte[] value.
	 */
	public JSONEntry(byte[] value) {
		setValue(JSONTypes.BINARY, value);
	}
	/**
	 * Constructor of a {@link LocalDate} entry.
	 *
	 * @param value The not null {@link LocalDate} value.
	 */
	public JSONEntry(LocalDate value) {
		setValue(JSONTypes.DATE, value);
	}
	/**
	 * Constructor of a {@link Date} entry.
	 *
	 * @param value The not null {@link Date} value.
	 */
	public JSONEntry(Date value) {
		setValue(JSONTypes.DATE, value);
	}
	/**
	 * Constructor of a {@link LocalTime} entry.
	 *
	 * @param value The not null {@link LocalTime} value.
	 */
	public JSONEntry(LocalTime value) {
		setValue(JSONTypes.TIME, value);
	}
	/**
	 * Constructor of a {@link Time} entry.
	 *
	 * @param value The not null {@link Time} value.
	 */
	public JSONEntry(Time value) {
		setValue(JSONTypes.TIME, value);
	}
	/**
	 * Constructor of a {@link LocalDateTime} entry.
	 *
	 * @param value The not null {@link LocalDateTime} value.
	 */
	public JSONEntry(LocalDateTime value) {
		setValue(JSONTypes.TIMESTAMP, value);
	}
	/**
	 * Constructor of a {@link Timestamp} entry.
	 *
	 * @param value The not null {@link Timestamp} value.
	 */
	public JSONEntry(Timestamp value) {
		setValue(JSONTypes.TIMESTAMP, value);
	}

	/**
	 * @return The internal {@link JSONObject} value.
	 */
	public JSONObject getObject() {
		return (JSONObject) getValue(JSONTypes.OBJECT);
	}
	/**
	 * @return The internal {@link JSONArray} value.
	 */
	public JSONArray getArray() {
		return (JSONArray) getValue(JSONTypes.ARRAY);
	}
	/**
	 * @return The internal {@link String} value.
	 */
	public String getString() {
		return (String) getValue(JSONTypes.STRING);
	}
	/**
	 * @return The internal {@link BigDecimal} value.
	 */
	public BigDecimal getNumber() {
		return (BigDecimal) getValue(JSONTypes.NUMBER);
	}
	/**
	 * @return The internal {@link Boolean} value.
	 */
	public Boolean getBoolean() {
		return (Boolean) getValue(JSONTypes.BOOLEAN);
	}
	/**
	 * @return The internal byte[] value.
	 */
	public byte[] getBinary() {
		return (byte[]) getValue(JSONTypes.BINARY);
	}
	/**
	 * @return The internal {@link LocalDate} value.
	 */
	public LocalDate getDate() {
		return (LocalDate) getValue(JSONTypes.DATE);
	}
	/**
	 * @return The internal {@link LocalTime} value.
	 */
	public LocalTime getTime() {
		return (LocalTime) getValue(JSONTypes.TIME);
	}
	/**
	 * @return The internal {@link LocalDateTime} value.
	 */
	public LocalDateTime getTimestamp() {
		return (LocalDateTime) getValue(JSONTypes.TIMESTAMP);
	}

	/**
	 * @return The {@link JSONTypes} type.
	 */
	public JSONTypes getType() {
		return type;
	}

	/**
	 * @param value The not null {@link JSONObject} value.
	 */
	public void set(JSONObject value) {
		setValue(JSONTypes.OBJECT, value);
	}
	/**
	 * @param value The not null {@link JSONArray} value.
	 */
	public void set(JSONArray value) {
		setValue(JSONTypes.ARRAY, value);
	}
	/**
	 * @param value The not null {@link String} value.
	 */
	public void set(String value) {
		setValue(JSONTypes.STRING, value);
	}
	/**
	 * @param value The not null {@link Number} value.
	 */
	public void set(Number value) {
		setValue(JSONTypes.NUMBER, value);
	}
	/**
	 * @param value The not null {@link Number} value.
	 */
	public void set(Boolean value) {
		setValue(JSONTypes.BOOLEAN, value);
	}
	/**
	 * @param value The not null byte[] value.
	 */
	public void set(byte[] value) {
		setValue(JSONTypes.BINARY, value);
	}
	/**
	 * @param value The not null {@link LocalDate} value.
	 */
	public void set(LocalDate value) {
		setValue(JSONTypes.DATE, value);
	}
	/**
	 * @param value The not null {@link Date} value.
	 */
	public void set(Date value) {
		setValue(JSONTypes.DATE, value);
	}
	/**
	 * @param value The not null {@link LocalTime} value.
	 */
	public void set(LocalTime value) {
		setValue(JSONTypes.TIME, value);
	}
	/**
	 * @param value The not null {@link Time} value.
	 */
	public void set(Time value) {
		setValue(JSONTypes.TIME, value);
	}
	/**
	 * @param value The not null {@link LocalDateTime} value.
	 */
	public void set(LocalDateTime value) {
		setValue(JSONTypes.TIMESTAMP, value);
	}
	/**
	 * @param value The not null {@link Timestamp} value.
	 */
	public void set(Timestamp value) {
		setValue(JSONTypes.TIMESTAMP, value);
	}

	/**
	 * Returns the internal value validating that it is of argument type.
	 *
	 * @param type The type to validate the value.
	 * @return The value.
	 */
	private Object getValue(JSONTypes type) {
		if (this.type != type) {
			throw new IllegalStateException("Invalid type " + type);
		}
		return value;
	}

	/**
	 * Set the value.
	 *
	 * @param type  The JSON type.
	 * @param value The value.
	 */
	private void setValue(JSONTypes type, Object value) {

		/* Only NULL and extended types can have a null value. */
		if (type != JSONTypes.NULL && !type.isExtended() && value == null) {
			throw new NullPointerException("Value can not be null for type " + type);
		}

		/* If the internal type is not null, check it. */
		if (this.type != null && this.type != type) {
			throw new IllegalStateException("Can not change the type " + type);
		}

		/* Register the type. */
		this.type = type;

		/* Standard types. */
		if (type == JSONTypes.OBJECT) {
			this.value = value;
		}
		if (type == JSONTypes.ARRAY) {
			this.value = value;
		}
		if (type == JSONTypes.STRING) {
			this.value = value;
		}
		if (type == JSONTypes.NUMBER) {
			this.value = new BigDecimal(value.toString());
		}
		if (type == JSONTypes.BOOLEAN) {
			this.value = value;
		}

		/* Extended types. */
		if (type == JSONTypes.DATE) {
			if (value instanceof Date date) {
				Instant instant = Instant.ofEpochMilli(date.getTime());
				this.value = instant.atZone(ZoneId.systemDefault()).toLocalDate();
			} else {
				this.value = value;
			}
		}
		if (type == JSONTypes.TIME) {
			if (value instanceof Time time) {
				Instant instant = Instant.ofEpochMilli(time.getTime());
				this.value = instant.atZone(ZoneId.systemDefault()).toLocalTime();
			} else {
				this.value = value;
			}
		}
		if (type == JSONTypes.TIMESTAMP) {
			if (value instanceof Timestamp timestamp) {
				Instant instant = Instant.ofEpochMilli(timestamp.getTime());
				this.value = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
			} else {
				this.value = value;
			}
		}
	}
}
