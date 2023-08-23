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
package msfx.lib.util.iterators;

import java.util.Iterator;
import java.util.function.Function;

/**
 * An iterator that applies a function to each data element.
 * @author Miquel Sas
 */
public class FunctionIterator<T, R> implements Iterator<R> {
	
	/** Source iterator. */
	private final Iterator<T> iterator;
	/** Function to apply. */
	private final Function<T, R> function;
	
	/**
	 * Constructor.
	 * @param iterator Data iterator.
	 * @param function Function to apply to data elements.
	 */
	public FunctionIterator(Iterator<T> iterator, Function<T, R> function) {
		if (iterator == null) throw new NullPointerException();
		if (function == null) throw new NullPointerException();
		this.iterator = iterator;
		this.function = function;
	}
	/**
	 * Return a boolean indicating whether there are more elements to retrieve.
	 */
	@Override
	public boolean hasNext() { return iterator.hasNext(); }
	/**
	 * Returns the next element.
	 */
	@Override
	public R next() { return function.apply(iterator.next()); }
}
