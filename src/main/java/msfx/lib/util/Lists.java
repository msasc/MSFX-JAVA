/*
 * Copyright (c) 2023 Miquel Sas.
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

package msfx.lib.util;

import msfx.lib.util.iterators.ArrayIterator;

import java.util.*;

/**
 * Array and list utility functions.
 *
 * @author Miquel Sas
 */
public class Lists {
	/**
	 * Returns a list given the argument array.
	 *
	 * @param <T>   The type.
	 * @param array The array.
	 * @return The list.
	 */
	@SafeVarargs
	public static <T> List<T> asList(T... array) {
		if (array == null) {
			return new ArrayList<>();
		}
		List<T> list = new ArrayList<>(array.length);
		list.addAll(Arrays.asList(array));
		return list;
	}
	/**
	 * Compares two lists of the same size.
	 *
	 * @param <T>        The type to compare.
	 * @param list1      First list.
	 * @param list2      Second list.
	 * @param comparator The comparator.
	 * @return The comparison integer.
	 */
	public static <T> int compare(List<T> list1, List<T> list2, Comparator<T> comparator) {
		if (list1 == null || list2 == null || comparator == null) {
			throw new NullPointerException();
		}
		for (int i = 0; i < list1.size(); i++) {
			int compare = comparator.compare(list1.get(i), list2.get(i));
			if (compare != 0) return compare;
		}
		return 0;
	}
	/**
	 * Check whether two lists are equal.
	 *
	 * @param l1 List 1.
	 * @param l2 List 2.
	 * @return A boolean.
	 */
	public static boolean equals(List<?> l1, List<?> l2) {
		if (l1.size() != l2.size()) return false;
		for (int i = 0; i < l1.size(); i++) {
			if (l1.get(i) == null && l2.get(i) != null) return false;
			if (l1.get(i) != null && l2.get(i) == null) return false;
			if (l1.get(i) == null && l2.get(i) == null) continue;
			if (!l1.get(i).equals(l2.get(i))) return false;
		}
		return true;
	}

	/**
	 * Return the first element of a collection.
	 *
	 * @param <T>        The type of the collection elements.
	 * @param collection The collection.
	 * @return The first element.
	 */
	public static <T> T getFirst(Collection<T> collection) {
		Iterator<T> i = collection.iterator();
		if (i.hasNext()) return i.next();
		return null;
	}

	/**
	 * Returns the first element of a list.
	 *
	 * @param <T>  The type.
	 * @param list The list.
	 * @return The first element.
	 */
	public static <T> T getFirst(List<T> list) {
		if (list == null || list.isEmpty()) return null;
		return list.get(0);
	}

	/**
	 * /**
	 * Returns the last element of a list.
	 *
	 * @param <T>  The type.
	 * @param list The list.
	 * @return The last element.
	 */
	public static <T> T getLast(List<T> list) {
		if (list == null || list.isEmpty()) return null;
		return list.get(list.size() - 1);
	}

	/**
	 * Check in the list.
	 *
	 * @param <T>    The type to check.
	 * @param value  The value to check.
	 * @param values The list of values.
	 * @return A boolean.
	 */
	public static <T> boolean in(T value, Collection<T> values) {
		return values.stream().anyMatch((v) -> (v.equals(value)));
	}

	/**
	 * Check in the list.
	 *
	 * @param <T>    The type to check in.
	 * @param value  The value to check.
	 * @param values The list of values.
	 * @return A boolean.
	 */
	@SafeVarargs
	public static <T> boolean in(T value, T... values) {
		for (T v : values) {
			if (v.equals(value)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Remove the last element in the list.
	 *
	 * @param <T>  The type.
	 * @param list The list.
	 * @return The removed element.
	 */
	public static <T> T removeLast(List<T> list) {
		return list.remove(list.size() - 1);
	}

	/**
	 * Reverse the list.
	 *
	 * @param <T>  The type.
	 * @param list The list to reverse.
	 */
	public static <T> void reverse(List<T> list) {
		int size = list.size();
		int head;
		int tail;
		for (int i = 0; i < size; i++) {
			head = i;
			tail = size - head - 1;
			if (tail < head) break;
			T e = list.get(head);
			list.set(head, list.get(tail));
			list.set(tail, e);
		}
	}

	/**
	 * Return a comma separated list of the values converted to string.
	 *
	 * @param <T>    The type.
	 * @param values The list of values.
	 * @return The comma separated list.
	 */
	@SafeVarargs
	public static <T> String toString(T... values) {
		return toString(new ArrayIterator<>(values));
	}
	/**
	 * Return a comma separated list of the values converted to string.
	 *
	 * @param iterator The iterator.
	 * @return The comma separated list.
	 */
	public static <T> String toString(Iterator<T> iterator) {
		StringBuilder b = new StringBuilder();
		boolean comma = false;
		while (iterator.hasNext()) {
			if (comma) b.append(", ");
			b.append(iterator.next());
			comma = true;
		}
		return b.toString();
	}
	/**
	 * Concatenates the strings.
	 *
	 * @param stringArrays The strings to concatenate.
	 * @return The concatenated string.
	 */
	public static String[] concat(String[]... stringArrays) {
		int len = 0;
		for (String[] strings : stringArrays) {
			if (strings != null) {
				len += strings.length;
			}
		}
		String[] arr = new String[len];
		int index = 0;
		for (String[] strings : stringArrays) {
			if (strings != null) {
				for (String string : strings) {
					arr[index++] = string;
				}
			}
		}
		return arr;
	}
}
