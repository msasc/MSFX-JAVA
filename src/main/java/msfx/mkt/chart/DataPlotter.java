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

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import msfx.mkt.DataSource;
import msfx.mkt.info.OutputInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for the different plotters of time data, like BAR, CANDLESTICK, LINE, HISTOGRAM...
 * <p>
 * The data plotter is responsible for calculating the data to be plotted and plot it.
 *
 * @author Miquel Sas
 */
public abstract class DataPlotter {

	/**
	 * List of data sources this plotter uses, in the proper order of precedence.
	 */
	private final List<DataSource> dataSources;

	/**
	 * Bearish color.
	 */
	private Color colorBearish = Color.BLACK;
	/**
	 * Bullish color.
	 */
	private Color colorBullish = Color.BLACK;

	/**
	 * Most used constructor assigning one data source.
	 *
	 * @param dataSource The data source.
	 */
	public DataPlotter(DataSource dataSource) {
		if (dataSource == null) {
			throw new NullPointerException("Source can not be null");
		}
		this.dataSources = new ArrayList<>();
		this.dataSources.add(dataSource);
	}
	/**
	 * Constructor of a more complex plotter that uses a list of data sources.
	 *
	 * @param dataSources The list of data sources.
	 */
	public DataPlotter(List<DataSource> dataSources) {
		if (dataSources == null) {
			throw new NullPointerException("List of sources can not be null");
		}
		if (dataSources.isEmpty()) {
			throw new NullPointerException("List of sources can not be empty");
		}
		this.dataSources = dataSources;
	}

	/**
	 * Returns the list of data sources used by this plotter.
	 *
	 * @return The list of data sources.
	 */
	public List<DataSource> getDataSources() {
		return dataSources;
	}
	/**
	 * Returns the list of values that will be plotted.
	 *
	 * @param index The index.
	 * @return The list of values that will be plotted.
	 */
	public abstract double[] getValues(int index);
	/**
	 * Returns the list of output information for each value of a given index.
	 *
	 * @param index The data index.
	 * @return The list of output information.
	 */
	public abstract OutputInfo[] getInfos(int index);

	/**
	 * Returns the bearish color.
	 *
	 * @return The bearish color.
	 */
	public Color getColorBearish() {
		return colorBearish;
	}
	/**
	 * Returns the bullish color.
	 *
	 * @return The bullish color.
	 */
	public Color getColorBullish() {
		return colorBullish;
	}
	/**
	 * Set the bearish color.
	 *
	 * @param colorBearish The bearish color.
	 */
	public void setColorBearish(Color colorBearish) {
		this.colorBearish = colorBearish;
	}
	/**
	 * Set the bullish color.
	 *
	 * @param colorBullish The bullish color.
	 */
	public void setColorBullish(Color colorBullish) {
		this.colorBullish = colorBullish;
	}

	/**
	 * Perform the plot.
	 *
	 * @param context The plot context.
	 */
	public abstract void plot(PlotContext context);
}
