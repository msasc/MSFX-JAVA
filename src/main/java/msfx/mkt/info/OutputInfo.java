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

package msfx.mkt.info;

import msfx.mkt.Data;

/**
 * Information about data output values.
 *
 * @author Miquel Sas
 */
public class OutputInfo {

	/**
	 * The output name, for instance <b>Close</b> for the close value of an
	 * {@link Data} instance.
	 */
	private final String name;
	/**
	 * A short name to build a short information string, like for instance <b>C</b> for the
	 * <b>Close</b> value.
	 */
	private final String shortName;
	/**
	 * Title or description.
	 */
	private final String title;
	/**
	 * The index of this output in the data object.
	 */
	private final int index;

	/**
	 * Constructor.
	 *
	 * @param name      The output name, for instance <b>Close</b> for the close value of an
	 *                  {@link Data} instance, not null.
	 * @param shortName A short name to build a short information string, like for instance <b>C</b>
	 *                  for the <b>Close</b> value, not null.
	 * @param title     Title or description, not null.
	 * @param index     The index of this output in the {@link Data} object, GE zero.
	 */
	public OutputInfo(String name, String shortName, String title, int index) {
		this.name = name;
		this.shortName = shortName;
		this.title = title;
		this.index = index;
	}

	/**
	 * Returns the output name, for instance <b>Close</b> for the close value of an {@link Data}
	 * instance.
	 *
	 * @return The output name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Returns the short name.
	 *
	 * @return The short name.
	 */
	public String getShortName() {
		return shortName;
	}
	/**
	 * Returns the title or description.
	 *
	 * @return The title or description.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Returns the index of this output in the {@link Data} object, GE zero.
	 *
	 * @return The index in the {@link Data} object.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns a string representation of this output info.
	 *
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getName());
		b.append(", ");
		b.append(getIndex());
		b.append("]");
		return b.toString();
	}
}
