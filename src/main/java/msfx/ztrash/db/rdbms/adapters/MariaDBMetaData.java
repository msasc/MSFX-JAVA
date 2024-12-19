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
package msfx.ztrash.db.rdbms.adapters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import msfx.ztrash.db.RecordSet;
import msfx.ztrash.db.rdbms.DBEngine;
import msfx.ztrash.db.rdbms.DBMetaData;

/**
 * MariaDB's implementation of the {@link DBMetaData} interface.
 *
 * @author Miquel Sas
 */
public class MariaDBMetaData extends DBMetaData {

	/** A pair of strings. */
	private static class Pair {
		private String name;
		private String value;
	}

	/**
	 * Constructor.
	 *
	 * @param db The underlying database engine.
	 */
	public MariaDBMetaData(DBEngine db) {
		super(db);
	}

	/**
	 * Return the catalogs record set.
	 */
	@Override
	public RecordSet getCatalogs() throws SQLException {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT CATALOG_NAME ");
		sql.append("FROM information_schema.SCHEMATA ");
		sql.append("GROUP BY CATALOG_NAME ");
		sql.append("ORDER BY CATALOG_NAME");

		return readRecordSet(sql, Catalog.fields());
	}

	/**
	 * Return the columns record set.
	 */
	@Override
	public RecordSet getColumns(
			String catalog,
			String schema,
			String table,
			String column) throws SQLException {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, ");
		sql.append("ORDINAL_POSITION, IS_NULLABLE, DATA_TYPE, COLUMN_TYPE, ");
		sql.append("CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE ");
		sql.append("FROM information_schema.COLUMNS ");

		List<Pair> pairs = new ArrayList<>();
		pairs.add(pair("TABLE_CATALOG", catalog));
		pairs.add(pair("TABLE_SCHEMA", schema));
		pairs.add(pair("TABLE_NAME", table));
		pairs.add(pair("COLUMN_NAME", column));
		where(sql, pairs);

		sql.append("ORDER BY TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME, ORDINAL_POSITION");

		return readRecordSet(sql, Column.fields());
	}
	/**
	 * Return the schemas record set.
	 */
	@Override
	public RecordSet getSchemas(String catalog, String schema) throws SQLException {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT CATALOG_NAME, SCHEMA_NAME ");
		sql.append("FROM information_schema.SCHEMATA ");

		List<Pair> pairs = new ArrayList<>();
		pairs.add(pair("CATALOG_NAME", catalog));
		pairs.add(pair("SCHEMA_NAME", schema));
		where(sql, pairs);

		sql.append("ORDER BY CATALOG_NAME, SCHEMA_NAME");

		return readRecordSet(sql, Schema.fields());
	}

	/**
	 * Return the tables record set.
	 */
	@Override
	public RecordSet getTables(String catalog, String schema, String table) throws SQLException {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME, TABLE_TYPE ");
		sql.append("FROM information_schema.TABLES ");

		List<Pair> pairs = new ArrayList<>();
		pairs.add(pair("TABLE_CATALOG", catalog));
		pairs.add(pair("TABLE_SCHEMA", schema));
		pairs.add(pair("TABLE_NAME", table));
		where(sql, pairs);

		sql.append("ORDER BY TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME");

		return readRecordSet(sql, Table.fields());
	}

	/**
	 * Helper to add the where clause.
	 *
	 * @param sql   The sql builder to fill.
	 * @param pairs The list of name/value pairs.
	 */
	private void where(StringBuilder sql, List<Pair> pairs) {
		boolean where = false;
		for (Pair pair : pairs) {
			where |= (pair.value != null && !pair.value.isEmpty());
		}
		if (where) {
			sql.append("WHERE ");
			boolean and = false;
			for (Pair pair : pairs) {
				if (pair.value != null && !pair.value.isEmpty()) {
					if (and) sql.append(" AND ");
					sql.append(pair.name);
					if (pair.value.startsWith("%") || pair.value.endsWith("%")) {
						sql.append(" LIKE '");
						sql.append(pair.value);
						sql.append("'");
					} else {
						sql.append(" = '");
						sql.append(pair.value);
						sql.append("'");
					}
					and = true;
				}
			}
			sql.append(" ");
		}
	}

	/**
	 * Create and return a new pair.
	 *
	 * @param name  The name.
	 * @param value The value.
	 * @return The pair.
	 */
	private Pair pair(String name, String value) {
		Pair pair = new Pair();
		pair.name = name;
		pair.value = value;
		return pair;
	}
}
