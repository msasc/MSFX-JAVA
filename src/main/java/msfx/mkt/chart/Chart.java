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
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Utilities used along all chart components.
 *
 * @author Miquel Sas
 */
class Chart {
	/**
	 * Width of the top toolbar pane buttons.
	 */
	static final double BUTTON_WIDTH = 32;
	/**
	 * Height of the top toolbar pane buttons.
	 */
	static final double BUTTON_HEIGHT = 24;
	/**
	 * Sides of the internal frame of the button where the icon is drawn.
	 */
	static final double BUTTON_FRAME = 8;
	/**
	 * Default font for all texts within char components.
	 */
	static final Font TEXT_FONT = new Font(10);
	/**
	 * A pane that has, as its first child, a canvas with the width and height properties bound to
	 * those of its parent pane.
	 *
	 * @author Miquel Sas
	 */
	static class CanvasPane {

		Pane pane;
		Canvas canvas;

		CanvasPane() {
			pane = new Pane();
			canvas = new Canvas();
			pane.getChildren().add(canvas);
			canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
			canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
		}
	}
	/**
	 * Creates the border used along all panes.
	 *
	 * @param t Top line width.
	 * @param r Right line width.
	 * @param b Bottom line width.
	 * @param l Left line width.
	 * @return The border.
	 */
	static Border getBorder(Double t, Double r, Double b, Double l) {
		BorderStroke borderStroke = new BorderStroke(
				Color.BLACK,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				new BorderWidths(t, r, b, l));
		return new Border(borderStroke);
	}
}
