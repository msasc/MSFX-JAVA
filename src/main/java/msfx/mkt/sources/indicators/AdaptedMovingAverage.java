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
import msfx.mkt.DataSourceList;
import msfx.mkt.IndicatorSource;
import msfx.mkt.chart.PlotData;
import msfx.mkt.info.DataInfo;
import msfx.mkt.info.IndicatorInfo;

import java.util.ArrayList;
import java.util.List;

public class AdaptedMovingAverage extends IndicatorSource {

	public enum Type { SMA, EMA, WMA }

	private int averagePeriods = 20;
	private int smoothPeriods = 0;
	private int adapterPeriods = 0;
	private int finalSmoothPeriods = 0;

	private Type averageType = Type.SMA;

	private int sourceIndex = Data.CLOSE;

	/**
	 * Constructor.
	 *
	 * @param indicatorInfo Information about the indicator source.
	 */
	public AdaptedMovingAverage(IndicatorInfo indicatorInfo) {
		super(indicatorInfo);
	}

	/**
	 * Recalculate the indicator source for the given indexes.
	 *
	 * @param plotData   Plot data.
	 * @param startIndex Start index.
	 * @param endIndex   End index.
	 */
	public void calculate(PlotData plotData, int startIndex, int endIndex) {

		/* Check the required source. */
		if (getRequiredSources().size() != 1) {
			throw new IllegalStateException("Required source not properly set");
		}

		/* Required source and indexes. */
		DataSource reqSource = getRequiredSources().get(0);
		List<Integer> dataIndexes = plotData.getIndexes(reqSource);
		int dataSize = plotData.getDataSize();

		/* Iterate indexes. */
		for (int index = startIndex; index <= endIndex; index++) {

			/*
			 * Check whether the index is valid and get the required source data index.
			 */
			if (index < 0) continue;
			if (index >= dataSize) continue;
			int dataIndex = dataIndexes.get(index);
			if (dataIndex < 0) continue;

			/*
			 * Get the start indexes for average, smooth and adapter.
			 */
			int startAverage = dataIndex - averagePeriods;
			if (startAverage < 0) startAverage = 0;
			int startSmooth = dataIndex - smoothPeriods;
			if (startSmooth < 0) startSmooth = 0;
			int startAdapt = dataIndex - adapterPeriods;
			if (startAdapt < 0) startAdapt = 0;
			int startFinalSmooth = dataIndex - finalSmoothPeriods;
			if (startFinalSmooth < 0) startFinalSmooth = 0;

			/*
			 * The data element of this indicator has six values:
			 * 0 - The final calculated value that will be shown.
			 * 1 - The average calculated value.
			 * 2 - The average smoothed value.
			 * 3 - The average difference between the smooth value and the original value.
			 * 4 - Partial result.
			 * 5 - Final smooth.
			 */
			int time = reqSource.getData(dataIndex).getTime();
			double[] values = new double[6];
			Data data = new Data(time, values);
			if (dataIndex >= dataList.size()) {
				dataList.add(data);
			} else {
				dataList.set(dataIndex, data);
			}

			/*
			 * First, calculate the average.
			 */
			if (averageType == Type.SMA) {
				double periods = dataIndex - startAverage + 1;
				for (int i = startAverage; i <= dataIndex; i++) {
					Data source = reqSource.getData(i);
					values[1] += source.getValue(sourceIndex);
				}
				values[1] /= periods;
				values[0] = values[1];
				values[4] = values[0];
			}

			/*
			 * Second, calculate the smoothed value.
			 */
			if (averageType == Type.SMA && smoothPeriods > 0) {
				double periods = dataIndex - startSmooth + 1;
				for (int i = startSmooth; i <= dataIndex; i++) {
					Data average = dataList.get(i);
					values[2] += average.getValue(4);
				}
				values[2] /= periods;
				values[0] = values[2];
				values[4] = values[0];
			}

			/*
			 * Third, calculate the average difference between the source and the smoothed
			 * value to adapt the result.
			 */
			if (averageType == Type.SMA && adapterPeriods > 0) {
				double periods = dataIndex - startAdapt + 1;
				for (int i = startAdapt; i <= dataIndex; i++) {
					Data source = reqSource.getData(i);
					double srcValue = source.getValue(sourceIndex);
					Data average = dataList.get(i);
					double value = average.getValue(4);
					double adapt = srcValue - value;
					values[3] += adapt;
				}
				values[3] /= periods;
				values[0] += values[3];
				values[4] = values[0];
			}

			/*
			 * Fourth, final smooth.
			 */
			if (averageType == Type.SMA && finalSmoothPeriods > 0) {
				double periods = dataIndex - startFinalSmooth + 1;
				for (int i = startFinalSmooth; i <= dataIndex; i++) {
					Data average = dataList.get(i);
					values[5] += average.getValue(4);
				}
				values[5] /= periods;
				values[0] = values[5];
			}

		}

		int move = averagePeriods / 2;
		for (int i_scan = move; i_scan < dataList.size() - move; i_scan++) {
			int i_data = i_scan - move;
			Data data = dataList.get(i_data);
			Data scan = dataList.get(i_scan);
			data.setValue(0, scan.getValue(0));
		}
		for (int i = dataList.size() - move - 1; i < dataList.size(); i++) {
			dataList.get(i).setValid(false);
		}
	}
}
