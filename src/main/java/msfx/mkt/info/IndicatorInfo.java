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

import msfx.mkt.Instrument;
import msfx.mkt.Period;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndicatorInfo extends DataInfo {

	/**
	 * List of parameters.
	 */
	private List<Parameter> parameters = new ArrayList<>();

	/**
	 * Default constructor
	 */
	public IndicatorInfo() { }

	/**
	 * Add a parameters.
	 *
	 * @param parameter The parameter to add.
	 */
	public void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}
	/**
	 * Return the parameter with the given name.
	 *
	 * @param id The name of the parameter.
	 * @return The parameter with the given name.
	 */
	public Parameter getParameter(String id) {
		for (Parameter parameter : parameters) {
			if (parameter.getId().equals(id)) {
				return parameter;
			}
		}
		throw new IllegalArgumentException("Invalid parameter name: " + id);
	}
	/**
	 * Returns the list of parameters.
	 *
	 * @return The list of parameters.
	 */
	public List<Parameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
}
