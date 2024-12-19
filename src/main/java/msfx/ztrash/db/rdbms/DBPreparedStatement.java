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
package msfx.ztrash.db.rdbms;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import msfx.ztrash.db.Types;

/**
 * Simplified {@link PreparedStatement}.
 *
 * @author Miquel Sas
 */
public class DBPreparedStatement extends DBAbstractStatement {

	/**
	 * Constructor.
	 *
	 * @param cn     The parent {@link DBConnection}.
	 * @param drv_ps The underlying {@link PreparedStatement}.
	 */
	DBPreparedStatement(DBConnection cn, PreparedStatement drv_ps) {
		super(cn, drv_ps);
	}

	/**
	 * Execute a SELECT query.
	 *
	 * @return A simplified forward only result set.
	 * @throws SQLException If an error occurs.
	 */
	public DBResultSet executeQuery() throws SQLException {
		if (getFetchSize() == 0) {
			setFetchSize(getConnection().getFetchSize());
		}
		PreparedStatement drv_ps = getDriverPreparedStatement();
		ResultSet drv_rs = drv_ps.executeQuery();
		DBResultSet rs = new DBResultSet(this, drv_rs);
		add(rs);
		return rs;
	}
	/**
	 * Execute an UPDATE query.
	 *
	 * @return The number of updated rows or zero if not applicable.
	 * @throws SQLException If an error occurs.
	 */
	public int executeUpdate() throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		return drv_ps.executeUpdate();
	}

	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setBigDecimal(int i, BigDecimal x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setBigDecimal(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setBytes(int i, byte[] x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setBytes(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setBinaryStream(int i, InputStream x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setBinaryStream(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setBoolean(int i, boolean x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setBoolean(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setInt(int i, int x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setInt(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setLong(int i, long x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setLong(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setDouble(int i, double x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setDouble(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setString(int i, String x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setString(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setDate(int i, Date x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setDate(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setTime(int i, Time x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setTime(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i Parameter index.
	 * @param x Parameter value.
	 * @throws SQLException If an error occurs.
	 */
	public void setTimestamp(int i, Timestamp x) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setTimestamp(i, x);
	}
	/**
	 * Set the parameter value.
	 *
	 * @param i      Parameter index.
	 * @param reader Source reader.
	 * @throws SQLException If an error occurs.
	 */
	public void setCharacterStream(int i, Reader reader) throws SQLException {
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setCharacterStream(i, reader);
	}

	/**
	 * Set the index value to ull.
	 *
	 * @param i    Parameter index.
	 * @param type The type.
	 * @throws SQLException If an error occurs.
	 */
	public void setNull(int i, Types type) throws SQLException {
		if (type == null) {
			throw new NullPointerException("Type can not be null");
		}
		int sqlType = 0;
		switch (type) {
		case BOOLEAN:
			sqlType = java.sql.Types.VARCHAR;
			break;
		case DECIMAL:
			sqlType = java.sql.Types.DECIMAL;
			break;
		case DOUBLE:
			sqlType = java.sql.Types.DOUBLE;
			break;
		case INTEGER:
			sqlType = java.sql.Types.INTEGER;
			break;
		case LONG:
			sqlType = java.sql.Types.BIGINT;
			break;
		case DATE:
			sqlType = java.sql.Types.DATE;
			break;
		case TIME:
			sqlType = java.sql.Types.TIME;
			break;
		case TIMESTAMP:
			sqlType = java.sql.Types.TIMESTAMP;
			break;
		case STRING:
			sqlType = java.sql.Types.VARCHAR;
			break;
		case BINARY:
			sqlType = java.sql.Types.LONGVARBINARY;
			break;
		case ARRAY:
			sqlType = java.sql.Types.LONGVARCHAR;
			break;
		case OBJECT:
			sqlType = java.sql.Types.LONGVARCHAR;
			break;
		}
		PreparedStatement drv_ps = getDriverPreparedStatement();
		drv_ps.setNull(i, sqlType);
	}
}
