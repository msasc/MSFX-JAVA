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

package msfx.mkt;

import msfx.mkt.chart.PlotData;
import msfx.mkt.info.DataInfo;
import msfx.mkt.info.IndicatorInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A data source that depends on other data sources ad thus must be calculated.
 */
public abstract class IndicatorSource extends DataSource {

	/**
	 * List of required data sources.
	 */
	private List<DataSource> requiredSources = new ArrayList<>();
	/**
	 * Internal data source.
	 */
	protected List<Data> dataList = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public IndicatorSource() {
		super(new IndicatorInfo());
	}

	/**
	 * Add a required source.
	 *
	 * @param source The required data source.
	 */
	public void addRequiredSource(DataSource source) {
		requiredSources.add(source);
	}

	/**
	 * Return this indicator info.
	 *
	 * @return The indicator indo.
	 */
	public IndicatorInfo getInfo() {
		return (IndicatorInfo) super.getInfo();
	}
	/**
	 * Return the list of required sources.
	 *
	 * @return The list of required sources.
	 */
	public List<DataSource> getRequiredSources() {
		return Collections.unmodifiableList(requiredSources);
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

	/**
	 * Calculate the indicator source from scratch for the data sources.
	 *
	 * @param plotData Plot data.
	 */
	public void calculate(PlotData plotData) {
		int dataSize = plotData.getDataSize();
		calculate(plotData, 0, dataSize - 1);
	}
	/**
	 * Recalculate the indicator source for the given indexes.
	 *
	 * @param plotData   Plot data.
	 * @param startIndex Start index.
	 * @param endIndex   End index.
	 */
	public abstract void calculate(PlotData plotData, int startIndex, int endIndex);
}
