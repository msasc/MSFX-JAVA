/*
 * Copyright (c) 2021. Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package msfx.lib.db;

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
