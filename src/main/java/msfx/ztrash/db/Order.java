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
		 * @param asc      The ascending boolean flag.
		 */
		public Segment(Field fieldRef, boolean asc) {
			this.fieldRef = fieldRef;
			this.asc = asc;
		}

		/**
		 * Get the field reference.
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
