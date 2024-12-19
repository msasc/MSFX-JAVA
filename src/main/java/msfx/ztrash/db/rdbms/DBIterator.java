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

import java.sql.SQLException;

import msfx.ztrash.db.FieldList;
import msfx.ztrash.db.Record;
import msfx.ztrash.db.rdbms.sql.Select;

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
