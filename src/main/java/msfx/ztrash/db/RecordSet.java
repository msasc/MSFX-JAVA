/*
 * Copyright (c) 2023-2024 Miquel Sas.
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

import java.util.Iterator;

/**
 * A record set is the base abstract class aimed to access a huge list of records by index, or
 * iterating through them. It also contains the list of field references.
 *
 * @author Miquel Sas
 */
public abstract class RecordSet implements Iterable<Record> {

	/**
	 * The list of fields.
	 */
	private FieldList fields;
	/**
	 * Index of the row number column if present.
	 */
	private Integer rowNumberIndex;

	/**
	 * Default constructor.
	 */
	public RecordSet() { }
	/**
	 * Constructor assigning the list of fields.
	 *
	 * @param fields The list of fields.
	 */
	public RecordSet(FieldList fields) {
		setFieldList(fields);
	}

	/**
	 * Sets the field list.
	 *
	 * @param fields The field list.
	 */
	public final void setFieldList(FieldList fields) {
		this.fields = fields;
		if (fields.containsAlias("ROW_NUMBER")) {
			rowNumberIndex = fields.indexOf("ROW_NUMBER");
		}
	}
	/**
	 * Returns the fields, for use in the friend class Cursor.
	 *
	 * @return The field list.
	 */
	public FieldList getFieldList() {
		return fields;
	}

	/**
	 * Get the field reference at the given index.
	 *
	 * @param index The index of the field.
	 * @return The field reference.
	 */
	public Field getFieldRef(int index) {
		return fields.getField(index);
	}
	/**
	 * Get a field reference by alias.
	 *
	 * @param alias The field alias.
	 * @return The field reference or null if not found.
	 */
	public Field getFieldRef(String alias) {
		return fields.getField(alias);
	}
	/**
	 * Returns the number of fields.
	 *
	 * @return The number of fields.
	 */
	public int getFieldCount() {
		return fields.size();
	}

	/**
	 * Get a record given its index in the record list.
	 *
	 * @param index The index in the record list.
	 * @return The Record.
	 */
	public Record getRecord(long index) {
		Record rc = getRecordFromSource(index);
		if (rc != null && rowNumberIndex != null) {
			rc.setValue(rowNumberIndex, new Value(index + 1));
		}
		return rc;
	}
	/**
	 * Get a record from the source given its index in the record list.
	 *
	 * @param index The index in the record list.
	 * @return The Record.
	 */
	protected abstract Record getRecordFromSource(long index);
	/**
	 * Returns this record set size.
	 *
	 * @return The size.
	 */
	public abstract long getRecordCount();

	/**
	 * Check whether the list of records is empty.
	 *
	 * @return If the record set is empty.
	 */
	public boolean isEmpty() {
		return getRecordCount() == 0;
	}

	/**
	 * Return a forward iterator along the list of records.
	 *
	 * @return The iterator.
	 */
	@Override
	public abstract Iterator<Record> iterator();

	/**
	 * Return a string representation.
	 *
	 * @return The string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < Math.min(getRecordCount(), 10); i++) {
			if (i > 0) b.append("\n");
			b.append(getRecord(i));
		}
		return b.toString();
	}

}
