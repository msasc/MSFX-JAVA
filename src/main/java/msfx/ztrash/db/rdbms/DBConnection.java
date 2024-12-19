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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simplified pooled database connection.
 *
 * @author Miquel Sas
 */
public class DBConnection {

	/**
	 * Connection pool.
	 */
	private final DBConnectionPool pool;
	/**
	 * The underlying driver connection.
	 */
	private final Connection drv_cn;
	/**
	 * Closed control flag.
	 */
	private boolean closed = false;

	/**
	 * List of created closeable statements.
	 */
	private final List<DBAbstractStatement> statements = new ArrayList<>();

	/**
	 * Lock to synchronize statement management.
	 */
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * The time in millis when the pooled connection was logically closed and
	 * returned to the pool.
	 */
	private long timeClosed = 0;

	/**
	 * Constructor.
	 *
	 * @param pool   The connection pool.
	 * @param drv_cn The underlying driver {@link Connection}.
	 */
	DBConnection(DBConnectionPool pool, Connection drv_cn) {
		this.pool = pool;
		this.drv_cn = drv_cn;
	}

	/**
	 * Returns the fetch size.
	 *
	 * @return The fetch size as a shortcut to the connection pool.
	 */
	public int getFetchSize() {
		return pool.getFetchSize();
	}
	/**
	 * Set the fetch size.
	 *
	 * @param fetchSize The fetch size to set.
	 */
	public void setFetchSize(int fetchSize) {
		pool.setFetchSize(fetchSize);
	}

	/**
	 * Returns the database engine.
	 *
	 * @return The database engine.
	 */
	public DBConnectionPool getEngine() {
		return pool;
	}

	/**
	 * Close the logical connection and return it to the pool of free connections.
	 *
	 * @throws SQLException If an error occurs.
	 */
	public void close() throws SQLException {
		try {
			lock.lock();

			/* Close statements. */
			for (DBAbstractStatement statement : statements) {
				statement.close();
			}
			statements.clear();

			/* Set closed. */
			closed = true;

			/* Set time not active. */
			timeClosed = System.currentTimeMillis();

			/* Free the connection. */
			pool.freeConnection(this);

		} finally {
			lock.unlock();
		}
	}
	/**
	 * Check whether this connection has to be considered closed.
	 *
	 * @return A boolean.
	 */
	public boolean isClosed() throws SQLException {
		return closed;
	}
	/**
	 * Close the underlying connection.
	 *
	 * @throws SQLException If an error occurs.
	 */
	void closeUnderlyingConnection() throws SQLException {
		drv_cn.close();
	}
	/**
	 * Return the time not active or time when the connection was logically closed.
	 *
	 * @return The time not active.
	 */
	long timeClosed() {
		return timeClosed;
	}
	/**
	 * Activate the sleeping connection.
	 */
	void activate() {
		timeClosed = 0;
		closed = false;
	}

	/**
	 * Creates a simplified statement to execute queries and updates.
	 *
	 * @return The simplified statement.
	 * @throws SQLException If an SQL error occurs.
	 */
	public DBStatement createStatement() throws SQLException {
		try {
			lock.lock();
			checkClosed();
			Statement drv_st = drv_cn.createStatement();
			DBStatement st = new DBStatement(this, drv_st);
			statements.add(st);
			return st;
		} finally {
			lock.unlock();
		}
	}
	/**
	 * Prepared the simplified prepared statement with the given SQL statement.
	 *
	 * @param sql The SQL statement.
	 * @return The simplified prepared statement.
	 * @throws SQLException If an error occurs.
	 */
	public DBPreparedStatement prepareStatement(String sql) throws SQLException {
		try {
			lock.lock();
			checkClosed();
			DBPreparedStatement ps = new DBPreparedStatement(this, drv_cn.prepareStatement(sql));
			statements.add(ps);
			return ps;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Commit changes.
	 *
	 * @throws SQLException If an error occurs.
	 */
	public void commit() throws SQLException {
		checkClosed();
		drv_cn.commit();
	}
	/**
	 * Roll back changes.
	 *
	 * @throws SQLException If an error occurs.
	 */
	public void rollback() throws SQLException {
		checkClosed();
		drv_cn.rollback();
	}

	/**
	 * Check whether the connection is auto-commit.
	 *
	 * @return A boolean.
	 * @throws SQLException If an error occurs.
	 */
	public boolean isAutoCommit() throws SQLException {
		checkClosed();
		return drv_cn.getAutoCommit();
	}
	/**
	 * Sets this connection's auto-commit mode to the given state.
	 *
	 * @param autoCommit A boolean to enable/disable the auto commit mode.
	 * @throws SQLException If an error occurs.
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		checkClosed();
		drv_cn.setAutoCommit(autoCommit);
	}

	/**
	 * Set the catalog name space.
	 *
	 * @param catalog The catalog name.
	 * @throws SQLException If an error occurs.
	 */
	public void setCatalog(String catalog) throws SQLException {
		drv_cn.setCatalog(catalog);
	}

	/**
	 * Set the schema name space.
	 *
	 * @param schema The schema name.
	 * @throws SQLException If an error occurs.
	 */
	public void setSchema(String schema) throws SQLException {
		drv_cn.setSchema(schema);
	}

	/**
	 * Removes the previously created statement.
	 *
	 * @param st The statement, either prepared or not.
	 */
	protected void remove(DBAbstractStatement st) {
		statements.remove(st);
	}

	/**
	 * Check whether the pooled connection is closed.
	 *
	 * @throws SQLException If the connection is closed.
	 */
	private void checkClosed() throws SQLException {
		if (closed) {
			throw new SQLException("Connection is closed.");
		}
	}
}
