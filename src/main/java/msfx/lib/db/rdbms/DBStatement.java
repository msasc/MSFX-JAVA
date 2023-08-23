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
