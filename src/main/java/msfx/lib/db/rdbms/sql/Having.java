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

import msfx.lib.db.rdbms.DBEngine;

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
