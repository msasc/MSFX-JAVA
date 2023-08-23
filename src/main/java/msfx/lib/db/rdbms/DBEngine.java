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

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import msfx.lib.db.*;
import msfx.lib.db.Record;
import msfx.lib.db.rdbms.sql.AddPrimaryKey;
import msfx.lib.db.rdbms.sql.CreateSchema;
import msfx.lib.db.rdbms.sql.CreateTable;
import msfx.lib.db.rdbms.sql.DropTable;
import msfx.lib.db.rdbms.sql.Select;
import msfx.lib.json.JSONObject;
import msfx.lib.task.progress.ProgressListener;

/**
 * A database engine represent a back-end RDBMS system to which we connect
 * through a JDBC driver.
 *
 * @author Miquel Sas
 */
public class DBEngine {

	/**
	 * Length to apply readers or writers.
	 */
	private static final int FIXED_LENGTH = 2000;

	/**
	 * Connection pool.
	 */
	private final DBConnectionPool connectionPool;
	/**
	 * Database engine adapter.
	 */
	private final DBEngineAdapter adapter;

	/**
	 * Constructor.
	 *
	 * @param adapter  <i>DBEngineAdapter</i>.
	 * @param database Database.
	 * @param user     User.
	 * @param password Password.
	 */
	public DBEngine(DBEngineAdapter adapter, String database, String user, String password) {
		this.adapter = adapter;
		this.connectionPool = new DBConnectionPool(
				adapter.getDriverClassName(),
				adapter.getDriverProtocol(),
				database,
				user,
				password);
	}

	/**
	 * Executes an add primary key statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int execAddPrimaryKey(Table table) throws SQLException {
		return executeUpdate(new AddPrimaryKey(this, table));
	}

	/**
	 * Executes a create schema statement.
	 *
	 * @param schema The schema.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int execCreateSchema(String schema) throws SQLException {
		return executeUpdate(new CreateSchema(this, schema));
	}

	/**
	 * Executes a create table statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int execCreateTable(Table table) throws SQLException {
		return executeUpdate(new CreateTable(this, table));
	}

	/**
	 * Executes a drop table statement.
	 *
	 * @param table The table.
	 * @return The number of rows updated of zero if not applicable.
	 * @throws SQLException If such an error occurs.
	 */
	public int execDropTable(Table table) throws SQLException {
		return executeUpdate(new DropTable(this, table));
	}

	/**
	 * Count the number of rows in the select query.
	 *
	 * @param sql The select query.
	 * @return The number of rows.
	 * @throws SQLException If an SQL error occurs.
	 */
	public long execSelectCount(String sql) throws SQLException {
		DBConnection cn = null;
		DBStatement st = null;
		DBResultSet rs = null;
		long count = -1;
		try {
			cn = getConnection();
			st = cn.createStatement();
			rs = st.executeQuery("SELECT COUNT(*) FROM (" + sql + ") AS COUNTER");
			if (rs.next()) {
				count = rs.getLong(1);
			}
			rs.close();
		} finally {
			if (rs != null && !rs.isClosed()) rs.close();
			if (st != null && !st.isClosed()) st.close();
			if (cn != null && !cn.isClosed()) cn.close();
		}
		return count;
	}

	/**
	 * Select a record from a table using the primary key.
	 *
	 * @param table The table
	 * @param pk    The primary key values.
	 * @return The record or null.
	 * @throws SQLException If such an error occurs.
	 */
	public Record execSelectRecord(Table table, OrderKey pk) throws SQLException {
		RecordSet rs = execSelectRecordSet(table, table.getPrimaryKey(), pk);
		if (!rs.isEmpty()) return rs.getRecord(0);
		return null;
	}

	/**
	 * Execute a select query and return the corresponding recordset.
	 *
	 * @param select The select query.
	 * @return The recordset
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet execSelectRecordSet(Select select) throws SQLException {
		String sql = select.toSQL();
		FieldList fields = select.getFields();
		return execSelectRecordSet(sql, fields);
	}
	/**
	 * Execute a select query and return the corresponding recordset.
	 *
	 * @param select   The select query.
	 * @param progress A progress listener to report the progress of the operation.
	 * @return The recordset
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet execSelectRecordSet(
			Select select,
			ProgressListener progress) throws SQLException {
		String sql = select.toSQL();
		FieldList fields = select.getFields();
		return execSelectRecordSet(sql, fields, progress);
	}
	/**
	 * Execute a select query and return the corresponding recordset.
	 *
	 * @param sql    The select query.
	 * @param fields The corresponding list of fields.
	 * @return The recordset
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet execSelectRecordSet(String sql, FieldList fields) throws SQLException {
		return execSelectRecordSet(sql, fields, null);
	}
	/**
	 * Execute a select query and return the corresponding recordset.
	 *
	 * @param sql      The select query.
	 * @param fields   The corresponding list of fields.
	 * @param progress A progress listener to report the progress of the operation.
	 * @return The recordset
	 * @throws SQLException If such an error occurs.
	 */
	public RecordSet execSelectRecordSet(
			String sql,
			FieldList fields,
			ProgressListener progress) throws SQLException {
		DBIterator iter = new DBIterator(this);
		double totalWork = 0;
		try {
			if (progress != null) {
				progress.notifyStart();
				progress.setIndeterminate(0, true);
				progress.notifyMessage(0, "Preparing");
				totalWork = execSelectCount(sql);
			}
			iter.select(sql, fields);
			if (progress != null) progress.setIndeterminate(0, false);
			RecordList recordSet = new RecordList();
			recordSet.setFieldList(fields);
			while (iter.hasNext()) {
				if (progress != null) {
					progress.notifyMessage(0, "Reading");
					progress.notifyProgress(0, 1.0, totalWork);
				}
				recordSet.add(iter.next());
			}
			iter.close();
			if (progress != null) progress.notifyEnd();
			return recordSet;
		} catch (Exception exc) {
			if (progress != null) progress.notifyEnd();
			exc.printStackTrace();
			return null;
		}
	}
	/**
	 * Execute a select query, with optional order and filter key values.
	 *
	 * @param table The table.
	 * @param order The order.
	 * @param key   The key values
	 * @return The recordset.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet execSelectRecordSet(
			Table table,
			Order order,
			OrderKey key) throws SQLException {
		Select select = new Select(this);
		select.select(table.getFields());
		select.from(table);
		if (order != null && key != null) select.where().where(table, order, key);
		if (order != null) select.orderBy(order);
		return execSelectRecordSet(select);
	}

	/**
	 * Execute an update statement.
	 *
	 * @param sql The update SQL.
	 * @return The number of rows affected or zero.
	 * @throws SQLException If an error occurs.
	 */
	public int executeUpdate(SQL sql) throws SQLException {
		return executeUpdate(sql.toSQL());
	}
	/**
	 * Execute an update statement.
	 *
	 * @param sql The update SQL.
	 * @return The number of rows affected or zero.
	 * @throws SQLException If an error occurs.
	 */
	public int executeUpdate(String sql) throws SQLException {
		DBConnection cn = null;
		DBStatement st = null;
		int count;
		try {
			cn = getConnection();
			st = cn.createStatement();
			count = st.executeUpdate(sql);
			return count;
		} finally {
			if (st != null && !st.isClosed()) st.close();
			if (cn != null && cn.isClosed()) cn.close();
		}
	}

	/**
	 * Return the <i>DBEngineAdapter</i>.
	 *
	 * @return The <i>DBEngineAdapter</i>.
	 */
	public DBEngineAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Returns a connection free to use.
	 *
	 * @return The connection.
	 * @throws SQLException If an error occurs.
	 */
	public DBConnection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}
	/**
	 * Returns the database metadata.
	 *
	 * @return The database metadata
	 * @throws SQLException If an error occurs.
	 */
	public DBMetaData getMetaData() throws SQLException {
		return adapter.getMetaData(this);
	}
	/**
	 * Close this database connection by closing the associated connection pool and
	 * related connections.
	 *
	 * @throws SQLException If an SQL error occurs.
	 */
	public void close() throws SQLException {
		connectionPool.close();
	}
	/**
	 * Read a record from a ResultSet.
	 *
	 * @param rs     The source result set
	 * @param fields The field list
	 * @return The record.
	 * @throws SQLException If such an error occurs.
	 */
	public Record readRecord(DBResultSet rs, FieldList fields) throws SQLException {
		Record record = new Record(fields);
		for (Field field : fields) {
			Types type = field.getType();
			int decimals = field.getDecimals();
			String alias = field.getAlias();
			Value value;
			if (field.isPersistent() || field.isVirtual()) {
				value = fromResultSet(rs, type, decimals, alias);
			} else {
				value = field.getDefaultValue();
			}
			record.setValue(field.getAlias(), value);
		}
		return record;
	}

	/**
	 * Reads the correspondent record set.
	 *
	 * @param rs     The JDBC result set
	 * @param fields The applying field list
	 * @return The record set.
	 * @throws SQLException If an SQL error occurs.
	 */
	public RecordSet readRecordSet(DBResultSet rs, FieldList fields) throws SQLException {
		RecordList recordSet = new RecordList();
		recordSet.setFieldList(fields);
		while (rs.next()) {
			Record record = readRecord(rs, fields);
			recordSet.add(record);
		}
		return recordSet;
	}

	/**
	 * Returns the string used in a query.
	 *
	 * @param value The value to format as a string.
	 * @return The string used in a query.
	 */
	public String toStringSQL(Value value) {
		return adapter.toStringSQL(value);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value A boolean.
	 * @return The string used in a query.
	 */
	public String toStringSQL(boolean value) {
		return adapter.toStringSQL(value);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value A {@link BigDecimal}
	 * @return The string used in a query.
	 */
	public String toStringSQL(BigDecimal value) {
		return adapter.toStringSQL(value);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value A {@link Double}
	 * @return The string used in a query.
	 */
	public String toStringSQL(Double value) {
		return adapter.toStringSQL(value);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value A {@link Integer}
	 * @return The string used in a query.
	 */
	public String toStringSQL(Integer value) {
		return adapter.toStringSQL(value);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value A {@link Long}
	 * @return The string used in a query.
	 */
	public String toStringSQL(Long value) {
		return adapter.toStringSQL(value);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value The {@link String} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(String value) {
		return adapter.toStringSQL(value);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value The {@link LocalDate} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(LocalDate value) {
		return adapter.toStringSQL(value);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value The {@link LocalTime} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(LocalTime value) {
		return adapter.toStringSQL(value);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value The {@link LocalDateTime} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(LocalDateTime value) {
		return adapter.toStringSQL(value);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param values The Value[] to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(Value[] values) {
		return adapter.toStringSQL(values);
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value The {@link JSONObject} value to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(JSONObject value) {
		return adapter.toStringSQL(value);
	}

	/**
	 * Check and return the value from a result set.
	 *
	 * @param rs       The {@link DBResultSet}.
	 * @param type     The {@link Types} type.
	 * @param decimals The number of decimal places when the type is DECIMAL.
	 * @param index    The index within the result set.
	 * @return The corresponding value.
	 * @throws SQLException If an error occurs.
	 */
	public Value fromResultSet(
			DBResultSet rs,
			Types type,
			int decimals,
			int index) throws SQLException {

		Value value = null;
		if (type == Types.BOOLEAN) {
			String str = rs.getString(index);
			value = new Value(str != null && str.equals("T"));
		} else if (type == Types.DECIMAL) {
			BigDecimal dec = rs.getBigDecimal(index);
			if (dec != null) {
				dec = dec.setScale(decimals, RoundingMode.HALF_UP);
				value = new Value(dec);
			}
		} else if (type == Types.DOUBLE) {
			Double num = rs.getDouble(index);
			value = new Value(num);
		} else if (type == Types.INTEGER) {
			Integer num = rs.getInt(index);
			value = new Value(num);
		} else if (type == Types.LONG) {
			Long num = rs.getLong(index);
			value = new Value(num);
		} else if (type == Types.DATE) {
			Date date = rs.getDate(index);
			if (date == null) {
				value = new Value((LocalDate) null);
			} else {
				value = new Value(date.toLocalDate());
			}
		} else if (type == Types.TIME) {
			Time time = rs.getTime(index);
			if (time == null) {
				value = new Value((LocalTime) null);
			} else {
				value = new Value(time.toLocalTime());
			}
		} else if (type == Types.TIMESTAMP) {
			Timestamp timestamp = rs.getTimestamp(index);
			if (timestamp == null) {
				value = new Value((LocalDateTime) null);
			} else {
				value = new Value(timestamp.toLocalDateTime());
			}
		} else if (type == Types.STRING) {
			String str = rs.getString(index);
			value = new Value(str);
		} else if (type == Types.BINARY) {
			byte[] bin = rs.getBytes(index);
			value = new Value(bin);
		} else if (type == Types.ARRAY) {
			String str = rs.getString(index);
			JSONObject json_obj = JSONObject.parse(str);
			Value[] values = Value.toValueArray(json_obj);
			value = new Value(values);
		} else if (type == Types.OBJECT) {
			String str = rs.getString(index);
			JSONObject json_obj = JSONObject.parse(str);
			value = new Value(json_obj);
		} else {
			throw new IllegalStateException("Should never come here");
		}
		return value;
	}

	/**
	 * Check and return the value from a result set.
	 *
	 * @param rs       The {@link DBResultSet}.
	 * @param type     The {@link Types} type.
	 * @param decimals The number of decimal places when the type is DECIMAL.
	 * @param alias    The alias within the result set.
	 * @return The corresponding value.
	 * @throws SQLException If an error occurs.
	 */
	public Value fromResultSet(
			DBResultSet rs,
			Types type,
			int decimals,
			String alias) throws SQLException {

		Value value = null;
		if (type == Types.BOOLEAN) {
			String str = rs.getString(alias);
			value = new Value(str != null && str.equals("T"));
		} else if (type == Types.DECIMAL) {
			BigDecimal dec = rs.getBigDecimal(alias);
			if (dec != null) {
				dec = dec.setScale(decimals, RoundingMode.HALF_UP);
				value = new Value(dec);
			}
		} else if (type == Types.DOUBLE) {
			Double num = rs.getDouble(alias);
			value = new Value(num);
		} else if (type == Types.INTEGER) {
			Integer num = rs.getInt(alias);
			value = new Value(num);
		} else if (type == Types.LONG) {
			Long num = rs.getLong(alias);
			value = new Value(num);
		} else if (type == Types.DATE) {
			Date date = rs.getDate(alias);
			if (date == null) {
				value = new Value((LocalDate) null);
			} else {
				value = new Value(date.toLocalDate());
			}
		} else if (type == Types.TIME) {
			Time time = rs.getTime(alias);
			if (time == null) {
				value = new Value((LocalTime) null);
			} else {
				value = new Value(time.toLocalTime());
			}
		} else if (type == Types.TIMESTAMP) {
			Timestamp timestamp = rs.getTimestamp(alias);
			if (timestamp == null) {
				value = new Value((LocalDateTime) null);
			} else {
				value = new Value(timestamp.toLocalDateTime());
			}
		} else if (type == Types.STRING) {
			String str = rs.getString(alias);
			value = new Value(str);
		} else if (type == Types.BINARY) {
			byte[] bin = rs.getBytes(alias);
			value = new Value(bin);
		} else if (type == Types.ARRAY) {
			String str = rs.getString(alias);
			JSONObject json_obj = JSONObject.parse(str);
			Value[] values = Value.toValueArray(json_obj);
			value = new Value(values);
		} else if (type == Types.OBJECT) {
			String str = rs.getString(alias);
			JSONObject json_obj = JSONObject.parse(str);
			value = new Value(json_obj);
		} else {
			throw new IllegalStateException("Should never come here");
		}
		return value;

	}
	/**
	 * Apply the value to an index position of a {@link DBPreparedStatement}.
	 *
	 * @param ps    Prepared statement.
	 * @param index Index.
	 * @param value Value to apply.
	 * @throws SQLException If an error occurs.
	 */
	public void toPreparedStatement(
			DBPreparedStatement ps,
			int index,
			Value value) throws SQLException {

		if (value.isNull()) {
			ps.setNull(index, value.getType());
			return;
		}

		Types type = value.getType();
		if (type == Types.BOOLEAN) {
			ps.setString(index, (value.getBoolean() ? "T" : "F"));
		} else if (type == Types.DECIMAL) {
			ps.setBigDecimal(index, value.getBigDecimal());
		} else if (type == Types.DOUBLE) {
			ps.setDouble(index, value.getDouble());
		} else if (type == Types.INTEGER) {
			ps.setInt(index, value.getInteger());
		} else if (type == Types.LONG) {
			ps.setLong(index, value.getLong());
		} else if (type == Types.DATE) {
			ps.setDate(index, Date.valueOf(value.getDate()));
		} else if (type == Types.TIME) {
			ps.setTime(index, Time.valueOf(value.getTime()));
		} else if (type == Types.TIMESTAMP) {
			ps.setTimestamp(index, Timestamp.valueOf(value.getTimestamp()));
		} else if (type == Types.STRING) {
			int length = value.getString().length();
			if (length <= FIXED_LENGTH) {
				ps.setString(index, value.getString());
			} else {
				String str = value.getString();
				ps.setCharacterStream(index, new StringReader(str));
			}
		} else if (type == Types.BINARY) {
			byte[] bytes = value.getBinary();
			if (bytes.length <= FIXED_LENGTH) {
				ps.setBytes(index, bytes);
			} else {
				ps.setBinaryStream(index, new ByteArrayInputStream(bytes));
			}
		} else if (type == Types.ARRAY) {
			String str = Value.toJSONObject(value.getArray()).toString();
			ps.setCharacterStream(index, new StringReader(str));
		} else if (type == Types.OBJECT) {
			String str = value.getObject().toString();
			ps.setCharacterStream(index, new StringReader(str));
		} else {
			throw new IllegalStateException("Should never come here");
		}
	}

}
