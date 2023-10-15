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

import msfx.lib.res.StringRes;
import msfx.lib.util.Numbers;
import msfx.lib.util.funtion.Function;
import msfx.mkt.Data;
import msfx.mkt.DataSource;
import msfx.mkt.IndicatorSource;
import msfx.mkt.chart.PlotData;
import msfx.mkt.info.DataInfo;
import msfx.mkt.info.Parameter;

import java.util.List;
import java.util.Locale;

/**
 * Generic multi-moving-average indicator.
 *
 * @author Miquel Sas
 */
public class MovingAverage extends IndicatorSource {

	public static final String PARAM_SOURCE = "SOURCE";
	public static final String PARAM_PERIOD = "PERIOD";
	public static final String PARAM_TYPE = "TYPE";
	public static final String PARAM_SMOOTH = "SMOOTH";
	public static final String PARAM_FIT = "FIT";

	/**
	 * Function to retrieve the source value.
	 */
	private Function.P1<Double, Data> sourceValue;

	/*
	 * Temporary variables common to calculate the different averages.
	 */

	/**
	 * Number of average periods.
	 */
	private int averagePeriods;
	/**
	 * Number of smooth periods.
	 */
	private int smoothPeriods;
	/**
	 * Data source.
	 */
	private DataSource source;
	/**
	 * Data indexes of merged sources.
	 */
	private List<Integer> dataIndexes;
	/**
	 * Size or number of time periods.
	 */
	private int dataSize;
	/**
	 * Size or number of values of the data object.
	 */
	private int valuesSize;
	/**
	 * Calculation start index.
	 */
	private int startIndex;
	/**
	 * Calculation end index.
	 */
	private int endIndex;
	/**
	 * Start index to effectively calculate the average.
	 */
	private int start;
	/**
	 * End index to effectively calculate the average.
	 */
	private int end;
	/**
	 * Fit to minimize the quadratic error.
	 */
	private boolean fit = false;

	/**
	 * Constructor.
	 *
	 * @param source The data source to builg the indicator.
	 */
	public MovingAverage(DataSource source) {
		super();

		Locale loc = Locale.getDefault();

		DataInfo srcInfo = source.getInfo();
		getInfo().setPeriod(srcInfo.getPeriod());
		getInfo().setInstrument(srcInfo.getInstrument());
		getInfo().setPipScale(srcInfo.getPipScale());
		getInfo().setTickScale(srcInfo.getTickScale());

		Parameter parameter;

		// Source value.
		parameter = new Parameter();
		parameter.setId(PARAM_SOURCE);
		parameter.setName(StringRes.get("avgSourceName", "Source", loc));
		parameter.setTitle(StringRes.get("avgSourceTitle", "Source value", loc));
		parameter.setType(Parameter.STRING);
		parameter.setValueFunction((v) -> {
			if (v.equals("OPEN")) {
				sourceValue = (data) -> data.getValue(Data.OPEN);
			}
			if (v.equals("HIGH")) {
				sourceValue = (data) -> data.getValue(Data.HIGH);
			}
			if (v.equals("LOW")) {
				sourceValue = (data) -> data.getValue(Data.LOW);
			}
			if (v.equals("CLOSE")) {
				sourceValue = (data) -> data.getValue(Data.CLOSE);
			}
			if (v.equals("MEDIAN")) {
				sourceValue = (data) -> {
					double high = data.getValue(Data.HIGH);
					double low = data.getValue(Data.LOW);
					return (high + low) / 2;
				};
			}
		});
		parameter.setPossibleValues("OPEN", "HIGH", "LOW", "CLOSE", "MEDIAN");
		parameter.setValue("CLOSE");
		getInfo().addParameter(parameter);

		// Number of periods.
		parameter = new Parameter();
		parameter.setId(PARAM_PERIOD);
		parameter.setName(StringRes.get("avgPeriodName", "Period", loc));
		parameter.setTitle(StringRes.get("avgPeriodTitle", "Number of periods", loc));
		parameter.setType(Parameter.NUMBER);
		parameter.setDecimals(0);
		parameter.setValue(30);
		getInfo().addParameter(parameter);

		// Average type: SMA, EMA or WMA.
		parameter = new Parameter();
		parameter.setId(PARAM_TYPE);
		parameter.setName(StringRes.get("avgTypeName", "Type", loc));
		parameter.setTitle(StringRes.get("avgTypeTitle", "Average type", loc));
		parameter.setType(Parameter.STRING);
		parameter.setValueFunction((type) -> {
			getInfo().setName((String) type);
		});
		parameter.setPossibleValues("SMA", "EMA", "WMA");
		parameter.setValue("SMA");
		getInfo().addParameter(parameter);

		// Smooth.
		parameter = new Parameter();
		parameter.setId(PARAM_SMOOTH);
		parameter.setName(StringRes.get("avgSmoothName", "Smooth", loc));
		parameter.setTitle(StringRes.get("avgSmoothTitle", "Smooth periods", loc));
		parameter.setType(Parameter.NUMBER);
		parameter.setValue(10);
		getInfo().addParameter(parameter);

		// Fit by moving the result average to minimize the quadratic error.
		parameter = new Parameter();
		parameter.setId(PARAM_FIT);
		parameter.setName(StringRes.get("avgFitterName", "Fit", loc));
		parameter.setTitle(StringRes.get("avgFitterTitle", "Fit to minimum quadratic error", loc));
		parameter.setType(Parameter.BOOLEAN);
		parameter.setValue(false);
		getInfo().addParameter(parameter);

	}

	/**
	 * Add a required source.
	 *
	 * @param source The required data source.
	 */
	@Override
	public void addRequiredSource(DataSource source) {
		if (!getRequiredSources().isEmpty()) {
			throw new IllegalStateException("Source already set");
		}
		super.addRequiredSource(source);
	}

	/**
	 * Recalculate the indicator source for the given indexes.
	 *
	 * @param plotData   Plot data.
	 * @param startIndex Start index.
	 * @param endIndex   End index.
	 */
	public void calculate(PlotData plotData, int startIndex, int endIndex) {

		this.averagePeriods = getInfo().getParameter(PARAM_PERIOD).getNumber().intValue();
		this.smoothPeriods = getInfo().getParameter(PARAM_SMOOTH).getNumber().intValue();
		this.fit = getInfo().getParameter(PARAM_FIT).getBoolean();
		this.source = getRequiredSources().get(0);
		this.dataIndexes = plotData.getIndexes(source);
		this.dataSize = plotData.getDataSize();
		this.valuesSize = 2;
		if (this.smoothPeriods > 0) {
			this.valuesSize++;
		}
		if (this.fit) {
			this.valuesSize++;
		}
		this.startIndex = startIndex;
		this.endIndex = endIndex;

		if (getInfo().getParameter(PARAM_TYPE).getString().equals("SMA")) {
			calculateSMA();
		}
		if (getInfo().getParameter(PARAM_TYPE).getString().equals("EMA")) {
			calculateEMA();
		}
		if (getInfo().getParameter(PARAM_TYPE).getString().equals("WMA")) {
			calculateWMA();
		}
		if (getInfo().getParameter(PARAM_FIT).getBoolean()) {
			calculateFIT();
		}
	}
	/**
	 * Recalculate the SMA (Simple Moving Average) for the given indexes.
	 */
	private void calculateSMA() {
		for (int index = startIndex; index <= endIndex; index++) {
			if (!checkRange(index, averagePeriods)) continue;
			double periods = end - start + 1;
			double[] values = getValues(valuesSize, end, source);
			for (int i = start; i <= end; i++) {
				values[1] += sourceValue.call(source.getData(i));
			}
			values[1] /= periods;
			values[0] = values[1];
		}
		if (smoothPeriods > 0) {
			for (int index = startIndex; index <= endIndex; index++) {
				if (!checkRange(index, smoothPeriods)) continue;
				double periods = end - start + 1;
				double[] values = getValues(valuesSize, end, source);
				for (int i = start; i <= end; i++) {
					values[2] += dataList.get(i).getValue(1);
				}
				values[2] /= periods;
				values[0] = values[2];
			}
		}
	}
	/**
	 * Recalculate the EMA (Exponential Moving Average) for the given indexes.
	 */
	private void calculateEMA() {
		for (int index = startIndex; index <= endIndex; index++) {
			if (!checkRange(index, averagePeriods)) continue;
			double periods = end - start + 1;
			double alpha = 2.0 / (periods + 1);
			double[] values = getValues(valuesSize, end, source);
			for (int i = start; i <= end; i++) {
				Data lastData = dataList.get(i - 1 >= 0 ? i - 1 : i);
				double lastAverage = lastData.getValue(1);
				double nextValue = sourceValue.call(source.getData(i));
				double average = nextValue * alpha + (1 - alpha) * lastAverage;
				values[1] = average;
			}
			values[0] = values[1];
		}
		if (smoothPeriods > 0) {
			for (int index = startIndex; index <= endIndex; index++) {
				if (!checkRange(index, smoothPeriods)) continue;
				double periods = end - start + 1;
				double alpha = 2.0 / (periods + 1);
				double[] values = getValues(valuesSize, end, source);
				for (int i = start; i <= end; i++) {
					Data lastData = dataList.get(i - 1 >= 0 ? i - 1 : i);
					double lastAverage = lastData.getValue(2);
					double nextValue = dataList.get(i).getValue(1);
					double average = nextValue * alpha + (1 - alpha) * lastAverage;
					values[2] = average;
				}
				values[0] = values[2];
			}
		}
	}
	/**
	 * Recalculate the WMA (Weighted Moving Average) for the given indexes.
	 */
	private void calculateWMA() {
		for (int index = startIndex; index <= endIndex; index++) {
			if (!checkRange(index, averagePeriods)) continue;
			double periods = end - start + 1;
			double factor = Math.pow(periods * 10, 1 / periods);
			double[] values = getValues(valuesSize, end, source);
			double weight = 1;
			double totalWeight = 0;
			for (int i = start; i <= end; i++) {
				values[1] += (sourceValue.call(source.getData(i)) * weight);
				totalWeight += weight;
				weight += factor;
			}
			values[1] /= totalWeight;
			values[0] = values[1];
		}
		if (smoothPeriods > 0) {
			for (int index = startIndex; index <= endIndex; index++) {
				if (!checkRange(index, smoothPeriods)) continue;
				double periods = end - start + 1;
				double factor = Math.pow(periods * 10, 1 / periods);
				double[] values = getValues(valuesSize, end, source);
				double weight = 1;
				double totalWeight = 0;
				for (int i = start; i <= end; i++) {
					values[2] += (dataList.get(i).getValue(1) * weight);
					totalWeight += weight;
					weight += factor;
				}
				values[2] /= totalWeight;
				values[0] = values[2];
			}
		}
	}
	/**
	 * Perform a FIT by moving left/right and up/down the average to minimize the quadratic error.
	 */
	private void calculateFIT() {

		int start = startIndex + averagePeriods;
		int end = endIndex - averagePeriods;
		int indexAvg = valuesSize - 2;
		int indexFit = valuesSize - 1;

		double minError = Numbers.MAX_DOUBLE;
		int horzMove = 0; // Positive translates back.

		// Put the displaced average data in the fit index.
		double error = 0;
		for (int i = start; i <= end; i++) {
			double src = sourceValue.call(source.getData(i));
			double avg = dataList.get(i + horzMove).getValue(indexAvg);
			double dif = src - avg;
			error += (dif * dif);
		}

	}

	/**
	 * Check the range and set the effective start and end indexes to calculate the average.
	 *
	 * @param index   Current scan index.
	 * @param periods Periods to eval and calculate the start and end indexes.
	 * @return
	 */
	private boolean checkRange(int index, int periods) {
		if (index < 0 || index >= dataSize) return false;
		end = dataIndexes.get(index);
		if (end < 0) return false;
		start = end - periods + 1;
		if (start < 0) start = 0;
		return true;
	}

	/**
	 * Create the data element with the given size and either sets or appends it to the data source.
	 *
	 * @param size   Size of the data element.
	 * @param index  Index int the data source.
	 * @param source Reference data source.
	 * @return The array of values.
	 */
	private double[] getValues(int size, int index, DataSource source) {
		// Create if not exists.
		if (index >= dataList.size()) {
			int time = source.getData(index).getTime();
			double[] values = new double[size];
			Data data = new Data(time, values);
			dataList.add(data);
			return values;
		}
		// Return current element values.
		return dataList.get(index).getValues();
	}
}
