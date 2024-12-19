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

import msfx.ztrash.db.rdbms.DBEngine;
import msfx.ztrash.db.rdbms.SQL;

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
