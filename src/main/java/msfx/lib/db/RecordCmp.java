/*
 * Copyright (C) 2018 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package msfx.lib.db;

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
