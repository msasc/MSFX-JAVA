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
import msfx.lib.util.Numbers;

/**
 * Provides the necessary components to the data plotter to be able to perform the plot task.
 *
 * @author Miquel Sas
 */
public interface PlotContext {
	/**
	 * Returns the plot data that provides the range of data indexes to as well as the corresponding
	 * indexes for a given data source.
	 *
	 * @return The plot data.
	 */
	PlotData getPlotData();
	/**
	 * The graphics context where the plot has to be performed.
	 *
	 * @return The graphics context.
	 */
	GraphicsContext getGraphicsContext();
	/**
	 * Calculates the coordinate X given the data index within the graphics context.
	 *
	 * @param index The index.
	 * @return The coordinate X.
	 */
	double getCoordinateX(int index);
	/**
	 * Calculates the coordinate Y given the value and the scale within the graphics context.
	 *
	 * @param value The value.
	 * @return The coordinate Y.
	 */
	double getCoordinateY(double value);
}
