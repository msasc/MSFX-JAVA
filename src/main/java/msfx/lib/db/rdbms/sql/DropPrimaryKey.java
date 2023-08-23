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

import msfx.lib.db.Table;
import msfx.lib.db.rdbms.DBEngine;
import msfx.lib.db.rdbms.SQL;

/**
 * An ALTER TABLE DROP PRIMARY KEY builder.
 *
 * @author Miquel Sas
 */
public class DropPrimaryKey extends SQL {

	/**
	 * The table to alter.
	 */
	private final Table table;

	/**
	 * Constructor.
	 *
	 * @param db    The database engine.
	 * @param table The table to create.
	 */
	public DropPrimaryKey(DBEngine db, Table table) {
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
		if (table == null) {
			throw new IllegalStateException("Malformed DROP PRIMARY KEY query: table is null");
		}
		StringBuilder b = new StringBuilder(256);
		b.append("ALTER TABLE ");
		b.append(table.getNameSchema());
		b.append(" DROP PRIMARY KEY");
		return b.toString();
	}

}
