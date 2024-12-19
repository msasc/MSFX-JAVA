/*
 * Copyright (c) 2024 Miquel Sas.
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

import msfx.ztrash.db.Table;
import msfx.ztrash.db.rdbms.DBEngine;
import msfx.ztrash.db.rdbms.SQL;

/**
 * A DROP TABLE statement builder.
 *
 * @author Miquel Sas
 */
public class DropTable extends SQL {

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
	public DropTable(DBEngine db, Table table) {
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
			throw new IllegalStateException("Malformed DROP TABLE query: table is null");
		}
		StringBuilder sql = new StringBuilder(256);
		sql.append("DROP TABLE ");
		sql.append(table.getNameSchema());
		return sql.toString();
	}

}
