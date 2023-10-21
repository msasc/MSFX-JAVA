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

import msfx.lib.util.funtion.Function;
import msfx.mkt.Data;
import msfx.mkt.DataSource;
import msfx.mkt.IndicatorSource;
import msfx.mkt.chart.PlotData;
import msfx.mkt.info.IndicatorInfo;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SourceFitter extends IndicatorSource {

	/**
	 * Number of periods.
	 */
	private int periods = 10;
	/**
	 * Fitter.
	 */
	private String fitter = "GAUSSIAN";
	/**
	 * Function to retrieve the data.
	 */
	private Function.P1<Double, Data> fdata = (d) -> {
		double[] values = d.getValues();
		double high = values[Data.HIGH];
		double low = values[Data.LOW];
		return (high + low) / 2;
	};

	/**
	 * Constructor.
	 */
	public SourceFitter() {	}
	/**
	 * Recalculate the indicator source for the given indexes.
	 *
	 * @param plotData   Plot data.
	 * @param startIndex Start index.
	 * @param endIndex   End index.
	 */
	@Override
	public void calculate(PlotData plotData, int startIndex, int endIndex) {

		/* Check the required source. */
		if (getRequiredSources().size() != 1) {
			throw new IllegalStateException("Required source not properly set");
		}

		/* Required source and indexes. */
		DataSource reqSource = getRequiredSources().get(0);
		List<Integer> dataIndexes = plotData.getIndexes(reqSource);
		int dataSize = plotData.getDataSize();

		GaussianCurveFitter fitter = GaussianCurveFitter.create();
		WeightedObservedPoints points = new WeightedObservedPoints();

		/* Iterate indexes. */
		for (int index = startIndex; index <= endIndex; index++) {

			/* Check whether the index is valid and get the required source data index.	 */
			if (index < 0) continue;
			if (index >= dataSize) continue;
			int dataIndex = dataIndexes.get(index);
			if (dataIndex < 0) continue;

			/* Last data (dataIndex) to calculate. */
			int time = reqSource.getData(dataIndex).getTime();
			double[] values = new double[1];
			Data data = new Data(time, values);
			if (dataIndex >= dataList.size()) {
				dataList.add(data);
			} else {
				dataList.set(dataIndex, data);
			}

			/* Get the start indexes for average, smooth and adapter. */
			int startFit = dataIndex - periods;
			if (startFit < 0) startFit = 0;
			if (dataIndex - startFit + 1 < 3) continue;

			for (int i = startFit; i <= dataIndex; i++) {
				Data source = reqSource.getData(i);
				double value = fdata.call(source);
				points.add(i, value);
			}

			double[] coeff = fitter.fit(points.toList());
			Gaussian gaussian = new Gaussian(coeff[0], coeff[1], coeff[2]);

//			for (int i = startFit; i <= dataIndex; i++) {
//				Data d = dataList.get(i);
//				d.getValues()[0] = gaussian.value(i);
//			}
			data.getValues()[0] = gaussian.value(dataIndex);
		}
	}
}
