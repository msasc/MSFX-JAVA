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

package msfx.mkt.chart;

import msfx.lib.util.Numbers;
import msfx.mkt.DataSource;
import msfx.mkt.Period;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Container of all information related to the data that is aligned to the same timeline.
 * <p>
 * A plot data pack can contain several data sources. All data sources must be of the same period,
 * but not all the sources need to have the same number of items and perhaps not continuous. Thus,
 * when data sources are added a merge must be performed to access them in a continuous timeline.
 *
 * @author Miquel Sas
 */
public class PlotData {

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
	 * Start plot index, can be negative.
	 */
	private int startIndex;
	/**
	 * End plot index, can be greater than the maximum data index.
	 */
	private int endIndex;

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
		if (!dataSources.contains(dataSource)) {
			dataSources.add(dataSource);
			mergeDataSources();
		}
	}
	/**
	 * Returns the start index.
	 *
	 * @return The start index.
	 */
	public int getStartIndex() {
		return startIndex;
	}
	/**
	 * Returns the end index.
	 *
	 * @return The end index.
	 */
	public int getEndIndex() {
		return endIndex;
	}
	/**
	 * Return the number of periods determined by start and end indexes.
	 *
	 * @return The number of periods.
	 */
	public int getPeriods() {
		return endIndex - startIndex + 1;
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

	/**
	 * Returns the data size or number of periods.
	 *
	 * @return The data size.
	 */
	public int getDataSize() {
		return dataTimes.size();
	}
	/**
	 * Returns the list of data times.
	 *
	 * @return The list of data times.
	 */
	public List<Integer> getDataTimes() {
		return Collections.unmodifiableList(dataTimes);
	}
	/**
	 * Returns the list of data sources.
	 *
	 * @return The list of data sources.
	 */
	public List<DataSource> getDataSources() {
		return Collections.unmodifiableList(dataSources);
	}

	public Period getPeriod() {
		if (dataSources.isEmpty()) {
			throw new IllegalStateException("No available data sources");
		}
		return dataSources.get(0).getInfo().getPeriod();
	}

	/**
	 * Returns the list of indexes of a data source. These indexes are aligned along the timeline
	 * and some can be negative and thus invalid.
	 *
	 * @param dataSource The data source to lookup the indexes for.
	 * @return The list of indexes.
	 */
	public List<Integer> getIndexes(DataSource dataSource) {
		for (int i = 0; i < dataSources.size(); i++) {
			if (dataSources.get(i).getID().equals(dataSource.getID())) {
				return dataIndexes.get(i);
			}
		}
		throw new IllegalArgumentException("Data source not contained in this plot data");
	}

	/**
	 * Returns the local date-time that corresponds to and index that, in turn, can be negative or
	 * GE the data size.
	 *
	 * @param index The index.
	 * @return The corresponding local date-time with if current offset.
	 */
	public LocalDateTime getTime(int index) {
		int dataSize = getDataSize();
		int dataSecond = 0;
		if (index >= 0 && index < dataSize) {
			dataSecond = getDataTimes().get(index);
		} else if (index < 0) {
			dataSecond = getDataTimes().get(0);
		} else if (index >= dataSize) {
			dataSecond = getDataTimes().get(dataSize - 1);
		}
		int offSet = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000;
		ZoneOffset zoneOffset = ZoneOffset.ofHours(offSet);
		LocalDateTime time = LocalDateTime.ofEpochSecond(dataSecond, 0, zoneOffset);
		if (index < 0) {
			time = getPeriod().add(time, index);
		}
		if (index >= dataSize) {
			time = getPeriod().add(time, index - dataSize + 1);
		}
		return time;
	}

	/**
	 * Set the range of start and end indexes to N periods that finish at the end of the available
	 * periods.
	 *
	 * @param periods The number of visible periods.
	 */
	public void setIndexesRangeFromEnd(int periods) {
		int size = getDataSize();
		endIndex = size - 1;
		startIndex = endIndex - periods + 1;
		if (startIndex < 0) {
			startIndex = 0;
		}
	}
	/**
	 * Set the range of start and end indexes to N periods that start at the beginning of the
	 * available periods.
	 *
	 * @param periods The number of visible periods.
	 */
	public void setIndexesRangeFromStart(int periods) {
		int size = getDataSize();
		startIndex = 0;
		endIndex = startIndex + periods - 1;
		if (endIndex >= size) {
			endIndex = size - 1;
		}
	}

	/**
	 * Scroll the data using the parameter factor. For instance, a factor of -0.01 scrolls a
	 * percentage of 1% to the origin of the timeline, while a factor of 0.05 scrolls a 5% to the
	 * end of the timeline.
	 *
	 * @param factor The unitary factor to scroll.
	 */
	public void scroll(double factor) {
		int periods = getPeriods();
		int toScroll = Numbers.getBigDecimal(periods * factor, 0).intValue();
		if (toScroll == 0 && factor < 0) toScroll = -1;
		if (toScroll == 0 && factor > 0) toScroll = 1;
		scroll(toScroll);
	}

	public void scroll(int toScroll) {
		int size = getDataSize();
		int periods = getPeriods();
		/* Scroll to the origin. */
		if (toScroll < 0) {
			startIndex += toScroll;
			endIndex = startIndex + periods - 1;
		}
		/* Scroll to the end. */
		if (toScroll > 0) {
			endIndex += toScroll;
			startIndex = endIndex - periods + 1;
		}
		if (startIndex >= size) startIndex = size - 1;
		if (endIndex < 0) endIndex = 0;
	}

	public void zoom(double factor) {
		int periods = getPeriods();
		int toZoom = Numbers.getBigDecimal(periods * factor / 2, 0).intValue();
		if (toZoom == 0 && factor > 0) toZoom = 1;
		zoom(toZoom);
	}

	public void zoom(int toZoom) {
		startIndex -= toZoom;
		endIndex += toZoom;
		int size = getDataSize();
		if (startIndex >= size) startIndex = size - 1;
		if (endIndex < 0) endIndex = 0;
	}

	public void moveStart() {
		int periods = getPeriods();
		startIndex = 0;
		endIndex = Math.min(startIndex + periods - 1, getDataSize() - 1);
	}

	public void moveEnd() {
		int periods = getPeriods();
		endIndex = getDataSize() - 1;
		startIndex = Math.max(endIndex - periods + 1, 0);
	}

	/**
	 * Returns the greater pip scale within all data sources.
	 * @return The pip scale.
	 */
	public int getPipScale() {
		int scale = Numbers.MIN_INTEGER;
		for (DataSource source : dataSources) {
			int pipScale = source.getInfo().getPipScale();
			if (pipScale > scale) {
				scale = pipScale;
			}
		}
		return scale;
	}
}
