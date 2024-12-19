/*
 * Copyright (c) 2024 Miquel Sas.
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

import java.util.Comparator;

/**
 * A comparator useful to compare records and sort them within a record set or list.
 *
 * @author Miquel Sas
 */
public class RecordCmp implements Comparator<Record> {

	/**
	 * The order.
	 */
	private final Order order;

	/**
	 * Constructor using an order to get the key pointers.
	 *
	 * @param order The order.
	 */
	public RecordCmp(Order order) {
		super();
		this.order = order;
	}

	@Override
	public int compare(Record r1, Record r2) {
		OrderKey k1 = r1.getOrderKey(order);
		OrderKey k2 = r2.getOrderKey(order);
		return k1.compareTo(k2);
	}

	/**
	 * Returns the corresponding order.
	 *
	 * @param record The base record.
	 * @return An order that corresponds to the key pointers.
	 */
	public Order getOrder(Record record) {
		return order;
	}
}
