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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import msfx.lib.db.*;
import msfx.lib.db.Record;

/**
 * Information about the database. This information is implemented by the adapters, either using
 * JDBC {@link DatabaseMetaData} or directly querying the database system tables when the driver
 * implementation is not satisfactorily implemented.
 *
 * @author Miquel Sas
 */
public abstract class DBMetaData {
	/**
	 * Helper to read values from a result set, performing the necessary updates.
	 * The reader property should be installed in the field with the "READER" key.
	 */
	public interface ValueReader {
		/**
		 * Key to store the reader in the field properties.
		 */
		String KEY = "reader";
		/**
		 * Read a field value.
		 *
		 * @param rs     The {@link DBResultSet}
		 * @param record The {@link Record}
		 * @param field  The {@link Field}
		 * @throws SQLException If an error occurs.
		 */
		void read(DBResultSet rs, Record record, Field field) throws SQLException;
	}

	/**
	 * Catalog record set.
	 */
	public static class Catalog {
		public static final String CATALOG_NAME = "CATALOG_NAME";
		/**
		 * Return the necessary field list to build the record set.
		 *
		 * @return The field list.
		 */
		public static FieldList fields() {
			FieldList fields = new FieldList();
			fields.addField(Field.create(CATALOG_NAME, Types.STRING));
			return fields;
		}
	}

	/**
	 * Column record set.
	 */
	public static class Column {
		public static final String TABLE_CATALOG = "TABLE_CATALOG";
		public static final String TABLE_SCHEMA = "TABLE_SCHEMA";
		public static final String TABLE_NAME = "TABLE_NAME";
		public static final String COLUMN_NAME = "COLUMN_NAME";
		public static final String ORDINAL_POSITION = "ORDINAL_POSITION";
		public static final String IS_NULLABLE = "IS_NULLABLE";
		public static final String DATA_TYPE = "DATA_TYPE";
		public static final String COLUMN_TYPE = "COLUMN_TYPE";
		public static final String LIB_TYPE = "LIB_TYPE";
		public static final String CHARACTER_MAXIMUM_LENGTH = "CHARACTER_MAXIMUM_LENGTH";
		public static final String NUMERIC_PRECISION = "NUMERIC_PRECISION";
		public static final String NUMERIC_SCALE = "NUMERIC_SCALE";
		/**
		 * Return the necessary field list to build the record set.
		 *
		 * @return The field list.
		 */
		public static FieldList fields() {
			FieldList fields = new FieldList();

			fields.addField(Field.create(TABLE_CATALOG, Types.STRING));
			fields.addField(Field.create(TABLE_SCHEMA, Types.STRING));
			fields.addField(Field.create(TABLE_NAME, Types.STRING));
			fields.addField(Field.create(COLUMN_NAME, Types.STRING));
			fields.addField(Field.create(ORDINAL_POSITION, Types.INTEGER));
			fields.addField(Field.create(IS_NULLABLE, Types.STRING));
			fields.addField(Field.create(DATA_TYPE, Types.STRING));
			fields.addField(Field.create(COLUMN_TYPE, Types.STRING));
			fields.addField(Field.create(CHARACTER_MAXIMUM_LENGTH, Types.LONG));
			fields.addField(Field.create(NUMERIC_PRECISION, Types.INTEGER));
			fields.addField(Field.create(NUMERIC_SCALE, Types.INTEGER));

			return fields;
		}
	}

	/**
	 * Schema record set.
	 */
	public static class Schema {
		public static final String CATALOG_NAME = "CATALOG_NAME";
		public static final String SCHEMA_NAME = "SCHEMA_NAME";
		/**
		 * Return the necessary field list to build the record set.
		 *
		 * @return The field list.
		 */
		public static FieldList fields() {
			FieldList fields = new FieldList();
			fields.addField(Field.create(CATALOG_NAME, Types.STRING));
			fields.addField(Field.create(SCHEMA_NAME, Types.STRING));
			return fields;
		}
	}

	/**
	 * Table record set.
	 */
	public static class Table {
		public static final String TABLE_CATALOG = "TABLE_CATALOG";
		public static final String TABLE_SCHEMA = "TABLE_SCHEMA";
		public static final String TABLE_NAME = "TABLE_NAME";
		public static final String TABLE_TYPE = "TABLE_TYPE";
		/**
		 * Return the necessary field list to build the record set.
		 *
		 * @return The field list.
		 */
		public static FieldList fields() {
			FieldList fields = new FieldList();
			fields.addField(Field.create(TABLE_CATALOG, Types.STRING));
			fields.addField(Field.create(TABLE_SCHEMA, Types.STRING));
			fields.addField(Field.create(TABLE_NAME, Types.STRING));
			fields.addField(Field.create(TABLE_TYPE, Types.STRING));
			return fields;
		}
	}

	/** Engine. */
	protected final DBEngine db;

	/**
	 * Constructor.
	 *
	 * @param db The underlying database engine.
	 */
	public DBMetaData(DBEngine db) { this.db = db; }

	/**
	 * Check whether the schema exists.
	 *
	 * @param schema Schema.
	 * @return A boolean.
	 * @throws SQLException If an SQL error occurs.
	 */
	public boolean existsSchema(String schema) throws SQLException {
		return existsSchema(null, schema);
	}
	/**
	 * Check whether the schema exists.
	 *
	 * @param catalog Catalog.
	 * @param schema  Schema.
	 * @return A boolean.
	 * @throws SQLException If an SQL error occurs.
	 */
	public boolean existsSchema(String catalog, String schema) throws SQLException {
		RecordSet rs = getSchemas(catalog, schema);
		return !rs.isEmpty();
	}

	/**
	 * Check whether the schema exists.
	 *
	 * @param schema Schema.
	 * @param table  Table.
	 * @return A boolean.
	 * @throws SQLException If an SQL error occurs.
	 */
	public boolean existsTable(String schema, String table) throws SQLException {
		return existsTable(null, schema, table);
	}
	/**
	 * Check whether the schema exists.
	 *
	 * @param catalog Catalog.
	 * @param schema  Schema.
	 * @param table   Table.
	 * @return A boolean.
	 * @throws SQLException If an SQL error occurs.
	 */
	public boolean existsTable(String catalog, String schema, String table) throws SQLException {
		RecordSet rs = getTables(catalog, schema, table);
		return !rs.isEmpty();
	}

	/**
	 * Return a record set with the list of catalogs.
	 *
	 * @return The list of catalogs.
	 * @throws SQLException If an SQL error occurs.
	 */
	public abstract RecordSet getCatalogs() throws SQLException;

	/**
	 * Return a record set with the list of columns.
	 *
	 * @param catalog The filter catalog, can be a string with a LIKE form as '%...%'.
	 * @param schema  The filter schema, can be a string with a LIKE form as '%...%'.
	 * @param table   The filter table, can be a string with a LIKE form as '%...%'.
	 * @param column  The filter column, can be a string with a LIKE form as '%...%'.
	 * @return The record set of schemas.
	 * @throws SQLException If an error occurs.
	 */
	public abstract RecordSet getColumns(
			String catalog,
			String schema,
			String table,
			String column) throws SQLException;

	/**
	 * Return a record set with the list of schemas.
	 *
	 * @param catalog The filter catalog, can be a string with a LIKE form as '%...%'.
	 * @param schema  The filter schema, can be a string with a LIKE form as '%...%'.
	 * @return The record set of schemas.
	 * @throws SQLException If an error occurs.
	 */
	public abstract RecordSet getSchemas(String catalog, String schema) throws SQLException;

	/**
	 * Return a record set with the list of tables.
	 *
	 * @param catalog The filter catalog, can be a string with a LIKE form as '%...%'.
	 * @param schema  The filter schema, can be a string with a LIKE form as '%...%'.
	 * @return The record set of schemas.
	 * @throws SQLException If an error occurs.
	 */
	public abstract RecordSet getTables(
			String catalog,
			String schema,
			String table) throws SQLException;

	/**
	 * Reads the correspondent record set applying value readers if present.
	 *
	 * @param rs     The JDBC result set
	 * @param fields The applying field list
	 * @return The record set.
	 * @throws SQLException If an SQL error occurs.
	 */
	protected RecordList readRecordSet(DBResultSet rs, FieldList fields) throws SQLException {
		RecordList recordSet = new RecordList();
		recordSet.setFieldList(fields);
		while (rs.next()) {
			Record record = new Record(fields);
			for (Field field : fields) {
				ValueReader reader = (ValueReader) field.getProperties().get(ValueReader.KEY);
				if (reader != null) {
					reader.read(rs, record, field);
				} else {
					Types type = field.getType();
					int decimals = field.getDecimals();
					String alias = field.getAlias();
					Value value;
					if (field.isPersistent() || field.isVirtual()) {
						value = db.fromResultSet(rs, type, decimals, alias);
					} else {
						value = field.getDefaultValue();
					}
					record.setValue(field.getAlias(), value);
				}
			}
			recordSet.add(record);
		}
		return recordSet;
	}
	/**
	 * Read records, adding exactly by position.
	 *
	 * @param sql    The query.
	 * @param fields The list of fields.
	 * @return The record set.
	 * @throws SQLException If an error occurs.
	 */
	protected RecordSet readRecordSet(StringBuilder sql, FieldList fields) throws SQLException {
		RecordList recordSet = new RecordList(fields);
		DBConnection cn = null;
		DBStatement st = null;
		DBResultSet rs = null;
		try {
			cn = db.getConnection();
			st = cn.createStatement();
			rs = st.executeQuery(sql.toString());
			recordSet = readRecordSet(rs, fields);
			while (rs.next()) {
				Record record = db.readRecord(rs, fields);
				recordSet.add(record);
			}
		} finally {
			if (rs != null && !rs.isClosed()) rs.close();
			if (st != null && !st.isClosed()) st.close();
			if (cn != null && !cn.isClosed()) cn.close();
		}
		return recordSet;
	}
}
