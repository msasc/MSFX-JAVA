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
package msfx.ztrash.db.rdbms.adapters;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import msfx.ztrash.db.Field;
import msfx.ztrash.db.Types;
import msfx.ztrash.db.rdbms.DBEngine;
import msfx.ztrash.db.rdbms.DBEngineAdapter;
import msfx.ztrash.db.rdbms.DBMetaData;
import msfx.lib.util.Strings;

/**
 * Oracle <i>DBEngineAdapter</i>.
 * 
 * @author Miquel Sas
 */
public class OracleAdapter extends DBEngineAdapter {

	public static final long MAX_VARCHAR = 2000;

	/**
	 * Constructor.
	 */
	public OracleAdapter() {}

	/**
	 * Returns the CURRENT DATE function as a string.
	 */
	@Override
	public String getCurrentDate() { return "SYSTIMESTAMP"; }
	/**
	 * Returns the CURRENT TIME function as a string.
	 */
	@Override
	public String getCurrentTime() { return "SYSTIMESTAMP"; }
	/**
	 * Returns the CURRENT TIMESTAMP function as a string.
	 */
	@Override
	public String getCurrentTimestamp() { return "SYSTIMESTAMP"; }

	/**
	 * Return the driver class name.
	 */
	@Override
	public String getDriverClassName() { return "oracle.jdbc.driver.OracleDriver"; }
	/**
	 * Return the driver protocol.
	 */
	@Override
	public String getDriverProtocol() { return "jdbc:oracle:thin:@"; }

	/**
	 * Return the field definition.
	 */
	public String getFieldDefinition(Field field) {

		if (field == null) {
			throw new NullPointerException("Field can not be null");
		}

		StringBuilder def = new StringBuilder();

		if (field.isBoolean()) { def.append("VARCHAR2(1)"); }

		if (field.isDecimal()) {
			int len = field.getLength();
			int dec = field.getDecimals();
			def.append("NUMBER(" + len + "," + dec + ")");
		}
		if (field.isDouble()) { def.append("NUMBER"); }
		if (field.isLong()) { def.append("NUMBER"); }
		if (field.isInteger()) { def.append("NUMBER"); }

		if (field.isDate()) { def.append("DATE"); }
		if (field.isTime()) { def.append("TIME"); }
		if (field.isTimestamp()) { def.append("TIMESTAMP"); }

		if (field.isBinary()) {
			int len = field.getLength();
			if (len <= MAX_VARCHAR) {
				def.append("RAW");
			} else {
				def.append("LONG RAW");
			}
		}

		if (field.isString()) {
			int len = field.getLength();
			if (len <= MAX_VARCHAR) {
				def.append("VARCHAR2(" + len + ")");
			} else {
				def.append("CLOB");
			}
		}

		if (field.isArray()) { def.append("CLOB"); }
		if (field.isObject()) { def.append("CLOB"); }

		return def.toString();
	}

	/**
	 * Return the database metadata.
	 */
	@Override
	public DBMetaData getMetaData(DBEngine db) throws SQLException { return null; }

	/**
	 * Return the library type.
	 */
	@Override
	public Types getType(String columnType) { return null; }

	/**
	 * @param value The {@link LocalDate} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(LocalDate value) {
		if (value == null) return "NULL";
		return "TO_TIMESTAMP('" + value + "', 'YYYY-MM-DD')";
	}
	/**
	 * @param value The {@link LocalTime} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(LocalTime value) {
		if (value == null) return "NULL";
		return "TO_TIMESTAMP('" + value + "', 'HH24:MI:SS.FF')";
	}
	/**
	 * @param value The {@link LocalDateTime} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(LocalDateTime value) {
		if (value == null) return "NULL";
		String timestamp = Strings.replace(value.toString(), "T", " ");
		return "TO_TIMESTAMP('" + timestamp + "', 'YYYY-MM-DD HH24:MI:SS.FF')";
	}
}
