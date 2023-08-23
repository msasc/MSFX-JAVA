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

package msfx.lib.util.iterators;

import java.util.Iterator;

/**
 * Iterator on an array.
 * @author Miquel Sas
 */
public class ArrayIterator<E> implements Iterator<E> {

	/** Underlying array. */
	private final E[] data;
	/** Index. */
	private int index;

	/**
	 * Constructor.
	 * @param data Data array.
	 */
	@SuppressWarnings("unchecked")
	public ArrayIterator(E... data) {
		if (data == null) throw new NullPointerException();
		this.data = data;
		this.index = 0;
	}
	/**
	 * Return a boolean indicating whether there are more elements to retrieve.
	 */
	@Override
	public boolean hasNext() { return index < data.length; }
	/**
	 * Returns the next element.
	 */
	@Override
	public E next() { return data[index++]; }
}
