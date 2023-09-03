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

import msfx.lib.util.Numbers;

import java.util.ArrayList;
import java.util.List;

/**
 * Container of all information related to the data to plot.
 * <p>
 * A plot data pack can contain several data sources. All data sources must be of the same period,
 * but not all the sources need to have the same number of items and perhaps not continuous. Thus,
 * when data sources are added a merge must be performed to access them in a continuous timeline.
 * <p>
 * Additionally, contains a list of data plotters. A data plotter uses one or more data sources to
 * plot, and there may more than one plotter per data source.
 *
 * @author Miquel Sas
 */
public class PlotData {

	/**
	 * Plot scale.
	 */
	public enum Scale {
		LINEAR, LOGARITHMIC, PERCENTAGE;
	}

	/**
	 * Enumerates the types of plots, line, bar, candlestick, histogram, etc.
	 */
	public enum Type {
		LINE, BAR, CANDLESTICK, HISTOGRAM;
	}

	/**
	 * List of data sources.
	 */
	private final List<DataSource> dataSources = new ArrayList<>();
	/**
	 * List of effective times including all times al oll data sources.
	 */
	private final List<Integer> dataTimes = new ArrayList<>();
	/**
	 * List of global indexes per data source, -1 for out of range indexes.
	 */
	private final List<List<Integer>> dataIndexes = new ArrayList<>();

	/**
	 * List of plotters, normally one per data source, but can be any number of plotters and each
	 * plotter can use any number of data sources.
	 */
	private final List<DataPlotter> plotters = new ArrayList<>();

	/**
	 * Start plot index, can be negative.
	 */
	private int startIndex;
	/**
	 * End plot index, can be greater than the maximum data index.
	 */
	private int endIndex;
	/**
	 * Minimum value within the plotted range.
	 */
	private double minValue;
	/**
	 * Maximum value within the plotted range.
	 */
	private double maxValue;

	/**
	 * Scale shared by all data sources.
	 */
	private Scale scale = Scale.LOGARITHMIC;

	/**
	 * Adds the data source, validates the period and rebuilds the list of data times and data
	 * indexes per data source.
	 *
	 * @param dataSource The data source.
	 */
	public void addDataSource(DataSource dataSource) {
		if (!dataSources.isEmpty()) {
			Period period = dataSources.get(0).getInfo().getPeriod();
			if (!period.equals(dataSource.getInfo().getPeriod())) {
				throw new IllegalArgumentException("Data source period invalid");
			}
		}
		dataSources.add(dataSource);
		mergeDataSources();
	}

	/**
	 * Adds a data plotter to this plot data. All data sources used by the data plotter must be
	 * contained in this plot data in order to be properly merged along the timeline. Thus, when
	 * adding a plotter non-contained sources are added, and thus the plot data can be directly
	 * configured only adding plotters.
	 *
	 * @param plotter The {@link DataPlotter}.
	 */
	public void addDataPlotter(DataPlotter plotter) {

		/* Validate null plotter. */
		if (plotter == null) {
			throw new NullPointerException("Plotter ncan not be null");
		}

		/* Ensure that plotter data sources are contained. */
		List<DataSource> plotterSources = plotter.getDataSources();
		for (DataSource plotterSource : plotterSources) {
			boolean exists = false;
			for (DataSource dataSource : dataSources) {
				if (dataSource.getID().equals(plotterSource.getID())) {
					exists = true;
				}
			}
			if (!exists) {
				addDataSource(plotterSource);
			}
		}

		/* Add the plotter. */
		plotters.add(plotter);
	}

	/**
	 * Calculate the minimum and maximum values within the frame.
	 */
	private void calculateMinMaxValues() {

		int dataSize = dataTimes.size();
		maxValue = Numbers.MIN_DOUBLE;
		minValue = Numbers.MAX_DOUBLE;

		for (int index = startIndex; index < endIndex; index++) {
			if (index < 0 || index >= dataSize) {
				continue;
			}
			for (DataPlotter plotter : plotters) {
				double[] values = plotter.getValues(index);
				for (double value : values) {
					if (value > maxValue) {
						maxValue = value;
					}
					if (value < minValue) {
						minValue = value;
					}
				}
			}
		}
	}

	private void calculateStartEndIndexes(int visibleBars, int endIndex) {
		this.endIndex = endIndex;
		this.startIndex = endIndex - visibleBars +1;
	}

	/**
	 * Merge data sources rebuilding data times and indexes.
	 */
	private void mergeDataSources() {

		dataTimes.clear();
		dataIndexes.clear();

		/*
		 * Initialize data indexes per source.
		 */
		for (int i = 0; i < dataSources.size(); i++) {
			dataIndexes.add(new ArrayList<>());
		}

		/*
		 * Determine first and last times.
		 */
		int firstTime = Numbers.MAX_INTEGER;
		int lastTime = Numbers.MIN_INTEGER;
		for (int i = 0; i < dataSources.size(); i++) {
			DataSource source = dataSources.get(i);
			int time;
			time = source.getData(0).getTime();
			if (time < firstTime) firstTime = time;
			time = source.getData(source.size() - 1).getTime();
			if (time > lastTime) lastTime = time;
		}

		/*
		 * List with the maximum index scanned per data source, start with the first index.
		 */
		List<Integer> maxIndexes = new ArrayList<>();
		for (int i = 0; i < dataSources.size(); i++) {
			maxIndexes.add(0);
		}

		/*
		 * Iterate filling indexes per source until the last time is reached.
		 */
		while (true) {

			/*
			 * Find next time.
			 */
			int nextTime = Numbers.MAX_INTEGER;
			if (dataTimes.isEmpty()) {
				nextTime = firstTime;
			} else {
				for (int i = 0; i < dataSources.size(); i++) {
					DataSource source = dataSources.get(i);
					int maxIndex = maxIndexes.get(i);
					if (maxIndex < source.size()) {
						int time = source.getData(maxIndex).getTime();
						if (time < nextTime) {
							nextTime = time;
						}
					}
				}
			}
			dataTimes.add(nextTime);

			/*
			 * Fill next index per data source.
			 */
			for (int i = 0; i < dataSources.size(); i++) {
				DataSource source = dataSources.get(i);
				List<Integer> indexes = dataIndexes.get(i);
				int maxIndex = maxIndexes.get(i);
				if (maxIndex < source.size()) {
					int time = source.getData(maxIndex).getTime();
					if (time == nextTime) {
						indexes.add(maxIndex);
						maxIndexes.set(i, ++maxIndex);
					} else {
						indexes.add(-1);
					}
				} else {
					indexes.add(-1);
				}
			}

			/*
			 * Check exit loop.
			 */
			if (nextTime == lastTime) break;
		}

	}
}
