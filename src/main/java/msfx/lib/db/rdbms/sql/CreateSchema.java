/*
 * Copyright (C) 2018 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package msfx.lib.db.rdbms.sql;

import msfx.lib.db.rdbms.DBEngine;
import msfx.lib.db.rdbms.SQL;

/**
 * A builder of a CREATE SCHEMA statement.
 *
 * @author Miquel Sas
 */
public class CreateSchema extends SQL {

	/**
	 * The schema.
	 */
	private final String schema;

	/**
	 * Constructor.
	 *
	 * @param db     The database engine.
	 * @param schema The schema.
	 */
	public CreateSchema(DBEngine db, String schema) {
		super(db);
		this.schema = schema;
	}

	/**
	 * Returns the query optionally formatted to be readable.
	 *
	 * @param formatted A boolean indicating whether the query should be formatted.
	 * @return The query.
	 */
	@Override
	public String toSQL(boolean formatted) {
		if (schema == null) {
			throw new IllegalStateException("The name of the schema must be set.");
		}
		StringBuilder b = new StringBuilder(256);
		b.append("CREATE SCHEMA ");
		b.append(schema);
		return b.toString();
	}

}
