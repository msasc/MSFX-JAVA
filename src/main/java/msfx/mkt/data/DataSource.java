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

package msfx.mkt.data;

import msfx.mkt.data.info.DataInfo;

import java.util.UUID;

/**
 * A source of timed data.
 *
 * @author Miquel Sas
 */
public abstract class DataSource {

	/**
	 * ID created at construction time to uniquely identify this data source.
	 */
	private final String id = UUID.randomUUID().toString();
	/**
	 * Information about the data elements of the source.
	 */
	private final DataInfo dataInfo;

	/**
	 * Constructor.
	 *
	 * @param dataInfo Information about the data elements of the source.
	 */
	public DataSource(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}

	/**
	 * Return the unique identifier.
	 *
	 * @return The ID.
	 */
	public String getID() {
		return id;
	}

	/**
	 * Return the data element at the given index.
	 *
	 * @param index The index.
	 * @return The data element.
	 */
	public abstract Data getData(int index);
	/**
	 * Returns the data info.
	 *
	 * @return The data info.
	 */
	public DataInfo getInfo() {
		return dataInfo;
	}
	/**
	 * Return the size. May increase with time.
	 *
	 * @return The size.
	 */
	public abstract int size();
	/**
	 * Check whether the list is empty.
	 *
	 * @return A boolean.
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
}
