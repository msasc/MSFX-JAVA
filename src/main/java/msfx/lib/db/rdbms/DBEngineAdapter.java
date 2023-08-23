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

import msfx.lib.db.Field;
import msfx.lib.db.Types;
import msfx.lib.db.Value;
import msfx.lib.json.JSONObject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A database engine adapter addresses the particular properties of different RDMS systems, like
 * column definition or type mapping.
 *
 * @author Miquel Sas
 */
public abstract class DBEngineAdapter {

	/**
	 * Returns the CURRENT DATE function.
	 *
	 * @return The CURRENT DATE function as a string.
	 */
	public abstract String getCurrentDate();
	/**
	 * Returns the CURRENT TIME function.
	 *
	 * @return The CURRENT TIME function as a string.
	 */
	public abstract String getCurrentTime();
	/**
	 * Returns the CURRENT TIMESTAMP function.
	 *
	 * @return The CURRENT TIMESTAMP function as a string.
	 */
	public abstract String getCurrentTimestamp();
	/**
	 * Return the JDBC driver class name, for example <i>org.mariadb.jdbc.Driver</i>
	 * for a MariaDB driver, <i>org.postgresql.Driver</i> for a PostgreSQL driver,
	 * or <i>oracle.jdbc.driver.OracleDriver</i> for an Oracle driver.
	 *
	 * @return The JDBC driver class name.
	 */
	public abstract String getDriverClassName();
	/**
	 * Return the driver protocol, for example <i>jdbc:mariadb://</i> for a MariaDB
	 * driver, <i>jdbc:postgresql://</i> for a PostgreSQL driver, or
	 * <i>jdbc:oracle:thin:@</i> for an Oracle driver.
	 *
	 * @return The driver protocol.
	 */
	public abstract String getDriverProtocol();
	/**
	 * Return the field definition to use in a <i>CREATE TABLE</i>.
	 *
	 * @param field The field.
	 * @return The field definition.
	 */
	public abstract String getFieldDefinition(Field field);
	/**
	 * Returns the suffix part of a field definition, that is standard, with DEFAULT
	 * values and NOT NULL.
	 *
	 * @param field The argument field.
	 * @return The suffix part of the field definition.
	 */
	public String getFieldDefinitionSuffix(Field field) {
		StringBuilder b = new StringBuilder();
		if (!field.isNullable()) {
			b.append(" NOT NULL");
		}
		if (field.getDefaultCreateValue() != null) {
			b.append(" DEFAULT ");
			b.append(toStringSQL(field.getDefaultCreateValue()));
		} else {
			if (!field.isNullable()) {
				if (field.isDate()) {
					b.append(" DEFAULT ");
					b.append(getCurrentDate());
				}
				if (field.isTime()) {
					b.append(" DEFAULT ");
					b.append(getCurrentTime());
				}
				if (field.isTimestamp()) {
					b.append(" DEFAULT ");
					b.append(getCurrentTimestamp());
				}
			}
			if (field.isBoolean()) b.append(" DEFAULT 'F'");
			if (field.isNumber()) b.append(" DEFAULT 0");
		}
		return b.toString();
	}
	/**
	 * Returns the database metadata information.
	 *
	 * @param db The database engine.
	 * @return The metadata information class.
	 * @throws SQLException If an error occurs.
	 */
	public abstract DBMetaData getMetaData(DBEngine db) throws SQLException;
	/**
	 * Return the library type.
	 *
	 * @param columnType The column data type.
	 * @return The library type.
	 */
	public abstract Types getType(String columnType);

	/**
	 * Convert the argument value to a string that can be used in a SELECT, INSERT,
	 * UPDATE or WHERE query. Note that type <i>BINARY</i> issues an error.
	 *
	 * @param value The value to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(Value value) {

		if (value.isBinary()) {
			throw new IllegalArgumentException("BINARY type not permitted");
		}

		if (value.isBoolean()) return toStringSQL(value.getBoolean());
		if (value.isDecimal()) return toStringSQL(value.getBigDecimal());
		if (value.isDouble()) return toStringSQL(value.getDouble());
		if (value.isInteger()) return toStringSQL(value.getInteger());
		if (value.isLong()) return toStringSQL(value.getLong());
		if (value.isString()) return toStringSQL(value.getString());

		if (value.isNull()) return "NULL";

		if (value.isDate()) return toStringSQL(value.getDate());
		if (value.isTime()) return toStringSQL(value.getTime());
		if (value.isTimestamp()) return toStringSQL(value.getTimestamp());
		if (value.isArray()) return toStringSQL(value.getArray());
		if (value.isObject()) return toStringSQL(value.getObject());

		throw new IllegalStateException("Unreachable code");
	}

	/**
	 * Returns the string used in a query.
	 *
	 * @param value A boolean.
	 * @return The string used in a query.
	 */
	public String toStringSQL(boolean value) {
		return value ? "'T'" : "'F'";
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value A {@link BigDecimal}
	 * @return The string used in a query.
	 */
	public String toStringSQL(BigDecimal value) {
		if (value == null) return "0";
		return value.toPlainString();
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value A {@link Double}
	 * @return The string used in a query.
	 */
	public String toStringSQL(Double value) {
		if (value == null) return "0";
		return BigDecimal.valueOf(value).toPlainString();
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value A {@link Integer}
	 * @return The string used in a query.
	 */
	public String toStringSQL(Integer value) {
		if (value == null) return "0";
		return BigDecimal.valueOf(value).toPlainString();
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value A {@link Long}
	 * @return The string used in a query.
	 */
	public String toStringSQL(Long value) {
		if (value == null) return "0";
		return BigDecimal.valueOf(value).toPlainString();
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value The {@link String} to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(String value) {
		if (value == null) return "NULL";
		return "'" + value + "'";
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value The {@link LocalDate} to format as a string.
	 * @return The formatted string.
	 */
	public abstract String toStringSQL(LocalDate value);
	/**
	 * Returns the string used in a query.
	 *
	 * @param value The {@link LocalTime} to format as a string.
	 * @return The formatted string.
	 */
	public abstract String toStringSQL(LocalTime value);
	/**
	 * Returns the string used in a query.
	 *
	 * @param value The {@link LocalDateTime} to format as a string.
	 * @return The formatted string.
	 */
	public abstract String toStringSQL(LocalDateTime value);
	/**
	 * Returns the string used in a query.
	 *
	 * @param values The Value[] to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(Value[] values) {
		return "'" + Value.toJSONObject(values) + "'";
	}
	/**
	 * Returns the string used in a query.
	 *
	 * @param value The {@link JSONObject} value to format as a string.
	 * @return The formatted string.
	 */
	public String toStringSQL(JSONObject value) {
		return "'" + value.toString() + "'";
	}
}
