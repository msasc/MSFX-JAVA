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

import msfx.lib.util.funtion.Function;
import msfx.mkt.Data;

/**
 * Information about data output values.
 *
 * @author Miquel Sas
 */
public class OutputInfo {

	/**
	 * ID or short name.
	 */
	private final String id;
	/**
	 * The name of the output.
	 */
	private final String name;
	/**
	 * Optional title.
	 */
	private final String title;
	/**
	 * The index of this output in the output data object.
	 */
	private final int index;
	/**
	 * Optional function when the output is a calculated value on the data object.
	 */
	private final Function.P1<Double, Data> function;

	/**
	 * Short constructor with an index.
	 *
	 * @param id    ID.
	 * @param name  Name.
	 * @param index Index.
	 */
	public OutputInfo(String id, String name, int index) {
		this(id, name, name, index);
	}
	/**
	 * Constructor with an index.
	 *
	 * @param id    ID.
	 * @param name  Name.
	 * @param title Title.
	 * @param index Index.
	 */
	public OutputInfo(String id, String name, String title, int index) {
		this.id = id;
		this.name = name;
		this.index = index;
		this.title = title;
		this.function = null;
	}
	/**
	 * Short constructor with a function.
	 *
	 * @param id       ID.
	 * @param name     Name.
	 * @param function Function.
	 */
	public OutputInfo(String id, String name, Function.P1<Double, Data> function) {
		this(id, name, name, function);
	}
	/**
	 * Constructor with a function.
	 *
	 * @param id       ID.
	 * @param name     Name.
	 * @param title    Title.
	 * @param function Function.
	 */
	public OutputInfo(String id, String name, String title, Function.P1<Double, Data> function) {
		this.id = id;
		this.name = name;
		this.index = -1;
		this.title = title;
		this.function = function;
	}

	/**
	 * Returns the output name, for instance <b>Close</b> for the close value of an {@link Data}
	 * instance.
	 *
	 * @return The output name.
	 */
	public String getId() {
		return id;
	}
	/**
	 * Returns the short name.
	 *
	 * @return The short name.
	 */
	public String getName() {
		return name;
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
	 * Returns the function to calculate the output based on the data object.
	 *
	 * @return The function.
	 */
	public Function.P1<Double, Data> getFunction() {
		return function;
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
		b.append(getId());
		b.append(", ");
		b.append(getIndex());
		b.append("]");
		return b.toString();
	}
}
