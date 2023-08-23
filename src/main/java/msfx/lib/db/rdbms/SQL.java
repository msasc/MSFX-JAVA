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
package msfx.lib.db.rdbms;

/**
 * Base SQL class.
 *
 * @author Miquel Sas
 */
public abstract class SQL {

	/**
	 * Left padding for SQL queries formatted.
	 */
	public final static int PAD = 14;

	/**
	 * Underlying database engine.
	 */
	protected final DBEngine db;

	/**
	 * Constructor.
	 *
	 * @param db The underlying database engine.
	 */
	public SQL(DBEngine db) {
		this.db = db;
	}
	/**
	 * Returns the query in raw form.
	 *
	 * @return The query in a raw form.
	 */
	public String toSQL() {
		return toSQL(false);
	}
	/**
	 * Returns the query optionally formatted to be readable.
	 *
	 * @param formatted A boolean indicating whether the query should be formatted.
	 * @return The query.
	 */
	public abstract String toSQL(boolean formatted);
}
