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

import java.util.Objects;

/**
 * An index definition, normally the index of a table in an SQL database.
 *
 * @author Miquel Sas
 */
public class Index extends Order {

	/**
	 * The name.
	 */
	private String name = null;
	/**
	 * An optional database schema. If not set, the database schema will be used.
	 */
	private String schema = null;
	/**
	 * The unique control flag.
	 */
	private boolean unique = false;
	/**
	 * The parent table.
	 */
	private Table table = null;

	/**
	 * Constructor.
	 */
	public Index() {
	}

	/**
	 * Returns the name of the index.
	 *
	 * @return The name
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
	 * Returns the optional database schema where this index will be located.
	 *
	 * @return The database schema.
	 */
	public String getSchema() {
		return schema;
	}
	/**
	 * Returns the parent table.
	 *
	 * @return The parent table..
	 */
	public Table getTable() {
		return table;
	}
	/**
	 * Check if this index is unique.
	 *
	 * @return A boolean
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * Sets the name of the index.
	 *
	 * @param name The name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Sets the database schema.
	 *
	 * @param schema The database schema.
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}
	/**
	 * Sets the parent table.
	 *
	 * @param table The table
	 */
	public void setTable(Table table) {
		this.table = table;
	}
	/**
	 * Set if this index is unique.
	 *
	 * @param unique A boolean
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	/**
	 * Check for equality.
	 *
	 * @param o The object to check.
	 * @return A boolean.
	 */
	public boolean equals(Object o) {
		if (o instanceof Index i) {
			boolean eq = Objects.equals(getTable(), i.getTable());
			eq &= getNameSchema().equals(i.getName());
			return eq;
		}
		return false;
	}
	/**
	 * Returns a suitable hash code.
	 *
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		Object[] objs = new Object[getSegments().size() + 2];
		objs[0] = table;
		objs[1] = getNameSchema();
		for (int i = 0; i < getSegments().size(); i++) {
			objs[i+2] = getSegments().get(i);
		}
		return Objects.hash(objs);
	}
}
