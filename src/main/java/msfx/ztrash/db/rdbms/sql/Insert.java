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
package msfx.ztrash.db.rdbms.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import msfx.ztrash.db.Field;
import msfx.ztrash.db.Table;
import msfx.ztrash.db.Value;
import msfx.ztrash.db.rdbms.DBEngine;
import msfx.ztrash.db.rdbms.SQL;
import msfx.lib.util.Strings;

/**
 * Builder of an insert statement.
 *
 * @author Miquel Sas
 */
public class Insert extends SQL {

	/** Into table. */
	private String into;
	/** List of fields. */
	private final List<String> fields = new ArrayList<>();
	/** List of values. */
	private final List<String> values = new ArrayList<>();

	/**
	 * Constructor.
	 *
	 * @param db The underlying database engine.
	 */
	public Insert(DBEngine db) { super(db); }

	/**
	 * Set the INTO part, normally a SCHEMA.TABLE.
	 *
	 * @param into The into part.
	 */
	public void into(String into) { this.into = into; }
	/**
	 * Set the INTO part.
	 *
	 * @param table The table to insert into.
	 */
	public void into(Table table) { this.into = table.getNameSchema(); }

	/**
	 * Add the list of fields.
	 *
	 * @param fields The list of fields to add.
	 */
	public void fields(Field... fields) {
		for (Field field : fields) { this.fields.add(field.getName()); }
	}
	/**
	 * Add the list of fields.
	 *
	 * @param fields The list of fields to add.
	 */
	public void fields(List<Field> fields) {
		for (Field field : fields) { this.fields.add(field.getName()); }
	}
	/**
	 * Add the list of fields.
	 *
	 * @param fields The list of fields to add.
	 */
	public void fields(String... fields) {
		this.fields.addAll(Arrays.asList(fields));
	}

	/**
	 * Add the list of values.
	 *
	 * @param values The list of values to add.
	 */
	public void values(List<Value> values) {
		for (Value value : values) { this.values.add(db.toStringSQL(value)); }
	}
	/**
	 * Add the list of values.
	 *
	 * @param values The list of values to add.
	 */
	public void values(String... values) {
		this.values.addAll(Arrays.asList(values));
	}
	/**
	 * Add the list of values.
	 *
	 * @param values The list of values to add.
	 */
	public void values(Value... values) {
		for (Value value : values) { this.values.add(db.toStringSQL(value)); }
	}

	/**
	 * Clear the list of fields.
	 */
	public void clearFields() { fields.clear(); }
	/**
	 * Clear the list of values.
	 */
	public void clearValues() { values.clear(); }

	/**
	 * Returns the query optionally formatted to be readable.
	 *
	 * @param formatted A boolean indicating whether the query should be formatted.
	 * @return The query.
	 */
	@Override
	public String toSQL(boolean formatted) {

		boolean illegal_state = (into == null);
		illegal_state |= fields.isEmpty();
		illegal_state |= values.isEmpty();
		illegal_state |= (fields.size() != values.size());
		if (illegal_state) {
			throw new IllegalStateException("Invalid INSERT data.");
		}

		StringBuilder sql = new StringBuilder();
		if (formatted) {
			sql.append(Strings.blank(PAD - "INSERT INTO ".length()));
		}
		sql.append("INSERT INTO ");
		sql.append(into);
		if (formatted) {
			sql.append("\n");
			sql.append(Strings.blank(PAD - 1));
		}
		sql.append(" (");
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				sql.append(", ");
			}
			if (formatted) {
				sql.append("\n");
				sql.append(Strings.blank(PAD));
			}
			sql.append(fields.get(i));
		}
		if (formatted) {
			sql.append("\n");
			sql.append(Strings.blank(PAD));
		}
		sql.append(")");
		if (formatted) {
			sql.append("\n");
			sql.append(Strings.blank(PAD - " VALUES ".length()));
		}
		sql.append(" VALUES ");
		sql.append("(");
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) {
				sql.append(", ");
			}
			if (formatted) {
				sql.append("\n");
				sql.append(Strings.blank(PAD));
			}
			sql.append(values.get(i));
		}
		if (formatted) {
			sql.append("\n");
			sql.append(Strings.blank(PAD));
		}
		sql.append(")");

		return sql.toString();
	}
}
