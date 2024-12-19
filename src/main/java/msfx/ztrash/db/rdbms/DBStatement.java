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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Simplified {@link Statement}.
 *
 * @author Miquel Sas
 */
public class DBStatement extends DBAbstractStatement {

	/**
	 * Constructor.
	 *
	 * @param cn     The parent {@link DBConnection}.
	 * @param drv_st The underlying {@link Statement}.
	 */
	DBStatement(DBConnection cn, Statement drv_st) {
		super(cn, drv_st);
	}

	/**
	 * Execute a SELECT query.
	 *
	 * @param sql The SELECT query.
	 * @return A simplified forward only {@link ResultSet}.
	 * @throws SQLException If an error occurs.
	 */
	public DBResultSet executeQuery(String sql) throws SQLException {
		if (getFetchSize() == 0) {
			setFetchSize(getConnection().getFetchSize());
		}
		Statement drv_st = getDriverStatement();
		ResultSet drv_rs = drv_st.executeQuery(sql);
		DBResultSet rs = new DBResultSet(this, drv_rs);
		add(rs);
		return rs;
	}
	/**
	 * Execute an UPDATE query.
	 *
	 * @param sql The UPDATE query.
	 * @return The number of updated rows or zero if not applicable.
	 * @throws SQLException If an error occurs.
	 */
	public int executeUpdate(String sql) throws SQLException {
		Statement drv_st = getDriverStatement();
		return drv_st.executeUpdate(sql);
	}
}
