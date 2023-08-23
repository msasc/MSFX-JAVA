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

package msfx.lib.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A useful and quite generic properties table with typed access for commonly used objects.
 *
 * @author Miquel Sas
 */
public class Properties {

	/**
	 * The properties map.
	 */
	private final Map<Object, Object> properties = new HashMap<>();

	/**
	 * Constructor.
	 */
	public Properties() { }

	/**
	 * Clear these properties.
	 */
	public void clear() {
		properties.clear();
	}

	/**
	 * Returns a stored Boolean value.
	 *
	 * @param key The key.
	 * @return The stored boolean value.
	 */
	public Boolean getBoolean(Object key) {
		return getBoolean(key, null);
	}
	/**
	 * Returns a stored Boolean value.
	 *
	 * @param key          The key.
	 * @param defaultValue Default value
	 * @return The stored boolean value.
	 */
	public Boolean getBoolean(Object key, Boolean defaultValue) {
		Boolean value = (Boolean) get(key);
		return (value == null ? defaultValue : value);
	}
	/**
	 * Returns a stored Double value.
	 *
	 * @param key The key.
	 * @return The stored double value.
	 */
	public Double getDouble(Object key) {
		return (Double) get(key);
	}
	/**
	 * Return a stored double vector.
	 *
	 * @param key The key.
	 * @return The double vector.
	 */
	public double[] getDouble1A(Object key) {
		return (double[]) get(key);
	}
	/**
	 * Return a stored double 2d matrix.
	 *
	 * @param key The key.
	 * @return The double 2d matrix.
	 */
	public double[][] getDouble2A(Object key) {
		return (double[][]) get(key);
	}
	/**
	 * Returns a stored Integer value.
	 *
	 * @param key The key.
	 * @return The stored integer value.
	 */
	public Integer getInteger(Object key) {
		return (Integer) get(key);
	}
	/**
	 * Returns a stored Long value.
	 *
	 * @param key The key.
	 * @return The stored long value.
	 */
	public Long getLong(Object key) {
		return (Long) get(key);
	}
	/**
	 * Returns a stored string value, returning <code>null</code> if not set.
	 *
	 * @param key The key.
	 * @return The stored string value.
	 */
	public String getString(Object key) {
		return (String) get(key);
	}

	/**
	 * Return a stored typed list.
	 *
	 * @param <T> The type of the elements of the list.
	 * @param key The key.
	 * @return The typed list.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Object key) {
		return (List<T>) get(key);
	}
	/**
	 * Return a stored typed map.
	 *
	 * @param key The key.
	 * @return The typed map.
	 */
	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getMap(Object key) {
		return (Map<K, V>) get(key);
	}

	/**
	 * Return the set of keys.
	 *
	 * @return The set of keys.
	 */
	public Set<Object> keySet() {
		return properties.keySet();
	}

	/**
	 * Returns a stored object.
	 *
	 * @param key The key.
	 * @return The stored object.
	 */
	public Object get(Object key) {
		return properties.get(key);
	}

	/**
	 * Put a value.
	 *
	 * @param key   The key.
	 * @param value The value.
	 */
	public void put(Object key, Object value) {
		properties.put(key, value);
	}
	/**
	 * Put all properties.
	 *
	 * @param properties The properties to use to fill.
	 */
	public void putAll(Properties properties) {
		this.properties.putAll(properties.properties);
	}

	/**
	 * Remove the property at key.
	 *
	 * @param key The key.
	 * @return The removed property or null.
	 */
	public Object remove(Object key) {
		return properties.remove(key);
	}
	/**
	 * Return the collection of values.
	 *
	 * @return The values.
	 */
	public Collection<Object> values() {
		return properties.values();
	}
}
