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

package msfx.ztrash.mkt.chart.plotter;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import msfx.ztrash.task.Task;
import msfx.ztrash.mkt.Data;
import msfx.ztrash.mkt.DataSource;
import msfx.ztrash.mkt.chart.DataPlotter;
import msfx.ztrash.mkt.chart.PlotContext;
import msfx.ztrash.mkt.info.OutputInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Line plotter on OHLC data.
 *
 * @author Miquel Sas
 */
public class LinePlotter extends DataPlotter {

	/**
	 * Index on data values.
	 */
	private int index;
	/**
	 * Output info.
	 */
	private OutputInfo outputInfo;

	/**
	 * Constructor.
	 *
	 * @param index      The index in the data array of values.
	 * @param dataSource The data source.
	 * @param id         The output information name.
	 * @param name       The output information short name.
	 */
	public LinePlotter(
			int index,
			DataSource dataSource,
			String id,
			String name) {
		super(dataSource);
		this.index = index;
		this.outputInfo = new OutputInfo(id, name, 0);
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
		return new double[] { data.getValue(this.index) };
	}

	/**
	 * Returns the list of output information for each value of a given index.
	 *
	 * @param index The data index.
	 * @return The list of output information.
	 */
	@Override
	public OutputInfo[] getInfos(int index) {
		return new OutputInfo[] { outputInfo };
	}

	private int startIndex;
	private int endIndex;
	private int dataSize;
	private double periods;
	private double marginRight;
	private double marginLeft;

	private class ParallelData {
		private int start;
		private int end;
		private List<Point2D> points;
		private ParallelData(int start, int end) {
			this.start = start;
			this.end = end;
			this.points = new ArrayList<>(end - start + 1);
		}
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
				double value = data.getValue(LinePlotter.this.index);

				/*
				 * The X coordinate to start painting, and the Y coordinate for each value.
				 * Also, check whether the OHLC bar is bullish or bearish to get the proper color.
				 */

				double x = context.getCoordinateX(index);
				double y = context.getCoordinateY(value);

				seg.points.add(new Point2D(x, y));
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
		gc.setLineWidth(0.5);

		/*
		 * Start and end periods to iterate.
		 * Calculate the available width per period in pixels.
		 * If the available width is less than 3, then the bat will be a single vertical line.
		 */

		startIndex = context.getPlotData().getStartIndex();
		endIndex = context.getPlotData().getEndIndex();
		dataSize = context.getPlotData().getDataSize();
		periods = context.getPlotData().getPeriods();
		marginRight = context.getMarginRight();
		marginLeft = context.getMarginLeft();

		/*
		 * Determine whether to plot concurrently. The number of concurrent segments will be the
		 * available parallelism, and the number of periods to calculate per segment must be GE
		 * the magic number of 100.
		 */

		int parallelism = context.getPlotPool().getParallelism();
		int periodsPerSegment = (int) (periods / parallelism);
		boolean parallel = periodsPerSegment >= 1000;

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
			Point2D p_prev = null;
			Point2D p_curr = null;
			for (TaskCalcPlot task : tasks) {
				List<Point2D> points = task.seg.points;
				for (Point2D point : points) {
					if (p_prev == null) p_prev = point;
					p_curr = point;
					if (!p_curr.equals(p_prev)) {
						gc.strokeLine(p_prev.getX(), p_prev.getY(), p_curr.getX(), p_curr.getY());
					}
					p_prev = p_curr;
				}
			}

		} else {

			DataSource source = getDataSources().get(0);
			List<Integer> indexes = context.getPlotData().getIndexes(source);

			Data data_prev = null;
			Data data_curr = null;
			Point2D p_prev = null;
			Point2D p_curr = null;
			for (int index = startIndex; index <= endIndex; index++) {

				if (index < 0) continue;
				if (index >= dataSize) continue;

				/*
				 * Get the data index that corresponds to the period index and, if there is no data for
				 * that period, skip it.
				 */
				int dataIndex = indexes.get(index);
				if (dataIndex < 0) continue;
				data_curr = source.getData(dataIndex);
				if (data_prev == null) data_prev = data_curr;

				/* Skip if any of both data is not valid. */
				if (!data_prev.isValid() || !data_curr.isValid()) continue;

				/* Current value. */
				double value = data_curr.getValue(this.index);

				/*
				 * The X coordinate to start painting, and the Y coordinate for each value.
				 * Also, check whether the OHLC bar is bullish or bearish to get the proper color.
				 */

				double x = context.getCoordinateX(index);
				double y = context.getCoordinateY(value);
				Point2D point = new Point2D(x, y);
				if (p_prev == null) p_prev = point;
				p_curr = point;
				if (!p_curr.equals(p_prev)) {
					gc.strokeLine(p_prev.getX(), p_prev.getY(), p_curr.getX(), p_curr.getY());
				}

				data_prev = data_curr;
				p_prev = p_curr;
			}
		}
	}
}
