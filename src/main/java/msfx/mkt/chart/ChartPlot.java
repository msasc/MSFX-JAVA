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

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import msfx.lib.util.Numbers;

import java.util.ArrayList;
import java.util.List;

/**
 * The chart component where data and drawings are plotted.
 *
 * @author Miquel Sas
 */
public class ChartPlot {

	/**
	 * The pane that contains the bound canvas.
	 */
	final Pane pane;
	/**
	 * The canvas to paint on.
	 */
	final Canvas canvas;

	/**
	 * List of data plotters that will plot on this chart canvas.
	 */
	final List<DataPlotter> plotters = new ArrayList<>();

	/**
	 * Top margin.
	 */
	double marginTop;
	/**
	 * Right margin.
	 */
	double marginRight;
	/**
	 * Bottom margin.
	 */
	double marginBottom;
	/**
	 * Left margin.
	 */
	double marginLeft;

	/**
	 * Minimum value within a start and an end index.
	 */
	double minimumValue;
	/**
	 * Maximum value within a start and an end index.
	 */
	double maximumValue;

	/**
	 * Plot scale applicable to all plots within this area and also the related vertical axis.
	 */
	PlotScale scale = PlotScale.LOGARITHMIC;

	/**
	 * Constructor.
	 */
	ChartPlot() {
		pane = new Pane();
		canvas = new Canvas();
		pane.getChildren().add(canvas);
		canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
		canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
	}

	/**
	 * Calculates the required margins.
	 *
	 * @param insets Margin insets as a percentage factor.
	 */
	void calculateMargins(Insets insets) {
		marginTop = Numbers.round(pane.getHeight() * insets.getTop(), 0);
		marginRight = Numbers.round(pane.getWidth() * insets.getRight(), 0);
		marginBottom = Numbers.round(pane.getHeight() * insets.getBottom(), 0);
		marginLeft = Numbers.round(pane.getWidth() * insets.getLeft(), 0);
	}

	/**
	 * Calculates the minimum and maximum values within the range indexes.
	 *
	 * @param startIndex Start index, included.
	 * @param endIndex   End index included.
	 */
	void calculateMinMaxValues(int startIndex, int endIndex) {

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
	 * Calculates the coordinate X given the index within the plot data indexes.
	 *
	 * @param index    The index.
	 * @param plotData Plot data container.
	 * @return The coordinate X.
	 */
	double getCoordinateX(int index, PlotData plotData) {
		double startIndex = plotData.startIndex;
		double endIndex = plotData.endIndex;
		double indexFactor = ((double) index - startIndex) / (endIndex - startIndex);
		double plotWidth = pane.getWidth() - marginLeft - marginRight;
		double relativeX = indexFactor * plotWidth;
		double coordinateX = Numbers.round(marginLeft + relativeX, 0);
		return coordinateX;
	}

	/**
	 * Calculates the coordinate Y given the value and the scale of this area.
	 * @param value The value.
	 * @return The coordinate Y.
	 */
	double getCoordinateY(double value) {

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
}
