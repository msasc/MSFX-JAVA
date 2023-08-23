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
import msfx.lib.db.Order;
import msfx.lib.db.OrderKey;
import msfx.lib.db.Table;
import msfx.lib.db.Value;
import msfx.lib.db.rdbms.DBEngine;

/**
 * Builder of WHERE filter clauses.
 *
 * @author Miquel Sas
 */
public class Where extends Filter {

	/**
	 * Constructor.
	 *
	 * @param db The underlying database engine.
	 */
	public Where(DBEngine db) {
		super(db);
	}

	/**
	 * Add an entire WHERE clause.
	 *
	 * @param where The WHERE clause to add.
	 */
	public void where(Where where) {
		filter(where);
	}
	/**
	 * Add an entire WHERE clause.
	 *
	 * @param logOp The logical operator to chain the first segment.
	 * @param where The WHERE clause to add.
	 */
	public void where(String logOp, Where where) {
		filter(logOp, where);
	}

	/**
	 * Set the where condition to match the primary key of the table.
	 *
	 * @param table The table.
	 * @param order The order.
	 * @param key   The order key.
	 */
	public void where(Table table, Order order, OrderKey key) {
		if (order == null) {
			throw new IllegalArgumentException("Invalid order");
		}
		if (order.getSegments().size() != key.size()) {
			throw new IllegalArgumentException("Invalid values");
		}
		for (int i = 0; i < order.getSegments().size(); i++) {
			Field field = order.getSegments().get(i).getField();
			Value value = key.get(i).getValue();
			condition("AND", field, "=", value);
		}
	}
}
