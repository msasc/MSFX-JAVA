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

package msfx.mkt.sources.data;

import msfx.lib.util.Files;
import msfx.lib.util.Numbers;
import msfx.lib.util.Strings;
import msfx.mkt.*;
import msfx.mkt.info.DataInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * Utility class to build basic price data sources from text files downloaded from VChart.
 *
 * @author Miquel Sas
 */
public class VChartSource {

	/**
	 * The text file.
	 */
	private final File file;

	/**
	 * Constructor.
	 *
	 * @param file The VChart text file.
	 */
	public VChartSource(File file) {

		/* OK. */
		this.file = file;

		/* Validate file name. */
		List<String> tokens = getNameTokens();
		if (tokens.size() != 5) {
			throw new IllegalArgumentException("Invalid file name: invalid tokens");
		}

		String type = tokens.get(0);
		if (!Strings.in(type, "FX", "ID", "ST")) {
			throw new IllegalArgumentException("Invalid file name: invalid id");
		}

		/* Forex. */
		if (type.equals("FX")) {
			Currency curr_a = Currency.getInstance(tokens.get(1));
			if (curr_a == null) {
				throw new IllegalArgumentException("Invalid file name: invalid forex currency A");
			}
			Currency curr_b = Currency.getInstance(tokens.get(2));
			if (curr_b == null) {
				throw new IllegalArgumentException("Invalid file name: invalid forex currency A");
			}
		}

		/* Index or stock. */
		if (type.equals("ID") || type.equals("ST")) {
			Currency curr_b = Currency.getInstance(tokens.get(2));
			if (curr_b == null) {
				throw new IllegalArgumentException("Invalid file name: invalid forex currency A");
			}
		}

		/* Period. */
		String period = tokens.get(3);
		if (!Strings.in(period, "DAY", "MIN")) {
			throw new IllegalArgumentException("Invalid file name: invalid period");
		}

		/* Size. */
		String size = tokens.get(4);
		try {
			Integer.parseInt(size);
		} catch (Exception exc) {
			throw new IllegalArgumentException("Invalid file name: invalid size");
		}
	}

	/**
	 * Returns the list of tokens.
	 *
	 * @return The list of tokens.
	 */
	private List<String> getNameTokens() {
		String name = Files.getFileName(file.getName());
		List<String> tokens = Strings.parse(name, "_");
		return tokens;
	}

	/**
	 * Reads a sample of 20 data lines to retrieve pip and tick value and scale.
	 *
	 * @return The sample list of data lines.
	 */
	private List<String> getSampleLines() {
		List<String> lines = new ArrayList<>();
		try {
			FileReader reader = new FileReader(file);
			BufferedReader buffer = new BufferedReader(reader);
			while (true) {
				String line = buffer.readLine();
				if (line == null) break;
				if (line.startsWith("<")) continue;
				lines.add(line);
				if (lines.size() >= 20) break;
			}
			buffer.close();
			reader.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return lines;
	}

	/**
	 * Return a list of sample values.
	 *
	 * @return The list of sample values.
	 */
	private List<String> getSampleValues() {
		List<String> lines = getSampleLines();
		List<String> values = new ArrayList<>();
		for (String line : lines) {
			if (line.startsWith("<")) continue;
			List<String> tokens = Strings.parse(line, ",");
			values.add(tokens.get(4));
			values.add(tokens.get(5));
			values.add(tokens.get(6));
			values.add(tokens.get(7));
		}
		return values;
	}

	/**
	 * Returns the pip value as a string.
	 *
	 * @return The pip value as a string.
	 */
	private String getStrPipValue() {

		List<String> values = getSampleValues();

		String valueChk = values.get(0);
		int index = valueChk.indexOf(".");
		if (index == -1) return "1";

		int trailingZeros = Numbers.MAX_INTEGER;
		for (String value : values) {
			int trailing = Strings.countMatchesTrailing(value, '0');
			trailingZeros = Math.min(trailing, trailingZeros);
		}

		String sdec = valueChk.substring(index + 1);
		String spip = "0." + Strings.repeat("0", sdec.length() - trailingZeros - 1) + "1";
		return spip;
	}

	/**
	 * Returns the tick value as a string.
	 *
	 * @return The tick value as a string.
	 */
	private String getStrTickValue() {

		List<String> values = getSampleValues();

		String valueChk = values.get(0);
		int index = valueChk.indexOf(".");
		if (index == -1) return "1";

		int decs = Numbers.MIN_INTEGER;
		for (String value : values) {
			index = value.indexOf(".");
			if (index >= 0) {
				decs = Math.max(decs, value.substring(index + 1).length());
			}
		}
		String stick = "0." + Strings.repeat("0", decs - 1) + "1";
		return stick;
	}

	/**
	 * Returns the data info.
	 *
	 * @return The data info.
	 */
	public DataInfo getDataInfo() {

		List<String> tokens = getNameTokens();
		String s_id = tokens.get(0);
		String s_curr_a = tokens.get(1);
		String s_curr_b = tokens.get(2);
		String s_period = tokens.get(3);
		String s_size = tokens.get(4);

		String name = null;
		Currency curr_a = null;
		Currency curr_b = null;
		Unit unit = null;
		int size = -1;
		Period period = null;

		if (s_id.equals("FX")) {
			name = s_curr_a + "/" + s_curr_b;
			curr_a = Currency.getInstance(s_curr_a);
			curr_b = Currency.getInstance(s_curr_b);
		} else {
			name = s_curr_a;
			curr_a = Currency.getInstance(s_curr_b);
		}
		if (s_period.equals("DAY")) {
			unit = Unit.DAY;
		} else {
			unit = Unit.MINUTE;
		}
		size = Integer.parseInt(s_size);
		period = new Period(unit, size);

		String s_pip_value = getStrPipValue();
		double pipValue = Double.parseDouble(s_pip_value);
		int pipScale = new BigDecimal(s_pip_value).scale();

		String s_tick_value = getStrTickValue();
		double tickValue = Double.parseDouble(s_tick_value);
		int tickScale = new BigDecimal(s_tick_value).scale();

		Instrument instr = new Instrument();
		instr.setName(name);
		instr.setTitle(name);
		instr.setPipValue(pipValue);
		instr.setPipScale(pipScale);
		instr.setTickValue(tickValue);
		instr.setTickScale(tickScale);
		instr.setVolumeScale(0);
		instr.setPrimaryCurrency(curr_a);
		instr.setSecondaryCurrency(curr_b);

		DataInfo info = new DataInfo();
		info.setPeriod(period);
		info.setTitle(name + " - " + period.toString());
		info.setInstrument(instr);

		info.addOutputInfo("Open", "O", "Open value", 0);
		info.addOutputInfo("High", "H", "High value", 1);
		info.addOutputInfo("Low", "L", "Low value", 2);
		info.addOutputInfo("Close", "C", "Close value", 3);
		info.addOutputInfo("Volume", "V", "Volume value", 4);

		return info;
	}

	/**
	 * Returns the data source.
	 *
	 * @return The data source.
	 */
	public DataSource getDataSource() {
		DataSourceList source = new DataSourceList(getDataInfo());

		try {
			FileReader reader = new FileReader(file);
			BufferedReader buffer = new BufferedReader(reader);

			while (true) {
				String line = buffer.readLine();
				if (line == null) {
					break;
				}
				if (line.startsWith("<")) {
					continue;
				}
				List<String> tokens = Strings.parse(line, ",");
				String sdate = tokens.get(2);
				String stime = Strings.rightPad(tokens.get(3), 6, "0");
				String sopen = tokens.get(4);
				String shigh = tokens.get(5);
				String slow = tokens.get(6);
				String sclose = tokens.get(7);
				String svolume = tokens.get(8);

				int year = Integer.parseInt(sdate.substring(0, 4));
				int month = Integer.parseInt(sdate.substring(4, 6));
				int day = Integer.parseInt(sdate.substring(6, 8));
				int hour = Integer.parseInt(stime.substring(0, 2));
				int min = Integer.parseInt(stime.substring(2, 4));
				LocalDateTime dt = LocalDateTime.of(year, month, day, hour, min);
				long millis = dt.toInstant(ZoneOffset.ofHours(2)).toEpochMilli();
				int time = ((int) (millis / 1000));

				double open = Double.parseDouble(sopen);
				double high = Double.parseDouble(shigh);
				double low = Double.parseDouble(slow);
				double close = Double.parseDouble(sclose);
				double volume = Double.parseDouble(svolume);

				Data data = new Data(time, open, high, low, close,  volume);
				source.addData(data);
			}

			buffer.close();
			reader.close();

			return source;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return source;
	}
}
