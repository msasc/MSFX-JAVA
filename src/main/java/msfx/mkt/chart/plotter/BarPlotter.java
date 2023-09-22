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
import msfx.lib.task.Task;
import msfx.lib.util.Numbers;
import msfx.mkt.Data;
import msfx.mkt.DataSource;
import msfx.mkt.chart.DataPlotter;
import msfx.mkt.chart.PlotContext;

import java.util.ArrayList;
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

	private int startIndex;
	private int endIndex;
	private int dataSize;
	private double periods;
	private double marginRight;
	private double marginLeft;
	private double periodWidth;

	private class ParallelData {
		private int start;
		private int end;
		private List<ParallelDataPlot> plots;
		private ParallelData(int start, int end) {
			this.start = start;
			this.end = end;
			this.plots = new ArrayList<>(end - start + 1);
		}
	}

	private class ParallelDataPlot {
		private double armLen;
		private double x_center;
		private double y_open;
		private double y_high;
		private double y_low;
		private double y_close;
		Color color;
	}

	private class TaskCalcPlot extends Task {

		private PlotContext context;
		private ParallelData seg;

		private TaskCalcPlot(PlotContext context, ParallelData seg) {
			this.context = context;
			this.seg = seg;
		}
		@Override
		public void execute() throws Throwable {

			DataSource source = getDataSources().get(0);
			List<Integer> indexes = context.getPlotData().getIndexes(source);
			GraphicsContext gc = context.getGraphicsContext();

			for (int index = seg.start; index <= seg.end; index++) {
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

				ParallelDataPlot plot = new ParallelDataPlot();
				plot.color = color;
				plot.armLen = armLen;
				plot.x_center = x_center;
				plot.y_open = y_open;
				plot.y_high = y_high;
				plot.y_low = y_low;
				plot.y_close = y_close;

				seg.plots.add(plot);

			}

		}
	}

	/**
	 * Perform the plot.
	 *
	 * @param context The plot context.
	 */
	@Override
	public void plot(PlotContext context) {

		double width = context.getWidth();
		double height = context.getHeight();

		GraphicsContext gc = context.getGraphicsContext();
		gc.clearRect(0, 0, width, height);
		gc.setLineWidth(1.0);

		/*
		 * Start and en period to iterate. Calculate the available width per period in pixels.
		 * If the available width is less than 3, then the bat will be a single vertical line.
		 */

		startIndex = context.getPlotData().getStartIndex();
		endIndex = context.getPlotData().getEndIndex();
		dataSize = context.getPlotData().getDataSize();
		periods = endIndex - startIndex + 1;
		marginRight = context.getMarginRight();
		marginLeft = context.getMarginLeft();
		periodWidth = (width - marginLeft - marginRight) / periods;

		/*
		 * Determine whether to plot concurrently. The number of concurrent segments will be the
		 * available parallelism, and the number of periods to calculate per segment must be GE
		 * the magic number of 100.
		 */

		int parallelism = context.getPlotPool().getParallelism();
		int periodsPerSegment = (int) (periods / parallelism);
		boolean parallel = periodsPerSegment >= 100;

		if (parallel) {

			/*
			 * Build the list of segments to calculate concurrently and their correspondent tasks,
			 * and execute them.
			 */
			List<TaskCalcPlot> tasks = new ArrayList<>();
			int start = startIndex;
			int end = start + periodsPerSegment - 1;
			while (true) {
				ParallelData seg = new ParallelData(start, end);
				tasks.add(new TaskCalcPlot(context, seg));
				start = end + 1;
				if (start > endIndex) {
					break;
				}
				end = start + periodsPerSegment - 1;
				if (end > endIndex) {
					end = endIndex;
				}
			}
			context.getPlotPool().execute(tasks);

			/*
			 * Do plot.
			 */
			for (TaskCalcPlot task : tasks) {
				List<ParallelDataPlot> plots = task.seg.plots;
				for (ParallelDataPlot plot : plots) {
					gc.setStroke(plot.color);
					gc.strokeLine(plot.x_center, plot.y_high, plot.x_center, plot.y_low);
					if (plot.armLen > 0) {
						gc.strokeLine(
								plot.x_center - plot.armLen, plot.y_open,
								plot.x_center, plot.y_open);
						gc.strokeLine(
								plot.x_center, plot.y_close,
								plot.x_center + plot.armLen, plot.y_close);
					}
				}
			}

		} else {
			plot(context, startIndex, endIndex);
		}
	}

	private void plot(PlotContext context, int start, int end) {

		DataSource source = getDataSources().get(0);
		List<Integer> indexes = context.getPlotData().getIndexes(source);
		GraphicsContext gc = context.getGraphicsContext();

		for (int index = start; index <= end; index++) {

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
			gc.setStroke(color);
			gc.strokeLine(x_center, y_high, x_center, y_low);
			if (armLen > 0) {
				gc.strokeLine(x_center - armLen, y_open, x_center, y_open);
				gc.strokeLine(x_center, y_close, x_center + armLen, y_close);
			}

		}
	}
}
