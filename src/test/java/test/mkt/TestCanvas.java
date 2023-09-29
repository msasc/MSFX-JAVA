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

package test.mkt;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TestCanvas extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private class SizeListener implements ChangeListener<Number> {
		@Override
		public void changed(
				ObservableValue<? extends Number> observable,
				Number oldValue,
				Number newValue) {
		}
	}

	@Override
	public void start(Stage stage) throws Exception {

		BorderPane root = new BorderPane();
		Canvas canvas = new Canvas();
		root.setCenter(canvas);
		canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
		canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));

		canvas.widthProperty().addListener(ev -> {
			System.out.println(canvas.getWidth());
		});

		Scene scene = new Scene(root);
		stage.setTitle("Test chart");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setHeight(500);
		stage.setWidth(1000);
		stage.show();
	}
}
