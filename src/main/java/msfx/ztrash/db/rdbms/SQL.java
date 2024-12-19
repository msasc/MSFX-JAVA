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
package msfx.ztrash.db.rdbms;

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
