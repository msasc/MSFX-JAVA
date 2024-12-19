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

package msfx.ztrash.db;

import msfx.lib.util.Lists;

import java.util.*;

/**
 * An ordered list of field references that can be efficiently accessed by index or by alias.
 *
 * @author Miquel Sas
 */
public class FieldList implements Iterable<Field> {

	/**
	 * List of field references.
	 */
	private final List<Field> fields = new ArrayList<>();

	/**
	 * List of aliases.
	 */
	private final List<String> aliases = new ArrayList<>();
	/**
	 * Map of indexes by alias.
	 */
	private final Map<String, Integer> indexes = new HashMap<>();

	/**
	 * The list of persistent fields.
	 */
	private final List<Field> persistentFields = new ArrayList<>();
	/**
	 * The list of primary key fields.
	 */
	private final List<Field> primaryKeyFields = new ArrayList<>();

	/**
	 * List of default values.
	 */
	private final List<Value> defaultValues = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public FieldList() { }

	/**
	 * Add a field.
	 *
	 * @param field The field.
	 */
	public void addField(Field field) {
		addField(field, null, null);
	}
	/**
	 * Add a field.
	 *
	 * @param field The field.
	 * @param alias The new alias.
	 */
	public void addField(Field field, String alias) {
		addField(field, alias, null);
	}
	/**
	 * Add a field specifying an alias and a function.
	 *
	 * @param field    The field.
	 * @param alias    The alias.
	 * @param function The function.
	 */
	public void addField(Field field, String alias, String function) {
		Field fieldDef = new Field(field);
		if (alias != null) {
			fieldDef.setAlias(alias);
		}
		if (function != null) {
			fieldDef.setFunction(function);
		}
		fields.add(fieldDef);
		setupAndValidate();
	}

	/**
	 * Return a default record.
	 *
	 * @return The default record.
	 */
	public Record getDefaultRecord() {
		return new Record(this, defaultValues);
	}

	/**
	 * Returns the list of default values.
	 *
	 * @return The list of values.
	 */
	public List<Value> getDefaultValues() {
		return defaultValues;
	}

	/**
	 * Returns the field or null if none is found.
	 *
	 * @param index The index of the field.
	 * @return The field or null if none is found.
	 */
	public Field getField(int index) {
		return fields.get(index);
	}
	/**
	 * Returns the field or null if none is found.
	 *
	 * @param alias The alias.
	 * @return The field or null if none is found.
	 */
	public Field getField(String alias) {
		return getField(indexOf(alias));
	}
	/**
	 * Returns an unmodifiable collection with the list of fields.
	 *
	 * @return A collection with the list of fields.
	 */
	public List<Field> getFieldRefs() {
		return Collections.unmodifiableList(fields);
	}

	/**
	 * Returns an unmodifiable collection with the list of keys.
	 *
	 * @return A collection with the list of keys.
	 */
	public List<String> getAliases() {
		return aliases;
	}

	/**
	 * Returns the list of persistent fields.
	 *
	 * @return The list of persistent fields.
	 */
	public List<Field> getPersistentFields() {
		return persistentFields;
	}
	/**
	 * Returns the list of primary key fields.
	 *
	 * @return The list of primary key fields.
	 */
	public List<Field> getPrimaryKeyFields() {
		return primaryKeyFields;
	}
	/**
	 * Returns the primary order.
	 *
	 * @return The primary order.
	 */
	public Order getPrimaryOrder() {
		Order order = new Order();
		List<Field> pkFields = getPrimaryKeyFields();
		for (Field fieldRef : pkFields) {
			order.addField(fieldRef);
		}
		return order;
	}

	/**
	 * Returns the index of the field.
	 *
	 * @param alias The key or field alias.
	 * @return The index of the field.
	 */
	public int indexOf(String alias) {
		Integer index = indexes.get(alias);
		if (index == null) {
			throw new IllegalArgumentException("Invalid alias");
		}
		return index;
	}

	/**
	 * Returns a boolean indicating whether the list of fields is empty.
	 *
	 * @return A boolean.
	 */
	public boolean isEmpty() {
		return fields.isEmpty();
	}

	/**
	 * Returns the size or number of fields.
	 *
	 * @return The size or number of fields.
	 */
	public int size() {
		return fields.size();
	}

	/**
	 * Check whether the alias is contained.
	 *
	 * @param alias The key to check.
	 * @return A boolean.
	 */
	public boolean containsAlias(String alias) {
		return indexes.containsKey(alias);
	}

	/**
	 * Returns the iterators on the list of fields.
	 *
	 * @return The iterator.
	 */
	@Override
	public Iterator<Field> iterator() {
		return fields.iterator();
	}

	/**
	 * Validates the list of values.
	 *
	 * @param values The array of values to validate.
	 */
	public void validate(Value[] values) {
		if (values == null) {
			throw new NullPointerException();
		}
		if (values.length != size()) {
			throw new IllegalArgumentException("Invalid values size " + values.length);
		}
		for (int i = 0; i < size(); i++) {
			if (getField(i).getType() != values[i].getType()) {
				StringBuilder error = new StringBuilder();
				error.append("Invalid value type ");
				error.append(i);
				error.append(" type ");
				error.append(values[i].getType());
				throw new IllegalArgumentException(error.toString());
			}
		}
	}

	/**
	 * Check for equality.
	 *
	 * @param o The object to check.
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof FieldList l) {
			return Lists.equals(fields, l.fields);
		}
		return false;
	}
	/**
	 * Return a suitable hash code.
	 *
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(fields.toArray());
	}

	/**
	 * Setup internal structures.
	 */
	private void setupAndValidate() {

		aliases.clear();
		indexes.clear();
		persistentFields.clear();
		primaryKeyFields.clear();
		defaultValues.clear();

		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			String alias = field.getAlias();
			aliases.add(alias);
			indexes.put(alias, i);
			if (field.isPrimaryKey()) {
				primaryKeyFields.add(field);
			}
			if (field.isPersistent()) {
				persistentFields.add(field);
			}
			defaultValues.add(field.getDefaultValue());
		}
	}
}
