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

import msfx.lib.util.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * An order key.
 *
 * @author Miquel Sas
 */
public class OrderKey implements Comparable<Object> {

	/**
	 * Singleton segment comparator.
	 */
	public static final SegmentComparator SEGMENT_COMPARATOR = new SegmentComparator();

	/**
	 * Segment comparator.
	 */
	public static class SegmentComparator implements Comparator<Segment> {
		/**
		 * Compare for order.
		 *
		 * @param s1 the first segment to be compared.
		 * @param s2 the second segment to be compared.
		 * @return The comparison integer.
		 */
		@Override
		public int compare(Segment s1, Segment s2) {
			int compare = s1.value.compareTo(s2.value);
			if (compare != 0) {
				return compare * (s1.asc ? 1 : -1);
			}
			return 0;
		}
	}

	/**
	 * An order key segment is a small structure to pack segment (value,asc/desc) information.
	 */
	public static class Segment implements Comparable<Object> {

		/**
		 * The value.
		 */
		private Value value;
		/**
		 * The ascending flag.
		 */
		private boolean asc;

		/**
		 * Constructor assigning value and ascending flag.
		 *
		 * @param value The value.
		 * @param asc   The ascending flag.
		 */
		public Segment(Value value, boolean asc) {
			if (value == null) {
				throw new NullPointerException("Value can not be null");
			}
			this.value = value;
			this.asc = asc;
		}

		/**
		 * Compare for order.
		 *
		 * @param obj the object to be compared.
		 * @return The comparator integer.
		 */
		@Override
		public int compareTo(Object obj) {
			if (obj instanceof Segment seg) {
				return SEGMENT_COMPARATOR.compare(this, seg);
			}
			throw new IllegalArgumentException("Not comparable type: " + obj);
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
				return compareTo(obj) == 0;
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
			return Objects.hash(value, asc);
		}

		/**
		 * Get the value.
		 *
		 * @return The value.
		 */
		public Value getValue() {
			return value;
		}
		/**
		 * Check the ascending flag.
		 *
		 * @return A boolean
		 */
		public boolean isAsc() {
			return asc;
		}

		/**
		 * Set the value.
		 *
		 * @param value The value.
		 */
		public void setValue(Value value) {
			if (value == null) {
				throw new NullPointerException("Value can not be null");
			}
			this.value = value;
		}
		/**
		 * Set the ascending flag.
		 *
		 * @param asc The ascending flag.
		 */
		public void setAsc(boolean asc) {
			this.asc = asc;
		}

		/**
		 * Return a string representation.
		 *
		 * @return A string representation.
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder(128);
			b.append(value);
			b.append(", ");
			b.append(isAsc() ? "ASC" : "DESC");
			return b.toString();
		}
	}

	/**
	 * List of segments.
	 */
	private final List<Segment> segments = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public OrderKey() { }

	/**
	 * Add a value to the key.
	 *
	 * @param value The value.
	 */
	public void add(Value value) {
		add(value, true);
	}
	/**
	 * Add a value segment to the segment list.
	 *
	 * @param value The value of the segment.
	 * @param asc   The ascending/descending flag
	 */
	public final void add(Value value, boolean asc) {
		segments.add(new Segment(value, asc));
	}

	/**
	 * Return the segment at the given index.
	 *
	 * @param index The index.
	 * @return The segment.
	 */
	public Segment get(int index) {
		return segments.get(index);
	}
	/**
	 * Return the value at the position.
	 *
	 * @param index The index position.
	 * @return The value.
	 */
	public Value getValue(int index) {
		return get(index).getValue();
	}

	/**
	 * Return the size or number of segments.
	 *
	 * @return The number of segments.
	 */
	public int size() {
		return segments.size();
	}

	/**
	 * Compare for order.
	 *
	 * @param obj the object to be compared.
	 * @return The comparison integer.
	 */
	@Override
	public int compareTo(Object obj) {
		if (obj instanceof OrderKey key) {
			return Lists.compare(segments, key.segments, SEGMENT_COMPARATOR);
		}
		throw new IllegalArgumentException();
	}
	/**
	 * Check for equality.
	 *
	 * @param obj The object to check.
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OrderKey key) {
			return Lists.equals(segments, key.segments);
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

	/**
	 * Return a string representation.
	 *
	 * @return The string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(256);
		for (int i = 0; i < size(); i++) {
			b.append(get(i).toString());
			if (i < size() - 1) {
				b.append("; ");
			}
		}
		return b.toString();
	}
}
