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

package msfx.mkt.chart.plotter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import msfx.lib.util.Numbers;
import msfx.mkt.Data;
import msfx.mkt.DataSource;
import msfx.mkt.chart.DataPlotter;
import msfx.mkt.chart.PlotContext;

import java.util.List;

/**
 * Bar plotter.
 *
 * @author Miquel Sas
 */
public class BarPlotter extends DataPlotter {

	/**
	 * Constructor.
	 *
	 * @param dataSource The data source.
	 */
	public BarPlotter(DataSource dataSource) {
		super(dataSource);
	}

	/**
	 * Returns the list of values that will be plotted.
	 *
	 * @param index The index.
	 * @return The list of values that will be plotted.
	 */
	@Override
	public double[] getValues(int index) {
		Data data = getDataSources().get(0).getData(index);
		double[] values = data.getValues();
		return new double[] { values[0], values[1], values[2], values[3] };
	}

	/**
	 * Perform the plot.
	 *
	 * @param context The plot context.
	 */
	@Override
	public void plot(PlotContext context) {

		DataSource source = getDataSources().get(0);
		List<Integer> indexes = context.getPlotData().getIndexes(source);
		GraphicsContext gc = context.getGraphicsContext();

		double width = context.getWidth();
		double height = context.getHeight();
		gc.clearRect(0, 0, width, height);

		/*
		 * Start and en period to iterate. Calculate the available width per period in pixels.
		 * If the available width is less than 3, then the bat will be a single vertical line.
		 */
		int startIndex = context.getPlotData().getStartIndex();
		int endIndex = context.getPlotData().getEndIndex();
		int dataSize = context.getPlotData().getDataSize();
		double periods = endIndex - startIndex + 1;
		double marginRight = context.getMarginRight();
		double marginLeft = context.getMarginLeft();
		double periodWidth = (width - marginLeft - marginRight) / periods;

		for (int index = startIndex; index <= endIndex; index++) {

			if (index < 0) continue;
			if (index >= dataSize) continue;

			/*
			 * Get the data index that corresponds to the period index and, if there is no data for
			 * that period, skip it.
			 */
			int dataIndex = indexes.get(index);
			if (dataIndex < 0) continue;
			Data data = source.getData(dataIndex);
			double[] values = data.getValues();

			/*
			 * The X coordinate to start painting, and the Y coordinate for each value.
			 * Also, check whether the OHLC bar is bullish or bearish to get the proper color.
			 */

			double x = context.getCoordinateX(index);
			double y_open = context.getCoordinateY(values[Data.OPEN]);
			double y_high = context.getCoordinateY(values[Data.HIGH]);
			double y_low = context.getCoordinateY(values[Data.LOW]);
			double y_close = context.getCoordinateY(values[Data.CLOSE]);
			boolean bullish = (values[Data.CLOSE] >= values[Data.OPEN]);
			Color color = bullish ? getColorBullish() : getColorBearish();

			/*
			 * Get the center of the period.
			 */
			double lineWidth = gc.getLineWidth();
			double x_center = x;
			if (periodWidth > 1) {
				x_center += ((periodWidth - lineWidth) / 2);
			}
			x_center = Numbers.round(x_center, 0);
			double armLen = 0;
			if (periodWidth >= 3) {
				armLen = (periodWidth - lineWidth) / 2;
				armLen = Math.floor(armLen);
				armLen = Math.min(armLen, 3);
			}

			/*
			 * Do plot.
			 */
			gc.setLineWidth(1.0);
			gc.setStroke(color);
			gc.strokeLine(x_center, y_high, x_center, y_low);
			if (armLen > 0) {
				gc.strokeLine(x_center - armLen, y_open, x_center, y_open);
				gc.strokeLine(x_center, y_close, x_center + armLen, y_close);
			}

		}
	}
}
