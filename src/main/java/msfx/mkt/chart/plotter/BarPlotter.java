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
import msfx.mkt.info.OutputInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Bar plotter on OHLC data.
 *
 * @author Miquel Sas
 */
public class BarPlotter extends DataPlotter {

	/**
	 * Data segment to calculate plot values in parallel.
	 */
	private class DataSeg {
		private int start;
		private int end;
		private List<DataPlot> plots;
		private DataSeg(int start, int end) {
			this.start = start;
			this.end = end;
			this.plots = new ArrayList<>(end - start + 1);
		}
	}

	/**
	 * Values necessary to plot the bar.
	 */
	private class DataPlot {
		private double armLen;
		private double x_center;
		private double y_open;
		private double y_high;
		private double y_low;
		private double y_close;
		private Color color;
		private boolean valid;
	}

	/**
	 * Task to calculate the plot values in parallel.
	 */
	private class TaskCalcPlot extends Task {
		private DataSeg seg;
		private TaskCalcPlot(DataSeg seg) {
			this.seg = seg;
		}
		@Override
		public void execute() throws Throwable {
			for (int index = seg.start; index <= seg.end; index++) {
				if (index < 0) continue;
				if (index >= dataSize) continue;
				int dataIndex = indexes.get(index);
				if (dataIndex < 0) continue;
				Data data = source.getData(dataIndex);
				DataPlot plot = getDataPlot(index, data);
				seg.plots.add(plot);
			}
		}
	}

	/**
	 * Output information.
	 */
	private OutputInfo[] infos;

	/*
	 * List of volatile members set at the start of the plot and used all along
	 * the calculations and final plot of the bar.
	 */

	private volatile DataSource source;
	private volatile List<Integer> indexes;
	private volatile GraphicsContext gc;
	private volatile PlotContext context;
	private volatile int startIndex;
	private volatile int endIndex;
	private volatile int dataSize;
	private volatile double periods;
	private volatile double marginRight;
	private volatile double marginLeft;
	private volatile double periodWidth;

	/**
	 * Constructor.
	 *
	 * @param dataSource The data source.
	 */
	public BarPlotter(DataSource dataSource) {
		super(dataSource);
		infos = new OutputInfo[] {
				new OutputInfo("O", "Open", "Open value", 0),
				new OutputInfo("H", "High", "High value", 1),
				new OutputInfo("L", "Low", "Low value", 2),
				new OutputInfo("C", "Close", "Close value", 3)
		};
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
		double open = data.getValue(Data.OPEN);
		double high = data.getValue(Data.HIGH);
		double low = data.getValue(Data.LOW);
		double close = data.getValue(Data.CLOSE);
		return new double[] { open, high, low, close };
	}
	/**
	 * Returns the list of output information for each value of a given index.
	 *
	 * @param index The data index.
	 * @return The list of output information.
	 */
	@Override
	public OutputInfo[] getInfos(int index) {
		return infos;
	}

	/**
	 * Calculate the data plot values.
	 *
	 * @param index The global index.
	 * @param data  The data object.
	 * @return The plot data.
	 */
	private DataPlot getDataPlot(int index, Data data) {

		double open = data.getValue(Data.OPEN);
		double high = data.getValue(Data.HIGH);
		double low = data.getValue(Data.LOW);
		double close = data.getValue(Data.CLOSE);

		double x = context.getCoordinateX(index);
		double y_open = context.getCoordinateY(open);
		double y_high = context.getCoordinateY(high);
		double y_low = context.getCoordinateY(low);
		double y_close = context.getCoordinateY(close);
		boolean bullish = (close >= open);
		Color color = bullish ? getColorBullish() : getColorBearish();

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

		DataPlot plot = new DataPlot();
		plot.valid = data.isValid();
		plot.color = color;
		plot.armLen = armLen;
		plot.x_center = x_center;
		plot.y_open = y_open;
		plot.y_high = y_high;
		plot.y_low = y_low;
		plot.y_close = y_close;

		return plot;
	}

	/**
	 * Perform the plot.
	 *
	 * @param context The plot context.
	 */
	@Override
	public void plot(PlotContext context) {

		this.source = getDataSources().get(0);
		this.indexes = context.getPlotData().getIndexes(source);
		this.gc = context.getGraphicsContext();
		this.context = context;
		this.startIndex = context.getPlotData().getStartIndex();
		this.endIndex = context.getPlotData().getEndIndex();
		this.dataSize = context.getPlotData().getDataSize();
		this.periods = context.getPlotData().getPeriods();
		this.marginRight = context.getMarginRight();
		this.marginLeft = context.getMarginLeft();
		this.periodWidth = (context.getWidth() - marginLeft - marginRight) / periods;
		gc.setLineWidth(1.0);

		/*
		 * Determine whether to plot concurrently. The number of concurrent segments will be the
		 * available parallelism, and the number of periods to calculate per segment must be GE
		 * the magic number of 100.
		 */

		int parallelism = context.getPlotPool().getParallelism();
		int periodsPerSegment = (int) (periods / parallelism);
		boolean parallel = periodsPerSegment >= 1000;

		if (parallel) {

			/* Build the list of segments to calculate concurrently and the tasks. */
			List<TaskCalcPlot> tasks = new ArrayList<>();
			int start = startIndex;
			int end = start + periodsPerSegment - 1;
			while (true) {
				DataSeg seg = new DataSeg(start, end);
				tasks.add(new TaskCalcPlot(seg));
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

			/* Do plot. */
			for (TaskCalcPlot task : tasks) {
				List<DataPlot> plots = task.seg.plots;
				for (DataPlot plot : plots) {
					if (plot.valid) plot(plot);
				}
			}

		} else {
			plot(startIndex, endIndex);
		}
	}

	/**
	 * Plot sequentially.
	 *
	 * @param start Start index.
	 * @param end   End index.
	 */
	private void plot(int start, int end) {
		for (int index = start; index <= end; index++) {
			if (index < 0) continue;
			if (index >= dataSize) continue;
			int dataIndex = indexes.get(index);
			if (dataIndex < 0) continue;
			DataPlot plot = getDataPlot(index, source.getData(dataIndex));
			if (plot.valid) plot(plot);
		}
	}

	/**
	 * Plot using the data plot info.
	 *
	 * @param plot Data plot info.
	 */
	private void plot(DataPlot plot) {
		gc.setStroke(plot.color);
		gc.strokeLine(plot.x_center, plot.y_high, plot.x_center, plot.y_low);
		if (plot.armLen > 0) {
			gc.strokeLine(plot.x_center - plot.armLen, plot.y_open, plot.x_center, plot.y_open);
			gc.strokeLine(plot.x_center, plot.y_close, plot.x_center + plot.armLen, plot.y_close);
		}
	}
}
