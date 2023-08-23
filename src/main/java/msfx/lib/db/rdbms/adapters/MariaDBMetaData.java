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
package msfx.lib.db.rdbms.adapters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import msfx.lib.db.RecordSet;
import msfx.lib.db.rdbms.DBEngine;
import msfx.lib.db.rdbms.DBMetaData;

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
