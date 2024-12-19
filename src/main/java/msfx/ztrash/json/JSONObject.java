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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A JSONObject implemented using a {@link LinkedHashMap} to preserve the order in which
 * entries are added.
 *
 * @author Miquel Sas
 */
public class JSONObject {

	/**
	 * Parse the string JSON object.
	 *
	 * @param obj The string JSON object to parse.
	 * @return The parsed JSON object.
	 */
	public static JSONObject parse(String obj) {
		try {
			StringReader reader = new StringReader(obj);
			return parse(reader);
		} catch (IOException exc) {
			throw new IllegalArgumentException("Invalid JSON object string", exc);
		}
	}

	/**
	 * Parse the reader and return the JSON object.
	 *
	 * @param reader The reader.
	 * @return The JSON object.
	 * @throws IOException If an error occurs.
	 */
	public static JSONObject parse(Reader reader) throws IOException {
		JSONParser parser = new JSONParser();
		return parser.parse(reader);
	}

	/**
	 * Internal map.
	 */
	private final Map<String, JSONEntry> map = new LinkedHashMap<>();

	/**
	 * Default constructor.
	 */
	public JSONObject() {
	}

	/**
	 * Append the content of a {@link JSONObject}.
	 *
	 * @param obj The {@link JSONObject} to append the content.
	 */
	public void append(JSONObject obj) {
		map.putAll(obj.map);
	}

	/**
	 * @param key The string key.
	 * @return The entry mapped with the given key.
	 * @throws IllegalArgumentException if the key is not contained.
	 */
	public JSONEntry get(String key) {
		if (key == null) {
			throw new NullPointerException("The key can not be null");
		}
		JSONEntry entry = map.get(key);
		if (entry == null) {
			throw new IllegalArgumentException("Invalid key " + key);
		}
		return entry;
	}

	/**
	 * @param key The string key.
	 * @return The {@link JSONObject} value mapped with the key.
	 */
	public JSONObject getObject(String key) {
		return get(key).getObject();
	}
	/**
	 * @param key The string key.
	 * @return The {@link JSONArray} value mapped with the key.
	 */
	public JSONArray getArray(String key) {
		return get(key).getArray();
	}
	/**
	 * @param key The string key.
	 * @return The {@link String} value mapped with the key.
	 */
	public String getString(String key) {
		return get(key).getString();
	}
	/**
	 * @param key The string key.
	 * @return The {@link Number} value mapped with the key.
	 */
	public Number getNumber(String key) {
		return get(key).getNumber();
	}
	/**
	 * @param key The string key.
	 * @return The {@link Boolean} value mapped with the key.
	 */
	public Boolean getBoolean(String key) {
		return get(key).getBoolean();
	}
	/**
	 * @param key The string key.
	 * @return The byte[] value mapped with the key.
	 */
	public byte[] getBinary(String key) {
		return get(key).getBinary();
	}
	/**
	 * @param key The string key.
	 * @return The {@link LocalDate} value mapped with the key.
	 */
	public LocalDate getDate(String key) {
		return get(key).getDate();
	}
	/**
	 * @param key The string key.
	 * @return The {@link LocalTime} value mapped with the key.
	 */
	public LocalTime getTime(String key) {
		return get(key).getTime();
	}
	/**
	 * @param key The string key.
	 * @return The {@link LocalDateTime} value mapped with the key.
	 */
	public LocalDateTime getTimestamp(String key) {
		return get(key).getTimestamp();
	}

	/**
	 * @param key   The non-null string key.
	 * @param entry A non-null {@link JSONEntry}
	 */
	public void put(String key, JSONEntry entry) {
		if (key == null) {
			throw new NullPointerException("The key can not be null");
		}
		if (entry == null) {
			throw new NullPointerException("The entry can not be null");
		}
		map.put(key, entry);
	}

	/**
	 * @param key   The key.
	 * @param value The {@link JSONObject} value.
	 */
	public void put(String key, JSONObject value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The {@link JSONArray} value.
	 */
	public void put(String key, JSONArray value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The {@link String} value.
	 */
	public void put(String key, String value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The {@link Number} value.
	 */
	public void put(String key, Number value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The {@link Boolean} value.
	 */
	public void put(String key, Boolean value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The byte[] value.
	 */
	public void put(String key, byte[] value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The {@link LocalDate} value.
	 */
	public void put(String key, LocalDate value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The {@link Date} value.
	 */
	public void put(String key, Date value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The {@link LocalTime} value.
	 */
	public void put(String key, LocalTime value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The {@link Time} value.
	 */
	public void put(String key, Time value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The {@link LocalDateTime} value.
	 */
	public void put(String key, LocalDateTime value) {
		put(key, new JSONEntry(value));
	}
	/**
	 * @param key   The key.
	 * @param value The {@link Timestamp} value.
	 */
	public void put(String key, Timestamp value) {
		put(key, new JSONEntry(value));
	}

	/**
	 * @param key The key.
	 * @return The {@link JSONEntry} or null.
	 */
	public JSONEntry remove(String key) {
		if (key == null) {
			throw new NullPointerException("The key can not be null");
		}
		return map.remove(key);
	}

	/**
	 * @return The collection of keys.
	 */
	public Collection<String> keys() {
		return map.keySet();
	}
	/**
	 * @return A boolean checking emptiness.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}
	/**
	 * @return The size or number of elements.
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns the string representation.
	 */
	@Override
	public String toString() {
		return toString(false);
	}
	/**
	 * Returns the string representation.
	 *
	 * @param readable A boolean indicating that the output should be formatted in a more readable
	 *                 form.
	 * @return A string.
	 */
	public String toString(boolean readable) {
		JSONWriter w = new JSONWriter(readable);
		return w.toString(this);
	}
}
