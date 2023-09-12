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

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import msfx.lib.util.Numbers;
import msfx.mkt.DataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * The chart component that manages the vertical layout of several plot areas and a horizontal axis
 * with the timeline at the bottom.
 * <p>
 * All plot areas panes have the same with, as well as all the vertical axes panes. Thus, the width
 * and the horizontal margins of the plot areas should be located at the chart frame level.
 * <p>
 * The vertical margins apply to each plot area and its associated vertical axis, thus they should
 * be located at the plot frame level.
 *
 * @author Miquel Sas
 */
public class ChartFrame {

	/**
	 * A border pane that contains a plot info pane in the top, a plot are in the center and a
	 * vertical axis in the right.
	 */
	private class PlotFrame {

		/**
		 * Effective border pane that contains the info pane, the plot area pane and the vertical
		 * axis.
		 */
		private final BorderPane pane;
		/**
		 * The plot area where the data charts are plotted.
		 */
		private final PlotArea plotArea;
		/**
		 * The associated vertical axis.
		 */
		private final VAxis vaxis;

		/**
		 * Top margin.
		 */
		private double marginTop;
		/**
		 * Bottom margin.
		 */
		private double marginBottom;
		/**
		 * Minimum value within a start and an end index.
		 */
		private double minimumValue;
		/**
		 * Maximum value within a start and an end index.
		 */
		private double maximumValue;

		private PlotScale scale;

		/**
		 * List of data plotters.
		 */
		private final List<DataPlotter> plotters;

		/**
		 * Constructor.
		 */
		private PlotFrame() {

			plotters = new ArrayList<>();
			scale = PlotScale.LOGARITHMIC;

			plotArea = new PlotArea();
			vaxis = new VAxis();
			pane = new BorderPane();
			pane.setCenter(plotArea.pane);
			pane.setRight(vaxis.pane);
		}

		/**
		 * Calculates the minimum and maximum values within the range indexes.
		 *
		 * @param startIndex Start index, included.
		 * @param endIndex   End index included.
		 */
		private void calculateMinMaxValues(int startIndex, int endIndex) {

			minimumValue = Numbers.MAX_DOUBLE;
			maximumValue = Numbers.MIN_DOUBLE;

			for (int index = startIndex; index <= endIndex; index++) {
				for (DataPlotter plotter : plotters) {
					double[] values = plotter.getValues(index);
					for (double value : values) {
						if (value < minimumValue) {
							minimumValue = value;
						}
						if (value > maximumValue) {
							maximumValue = value;
						}
					}
				}
			}

		}
		/**
		 * Calculate the vertical margins.
		 */
		private void calculateVerticalMargins() {
			marginTop = Numbers.round(plotArea.pane.getHeight() * insets.getTop(), 0);
			marginBottom = Numbers.round(plotArea.pane.getHeight() * insets.getBottom(), 0);
		}
		/**
		 * Calculates the coordinate Y given the value and the scale of this area.
		 *
		 * @param value The value.
		 * @return The coordinate Y.
		 */
		private double getCoordinateY(double value) {

			double minValue = minimumValue;
			double maxValue = maximumValue;
			if (scale == PlotScale.LOGARITHMIC) {
				minValue = Math.log1p(minValue);
				maxValue = Math.log1p(maxValue);
				value = Math.log1p(value);
			}
			if (scale == PlotScale.PERCENTAGE) {
				// TODO pending to implement PlotScale.PERCENTAGE.
			}

			double valueFactor = (value - minValue) / (maxValue - minValue);
			double relativeY = 0;
			double chartHeight = pane.getHeight() - marginTop - marginBottom;
			if (Double.isFinite(value) && Double.isFinite(valueFactor)) {
				relativeY = valueFactor * chartHeight;
			}

			double coordinateY = marginTop + chartHeight - relativeY;
			return coordinateY;
		}
		/**
		 * Returns a suitable plot context.
		 *
		 * @return The plot context to draw.
		 */
		private PlotContext getContext() {
			return new Context(this);
		}
		/**
		 * Plot the frame.
		 */
		private void plot() {
			PlotContext context = getContext();
			for (DataPlotter plotter : plotters) {
				plotter.plot(context);
			}
		}
	}

	/**
	 * The chart component where data and drawings are plotted.
	 */
	private class PlotArea {
		/**
		 * The pane that contains the bound canvas.
		 */
		private final Pane pane;
		/**
		 * The canvas to paint on.
		 */
		private final Canvas canvas;
		/**
		 * Constructor.
		 */
		private PlotArea() {
			pane = new Pane();
			canvas = new Canvas();
			pane.getChildren().add(canvas);
			canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
			canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
		}
	}

	/**
	 * The plot info component contains a text flow control that expands to the right and, at the
	 * right, a close button.
	 */
	private class PlotInfo {
		/**
		 * Grid that contains the cursor information and the buttons.
		 */
		private final GridPane pane = new GridPane();
	}

	/**
	 * Vertical axis, the component where scaled values of the plot are shown.
	 */
	private class VAxis {
		/**
		 * The pane that contains the bound canvas.
		 */
		private final Pane pane;
		/**
		 * The canvas to paint on.
		 */
		private final Canvas canvas;
		/**
		 * Constructor.
		 */
		private VAxis() {
			pane = new Pane();
			canvas = new Canvas();
			pane.getChildren().add(canvas);
			canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
			canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
		}
	}

	/**
	 * Horizontal axis, the component where the timeline is shown.
	 */
	private class XAxis {
		/**
		 * The pane that contains the bound canvas.
		 */
		private final Pane pane;
		/**
		 * The canvas to paint on.
		 */
		private final Canvas canvas;
		/**
		 * Constructor.
		 */
		private XAxis() {
			pane = new Pane();
			canvas = new Canvas();
			pane.getChildren().add(canvas);
			canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
			canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
		}
	}

	/**
	 * Width and height change listener to respond to size events.
	 */
	private class SizeListener implements ChangeListener<Number> {
		@Override
		public void changed(
				ObservableValue<? extends Number> observable,
				Number oldValue,
				Number newValue) {
			Platform.runLater(() -> { plot(); });
		}
	}

	/**
	 * The plot context to pass to data plotters.
	 */
	public class Context implements PlotContext {
		/**
		 * The associated plot frame.
		 */
		private PlotFrame plotFrame;
		/**
		 * Constructor.
		 *
		 * @param plotFrame The plot frame.
		 */
		private Context(PlotFrame plotFrame) {
			this.plotFrame = plotFrame;
		}

		/**
		 * Returns the plot data.
		 *
		 * @return The plot data.
		 */
		public PlotData getPlotData() {
			return plotData;
		}

		/**
		 * Returns the graphics context.
		 *
		 * @return The graphics context.
		 */
		public GraphicsContext getGraphicsContext() {
			return plotFrame.plotArea.canvas.getGraphicsContext2D();
		}

		/**
		 * Returns the width of the plot area.
		 *
		 * @return The width.
		 */
		public double getWidth() {
			return plotFrame.plotArea.canvas.getWidth();
		}
		/**
		 * Returns the height of the plot area.
		 *
		 * @return The height.
		 */
		public double getHeight() {
			return plotFrame.plotArea.canvas.getHeight();
		}

		/**
		 * Returns the top margin.
		 *
		 * @return The top margin.
		 */
		public double getMarginTop() {
			return plotFrame.marginTop;
		}
		/**
		 * Returns the right margin.
		 *
		 * @return The right margin.
		 */
		public double getMarginRight() {
			return marginRight;
		}
		/**
		 * Returns the bottom margin.
		 *
		 * @return The bottom margin.
		 */
		public double getMarginBottom() {
			return plotFrame.marginBottom;
		}
		/**
		 * Returns the left margin.
		 *
		 * @return The left margin.
		 */
		public double getMarginLeft() {
			return marginLeft;
		}

		/**
		 * Returns the coordinate X.
		 *
		 * @param index The index.
		 * @return The coordinate X.
		 */
		public double getCoordinateX(int index) {
			return ChartFrame.this.getCoordinateX(index);
		}
		/**
		 * Returns the coordinate Y.
		 *
		 * @param value The value.
		 * @return The coordinate Y.
		 */
		public double getCoordinateY(double value) {
			return plotFrame.getCoordinateY(value);
		}

		/**
		 * Return the minimum value within the range of indexes.
		 *
		 * @return The minimum value.
		 */
		public double getMinimumValue() {
			return plotFrame.minimumValue;
		}
		/**
		 * Return the maximum value within the range of indexes.
		 *
		 * @return The maximum value.
		 */
		public double getMaximumValue() {
			return plotFrame.maximumValue;
		}
	}

	/**
	 * The insets, as a unitary factor, that define the margins of the panes. The plot area has a
	 * margin all around. The vertical axis has a top and bottom margin, that are the as the ones
	 * of its correspondent plot area. The horizontal axis and the plot info area have no margins
	 * defined by these insets.
	 */
	private Insets insets = new Insets(0.05, 0.05, 0.05, 0.05);
	/**
	 * Right margin.
	 */
	private double marginRight;
	/**
	 * Left margin.
	 */
	private double marginLeft;

	/**
	 * Plot data.
	 */
	private final PlotData plotData;
	/**
	 * List of plot frames.
	 */
	private final List<PlotFrame> plotFrames;
	/**
	 * Horizontal axis.
	 */
	private final XAxis xAxis;

	private final BorderPane pane;

	/**
	 * Constructor.
	 */
	public ChartFrame() {

		plotData = new PlotData();
		plotFrames = new ArrayList<>();
		xAxis = new XAxis();
		pane = new BorderPane();

		SizeListener sizeListener = new SizeListener();
		pane.widthProperty().addListener(sizeListener);
		pane.heightProperty().addListener(sizeListener);
	}

	/**
	 * Add a plot frame.
	 *
	 * @param plotters The list of plotters of the frame.
	 */
	public void addPlotFrame(DataPlotter... plotters) {
		PlotFrame plotFrame = new PlotFrame();
		for (DataPlotter plotter : plotters) {
			plotFrame.plotters.add(plotter);
			for (DataSource source : plotter.getDataSources()) {
				plotData.addDataSource(source);
			}
		}
		plotFrames.add(plotFrame);
		if (plotFrames.size() == 1) {
			pane.setCenter(plotFrame.pane);
		}
	}

	/**
	 * Calculate the horizontal margins common to all plot areas.
	 */
	private void calculateHorizontalMargins() {
		if (plotFrames.isEmpty()) {
			throw new IllegalStateException("No plot frames available");
		}
		double width = plotFrames.get(0).plotArea.pane.getWidth();
		marginRight = Numbers.round(width * insets.getRight(), 0);
		marginLeft = Numbers.round(width * insets.getLeft(), 0);
	}

	/**
	 * Calculates the coordinate X given the index within the plot data indexes.
	 *
	 * @param index The index.
	 * @return The coordinate X.
	 */
	private double getCoordinateX(int index) {
		if (plotFrames.isEmpty()) {
			throw new IllegalStateException("No plot frames available");
		}
		double width = plotFrames.get(0).plotArea.pane.getWidth();
		double startIndex = plotData.getStartIndex();
		double endIndex = plotData.getEndIndex();
		double indexFactor = ((double) index - startIndex) / (endIndex - startIndex);
		double plotWidth = width - marginLeft - marginRight;
		double relativeX = indexFactor * plotWidth;
		double coordinateX = Numbers.round(marginLeft + relativeX, 0);
		return coordinateX;
	}

	/**
	 * Returns the main frame pane.
	 *
	 * @return The main frame pane.
	 */
	public Pane getPane() {
		return pane;
	}

	/**
	 * Set the range of start and end indexes to N periods that finish at the end of the available
	 * periods.
	 *
	 * @param periods The number of visible periods.
	 */
	public void setIndexesRangeFromEnd(int periods) {
		plotData.setIndexesRangeFromEnd(periods);
	}
	/**
	 * Set the range of start and end indexes to N periods that start at the beginning of the
	 * available periods.
	 *
	 * @param periods The number of visible periods.
	 */
	public void setIndexesRangeFromStart(int periods) {
		plotData.setIndexesRangeFromStart(periods);
	}

	public void plot() {
		for (PlotFrame plotFrame : plotFrames) {
			plotFrame.calculateMinMaxValues(plotData.getStartIndex(), plotData.getEndIndex());
			plotFrame.calculateVerticalMargins();;
		}
		calculateHorizontalMargins();
		for (PlotFrame plotFrame : plotFrames) {
			plotFrame.plot();
		}
	}

}
