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

package msfx.mkt.sources.indicators;

import msfx.mkt.Data;
import msfx.mkt.DataSource;
import msfx.mkt.IndicatorSource;
import msfx.mkt.chart.PlotData;
import msfx.mkt.info.DataInfo;
import msfx.mkt.info.IndicatorInfo;
import msfx.mkt.info.Parameter;

import java.util.List;

/**
 * Generic multi-moving-average indicator.
 *
 * @author Miquel Sas
 */
public class MovingAverage extends IndicatorSource {

	/**
	 * Average types.
	 */
	public enum Type { SMA, EMA, WMA }

	/**
	 * Average type.
	 */
	private Type averageType = Type.SMA;
	/**
	 * Average periods.
	 */
	private int averagePeriods = 50;
	/**
	 * Index in the source data.
	 */
	private int sourceIndex = Data.CLOSE;

	/**
	 * Constructor.
	 *
	 * @param source The data source to builg the indicator.
	 */
	public MovingAverage(DataSource source) {
		super();

		DataInfo srcInfo = source.getInfo();
		getInfo().setPeriod(srcInfo.getPeriod());
		getInfo().setInstrument(srcInfo.getInstrument());
		getInfo().setPipScale(srcInfo.getPipScale());
		getInfo().setTickScale(srcInfo.getTickScale());

		Parameter parameter;

		parameter = new Parameter();
		parameter.setName("Periods");
		parameter.setTitle("Number of periods");
		parameter.setType("NUMBER");
		parameter.setDecimals(0);
		parameter.setValueFunction((periods) -> averagePeriods = ((Number) periods).intValue());
		getInfo().addParameter(parameter);

		parameter = new Parameter();
		parameter.setName("Type");
		parameter.setTitle("Average type");
		parameter.setType("STRING");
		parameter.setPossibleValues("SMA", "EMA", "WMA", "HMA");
		parameter.setValue("SMA");
		getInfo().addParameter(parameter);





	}

	/**
	 * Recalculate the indicator source for the given indexes.
	 *
	 * @param plotData   Plot data.
	 * @param startIndex Start index.
	 * @param endIndex   End index.
	 */
	public void calculate(PlotData plotData, int startIndex, int endIndex) {

		// Check the required source.
		if (getRequiredSources().size() != 1) {
			throw new IllegalStateException("Required source not properly set");
		}

		// Required source and indexes.
		DataSource reqSource = getRequiredSources().get(0);
		List<Integer> dataIndexes = plotData.getIndexes(reqSource);
		int dataSize = plotData.getDataSize();

		// Iterate indexes.
		for (int index = startIndex; index <= endIndex; index++) {

			/*
			 * Check whether the index is valid.
			 * Get the required source data index.
			 * Get the start index for the average.
			 */
			if (index < 0) continue;
			if (index >= dataSize) continue;
			int dataIndex = dataIndexes.get(index);
			if (dataIndex < 0) continue;
			int startAverage = dataIndex - averagePeriods;
			if (startAverage < 0) startAverage = 0;

			/*
			 * Create thea timed data. Either set or add it.
			 */
			int time = reqSource.getData(dataIndex).getTime();
			double[] values = new double[1];
			Data data = new Data(time, values);
			if (dataIndex >= dataList.size()) {
				dataList.add(data);
			} else {
				dataList.set(dataIndex, data);
			}

			/*
			 * Calculate the average value.
			 */
			if (averageType == Type.SMA) {
				double periods = dataIndex - startAverage + 1;
				for (int i = startAverage; i <= dataIndex; i++) {
					Data source = reqSource.getData(i);
					values[0] += source.getValue(sourceIndex);
				}
				values[0] /= periods;
			}
			if (averageType == Type.WMA) {
				double weight = 1;
				double totalWeight = 0;
				for (int i = startAverage; i <= dataIndex; i++) {
					Data source = reqSource.getData(i);
					values[0] += (source.getValue(sourceIndex) * weight);
					totalWeight += weight;
					weight += 1;
				}
				values[0] /= totalWeight;
			}
		}
	}
}
