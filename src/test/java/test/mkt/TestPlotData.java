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

import msfx.mkt.*;
import msfx.mkt.chart.PlotData;
import msfx.mkt.info.DataInfo;
import msfx.mkt.info.OutputInfo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class TestPlotData {
	public static void main(String[] args) {

		Period min5 = new Period(Unit.MINUTE, 5);
		List<OutputInfo> outputInfos = new ArrayList<>();
		outputInfos.add(new OutputInfo("Open", "O", "Open price", 0));
		outputInfos.add(new OutputInfo("High", "H", "High price", 1));
		outputInfos.add(new OutputInfo("Low", "L", "Low price", 2));
		outputInfos.add(new OutputInfo("Close", "C", "Close price", 3));

		DataInfo info = new DataInfo("X", min5);

		LocalDateTime start = LocalDateTime.of(2023, 9, 1, 12, 0);
		int secs = (int) start.toEpochSecond(ZoneOffset.UTC);

		DataSourceList list_0 = new DataSourceList(info);
		list_0.addData(new Data(secs + (0 * 300), 0, 0, 0, 0));
		list_0.addData(new Data(secs + (1 * 300), 0, 0, 0, 0));
		list_0.addData(new Data(secs + (2 * 300), 0, 0, 0, 0));
		list_0.addData(new Data(secs + (3 * 300), 0, 0, 0, 0));
		list_0.addData(new Data(secs + (4 * 300), 0, 0, 0, 0));
		list_0.addData(new Data(secs + (5 * 300), 0, 0, 0, 0));
		list_0.addData(new Data(secs + (6 * 300), 0, 0, 0, 0));
		list_0.addData(new Data(secs + (7 * 300), 0, 0, 0, 0));

		DataSourceList list_1 = new DataSourceList(info);
		list_1.addData(new Data(secs + (2 * 300), 0, 0, 0, 0));
		list_1.addData(new Data(secs + (3 * 300), 0, 0, 0, 0));
		list_1.addData(new Data(secs + (5 * 300), 0, 0, 0, 0));
		list_1.addData(new Data(secs + (6 * 300), 0, 0, 0, 0));

		DataSourceList list_2 = new DataSourceList(info);
		list_2.addData(new Data(secs + (2 * 300), 0, 0, 0, 0));
		list_2.addData(new Data(secs + (3 * 300), 0, 0, 0, 0));
		list_2.addData(new Data(secs + (4 * 300), 0, 0, 0, 0));
		list_2.addData(new Data(secs + (5 * 300), 0, 0, 0, 0));
		list_2.addData(new Data(secs + (6 * 300), 0, 0, 0, 0));
		list_2.addData(new Data(secs + (7 * 300), 0, 0, 0, 0));
		list_2.addData(new Data(secs + (8 * 300), 0, 0, 0, 0));
		list_2.addData(new Data(secs + (9 * 300), 0, 0, 0, 0));

		PlotData plotData = new PlotData();
		plotData.addDataSource(list_0);
		plotData.addDataSource(list_1);
		plotData.addDataSource(list_2);

	}
}
