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
package msfx.lib.db.rdbms.sql;

import msfx.lib.db.Field;
import msfx.lib.db.Table;
import msfx.lib.db.rdbms.DBEngine;
import msfx.lib.db.rdbms.SQL;

/**
 * Builder of a CREATE TABLE statement.
 *
 * @author Miquel Sas
 */
public class CreateTable extends SQL {

	/** The table to create. */
	private final Table table;

	/**
	 * Constructor.
	 *
	 * @param db    The database engine.
	 * @param table The table to create.
	 */
	public CreateTable(DBEngine db, Table table) {
		super(db);
		this.table = table;
	}

	/**
	 * Returns the query optionally formatted to be readable.
	 *
	 * @param formatted A boolean indicating whether the query should be formatted.
	 * @return The query.
	 */
	@Override
	public String toSQL(boolean formatted) {

		StringBuilder sql = new StringBuilder(256);
		sql.append("CREATE TABLE ");
		sql.append(table.getNameSchema());
		sql.append(" (");

		boolean first = true;
		for (int i = 0; i < table.getFieldCount(); i++) {
			Field field = table.getField(i);
			if (field.isPersistent()) {
				if (!first) {
					sql.append(", ");
				}
				sql.append(db.getAdapter().getFieldDefinition(field));
				sql.append(db.getAdapter().getFieldDefinitionSuffix(field));
				first = false;
			}
		}
		sql.append(") ");

		return sql.toString();
	}

}
