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

package msfx.mkt.info;

import msfx.mkt.Instrument;
import msfx.mkt.Period;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Base information that describes data in a data source.
 *
 * @author Miquel Sas
 */
public class DataInfo {

	/**
	 * ID or name.
	 */
	private final String id;
	/**
	 * A title or description.
	 */
	private final String title;

	/**
	 * Instrument of data if applicable.
	 */
	private final Instrument instrument;
	/**
	 * Period.
	 */
	private final Period period;

	/**
	 * The pip scale used for the data in this data list.
	 */
	private final int pipScale;
	/**
	 * The tick scale used for the data in this data list.
	 */
	private final int tickScale;

	/**
	 * The list of information about outputs.
	 */
	private final List<OutputInfo> outputs;

	/**
	 * Constructor.
	 *
	 * @param id         ID or name, not null.
	 * @param title      Title or description, not null.
	 * @param instrument Instrument of data, null if not applicable.
	 * @param period     Period, not null.
	 * @param pipScale   The pip scale, if the instrument is not null, can be -1, and then it is
	 *                   picked from the instrument. If the instrument is null it must be GE zero.
	 * @param tickScale  The tick scale, if the instrument is not null, can be -1, and then it is
	 *                   picked from the instrument. If the instrument is null it must be GE zero.
	 * @param outputs    List of information of outputs, with sequential indexes starting at zero.
	 */
	public DataInfo(
			String id,
			String title,
			Instrument instrument,
			Period period,
			int pipScale,
			int tickScale,
			List<OutputInfo> outputs) {

		/* Validate ID. */
		if (id == null) {
			String error = "ID can not be null";
			throw new NullPointerException(error);
		}

		/* Validate title. */
		if (title == null) {
			String error = "Title can not be null";
			throw new NullPointerException(error);
		}

		/* Validate instrument and optionally pip and tick scales. */
		if (instrument == null) {
			if (pipScale < 0) {
				String error = "Pip scale must be GE zero when instrument is null";
				throw new IllegalArgumentException(error);
			}
			if (tickScale < 0) {
				String error = "Tick scale must be GE zero when instrument is null";
				throw new IllegalArgumentException(error);
			}
		}

		/* Validate outputs, not null and sequential starting at zero. */
		if (outputs == null) {
			String error = "Information about out`put can not be null";
			throw new NullPointerException(error);
		}
		outputs.sort(Comparator.comparingInt(OutputInfo::getIndex));
		for (int i = 0; i < outputs.size(); i++) {
			if (i == 0 && outputs.get(i).getIndex() != 0) {
				String error = "First output must have index 0";
				throw new IllegalArgumentException(error);
			}
			if (i > 0) {
				if (outputs.get(i).getIndex() != outputs.get(i - 1).getIndex() + 1) {
					String error = "Outputs must have consecutive indexes";
					throw new IllegalArgumentException(error);
				}
			}
		}

		this.id = id;
		this.title = title;
		this.instrument = instrument;
		this.period = period;
		if (instrument != null && pipScale < 0) {
			this.pipScale = instrument.getPipScale();
		} else {
			this.pipScale = pipScale;
		}
		if (instrument != null && tickScale < 0) {
			this.tickScale = instrument.getTickScale();
		} else {
			this.tickScale = tickScale;
		}
		this.outputs = new ArrayList<>(outputs);
	}

	/**
	 * Return the ID or name.
	 *
	 * @return The ID or name.
	 */
	public String getId() {
		return id;
	}
	/**
	 * Return the title.
	 *
	 * @return The title.
	 */
	public String getTitle() {
		return title;
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
	 * Return the period.
	 *
	 * @return The period.
	 */
	public Period getPeriod() {
		return period;
	}
	/**
	 * Return the pip scale.
	 *
	 * @return The pip scale.
	 */
	public int getPipScale() {
		return pipScale;
	}
	/**
	 * Return the tick scale.
	 *
	 * @return The tick scale.
	 */
	public int getTickScale() {
		return tickScale;
	}

	/**
	 * Returns the output info at the given index.
	 *
	 * @param index The index.
	 * @return The output info at the given index.
	 */
	public OutputInfo getOutputInfo(int index) {
		return outputs.get(index);
	}
	/**
	 * Returns the size or number of outputs.
	 *
	 * @return The size or number of outputs.
	 */
	public int size() {
		return outputs.size();
	}
}
