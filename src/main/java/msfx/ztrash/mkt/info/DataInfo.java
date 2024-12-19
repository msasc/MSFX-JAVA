/*
 * Copyright (c) 2023-2024 Miquel Sas.
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

package msfx.ztrash.mkt.info;

import msfx.ztrash.mkt.Data;
import msfx.ztrash.mkt.Instrument;
import msfx.ztrash.mkt.Period;

import java.util.ArrayList;
import java.util.List;

/**
 * Base information that describes data in a data source.
 *
 * @author Miquel Sas
 */
public class DataInfo {

	/**
	 * A short name.
	 */
	private String name;
	/**
	 * A title or description.
	 */
	private String title;

	/**
	 * Instrument of data if applicable.
	 */
	private Instrument instrument;
	/**
	 * Period.
	 */
	private Period period;

	/**
	 * The pip scale used for the data in this data list.
	 */
	private Integer pipScale;
	/**
	 * The tick scale used for the data in this data list.
	 */
	private Integer tickScale;

	/**
	 * The list of information about outputs.
	 */
	private List<OutputInfo> outputInfos = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public DataInfo() { }

	/**
	 * Add output information.
	 *
	 * @param outputInfo Output information.
	 */
	public void addOutputInfo(OutputInfo outputInfo) {
		outputInfos.add(outputInfo);
	}
	/**
	 * Add output information.
	 *
	 * @param name      The output name, for instance <b>Close</b> for the close value of an
	 *                  {@link Data} instance, not null.
	 * @param shortName A short name to build a short information string, like for instance <b>C</b>
	 *                  for the <b>Close</b> value, not null.
	 * @param title     Title or description, not null.
	 * @param index     The index of this output in the {@link Data} object, GE zero.
	 */
	public void addOutputInfo(String name, String shortName, String title, int index) {
		OutputInfo outputInfo = new OutputInfo(name, title, index);
		outputInfos.add(outputInfo);
	}

	/**
	 * Return the name.
	 *
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Set the name.
	 *
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Return the period.
	 *
	 * @return The period.
	 */
	public Period getPeriod() {
		return period;
	}
	/**
	 * Set the period.
	 *
	 * @param period The time period.
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}
	/**
	 * Return the title.
	 *
	 * @return The title.
	 */
	public String getTitle() {
		return (title == null ? "" : title);
	}
	/**
	 * Set the title.
	 *
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Return the optional instrument.
	 *
	 * @return The instrument.
	 */
	public Instrument getInstrument() {
		return instrument;
	}
	/**
	 * Set the instrument.
	 *
	 * @param instrument The instrument.
	 */
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
		if (instrument != null) {
			if (pipScale == null) {
				pipScale = instrument.getPipScale();
			}
			if (tickScale == null) {
				tickScale = instrument.getTickScale();
			}
		}
	}

	/**
	 * Return the pip scale.
	 *
	 * @return The pip scale.
	 */
	public int getPipScale() {
		return (pipScale == null ? 0 : pipScale);
	}
	/**
	 * Set the pip scale.
	 *
	 * @param pipScale The pip scale.
	 */
	public void setPipScale(int pipScale) {
		if (pipScale < 0) throw new IllegalArgumentException("Invalid pip scale: " + pipScale);
		this.pipScale = pipScale;
	}
	/**
	 * Return the tick scale.
	 *
	 * @return The tick scale.
	 */
	public int getTickScale() {
		return (tickScale == null ? 0 : tickScale);
	}
	/**
	 * Set the tick scale.
	 *
	 * @param tickScale The tick scale.
	 */
	public void setTickScale(int tickScale) {
		if (tickScale < 0) throw new IllegalArgumentException("Invalid tick scale: " + tickScale);
		this.tickScale = tickScale;
	}

	/**
	 * Returns the output info at the given index.
	 *
	 * @param index The index.
	 * @return The output info at the given index.
	 */
	public OutputInfo getOutputInfo(int index) {
		return outputInfos.get(index);
	}
	/**
	 * Returns the size or number of outputs.
	 *
	 * @return The size or number of outputs.
	 */
	public int size() {
		return outputInfos.size();
	}
}
