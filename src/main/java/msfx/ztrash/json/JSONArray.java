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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A list of {@link JSONEntry} entries.
 *
 * @author Miquel Sas
 */
public class JSONArray implements Iterable<JSONEntry> {

	/**
	 * List of entries.
	 */
	private final List<JSONEntry> entries = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public JSONArray() {
	}

	/**
	 * @param entry A non null {@link JSONEntry}.
	 */
	public void add(JSONEntry entry) {
		if (entry == null) {
			throw new NullPointerException("Entry can not be null");
		}
		entries.add(entry);
	}
	/**
	 * @param value A {@link JSONObject} value.
	 */
	public void add(JSONObject value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A {@link JSONArray} value.
	 */
	public void add(JSONArray value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A {@link String} value.
	 */
	public void add(String value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A {@link Number} value.
	 */
	public void add(Number value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A {@link Boolean} value.
	 */
	public void add(Boolean value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A byte[] value.
	 */
	public void add(byte[] value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A {@link LocalDate} value.
	 */
	public void add(LocalDate value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A {@link Date} value.
	 */
	public void add(Date value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A {@link LocalTime} value.
	 */
	public void add(LocalTime value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A {@link Time} value.
	 */
	public void add(Time value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A {@link LocalDateTime} value.
	 */
	public void add(LocalDateTime value) {
		add(new JSONEntry(value));
	}
	/**
	 * @param value A {@link Timestamp} value.
	 */
	public void add(Timestamp value) {
		add(new JSONEntry(value));
	}

	/**
	 * @param index The index within the list.
	 * @param entry A non null {@link JSONEntry}.
	 */
	public void add(int index, JSONEntry entry) {
		if (entry == null) {
			throw new NullPointerException("Entry can not be null");
		}
		entries.add(index, entry);
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link JSONObject} value.
	 */
	public void add(int index, JSONObject value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link JSONArray} value.
	 */
	public void add(int index, JSONArray value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link String} value.
	 */
	public void add(int index, String value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link Number} value.
	 */
	public void add(int index, Number value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link Boolean} value.
	 */
	public void add(int index, Boolean value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A byte[] value.
	 */
	public void add(int index, byte[] value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link LocalDate} value.
	 */
	public void add(int index, LocalDate value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link Date} value.
	 */
	public void add(int index, Date value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link LocalTime} value.
	 */
	public void add(int index, LocalTime value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link Time} value.
	 */
	public void add(int index, Time value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link LocalDateTime} value.
	 */
	public void add(int index, LocalDateTime value) {
		add(index, new JSONEntry(value));
	}
	/**
	 * @param index The index within the list.
	 * @param value A {@link Timestamp} value.
	 */
	public void add(int index, Timestamp value) {
		add(index, new JSONEntry(value));
	}

	/**
	 * @param index The index of the entry.
	 * @return The {@link JSONEntry} at the given index.
	 */
	public JSONEntry get(int index) {
		return entries.get(index);
	}
	/**
	 * @param index The index of the entry.
	 * @return The {@link JSONObject} value at the given index.
	 */
	public JSONObject getObject(int index) {
		return get(index).getObject();
	}
	/**
	 * @param index The index of the entry.
	 * @return The {@link JSONArray} value at the given index.
	 */
	public JSONArray getArray(int index) {
		return get(index).getArray();
	}
	/**
	 * @param index The index of the entry.
	 * @return The {@link String} value at the given index.
	 */
	public String getString(int index) {
		return get(index).getString();
	}
	/**
	 * @param index The index of the entry.
	 * @return The {@link BigDecimal} value at the given index.
	 */
	public BigDecimal getNumber(int index) {
		return get(index).getNumber();
	}
	/**
	 * @param index The index of the entry.
	 * @return The {@link Boolean} value at the given index.
	 */
	public Boolean getBoolean(int index) {
		return get(index).getBoolean();
	}
	/**
	 * @param index The index of the entry.
	 * @return The byte[] value at the given index.
	 */
	public byte[] getBinary(int index) {
		return get(index).getBinary();
	}
	/**
	 * @param index The index of the entry.
	 * @return The {@link LocalDate} value at the given index.
	 */
	public LocalDate getDate(int index) {
		return get(index).getDate();
	}
	/**
	 * @param index The index of the entry.
	 * @return The {@link LocalTime} value at the given index.
	 */
	public LocalTime getTime(int index) {
		return get(index).getTime();
	}
	/**
	 * @param index The index of the entry.
	 * @return The {@link LocalDateTime} value at the given index.
	 */
	public LocalDateTime getTimestamp(int index) {
		return get(index).getTimestamp();
	}

	/**
	 * @param index The index to remove the entry.
	 * @return The removed {@link JSONEntry} at the given index.
	 */
	public JSONEntry remove(int index) {
		return entries.remove(index);
	}

	/**
	 * @param index The index to set the value.
	 * @param entry The {@link JSONEntry} to set.
	 */
	public void set(int index, JSONEntry entry) {
		entries.set(index, entry);
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link JSONObject} value to set.
	 */
	public void set(int index, JSONObject value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link JSONArray} value to set.
	 */
	public void set(int index, JSONArray value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link String} value to set.
	 */
	public void set(int index, String value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link Number} value to set.
	 */
	public void set(int index, Number value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link Boolean} value to set.
	 */
	public void set(int index, Boolean value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The byte[] value to set.
	 */
	public void set(int index, byte[] value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link LocalDate} value to set.
	 */
	public void set(int index, LocalDate value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link Date} value to set.
	 */
	public void set(int index, Date value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link LocalTime} value to set.
	 */
	public void set(int index, LocalTime value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link Time} value to set.
	 */
	public void set(int index, Time value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link LocalDateTime} value to set.
	 */
	public void set(int index, LocalDateTime value) {
		set(index, new JSONEntry(value));
	}
	/**
	 * @param index The index to set the value.
	 * @param value The {@link Timestamp} value to set.
	 */
	public void set(int index, Timestamp value) {
		set(index, new JSONEntry(value));
	}

	/**
	 * @return An iterator over the list of entries.
	 */
	public Iterator<JSONEntry> iterator() {
		return entries.iterator();
	}

	/**
	 * @return The size of the array.
	 */
	public int size() {
		return entries.size();
	}
}
