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

package msfx.ztrash.mkt.chart;

import javafx.scene.canvas.GraphicsContext;
import msfx.ztrash.task.Pool;
import msfx.lib.util.Numbers;

/**
 * Base context to plot on the different chart panes.
 *
 * @author Miquel Sas
 */
public abstract class PlotContext {

	/**
	 * Returns a suitable pool to prepare plot calculations concurrently.
	 *
	 * @return The pool.
	 */
	public abstract Pool getPlotPool();

	/**
	 * Returns the plot data that provides the range of data indexes to as well as the corresponding
	 * indexes for a given data source.
	 *
	 * @return The plot data.
	 */
	public abstract PlotData getPlotData();

	/**
	 * The graphics context where the plot has to be performed.
	 *
	 * @return The graphics context.
	 */
	public abstract GraphicsContext getGraphicsContext();

	/**
	 * Returns the width of the plot area.
	 *
	 * @return The width.
	 */
	public abstract double getWidth();
	/**
	 * Returns the height of the plot area.
	 *
	 * @return The height.
	 */
	public abstract double getHeight();

	/**
	 * Returns the top margin.
	 *
	 * @return The top margin.
	 */
	public abstract double getMarginTop();
	/**
	 * Returns the right margin.
	 *
	 * @return The right margin.
	 */
	public abstract double getMarginRight();
	/**
	 * Returns the bottom margin.
	 *
	 * @return The bottom margin.
	 */
	public abstract double getMarginBottom();
	/**
	 * Returns the left margin.
	 *
	 * @return The left margin.
	 */
	public abstract double getMarginLeft();

	/**
	 * Calculates the coordinate X given the data index within the graphics context.
	 *
	 * @param index The index.
	 * @return The coordinate X.
	 */
	public abstract double getCoordinateX(int index);
	/**
	 * Calculates the coordinate Y given the value and the scale within the graphics context.
	 *
	 * @param value The value.
	 * @return The coordinate Y.
	 */
	public abstract double getCoordinateY(double value);

	/**
	 * Return the minimum value within the range of indexes.
	 *
	 * @return The minimum value.
	 */
	public abstract double getMinimumValue();
	/**
	 * Return the maximum value within the range of indexes.
	 *
	 * @return The maximum value.
	 */
	public abstract double getMaximumValue();

	/**
	 * Returns the most approximate index given the x coordinate within the plot area.
	 *
	 * @param x The x coordinate within the plot area.
	 * @return The most approximate index.
	 */
	public int getIndex(double x) {

		/* Within the left margin. */
		if (x <= getMarginLeft()) {
			return getPlotData().getStartIndex();
		}

		/* Within the right margin. */
		if (x >= getWidth() - getMarginRight()) {
			return getPlotData().getEndIndex();
		}

		/* Proportional x position as a factor. */
		double fx = (x - getMarginLeft()) / (getWidth() - getMarginLeft() - getMarginRight());

		/* Indexes from start to x position. */
		double periods = getPlotData().getPeriods();
		int indexes = (int) Numbers.round(periods * fx, 0);

		/* Approximate index. */
		int index = getPlotData().getStartIndex() + indexes - 1;
		return index;
	}
	/**
	 * Returns the most approximate value given the y coordinate within the plot area.
	 *
	 * @param y The y coordinate within the plot area.
	 * @return The most approximate value.
	 */
	public double getValue(double y) {

		/* Within the top margin. */
		if (y <= getMarginTop()) {
			return getMaximumValue();
		}

		/* Within the bottom margin. */
		if (y >= getHeight() - getMarginBottom()) {
			return getMinimumValue();
		}

		/* Proportional y position as a factor. */
		double fy = (y - getMarginTop()) / (getHeight() - getMarginTop() - getMarginBottom());

		/* Approximate value. */
		double value = getMaximumValue() - ((getMaximumValue() - getMinimumValue()) * fy);

		/* Rounding. */
		int scale = getPlotData().getPipScale();
		return Numbers.round(value, scale);
	}
}
