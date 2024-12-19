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

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A connection pool to a back end RDBMS database.
 *
 * @author Miquel Sas
 */
@SuppressWarnings("ALL")
public class DBConnectionPool {

	/**
	 * Connection closer timer task.
	 */
	private class ConnectionCloser extends TimerTask {
		public void run() {
			closeConnections();
		}
	}

	/**
	 * Driver class name, for instance "oracle.jdbc.driver.OracleDriver".
	 */
	private String driverClassName;
	/**
	 * Protocol, for instance "jdbc:oracle:thin:@".
	 */
	private String driverProtocol;
	/**
	 * Database, for instance "ocma.eu.roca.net:1521:OCMA".
	 */
	private String database;
	/**
	 * User.
	 */
	private String user;
	/**
	 * Password.
	 */
	private String password;

	/**
	 * A boolean that indicates that the driver has been registered.
	 */
	private boolean driverRegistered = false;

	/**
	 * List of connections instantiated and ready to be used.
	 */
	private List<DBConnection> connectionsFree = new ArrayList<>();
	/**
	 * List of connections in use.
	 */
	private List<DBConnection> connectionsInUse = new ArrayList<>();
	/**
	 * Lock to coordinate requests.
	 */
	private ReentrantLock lock = new ReentrantLock();

	/**
	 * Timer to schedule the connection closer.
	 */
	private Timer timer;
	/**
	 * Timeout to close free connections.
	 */
	private long timeout = 60000;

	/**
	 * Default fetch size.
	 */
	private int fetchSize = 100;

	/**
	 * Counter of the times the closer executes.
	 */
	private int closerExecutions = 0;
	/**
	 * Counter of connections closed and removed.
	 */
	private int removedConnections = 0;

	/**
	 * A boolean that indicates that this database engine has been closed and no more connections
	 * can be requested.
	 */
	private boolean closed = false;

	/**
	 * Constructor passing the necessary connection information.
	 *
	 * @param driverClassName Driver class name.
	 * @param driverProtocol  Driver protocol.
	 * @param database        Database or SID.
	 * @param user            User.
	 * @param password        Password.
	 */
	public DBConnectionPool(
			String driverClassName,
			String driverProtocol,
			String database,
			String user,
			String password) {
		this.driverClassName = driverClassName;
		this.driverProtocol = driverProtocol;
		this.database = database;
		this.user = user;
		this.password = password;

		/* Instantiate the timer and schedule it. */
		this.timer = new Timer("connection-closer");
		this.timer.schedule(new ConnectionCloser(), timeout, timeout);
	}

	/**
	 * Return the default fetch size.
	 *
	 * @return The default fetch size.
	 */
	public int getFetchSize() {
		return fetchSize;
	}
	/**
	 * Set the default fetch size.
	 *
	 * @param fetchSize The default fetch size.
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * Close this pool. Once it is closed, no more connections can be requested.
	 *
	 * @throws SQLException If an error occurs.
	 */
	public void close() throws SQLException {
		try {
			lock.lock();
			timer.cancel();
			for (DBConnection cn : connectionsInUse) {
				cn.closeUnderlyingConnection();
			}
			connectionsInUse.clear();
			for (DBConnection cn : connectionsFree) {
				cn.closeUnderlyingConnection();
			}
			connectionsFree.clear();
			closed = true;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Close connections that exceed the timeout sleeping.
	 */
	private void closeConnections() {
		try {
			lock.lock();
			long time = System.currentTimeMillis();
			for (int i = connectionsFree.size() - 1; i >= 0; i--) {
				DBConnection cn = connectionsFree.get(i);
				if (time - cn.timeClosed() >= timeout) {
					try {
						cn.closeUnderlyingConnection();
					} catch (SQLException exc) {
						exc.printStackTrace();
					}
					connectionsFree.remove(i);
					removedConnections++;
				}
			}
			closerExecutions++;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns the number of closer executions for control purposes.
	 *
	 * @return The number of closer executions.
	 */
	public int getCloserExecutions() {
		return closerExecutions;
	}
	/**
	 * Returns the number of closed and removed connections for control purposes.
	 *
	 * @return The number of closed and removed connections.
	 */
	public int getRemovedConnections() {
		return removedConnections;
	}

	/**
	 * Frees the connection by removing it from the connections in use and adding it
	 * to the free connections.
	 *
	 * @param cn The connection to free.
	 */
	void freeConnection(DBConnection cn) {
		try {
			lock.lock();
			connectionsInUse.remove(cn);
			connectionsFree.add(cn);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns the total number of connections available in this pool.
	 *
	 * @return The total number of connections.
	 */
	public int getConnectionCount() {
		int count = 0;
		try {
			lock.lock();
			count += connectionsFree.size();
			count += connectionsInUse.size();
		} finally {
			lock.unlock();
		}
		return count;
	}
	/**
	 * Returns the number of free connections.
	 *
	 * @return The number of free connections.
	 */
	public int getConnectionCountFree() {
		int count = 0;
		try {
			lock.lock();
			count += connectionsFree.size();
		} finally {
			lock.unlock();
		}
		return count;
	}
	/**
	 * Returns the number of connections in use.
	 *
	 * @return The number of connections in use.
	 */
	public int getConnectionCountInUse() {
		int count = 0;
		try {
			lock.lock();
			count += connectionsInUse.size();
		} finally {
			lock.unlock();
		}
		return count;
	}

	/**
	 * Returns a connection free to use.
	 *
	 * @return The connection.
	 * @throws SQLException If an error occurs.
	 */
	public DBConnection getConnection() throws SQLException {
		try {
			lock.lock();
			if (closed) {
				throw new SQLException("DBEngine is closed");
			}
			DBConnection cn = null;
			if (!connectionsFree.isEmpty()) {
				cn = connectionsFree.remove(connectionsFree.size() - 1);
			}
			if (cn == null) {
				cn = new DBConnection(this, createConnection());
			}
			cn.activate();
			connectionsInUse.add(cn);
			return cn;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Creates a new underlying connection.
	 *
	 * @return The underlying <i>Connection</i>.
	 * @throws SQLException
	 */
	private Connection createConnection() throws SQLException {
		if (!driverRegistered) {
			registerDriver();
			driverRegistered = true;
		}
		Connection cn = DriverManager.getConnection(driverProtocol + database, user, password);
		return cn;
	}

	/**
	 * Registers the driver.
	 *
	 * @throws SQLException If an error occurs.
	 */
	public void registerDriver() throws SQLException {
		try {
			Class<?> driverClass = Class.forName(driverClassName);
			Constructor<?> constructor = driverClass.getConstructor((Class<?>[]) null);
			Driver driver = (Driver) constructor.newInstance((Object[]) null);
			DriverManager.registerDriver(driver);
		} catch (Exception exc) {
			throw new SQLException("Could not register the driver " + driverClassName, exc);
		}
	}
}
