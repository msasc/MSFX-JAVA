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

import msfx.ztrash.db.rdbms.DBEngine;

/**
 * Builder of HAVING filter clauses.
 *
 * @author Miquel Sas
 */
public class Having extends Filter {

	/**
	 * Constructor.
	 *
	 * @param db The underlying database engine.
	 */
	public Having(DBEngine db) {
		super(db);
	}

	/**
	 * Add an entire HAVING clause.
	 *
	 * @param having The HAVING clause to add.
	 */
	public void having(Having having) {
		filter(having);
	}
	/**
	 * Add an entire HAVING clause.
	 *
	 * @param logOp  The logical operator to chain the first segment.
	 * @param having The HAVING clause to add.
	 */
	public void having(String logOp, Having having) {
		filter(logOp, having);
	}
}
