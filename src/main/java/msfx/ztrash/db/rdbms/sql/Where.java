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

import msfx.ztrash.db.Field;
import msfx.ztrash.db.Order;
import msfx.ztrash.db.OrderKey;
import msfx.ztrash.db.Table;
import msfx.ztrash.db.Value;
import msfx.ztrash.db.rdbms.DBEngine;

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
