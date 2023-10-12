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
import msfx.mkt.Data;
import msfx.mkt.DataSource;
import msfx.mkt.IndicatorSource;
import msfx.mkt.chart.ChartFrame;
import msfx.mkt.chart.plotter.BarPlotter;
import msfx.mkt.chart.plotter.LinePlotter;
import msfx.mkt.info.IndicatorInfo;
import msfx.mkt.sources.data.VChartSource;
import msfx.mkt.sources.indicators.AdaptedMovingAverage;
import msfx.mkt.sources.indicators.MovingAverage;
import msfx.mkt.sources.indicators.SourceFitter;

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

		File file = new File("D:\\Development\\Databases\\VChart\\ID_DAX_EUR_MIN_005.txt");

		VChartSource src = new VChartSource(file);

		VChartSource v_chart = new VChartSource(file);
		DataSource source = v_chart.getDataSource();
		BarPlotter b_plotter = new BarPlotter(source);
		LinePlotter l_plotter = new LinePlotter(Data.CLOSE, source, "C", "Close");

		IndicatorInfo indInf = new IndicatorInfo("ASAVG", source.getInfo().getPeriod());
		IndicatorSource average = new MovingAverage(indInf);
		average.addRequiredSource(source);

		LinePlotter a_plotter = new LinePlotter(0, average, "Avg", "Average");

		ChartFrame frame = new ChartFrame(b_plotter, a_plotter);
//		frame.addPlotFrame(a_plotter);

		Scene scene = new Scene(frame.getPaneFrame());
		stage.setTitle("Test chart");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setHeight(500);
		stage.setWidth(1000);
		stage.show();
	}

}
