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
	 * The name of the output.
	 */
	private String name;
	/**
	 * Optional title.
	 */
	private String title;
	/**
	 * The index of this output in the output data object.
	 */
	private int index;
	/**
	 * Optional function when the output is a calculated value on the data object.
	 */
	private Function.P1<Double, Data> function;

	/**
	 * Default constructor.
	 */
	public OutputInfo() { }
	/**
	 * Constructor with an index.
	 *
	 * @param name  Name.
	 * @param title Title.
	 * @param index Index.
	 */
	public OutputInfo(String name, String title, int index) {
		this.name = name;
		this.index = index;
		this.title = title;
		this.function = null;
	}
	/**
	 * Constructor with a function.
	 *
	 * @param name     Name.
	 * @param title    Title.
	 * @param function Function.
	 */
	public OutputInfo(String name, String title, Function.P1<Double, Data> function) {
		this.name = name;
		this.index = -1;
		this.title = title;
		this.function = function;
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
	 * Set the name.
	 *
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Set the title.
	 *
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * Set the index of the output.
	 *
	 * @param index The index.
	 */
	public void setIndex(int index) {
		this.index = index;
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
	 * Set the function to calculate the output.
	 *
	 * @param function The function.
	 */
	public void setFunction(Function.P1<Double, Data> function) {
		this.function = function;
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
