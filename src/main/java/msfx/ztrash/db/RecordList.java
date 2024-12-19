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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A {@link RecordSet} backed by an array list of records.
 *
 * @author Miquel Sas
 */
public class RecordList extends RecordSet {

	/**
	 * The list of records.
	 */
	private final List<Record> records = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public RecordList() { }
	/**
	 * Constructor assigning the list of fields.
	 *
	 * @param fields The list of fields.
	 */
	public RecordList(FieldList fields) {
		setFieldList(fields);
	}

	/**
	 * Clear the list of records.
	 */
	public void clear() { records.clear(); }

	/**
	 * Internally get a record given its index in the record list.
	 *
	 * @param index The index in the record list.
	 * @return The Record.
	 */
	protected Record getRecordFromSource(long index) {
		if (index < 0 || index >= getRecordCount()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return records.get((int) index);
	}
	/**
	 * Returns this record set size.
	 *
	 * @return The size.
	 */
	public long getRecordCount() { return records.size(); }

	/**
	 * @return If the record set is empty.
	 * @see ArrayList#isEmpty()
	 */
	public boolean isEmpty() {
		return records.isEmpty();
	}

	/**
	 * Add a record to the list.
	 *
	 * @param record The record to add
	 */
	public void add(Record record) {
		if (getFieldList() == null) {
			setFieldList(record.getFields());
		}
		records.add(record);
	}

	/**
	 * Gets the insert index using the order key.
	 *
	 * @param record The record.
	 * @return The insert index.
	 */
	public int getInsertIndex(Record record) {
		return getInsertIndex(record, getFieldList().getPrimaryOrder());
	}
	/**
	 * Gets the insert index using the order key.
	 *
	 * @param record The record.
	 * @param order  The order.
	 * @return The insert index.
	 */
	public int getInsertIndex(Record record, Order order) {
		OrderKey key = record.getOrderKey(order);
		int index;
		for (index = 0; index < records.size(); index++) {
			Record scanRecord = records.get(index);
			OrderKey scanKey = scanRecord.getOrderKey(order);
			if (key.compareTo(scanKey) <= 0) {
				break;
			}
		}
		return index;
	}

	/**
	 * Find the index of the given record.
	 *
	 * @param record The record to find its index.
	 * @return The index of the given record.
	 */
	public int indexOf(Record record) {
		return records.indexOf(record);
	}
	/**
	 * Find the index of the given key.
	 *
	 * @param key The key to find its index.
	 * @return The index of the record with the given key.
	 */
	public int indexOf(OrderKey key) {
		for (int i = 0; i < getRecordCount(); i++) {
			if (getRecord(i).getPrimaryKey().equals(key)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Check if the record set contains the record.
	 *
	 * @param record The record to check.
	 * @return A boolean.
	 */
	public boolean contains(Record record) {
		return indexOf(record) >= 0;
	}
	/**
	 * Check if the record set contains a record with the given primary key.
	 *
	 * @param key The key to look for.
	 * @return A boolean.
	 */
	public boolean contains(OrderKey key) {
		return indexOf(key) >= 0;
	}

	/**
	 * Remove a record given its index in the list.
	 *
	 * @param index The index in the list of records.
	 * @return The removed record.
	 */
	public Record remove(int index) {
		if (index < 0 || index >= getRecordCount()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		Record record = getRecord(index);
		records.remove(index);
		return record;
	}

	/**
	 * Sets a record given its index in the record list.
	 *
	 * @param index  The index in the record list.
	 * @param record The record.
	 */
	public void set(int index, Record record) {
		records.set(index, record);
	}

	/**
	 * Sort this list of records based on the order by key pointers, or in its
	 * default the primary key pointers.
	 */
	public void sort() {
		if (getRecordCount() == 0) return;
		sort(getFieldList().getPrimaryOrder());
	}
	/**
	 * @param order The <code>Order</code> to use in the sort.
	 */
	public void sort(Order order) {
		sort(new RecordCmp(order));
	}
	/**
	 * Sort this list of records based on a comparator.
	 *
	 * @param comparator The comparator.
	 */
	public void sort(Comparator<Record> comparator) {
		Record[] recordArray = toArray();
		Arrays.sort(recordArray, comparator);
		records.clear();
		records.addAll(Arrays.asList(recordArray));
	}

	/**
	 * Returns an array containing all the records.
	 *
	 * @return The array of records.
	 */
	public Record[] toArray() {
		return records.toArray(new Record[records.size()]);
	}

	/**
	 * Return a forward iterator along the list of records.
	 *
	 * @return The iterator.
	 */
	@Override
	public Iterator<Record> iterator() {
		return records.iterator();
	}
}
