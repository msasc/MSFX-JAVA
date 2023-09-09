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

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import msfx.lib.util.Numbers;

/**
 * Horizontal axis, the component where the timeline is shown.
 *
 * @author Miquel Sas
 */
public class ChartXAxis {

	/**
	 * The pane that contains the bound canvas.
	 */
	final Pane pane;
	/**
	 * The canvas to paint on.
	 */
	final Canvas canvas;

	/**
	 * Constructor.
	 */
	ChartXAxis() {
		pane = new Pane();
		canvas = new Canvas();
		pane.getChildren().add(canvas);
		canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
		canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
	}
}
