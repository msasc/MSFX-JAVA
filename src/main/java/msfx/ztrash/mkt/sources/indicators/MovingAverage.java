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

package msfx.ztrash.mkt.sources.indicators;

import msfx.ztrash.res.StringRes;
import msfx.lib.util.Numbers;
import msfx.lib.util.funtion.Function;
import msfx.ztrash.mkt.Data;
import msfx.ztrash.mkt.DataSource;
import msfx.ztrash.mkt.IndicatorSource;
import msfx.ztrash.mkt.chart.PlotData;
import msfx.ztrash.mkt.info.DataInfo;
import msfx.ztrash.mkt.info.Parameter;

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
	 * Index within the data object of the result calculation.
	 */
	private int indexResult = -1;
	/**
	 * Index within the data object of the average calculation.
	 */
	private int indexAverage = -1;
	/**
	 * Index within the data object of the smooth calculation.
	 */
	private int indexSmooth = -1;
	/**
	 * Index within the data object of the fit calculation.
	 */
	private int indexFit = -1;

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

		/* Source value. */
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

		/* Number of periods. */
		parameter = new Parameter();
		parameter.setId(PARAM_PERIOD);
		parameter.setName(StringRes.get("avgPeriodName", "Period", loc));
		parameter.setTitle(StringRes.get("avgPeriodTitle", "Number of periods", loc));
		parameter.setType(Parameter.NUMBER);
		parameter.setDecimals(0);
		parameter.setValue(30);
		getInfo().addParameter(parameter);

		/* Average type: SMA, EMA or WMA. */
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

		/* Smooth. */
		parameter = new Parameter();
		parameter.setId(PARAM_SMOOTH);
		parameter.setName(StringRes.get("avgSmoothName", "Smooth", loc));
		parameter.setTitle(StringRes.get("avgSmoothTitle", "Smooth periods", loc));
		parameter.setType(Parameter.NUMBER);
		parameter.setValue(10);
		getInfo().addParameter(parameter);

		/* Fit by moving the result average to minimize the quadratic error. */
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

		int index = 0;
		this.indexResult = index++;
		this.indexAverage = index++;
		if (this.smoothPeriods > 0) {
			this.valuesSize++;
			this.indexSmooth = index++;
		}
		if (this.fit) {
			this.valuesSize++;
			this.indexFit = index++;
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
				values[indexAverage] += sourceValue.call(source.getData(i));
			}
			values[indexAverage] /= periods;
			values[indexResult] = values[indexAverage];
		}
		if (smoothPeriods > 0) {
			for (int index = startIndex; index <= endIndex; index++) {
				if (!checkRange(index, smoothPeriods)) continue;
				double periods = end - start + 1;
				double[] values = getValues(valuesSize, end, source);
				for (int i = start; i <= end; i++) {
					values[indexSmooth] += dataList.get(i).getValue(1);
				}
				values[indexSmooth] /= periods;
				values[indexResult] = values[indexSmooth];
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
				values[indexAverage] = average;
			}
			values[indexResult] = values[indexAverage];
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
					values[indexSmooth] = average;
				}
				values[indexResult] = values[indexSmooth];
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
				values[indexAverage] += (sourceValue.call(source.getData(i)) * weight);
				totalWeight += weight;
				weight += factor;
			}
			values[indexAverage] /= totalWeight;
			values[indexResult] = values[indexAverage];
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
					values[indexSmooth] += (dataList.get(i).getValue(1) * weight);
					totalWeight += weight;
					weight += factor;
				}
				values[indexSmooth] /= totalWeight;
				values[indexResult] = values[indexSmooth];
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

		/*
		 * FIT horizontally.
		 * A positive increase in the horizontal move means that the average result is moved back.
		 * Although we know that an average has to be moved back to decrease the quadratic error,
		 * we will start with a negative increase to verify that the system switches the move and
		 * finally reaches the minimum.
		 */
		double minError = Numbers.MAX_DOUBLE;
		int horzMin = -1;
		int horzMove = 0;
		int horzIncr = -1;
		boolean switched = false;

		while (true) {
			double error = 0;
			for (int i = start; i <= end; i++) {
				double src = sourceValue.call(source.getData(i));
				double avg = dataList.get(i + horzMove).getValue(indexAvg);
				double dif = src - avg;
				error += (dif * dif);
			}

			/*
			 * The error is LT the minimum error.
			 * - Register the new minimum error.
			 * - Register the horizontal move of the minimum.
			 * - Continue moving in the same direction.
			 */
			if (error <= minError) {
				minError = error;
				horzMin = horzMove;
				horzMove += horzIncr;
				continue;
			}

			/*
			 * The error is GT the minimum error.
			 * - If the movement has not been switched, switch it and continue
			 *   in the opposite direction.
			 * - If the movement has been switched, just restore the last horizontal
			 *   move that was minimum.
			 */
			if (error > minError) {
				if (!switched) {
					horzIncr *= (-1);
					horzMove += horzIncr;
					switched = true;
					continue;
				} else {
					horzMove = horzMin;
					break;
				}
			}
		}

		for (int i = startIndex; i < endIndex - horzMove; i++) {
			Data dataDst = dataList.get(i);
			Data dataSrc = dataList.get(i + horzMove);
			dataDst.setValue(indexFit, dataSrc.getValue(indexResult));
			dataDst.setValue(indexResult, dataDst.getValue(indexFit));
		}
		for (int i = endIndex - horzMove; i <= endIndex; i++) {
			dataList.get(i).setValid(false);
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
