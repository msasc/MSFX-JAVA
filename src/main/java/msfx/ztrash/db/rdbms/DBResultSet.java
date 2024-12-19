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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * A simplified forward only <i>ResultSet</i>.
 *
 * @author Miquel Sas
 */
public class DBResultSet {

	/**
	 * Parent statement.
	 */
	private DBAbstractStatement st;
	/**
	 * The underlying driver <i>ResultSet</i>.
	 */
	private final ResultSet drv_rs;

	/**
	 * Constructor.
	 *
	 * @param drv_rs The underlying driver <i>ResultSet</i>.
	 */
	public DBResultSet(ResultSet drv_rs) { this.drv_rs = drv_rs; }

	/**
	 * Constructor.
	 *
	 * @param st     The parent <i>DBAbstractStatement</i>.
	 * @param drv_rs The underlying driver <i>ResultSet</i>.
	 */
	DBResultSet(DBAbstractStatement st, ResultSet drv_rs) {
		this.st = st;
		this.drv_rs = drv_rs;
	}

	/**
	 * Close the underlying <i>ResultSet</i>.
	 *
	 * @throws SQLException If an error occurs.
	 */
	public void close() throws SQLException {
		if (st != null) st.remove(this);
		drv_rs.close();
	}
	/**
	 * Check whether the underlying <i>ResultSet</i> is closed.
	 *
	 * @return A boolean.
	 * @throws SQLException If an error occurs.
	 */
	public boolean isClosed() throws SQLException { return drv_rs.isClosed(); }

	/**
	 * Moves the cursor forward one row from its current position.
	 *
	 * @return A boolean, false if the cursor has moved after the last row.
	 * @throws SQLException If an error occurs.
	 */
	public boolean next() throws SQLException { return drv_rs.next(); }

	/**
	 * Retrieves the column value as a <i>BigDecimal</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public BigDecimal getBigDecimal(int index) throws SQLException {
		BigDecimal value = drv_rs.getBigDecimal(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>BigDecimal</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public BigDecimal getBigDecimal(String label) throws SQLException {
		BigDecimal value = drv_rs.getBigDecimal(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Retrieves the column value as an <i>InputStream</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public InputStream getBinaryStream(int index) throws SQLException {
		InputStream value = drv_rs.getBinaryStream(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as an <i>InputStream</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public InputStream getBinaryStream(String label) throws SQLException {
		InputStream value = drv_rs.getBinaryStream(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Retrieves the column value as a <i>boolean</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Boolean getBoolean(int index) throws SQLException {
		Boolean value = drv_rs.getBoolean(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>boolean</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Boolean getBoolean(String label) throws SQLException {
		Boolean value = drv_rs.getBoolean(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Retrieves the column value as a <i>byte[]</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public byte[] getBytes(int index) throws SQLException {
		byte[] value = drv_rs.getBytes(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>byte[]</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public byte[] getBytes(String label) throws SQLException {
		byte[] value = drv_rs.getBytes(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Retrieves the column value as a <i>Reader</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Reader getCharacterStream(int index) throws SQLException {
		Reader value = drv_rs.getCharacterStream(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>Reader</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Reader getCharacterStream(String label) throws SQLException {
		Reader value = drv_rs.getCharacterStream(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Retrieves the column value as a <i>Date</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Date getDate(int index) throws SQLException {
		Date value = drv_rs.getDate(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>Date</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Date getDate(String label) throws SQLException {
		Date value = drv_rs.getDate(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Retrieves the column value as a <i>double</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Double getDouble(int index) throws SQLException {
		Double value = drv_rs.getDouble(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>double</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Double getDouble(String label) throws SQLException {
		Double value = drv_rs.getDouble(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Retrieves the column value as a <i>int</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Integer getInt(int index) throws SQLException {
		Integer value = drv_rs.getInt(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>int</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Integer getInt(String label) throws SQLException {
		Integer value = drv_rs.getInt(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Retrieves the column value as a <i>long</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Long getLong(int index) throws SQLException {
		Long value = drv_rs.getLong(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>long</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Long getLong(String label) throws SQLException {
		Long value = drv_rs.getLong(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Return the <i>ResultSetMetaData</i> of the view.
	 *
	 * @return The result set metadata.
	 * @throws SQLException If an error occurs.
	 */
	public ResultSetMetaData getMetaData() throws SQLException { return drv_rs.getMetaData(); }

	/**
	 * Return the row number, if supported.
	 *
	 * @return The row number, if supported.
	 * @throws SQLException If an error occurs.
	 */
	public int getRow() throws SQLException { return drv_rs.getRow(); }

	/**
	 * Retrieves the column value as a <i>String</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public String getString(int index) throws SQLException {
		String value = drv_rs.getString(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>String</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public String getString(String label) throws SQLException {
		String value = drv_rs.getString(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Retrieves the column value as a <i>Time</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Time getTime(int index) throws SQLException {
		Time value = drv_rs.getTime(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>Time</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Time getTime(String label) throws SQLException {
		Time value = drv_rs.getTime(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Retrieves the column value as a <i>Timestamp</i>.
	 *
	 * @param index The column index.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Timestamp getTimestamp(int index) throws SQLException {
		Timestamp value = drv_rs.getTimestamp(index);
		if (drv_rs.wasNull()) return null;
		return value;
	}
	/**
	 * Retrieves the column value as a <i>Timestamp</i>.
	 *
	 * @param label The column label.
	 * @return The column value.
	 * @throws SQLException If an error occurs.
	 */
	public Timestamp getTimestamp(String label) throws SQLException {
		Timestamp value = drv_rs.getTimestamp(label);
		if (drv_rs.wasNull()) return null;
		return value;
	}

	/**
	 * Return this result set fetch size.
	 *
	 * @return The fetch size.
	 * @throws SQLException If an SQL error occurs.
	 */
	public int getFetchSize() throws SQLException {
		return drv_rs.getFetchSize();
	}
}
