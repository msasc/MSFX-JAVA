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

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
import msfx.lib.fx.FX;
import msfx.lib.util.Numbers;

import java.util.ArrayList;
import java.util.List;

import static msfx.mkt.chart.Chart.*;

/**
 * This chart component contains a top information pane, a central chart pane, and a right y-axis
 * pane.
 *
 * @author Miquel Sas
 */
class ChartPlot {

	/**
	 * Chart canvas-pane.
	 */
	private CanvasPane chart;
	/**
	 * Y-Axis canvas-pane.
	 */
	private CanvasPane yaxis;

	/**
	 * Info pane.
	 */
	private FlowPane infoPane;
	/**
	 * Text flow installed in the info pane.
	 */
	private TextFlow infoText;

	/**
	 * Border pane that contains the rest of panes.
	 */
	private BorderPane borderPane;

	/**
	 * Top margin, common to the chart and the y-axis.
	 */
	private double marginTop;
	/**
	 * Bottom margin, common to the chart and the y-axis.
	 */
	private double marginBottom;
	/**
	 * Left margin, that applies only to the chart.
	 */
	private double marginLeft;
	/**
	 * Right margin, that applies only to the chart.
	 */
	private double marginRight;

	/**
	 * Minimum value within a start and an end index.
	 */
	private double minimumValue;
	/**
	 * Maximum value within a start and an end index.
	 */
	private double maximumValue;

	/**
	 * The plot scale that applies to all data plotters that plot on this plot frame area.
	 */
	private PlotScale scale;
	/**
	 * List of data plotters.
	 */
	private List<DataPlotter> plotters;

	/**
	 * Indicator that the cross-cursor horizontal line is on or off.
	 */
	private boolean crossCursorHorz = false;
	/**
	 * Indicator that the cross-cursor vertical line is on or off.
	 */
	private boolean crossCursorVert = false;

	/**
	 * Constructor of a chart plotter with a list of initial data plotters.
	 *
	 * @param initialPlotters The list of initial data plotters.
	 */
	ChartPlot(DataPlotter... initialPlotters) {

		plotters = new ArrayList<>();
		scale = PlotScale.LOGARITHMIC;

		chart = new CanvasPane();
		yaxis = new CanvasPane();
		yaxis.pane.setBorder(getBorder(0.0, 0.0, 0.0, 0.5));
		yaxis.pane.setPrefWidth(50);
		yaxis.pane.setMinWidth(50);

		infoPane = new FlowPane();
		infoPane.setBorder(getBorder(0.0, 0.0, 0.5, 0.0));
		infoPane.setPrefHeight(BUTTON_HEIGHT);

		infoText = new TextFlow();
		infoText.setMaxHeight(BUTTON_HEIGHT);
		infoText.setMinHeight(BUTTON_HEIGHT);

		Bounds bounds = FX.getStringBounds("Some data", TEXT_FONT);
		double top = (BUTTON_HEIGHT - bounds.getHeight()) / 2;
		infoText.setPadding(new Insets(top, 10, 0, 10));

		infoPane.getChildren().add(infoText);

		borderPane = new BorderPane();
		borderPane.setTop(infoPane);
		borderPane.setCenter(chart.pane);
		borderPane.setRight(yaxis.pane);

	}
	/**
	 * Calculates the horizontal margins given the global insets for charts.
	 *
	 * @param insets The insets as a unitary factor that defines the margins of the panes.
	 */
	void calculateHorizontalMargins(Insets insets) {
		double width = chart.pane.getWidth();
		marginRight = Numbers.round(width * insets.getRight(), 0);
		marginLeft = Numbers.round(width * insets.getLeft(), 0);
	}
	/**
	 * Calculates the minimum and maximum values within the range indexes.
	 *
	 * @param startIndex Start index, included.
	 * @param endIndex   End index included.
	 */
	void calculateMinMaxValues(PlotData plotData, int startIndex, int endIndex) {
		int dataSize = plotData.getDataSize();
		minimumValue = Numbers.MAX_DOUBLE;
		maximumValue = Numbers.MIN_DOUBLE;
		for (int index = startIndex; index <= endIndex; index++) {
			if (index < 0) continue;
			if (index >= dataSize) continue;
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
	 *
	 * @param insets The insets as a unitary factor that defines the margins of the panes.
	 */
	void calculateVerticalMargins(Insets insets) {
		marginTop = Numbers.round(chart.pane.getHeight() * insets.getTop(), 0);
		marginBottom = Numbers.round(chart.pane.getHeight() * insets.getBottom(), 0);
	}
	/**
	 * Clear the canvas of the argument chart-plot plot area.
	 */
	void clearPlotArea() {
		double width = chart.canvas.getWidth();
		double height = chart.canvas.getHeight();
		GraphicsContext gc = chart.canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);
	}
}
