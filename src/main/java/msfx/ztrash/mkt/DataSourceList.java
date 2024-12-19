/*
 * Copyright (c) 2023-2024 Miquel Sas.
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

package msfx.ztrash.mkt;

import msfx.ztrash.mkt.info.DataInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A data source backed by an array list.
 *
 * @author Miquel Sas
 */
public class DataSourceList extends DataSource {

	/**
	 * The list.
	 */
	private final List<Data> dataList = new ArrayList<>();

	/**
	 * Constructor.
	 *
	 * @param dataInfo Information about the data elements of the source.
	 */
	public DataSourceList(DataInfo dataInfo) {
		super(dataInfo);
	}
	/**
	 * Add data to the list.
	 * @param data The data.
	 */
	public void addData(Data data) {
		dataList.add(data);
	}
	/**
	 * Return the data element at the given index.
	 *
	 * @param index The index.
	 * @return The data element.
	 */
	@Override
	public Data getData(int index) {
		return dataList.get(index);
	}
	/**
	 * Return the size. May increase with time.
	 *
	 * @return The size.
	 */
	@Override
	public int size() {
		return dataList.size();
	}
}
