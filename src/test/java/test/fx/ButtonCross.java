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

package test.fx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class ButtonCross extends Button {

	public ButtonCross() {
		Canvas canvas = new Canvas(10, 10);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(0.5);
		gc.strokeLine(0, 5, 10, 5);
		gc.strokeLine(5, 0, 5, 10);
		setGraphic(canvas);
		setGraphicTextGap(0);
		setPrefSize(8, 8);
		setMaxSize(8, 8);
		setDefaultButton(false);
		setCancelButton(false);
		setStyle("-fx-border-style: none");
//		setPadding(new Insets(1));
	}
}
