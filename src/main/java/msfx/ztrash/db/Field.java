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
package msfx.ztrash.db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Objects;

import msfx.ztrash.json.JSONObject;
import msfx.ztrash.res.StringRes;
import msfx.lib.util.Properties;

/**
 * Field metadata definition. These are the essential table properties.
 *
 * @author Miquel Sas
 */
public class Field {

	/**
	 * Helper to rapidly create fields.
	 *
	 * @param name Field name.
	 * @param type Type.
	 * @return The field definition.
	 */
	public static Field create(String name, Types type) {
		return create(name, type, null, null, false);
	}
	/**
	 * Helper to rapidly create fields.
	 *
	 * @param name   Field name.
	 * @param type   Type.
	 * @param length Length.
	 * @return The field definition.
	 */
	public static Field create(String name, Types type, Integer length) {
		return create(name, type, length, null, false);
	}
	/**
	 * Helper to rapidly create fields.
	 *
	 * @param name     Field name.
	 * @param type     Type.
	 * @param length   Length.
	 * @param decimals Decimals.
	 * @return The field definition.
	 */
	public static Field create(String name, Types type, Integer length, Integer decimals) {
		return create(name, type, length, decimals, false);
	}
	/**
	 * Helper to rapidly create fields.
	 *
	 * @param name       Field name.
	 * @param type       Type.
	 * @param length     Length.
	 * @param decimals   Decimals.
	 * @param primaryKey primary key indicator.
	 * @return The field definition.
	 */
	public static Field create(
			String name,
			Types type,
			Integer length,
			Integer decimals,
			boolean primaryKey) {
		Field field = new Field();
		field.setName(name);
		field.setType(type);
		field.setLength(length);
		field.setDecimals(decimals);
		field.setPrimaryKey(primaryKey);
		return field;
	}
	/**
	 * Creates the default row number field.
	 *
	 * @return The field.
	 */
	public static Field createRowNum() {
		return createRowNum(Locale.getDefault());
	}
	/**
	 * Creates the default row number field.
	 *
	 * @param locale The required locale for header and label.
	 * @return The field.
	 */
	public static Field createRowNum(Locale locale) {
		Field field = create("ROW_NUMBER", Types.LONG);
		field.setHeader(StringRes.get("FIELD.ROW_NUMBER", "Row", locale));
		field.setLabel(StringRes.get("FIELD.ROW_NUMBER", "Row number", locale));
		field.setTitle(StringRes.get("FIELD.ROW_NUMBER", "Row number", locale));
		return field;
	}

	/**
	 * The name or key used to access the field within a document or row.
	 */
	private String name;
	/**
	 * Optional field alias.
	 */
	private String alias;
	/**
	 * The type.
	 */
	private Types type;
	/**
	 * Length if applicable or null. This is a display or validation length.
	 */
	private Integer length;
	/**
	 * The number of decimal places if applicable or null.
	 */
	private Integer decimals;

	/**
	 * A flag that indicates whether this field is a primary key field.
	 */
	private boolean primaryKey = false;

	/**
	 * Optional default create value.
	 */
	private Value defaultCreateValue;

	/**
	 * Optional table.
	 */
	private Table table;

	/**
	 * Optional database function, either row or aggregate.
	 */
	private String function;

	/**
	 * Header in table views.
	 */
	private String header;
	/**
	 * Label in form views.
	 */
	private String label;
	/**
	 * Title or description.
	 */
	private String title;

	/**
	 * Uppercase indicator.
	 */
	private boolean uppercase = false;
	/**
	 * Optional display length.
	 */
	private Integer displayLength;
	/**
	 * Optional display decimals.
	 */
	private Integer displayDecimals;

	/**
	 * Additional and optional properties.
	 */
	private final Properties properties = new Properties();

	/**
	 * Constructor.
	 */
	public Field() { }
	/**
	 * Copy constructor.
	 *
	 * @param field The source field.
	 */
	public Field(Field field) {

		this.name = field.name;
		this.alias = field.alias;
		this.type = field.type;
		this.length = field.length;
		this.decimals = field.decimals;

		this.primaryKey = field.primaryKey;
		this.defaultCreateValue = field.defaultCreateValue;
		this.table = field.table;
		this.function = field.function;

		this.header = field.header;
		this.label = field.label;
		this.title = field.title;

		this.uppercase = field.uppercase;
		this.displayLength = field.displayLength;
		this.displayDecimals = field.displayDecimals;

		this.properties.putAll(field.properties);
	}

	/**
	 * Returns the name.
	 *
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Returns the type.
	 *
	 * @return The type.
	 */
	public Types getType() {
		return type;
	}
	/**
	 * Returns the length if applicable, otherwise null.
	 *
	 * @return The length.
	 */
	public Integer getLength() {
		return length;
	}
	/**
	 * Returns the number of decimal places if applicable, otherwise null.
	 *
	 * @return The decimals.
	 */
	public Integer getDecimals() {
		return decimals;
	}
	/**
	 * Return the optional field alias.
	 *
	 * @return The alias.
	 */
	public String getAlias() {
		return alias == null ? name : alias;
	}

	/**
	 * Set the name.
	 *
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Set the optional field alias.
	 *
	 * @param alias The alias.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	/**
	 * Set the type.
	 *
	 * @param type The type.
	 */
	public void setType(Types type) {
		this.type = type;
	}
	/**
	 * Set the length.
	 *
	 * @param length The length.
	 */
	public void setLength(Integer length) {
		this.length = length;
	}
	/**
	 * Set the decimal places, only valid for decimal fields.
	 *
	 * @param decimals The field decimals.
	 */
	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	/**
	 * Return the default create value if any.
	 *
	 * @return The default create value or null.
	 */
	public Value getDefaultCreateValue() {
		return defaultCreateValue;
	}
	/**
	 * Returns the default value for this field.
	 *
	 * @return The default value.
	 */
	public Value getDefaultValue() {
		switch (type) {
		case BOOLEAN:
			return new Value(false);
		case DECIMAL:
			new Value(new BigDecimal(0).setScale(getDecimals(), RoundingMode.HALF_UP));
		case DOUBLE:
			return new Value((double) 0);
		case INTEGER:
			return new Value(0);
		case LONG:
			return new Value((long) 0);
		case DATE:
			return new Value((LocalDate) null);
		case TIME:
			return new Value((LocalTime) null);
		case TIMESTAMP:
			return new Value((LocalDateTime) null);
		case STRING:
			return new Value("");
		case BINARY:
			return new Value(new byte[0]);
		case ARRAY:
			return new Value(new Value[0]);
		case OBJECT:
			return new Value(new JSONObject());
		}
		throw new IllegalStateException("Never should come here");
	}

	/**
	 * Set the default create value.
	 *
	 * @param value The default create value.
	 */
	public void setDefaultCreateValue(Value value) {
		if (value != null && value.getType() != getType()) {
			throw new IllegalArgumentException("Value type must match field type");
		}
		this.defaultCreateValue = value;
	}

	/**
	 * Check whether this field is a primary key field.
	 *
	 * @return A boolean
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	/**
	 * Check whether the field is persistent. Fields that belong to a table are always persistent.
	 *
	 * @return A boolean.
	 */
	public boolean isPersistent() {
		if (isVirtual()) {
			return false;
		}
		return table != null;
	}
	/**
	 * Check whether the field is nullable.
	 *
	 * @return A boolean.
	 */
	public boolean isNullable() {
		if (isPrimaryKey()) {
			return false;
		}
		if (isBoolean() || isNumber()) {
			return false;
		}
		return true;
	}

	/**
	 * Set whether this field is a primary key field.
	 *
	 * @param primaryKey A boolean.
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * Return this field parent table if any.
	 *
	 * @return The parent table.
	 */
	public Table getTable() {
		return table;
	}
	/**
	 * Set this field parent table.
	 *
	 * @param table The parent table.
	 */
	public void setTable(Table table) {
		this.table = table;
		this.function = null;
	}

	/**
	 * Return the optional database function.
	 *
	 * @return The function.
	 */
	public String getFunction() {
		return function;
	}
	/**
	 * Set te optional database function.
	 *
	 * @param function The optional database function.
	 */
	public void setFunction(String function) {
		this.function = function;
		this.table = null;
	}

	/**
	 * Check whether this field is virtual, that is, is a function.
	 *
	 * @return A boolean.
	 */
	public boolean isVirtual() {
		return function != null && !function.isEmpty();
	}

	/**
	 * Returns the name to use in a <i>GROUP BY</i> clause of a <i>SELECT</i> query.
	 *
	 * @return The name.
	 */
	public String getNameGroupBy() {
		return getNameSelect();
	}
	/**
	 * Returns the name to use in an <i>ORDER BY</i> clause of a select query.
	 *
	 * @return The name.
	 */
	public String getNameOrderBy() {
		return getNameSelect();
	}
	/**
	 * Returns the name qualified with the parent table alias if present.
	 *
	 * @return The qualified name.
	 */
	public String getNameParent() {
		StringBuilder name = new StringBuilder();
		if (table != null) {
			name.append(table.getAlias());
			name.append(".");
		}
		name.append(getName());
		return name.toString();
	}
	/**
	 * Returns the name to use in a <i>SELECT</i> clause of a select query.
	 *
	 * @return The name.
	 */
	public String getNameSelect() {
		StringBuilder name = new StringBuilder();
		if (isVirtual()) {
			name.append("(");
			name.append(getFunction());
			name.append(")");
		} else {
			name.append(getNameParent());
		}
		return name.toString();
	}

	/**
	 * Return the header in table views.
	 *
	 * @return The header.
	 */
	public String getHeader() {
		return header;
	}
	/**
	 * Return the label in form views.
	 *
	 * @return The label.
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * Return the title or description.
	 *
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the header to use table views.
	 *
	 * @param header The header.
	 */
	public void setHeader(String header) {
		this.header = header;
	}
	/**
	 * Set the label to use in form views.
	 *
	 * @param label The label.
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * Set the title or description.
	 *
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Check whether the field is uppercase.
	 *
	 * @return A boolean.
	 */
	public boolean isUppercase() {
		return uppercase;
	}
	/**
	 * Set whether this field value should be uppercase.
	 *
	 * @param uppercase A boolean.
	 */
	public void setUppercase(boolean uppercase) {
		this.uppercase = uppercase;
	}

	/**
	 * Return the display length.
	 *
	 * @return The display length
	 */
	public Integer getDisplayLength() {
		return displayLength != null ? displayLength : length;
	}
	/**
	 * Return the display decimals.
	 *
	 * @return The display decimals.
	 */
	public Integer getDisplayDecimals() {
		return displayDecimals != null ? displayDecimals : decimals;
	}

	/**
	 * Set the display length.
	 *
	 * @param displayLength The display length.
	 */
	public void setDisplayLength(Integer displayLength) {
		this.displayLength = displayLength;
	}
	/**
	 * Set the display decimals.
	 *
	 * @param displayDecimals The display decimals.
	 */
	public void setDisplayDecimals(Integer displayDecimals) {
		this.displayDecimals = displayDecimals;
	}

	/**
	 * Give access to additional and optional properties.
	 *
	 * @return The properties' container.
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Return a boolean indicating whether the type is BOOLEAN.
	 *
	 * @return A boolean.
	 */
	public boolean isBoolean() {
		return type.isBoolean();
	}
	/**
	 * Return a boolean indicating whether the type is DECIMAL.
	 *
	 * @return A boolean.
	 */
	public boolean isDecimal() {
		return type.isDecimal();
	}
	/**
	 * Return a boolean indicating whether the type is DOUBLE.
	 *
	 * @return A boolean.
	 */
	public boolean isDouble() {
		return type.isDouble();
	}
	/**
	 * Return a boolean indicating whether the type is INTEGER.
	 *
	 * @return A boolean.
	 */
	public boolean isInteger() {
		return type.isInteger();
	}
	/**
	 * Return a boolean indicating whether the type is LONG.
	 *
	 * @return A boolean.
	 */
	public boolean isLong() {
		return type.isLong();
	}
	/**
	 * Return a boolean indicating whether the type is a number.
	 *
	 * @return A boolean.
	 */
	public boolean isNumber() {
		return type.isNumber();
	}
	/**
	 * Return a boolean indicating whether the type is STRING.
	 *
	 * @return A boolean.
	 */
	public boolean isString() {
		return type.isString();
	}
	/**
	 * Return a boolean indicating whether the type is BINARY.
	 *
	 * @return A boolean.
	 */
	public boolean isBinary() {
		return type.isBinary();
	}
	/**
	 * Return a boolean indicating whether the type is DATE.
	 *
	 * @return A boolean.
	 */
	public boolean isDate() {
		return type.isDate();
	}
	/**
	 * Return a boolean indicating whether the type is TIME.
	 *
	 * @return A boolean.
	 */
	public boolean isTime() {
		return type.isTime();
	}
	/**
	 * Return a boolean indicating whether the type is TIMESTAMP.
	 *
	 * @return A boolean.
	 */
	public boolean isTimestamp() {
		return type.isTimestamp();
	}
	/**
	 * Return a boolean indicating whether the type an ARRAY.
	 *
	 * @return A boolean.
	 */
	public boolean isArray() {
		return type.isArray();
	}
	/**
	 * Return a boolean indicating whether the type is OBJECT.
	 *
	 * @return A boolean.
	 */
	public boolean isObject() {
		return type.isObject();
	}

	/**
	 * Check for equality.
	 *
	 * @param o The object to check.
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Field f) {
			boolean eq = Objects.equals(getTable(), f.getTable());
			eq &= Objects.equals(getName(), f.getName());
			eq &= Objects.equals(getAlias(), f.getAlias());
			eq &= Objects.equals(getType(), f.getType());
			eq &= Objects.equals(getLength(), f.getLength());
			eq &= Objects.equals(getDecimals(), f.getDecimals());
			eq &= Objects.equals(getFunction(), f.getFunction());
			return eq;
		}
		return false;
	}
	/**
	 * Return a suitable hash code.
	 *
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		Object[] objs = new Object[] {
				getName(), getAlias(), getType(), getLength(), getDecimals(), getFunction(),
				getTable()
		};
		return Objects.hash(objs);
	}
	/**
	 * Return a string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Field: ");
		b.append(getName());
		b.append(", ");
		b.append(getType());
		b.append(", ");
		b.append(getLength());
		b.append(", ");
		b.append(getDecimals());
		return b.toString();
	}
}
