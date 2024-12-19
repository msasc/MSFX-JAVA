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

import msfx.lib.util.Numbers;
import msfx.lib.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A table definition.
 *
 * @author Miquel Sas
 */
public class Table {

	/**
	 * The name of the table.
	 */
	private String name;
	/**
	 * Alias, normally in the context of SQ queries.
	 */
	private String alias;
	/**
	 * The database schema.
	 */
	private String schema;

	/**
	 * The list of fields (field references).
	 */
	private final FieldList fields = new FieldList();

	/**
	 * The primary key if any.
	 */
	private Index primaryKey;
	/**
	 * The optional list of secondary indexes.
	 */
	private final List<Index> indexes = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public Table() { }
	/**
	 * Copy constructor.
	 *
	 * @param table The source table.
	 */
	public Table(Table table) {

		this.name = table.name;
		this.alias = table.alias;
		this.schema = table.schema;

		for (Field field : table.getFields()) {
			addField(field);
		}

		for (Index srcIndex : table.indexes) {
			Index index = new Index();
			index.setSchema(srcIndex.getSchema());
			index.setUnique(srcIndex.isUnique());
			for (Order.Segment srcSegment : srcIndex.getSegments()) {
				Field field = new Field(srcSegment.getField());
				boolean asc = srcSegment.isAscending();
				index.addField(field, asc);
			}
			addIndex(index);
		}
	}

	/**
	 * Add a field to the field list.
	 *
	 * @param field The field to add.
	 */
	public void addField(Field field) {
		if (field == null) {
			throw new NullPointerException("Field can not be null");
		}
		field = new Field(field);
		field.setTable(this);
		fields.addField(field);
		setupAndValidate();
	}
	/**
	 * Add an index to the list of secondary indexes.
	 *
	 * @param index The index to add
	 */
	public void addIndex(Index index) {
		if (index == null) {
			throw new NullPointerException("Index can not be null");
		}
		indexes.add(index);
		setupAndValidate();
	}

	/**
	 * Check for equality.
	 *
	 * @param o The object to check.
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Table t) {
			boolean eq = Objects.equals(getName(), t.getName());
			eq &= Objects.equals(getAlias(), t.getAlias());
			eq &= Objects.equals(getSchema(), t.getSchema());
			return eq;
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
		return Objects.hash(getName(), getAlias(), getSchema());
	}

	/**
	 * Returns the given fields.
	 *
	 * @param alias Field alias.
	 * @return The field.
	 */
	public Field getField(String alias) {
		return fields.getField(alias);
	}
	/**
	 * Returns the given fields.
	 *
	 * @param index Field index within the list of fields.
	 * @return The field.
	 */
	public Field getField(int index) {
		return fields.getField(index);
	}
	/**
	 * Returns the number of fields.
	 *
	 * @return The size or number of fields.
	 */
	public int getFieldCount() {
		return fields.size();
	}

	/**
	 * Returns the list of field references.
	 *
	 * @return The list of field references.
	 */
	public FieldList getFields() {
		return fields;
	}

	/**
	 * Returns the secondary index at the given position.
	 *
	 * @param index The position in the list of indexes.
	 * @return The secondary index.
	 */
	public Index getIndex(int index) {
		return indexes.get(index);
	}
	/**
	 * Returns the number of secondary indexes.
	 *
	 * @return The number of secondary indexes.
	 */
	public int getIndexCount() {
		return indexes.size();
	}

	/**
	 * Get the name.
	 *
	 * @return The table name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Gets the name qualified with the schema.
	 *
	 * @return The name qualified with the schema.
	 */
	public String getNameSchema() {
		if (schema != null && name != null) {
			return schema + "." + name;
		}
		return name;
	}
	/**
	 * Get the schema.
	 *
	 * @return The table schema
	 */
	public String getSchema() {
		return schema;
	}
	/**
	 * Return the alias.
	 *
	 * @return The alias.
	 */
	public String getAlias() {
		return alias != null ? alias : name;
	}

	/**
	 * Set the name.
	 *
	 * @param name The name of the table.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Set the schema.
	 *
	 * @param schema Te database schema.
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}
	/**
	 * Set the alias.
	 *
	 * @param alias The alias.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Returns the primary key index.
	 *
	 * @return The primary key index.
	 */
	public Index getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * Setup internal structures.
	 */
	private void setupAndValidate() {

		/* Build primary key. */
		if (!fields.getPrimaryKeyFields().isEmpty()) {
			primaryKey = new Index();
			primaryKey.setTable(this);
			primaryKey.setUnique(true);
			primaryKey.setName(getName() + "_PK");
			primaryKey.setSchema(getSchema());
			for (Field field : fields.getPrimaryKeyFields()) {
				primaryKey.addField(field);
			}
		}

		/* Setup indexes. */
		for (int i = 0; i < indexes.size(); i++) {
			Index index = indexes.get(i);
			index.setTable(this);
			if (index.getSchema() == null) {
				index.setSchema(getSchema());
			}
			if (index.getName() == null) {
				String name = getName() + "_SK";
				name += Strings.leftPad(i, Numbers.getDigits(indexes.size()), "0");
				index.setName(name);
			}
		}
	}
}
