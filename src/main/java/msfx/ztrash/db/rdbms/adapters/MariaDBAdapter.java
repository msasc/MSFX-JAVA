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

/**
 * MariaDB <i>DBEngineAdapter</i>.
 * 
 * @author Miquel Sas
 */
public class MariaDBAdapter extends DBEngineAdapter {

	public static final long SIZE_TINY = 255;
	public static final long SIZE_SHORT = 65535;
	public static final long SIZE_MEDIUM = 16777215;
	public static final long SIZE_LONG = 4294967295L;

	/**
	 * Constructor.
	 */
	public MariaDBAdapter() {}

	/**
	 * Returns the CURRENT DATE function as a string.
	 */
	@Override
	public String getCurrentDate() { return "CURRENT_DATE"; }

	/**
	 * Returns the CURRENT TIME function as a string.
	 */
	@Override
	public String getCurrentTime() { return "CURRENT_TIME"; }

	/**
	 * Returns the CURRENT TIMESTAMP function as a string.
	 */
	@Override
	public String getCurrentTimestamp() { return "CURRENT_TIMESTAMP"; }

	/**
	 * Return the driver class name.
	 */
	@Override
	public String getDriverClassName() { return "org.mariadb.jdbc.Driver"; }

	/**
	 * Return the driver protocol.
	 */
	@Override
	public String getDriverProtocol() { return "jdbc:mariadb://"; }

	/**
	 * Return the field definition.
	 */
	@SuppressWarnings("ConstantConditions")
	public String getFieldDefinition(Field field) {

		if (field == null) {
			throw new NullPointerException("Field can not be null");
		}

		StringBuilder def = new StringBuilder();
		def.append(field.getName());
		def.append(" ");

		if (field.isBoolean()) {
			def.append("VARCHAR(1)");
		}

		if (field.isDecimal()) {
			if (field.getLength() == null) throw new IllegalArgumentException("Field " + field
					.getName() + " without length");
			if (field.getDecimals() == null) throw new IllegalArgumentException("Field " + field
					.getName() + " without decimals");
			int len = field.getLength();
			int dec = field.getDecimals();
			if (dec < 0) dec = 0;
			if (len <= 0) len = 65 - dec;
			def.append("DECIMAL(");
			def.append(len);
			def.append(",");
			def.append(dec);
			def.append(")");
		}
		if (field.isDouble()) {
			def.append("DOUBLE");
		}
		if (field.isLong()) {
			def.append("BIGINT");
		}
		if (field.isInteger()) {
			def.append("INTEGER");
		}

		if (field.isDate()) {
			def.append("DATE");
		}
		if (field.isTime()) {
			def.append("TIME");
		}
		if (field.isTimestamp()) {
			def.append("TIMESTAMP");
		}

		if (field.isBinary()) {
			int len = field.getLength();
			if (len > SIZE_LONG) {
				throw new IllegalArgumentException("Too big field length: " + len);
			}
			if (len <= SIZE_TINY) def.append("TINYBLOB");
			else if (len <= SIZE_SHORT) def.append("BLOB");
			else if (len <= SIZE_MEDIUM) def.append("MEDIUMBLOB");
			else def.append("LONGBLOB");
		}

		if (field.isString()) {
			int len = field.getLength();
			if (len > SIZE_LONG) {
				throw new IllegalArgumentException("Too big field length: " + len);
			}
			if (len > 0 && len <= SIZE_SHORT) {
				def.append("VARCHAR(");
				def.append(len);
				def.append(")");
			} else {
				if (len <= SIZE_TINY) def.append("TINYTEXT");
				else if (len <= SIZE_SHORT) def.append("TEXT");
				else if (len <= SIZE_MEDIUM) def.append("MEDIUMTEXT");
				else def.append("LONGTEXT");
			}
		}

		if (field.isArray()) {
			def.append("JSON");
		}
		if (field.isObject()) {
			def.append("JSON");
		}

		return def.toString();
	}

	/**
	 * Return the database metadata.
	 */
	@Override
	public DBMetaData getMetaData(DBEngine db) throws SQLException {
		return new MariaDBMetaData(db);
	}

	/**
	 * Return the library type.
	 */
	@Override
	public Types getType(String columnType) {
		return null;
	}

	/**
	 * @param value The {@link LocalDate} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(LocalDate value) {
		if (value == null) return "NULL";
		return "DATE'" + value + "'";
	}
	/**
	 * @param value The {@link LocalTime} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(LocalTime value) {
		if (value == null) return "NULL";
		return "TIME'" + value + "'";
	}
	/**
	 * @param value The {@link LocalDateTime} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(LocalDateTime value) {
		if (value == null) return "NULL";
		return "TIMESTAMP'" + value + "'";
	}
}
