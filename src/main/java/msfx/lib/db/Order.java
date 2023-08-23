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
package msfx.lib.db;

import msfx.lib.util.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An order definition.
 *
 * @author Miquel Sas
 */
public class Order {

	/**
	 * An order segment.
	 */
	public static class Segment {

		/**
		 * The field reference.
		 */
		private final Field fieldRef;
		/**
		 * The ascending flag.
		 */
		private final boolean asc;

		/**
		 * Constructor.
		 *
		 * @param fieldRef The field reference.
		 * @param asc      Ascending boolean flag.
		 */
		public Segment(Field fieldRef, boolean asc) {
			this.fieldRef = fieldRef;
			this.asc = asc;
		}

		/**
		 * Get the field reference..
		 *
		 * @return The field reference.
		 */
		public Field getField() {
			return fieldRef;
		}
		/**
		 * Check the ascending flag.
		 *
		 * @return A boolean that indicates whether the key is ascending or descending.
		 */
		public boolean isAscending() {
			return asc;
		}

		/**
		 * Check for equality.
		 *
		 * @param obj The object to check.
		 * @return A boolean.
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Segment seg) {
				return fieldRef.equals(seg.fieldRef) && asc == seg.asc;
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
			return Objects.hash(fieldRef, asc);
		}
	}

	/** List of segments or field keys. */
	private final List<Segment> segments = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public Order() { }

	/**
	 * Add an ascending field to the list of field keys.
	 *
	 * @param field The field reference to add.
	 */
	public void addField(Field field) {
		addField(field, true);
	}
	/**
	 * Add a field to the list of field keys.
	 *
	 * @param field  The field to add.
	 * @param ascending The ascending indicator.
	 */
	public void addField(Field field, boolean ascending) {
		segments.add(new Segment(field, ascending));
	}
	/**
	 * Return the list of segments.
	 *
	 * @return The segments.
	 */
	public List<Segment> getSegments() {
		return segments;
	}

	/**
	 * Check for equality.
	 *
	 * @param obj The object to check.
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Order o) {
			return Lists.equals(segments, o.segments);
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
		return Objects.hash(segments.toArray());
	}
}
