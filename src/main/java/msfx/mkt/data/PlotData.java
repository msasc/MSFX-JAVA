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
 * but not all the sources need to have the same number of items and perhaps not continuous.
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
	private List<DataSource> dataSources = new ArrayList<>();
	/**
	 * List of effective times including all times al oll data sources.
	 */
	private List<Integer> dataTimes = new ArrayList<>();
	/**
	 * List of global indexes per data source, -1 for out of range indexes.
	 */
	private List<List<Integer>> dataIndexes = new ArrayList<>();

	/**
	 * Adds the data source, validates the period and rebuilds the list of data times and data
	 * indexes per data source.
	 *
	 * @param dataSource The data source.
	 */
	public void addDataSource(DataSource dataSource) {
		if (!dataSources.isEmpty()) {
			Period period = dataSources.get(0).getInfo().getPeriod();
			if (period.equals(dataSource.getInfo().getPeriod())) {
				throw new IllegalArgumentException("Data source period invalid");
			}
		}
		dataSources.add(dataSource);
		rebuildIndexes();
	}

	/**
	 * Rebuild data times and indexes.
	 */
	private void rebuildIndexes() {

		dataTimes.clear();
		dataIndexes.clear();

		for (int i = 0; i < dataSources.size(); i++) {
			dataIndexes.add(new ArrayList<>());
		}

		while (true) {
			int time = Numbers.MAX_INTEGER;
			for (int i = 0; i < dataSources.size(); i++) {
				DataSource source = dataSources.get(i);
				List<Integer> indexes = dataIndexes.get(i);
				int index = getNextSourceIndex(source, indexes);
				if (index >= 0) {
					int timeSrc = source.getData(index).getTime();
					if (timeSrc < time) {
						time = timeSrc;
					}
				}
			}

			break;
		}

	}

	private int getNextSourceIndex(DataSource source, List<Integer> indexes) {
		if (!source.isEmpty()) {
			if (indexes.isEmpty()) {
				return 0;
			} else {
				int lastIndex = indexes.get(indexes.size() - 1);
				if (lastIndex == -1) {
					return 0;
				} else {
					if (lastIndex >= source.size() - 1) {
						return -1;
					} else {
						return lastIndex + 1;
					}
				}
			}
		} else {
			return -1;
		}
	}
}
