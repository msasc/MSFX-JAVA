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
import javafx.scene.Scene;
import javafx.stage.Stage;
import msfx.ztrash.mkt.Data;
import msfx.ztrash.mkt.DataSource;
import msfx.ztrash.mkt.IndicatorSource;
import msfx.ztrash.mkt.chart.ChartFrame;
import msfx.ztrash.mkt.chart.plotter.BarPlotter;
import msfx.ztrash.mkt.chart.plotter.LinePlotter;
import msfx.ztrash.mkt.sources.data.VChartSource;
import msfx.ztrash.mkt.sources.indicators.MovingAverage;

import java.io.File;

public class TestChart extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

//		FileChooser fileChooser = new FileChooser();
//		fileChooser.setTitle("Select an VChart data source");
//		fileChooser.getExtensionFilters().add(
//				new FileChooser.ExtensionFilter("Text data files", "*.txt"));
//		fileChooser.setInitialDirectory(new File("D:/Development/Databases/VChart"));
//		File file = fileChooser.showOpenDialog(stage);
//		if (file == null) System.exit(1);
//		if (file != null) System.out.println(file);

		File file = new File("D:\\Development\\Databases\\VChart\\ID_DAX_EUR_MIN_015.txt");

		VChartSource src = new VChartSource(file);

		VChartSource v_chart = new VChartSource(file);
		DataSource source = v_chart.getDataSource();
		BarPlotter b_plotter = new BarPlotter(source);
		LinePlotter l_plotter = new LinePlotter(Data.CLOSE, source, "C", "Close");

		IndicatorSource avg_a = new MovingAverage(source);
		avg_a.addRequiredSource(source);
		avg_a.getInfo().getParameter(MovingAverage.PARAM_SOURCE).setValue("CLOSE");
		avg_a.getInfo().getParameter(MovingAverage.PARAM_TYPE).setValue("WMA");
		avg_a.getInfo().getParameter(MovingAverage.PARAM_PERIOD).setValue(377);
		avg_a.getInfo().getParameter(MovingAverage.PARAM_SMOOTH).setValue(55);
		avg_a.getInfo().getParameter(MovingAverage.PARAM_FIT).setValue(true);

		LinePlotter a_plotter = new LinePlotter(0, avg_a, "Avg", "Average");

		IndicatorSource avg_b = new MovingAverage(source);
		avg_b.addRequiredSource(source);
		avg_b.getInfo().getParameter(MovingAverage.PARAM_SOURCE).setValue("CLOSE");
		avg_b.getInfo().getParameter(MovingAverage.PARAM_TYPE).setValue("WMA");
		avg_b.getInfo().getParameter(MovingAverage.PARAM_PERIOD).setValue(55);
		avg_b.getInfo().getParameter(MovingAverage.PARAM_SMOOTH).setValue(13);
		avg_b.getInfo().getParameter(MovingAverage.PARAM_FIT).setValue(true);

		LinePlotter c_plotter = new LinePlotter(0, avg_b, "Avg", "Average");

		ChartFrame frame = new ChartFrame(b_plotter);
		frame.addPlotFrame(a_plotter);

		Scene scene = new Scene(frame.getPaneFrame());
		stage.setTitle("Test chart");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setHeight(500);
		stage.setWidth(1000);
		stage.show();
	}

}
