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

package msfx.mkt.chart_backup;

import javafx.geometry.Insets;

import java.util.ArrayList;
import java.util.List;

/**
 * The chart component that manages the vertical layout of several plot areas, optionally each one
 * with its vertical axis for values, and a horizontal axis wit the timeline at the bottom.
 *
 * @author Miquel Sas
 */
public class ChartFrame {

	/**
	 * List of plot areas.
	 */
	private final List<ChartPlot> plotAreas = new ArrayList<>();
	/**
	 * List of vertical axes, one per plot area.
	 */
	private final List<ChartVAxis> vertAxes = new ArrayList<>();
	/**
	 * Horizontal axis where the timeline is drawn.
	 */
	private final ChartXAxis horzAxis = new ChartXAxis();

	/**
	 * The insets of all vertically chart components. Insets are a factor that leaves a margin
	 * around the chart.
	 */
	private Insets insets = new Insets(0.05, 0.05, 0.05, 0.05);

	/**
	 * Ask all plot areas to calculate the margins they require, given the percentage insets. Called
	 * before the main plot loop over all visible indexes starts.
	 */
	private void calculateMargins() {
		for (ChartPlot area : plotAreas) {
			area.calculateMargins(insets);
		}
	}
}