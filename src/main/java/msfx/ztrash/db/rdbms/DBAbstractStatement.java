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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Root of {@link DBStatement} and {@link DBPreparedStatement}.
 *
 * @author Miquel Sas
 */
public abstract class DBAbstractStatement {

	/**
	 * The parent {@link DBConnection}.
	 */
	private final DBConnection cn;
	/**
	 * The underlying driver {@link Statement} or {@link PreparedStatement} depending on
	 * whether this abstract statement is a {@link DBStatement} or a {@link DBPreparedStatement}.
	 */
	private final Statement drv_st;

	/**
	 * List of created {@link DBResultSet} objects with this statement.
	 */
	private final List<DBResultSet> resultSets = new ArrayList<>();

	/**
	 * Constructor.
	 *
	 * @param cn     The parent {@link DBConnection}.
	 * @param drv_st The underlying driver {@link Statement} or {@link PreparedStatement}.
	 */
	protected DBAbstractStatement(DBConnection cn, Statement drv_st) {
		this.cn = cn;
		this.drv_st = drv_st;
	}

	/**
	 * Return the connection.
	 *
	 * @return The connection.
	 */
	public DBConnection getConnection() {
		return cn;
	}

	/**
	 * Return the underling {@link Statement} when it is not a {@link PreparedStatement}, otherwise
	 * throws an exception.
	 *
	 * @return The underling {@link Statement}.
	 */
	protected Statement getDriverStatement() {
		if (drv_st instanceof PreparedStatement) {
			throw new IllegalStateException("Underlying driver Statement is a PreparedStatement");
		}
		return drv_st;
	}
	/**
	 * Return the underlying {@link Statement} as a {@link PreparedStatement} when it effectively
	 * is, otherwise throw an exception.
	 *
	 * @return The underlying {@link Statement} as a {@link PreparedStatement}.
	 */
	protected PreparedStatement getDriverPreparedStatement() {
		if (drv_st instanceof PreparedStatement drv_ps) {
			return drv_ps;
		}
		throw new IllegalStateException("Underlying driver Statement is no a PreparedStatement");
	}

	/**
	 * Close the underlying driver {@link Statement} or {@link PreparedStatement}.
	 *
	 * @throws SQLException If an error occurs.
	 */
	public void close() throws SQLException {
		for (DBResultSet rs : resultSets) {
			if (!rs.isClosed()) {
				rs.close();
			}
		}
		resultSets.clear();
		cn.remove(this);
		drv_st.close();
	}
	/**
	 * Check whether the underlying driver {@link Statement} or {@link PreparedStatement}
	 * is closed.
	 *
	 * @return A boolean.
	 * @throws SQLException If an error occurs.
	 */
	public boolean isClosed() throws SQLException {
		return drv_st.isClosed();
	}

	/**
	 * Retrieves the number of result set rows that is the default fetch size.
	 *
	 * @return The fetch size.
	 * @throws SQLException If an error occurs.
	 */
	public int getFetchSize() throws SQLException {
		return drv_st.getFetchSize();
	}
	/**
	 * Sets the number of result set rows that should be the default fetch size.
	 *
	 * @param rows The fetch size.
	 * @throws SQLException If an error occurs.
	 */
	public void setFetchSize(int rows) throws SQLException {
		drv_st.setFetchSize(rows);
	}

	/**
	 * Add a result set to the list of opened result sets by this statement.
	 *
	 * @param rs The result set.
	 */
	protected void add(DBResultSet rs) {
		resultSets.add(rs);
	}
	/**
	 * Remove the result set from the list of opened result sets.
	 *
	 * @param rs The result set to remove.
	 */
	protected void remove(DBResultSet rs) {
		resultSets.remove(rs);
	}
}
