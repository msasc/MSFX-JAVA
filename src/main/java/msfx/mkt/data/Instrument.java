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

package msfx.mkt.data;

import java.util.Currency;

/**
 * Financial instrument definition.
 *
 * @author Miquel Sas
 */
public class Instrument {

	/**
	 * Instrument unique identifier, normally in the context of a provider of financial data.
	 */
	private final String id;
	/**
	 * Instrument title.
	 */
	private final String title;
	/**
	 * The value of a pip, e.g. 0.0001 for the currency pair EUR/USD.
	 */
	private final double pipValue;
	/**
	 * The scale of a pip.
	 */
	private final int pipScale;
	/**
	 * The minimum value of a tick, e.g., 0.00001 for the currency pair EUR/USD.
	 */
	private final double tickValue;
	/**
	 * The scale of a tick.
	 */
	private final int tickScale;
	/**
	 * The volume scale if applicable.
	 */
	private final int volumeScale;
	/**
	 * Primary currency. For the currency pair EUR/UDS it would be EUR. For the index DAX it would
	 * be EUR, while for the NASDAQ it would USD.
	 */
	private final Currency primaryCurrency;
	/**
	 * Secondary currency. For the currency pair EUR/UDS it would be USD, while for an index it can
	 * be null or the primary one.
	 */
	private final Currency secondaryCurrency;

	/**
	 * @param id                Instrument unique identifier, normally in the context of a provider
	 *                          of financial data.
	 * @param title             Instrument title.
	 * @param pipValue          The minimum value of a pip, e.g. 0.0001 for the currency pair
	 *                          EUR/USD.
	 * @param pipScale          The scale of a pip.
	 * @param tickValue         The minimum value of a tick, e.g., 0.00001 for the currency pair
	 *                          EUR/USD.
	 * @param tickScale         The scale of a tick.
	 * @param volumeScale       The volume scale if applicable.
	 * @param primaryCurrency   Primary currency. For the currency pair EUR/UDS it would be EUR. For
	 *                          the index DAX it would be EUR, while for the NASDAQ it would USD.
	 * @param secondaryCurrency Secondary currency. For the currency pair EUR/UDS it would be USD,
	 *                          while for an index it can be null or the primary one.
	 */
	public Instrument(
			String id,
			String title,
			double pipValue,
			int pipScale,
			double tickValue,
			int tickScale,
			int volumeScale,
			Currency primaryCurrency,
			Currency secondaryCurrency) {
		this.id = id;
		this.title = title;
		this.pipValue = pipValue;
		this.pipScale = pipScale;
		this.tickValue = tickValue;
		this.tickScale = tickScale;
		this.volumeScale = volumeScale;
		this.primaryCurrency = primaryCurrency;
		this.secondaryCurrency = secondaryCurrency;
	}

	/**
	 * Returns the instrument unique identifier, normally in the context of a provider of financial
	 * data.
	 *
	 * @return The id.
	 */
	public String getId() {
		return id;
	}
	/**
	 * Returns instrument title.
	 *
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Returns the minimum value of a pip, e.g. 0.0001 for the currency pair EUR/USD.
	 *
	 * @return The minimum value of a pip.
	 */
	public double getPipValue() {
		return pipValue;
	}
	/**
	 * returns the scale of a pip.
	 *
	 * @return The scale of a pip.
	 */
	public int getPipScale() {
		return pipScale;
	}
	/**
	 * Returns the minimum value of a tick, e.g., 0.00001 for the currency pair EUR/USD.
	 *
	 * @return The tick value.
	 */
	public double getTickValue() {
		return tickValue;
	}
	/**
	 * The scale of a tick.
	 *
	 * @return The scale of a tick.
	 */
	public int getTickScale() {
		return tickScale;
	}
	/**
	 * Returns the volume scale.
	 *
	 * @return The volume scale.
	 */
	public int getVolumeScale() {
		return volumeScale;
	}
	/**
	 * Returns the primary currency. For the currency pair EUR/UDS it would be EUR. For the index
	 * DAX it would
	 * be EUR, while for the NASDAQ it would USD.
	 *
	 * @return The primary currency.
	 */
	public Currency getPrimaryCurrency() {
		return primaryCurrency;
	}
	/**
	 * Returns the secondary currency. For the currency pair EUR/UDS it would be USD, while for an
	 * index it can
	 * be null or the primary one.
	 *
	 * @return The secondary currency.
	 */
	public Currency getSecondaryCurrency() {
		return secondaryCurrency;
	}
}
