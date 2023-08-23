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

import java.sql.SQLException;

import msfx.lib.db.FieldList;
import msfx.lib.db.Record;
import msfx.lib.db.rdbms.sql.Select;

/**
 * A record iterator that closes itself when there are no more records to read.
 *
 * @author Miquel Sas
 */
public class DBIterator {

	/**
	 * Underlying engine.
	 */
	private final DBEngine db;
	/**
	 * Connection.
	 */
	private DBConnection cn = null;
	/**
	 * Statement.
	 */
	private DBStatement st = null;
	/**
	 * Result set.
	 */
	private DBResultSet rs = null;

	/**
	 * Field list.
	 */
	private FieldList fields = null;

	/**
	 * Constructor.
	 *
	 * @param db Underlying database engine.
	 */
	public DBIterator(DBEngine db) {
		this.db = db;
	}

	/**
	 * Start a select.
	 *
	 * @param select Select query.
	 * @throws SQLException If such an error occurs.
	 */
	public void select(Select select) throws SQLException {
		select(select.toSQL(), select.getFields());
	}
	/**
	 * Start a select.
	 *
	 * @param sql    The SQL query.
	 * @param fields The list of fields to determine types.
	 * @throws SQLException If an SQL error occurs.
	 */
	public void select(String sql, FieldList fields) throws SQLException {
		if (!isClosed()) {
			throw new SQLException("Iterator is open");
		}
		this.fields = fields;
		this.cn = db.getConnection();
		this.st = cn.createStatement();
		this.rs = st.executeQuery(sql);
	}

	/**
	 * Close the iterator.
	 *
	 * @throws SQLException If such an error occurs.
	 */
	public void close() throws SQLException {
		if (rs != null && !rs.isClosed()) rs.close();
		if (st != null && !st.isClosed()) st.close();
		if (cn != null && !cn.isClosed()) cn.close();
		cn = null;
		st = null;
		rs = null;
	}

	/**
	 * Check whether there are records to read.
	 *
	 * @return A boolean.
	 * @throws SQLException If such an error occurs.
	 */
	public boolean hasNext() throws SQLException {
		if (isClosed()) {
			throw new SQLException("Iterator is closed");
		}
		boolean next = rs.next();
		if (!next) {
			close();
		}
		return next;
	}

	/**
	 * Red the next record.
	 *
	 * @return The record.
	 * @throws SQLException If an SQL error occurs.
	 */
	public Record next() throws SQLException {
		if (isClosed()) {
			throw new SQLException("Iterator is closed");
		}
		return db.readRecord(rs, fields);
	}

	/**
	 * Check whether the iterator is closed.
	 *
	 * @return A boolean.
	 */
	public boolean isClosed() {
		return cn == null && st == null && rs == null;
	}
}
