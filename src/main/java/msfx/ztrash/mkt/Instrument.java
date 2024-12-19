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

package msfx.ztrash.mkt;

import java.util.Currency;

/**
 * Financial instrument definition.
 *
 * @author Miquel Sas
 */
public class Instrument {

	/**
	 * Instrument unique name, normally in the context of a provider of financial data.
	 */
	private String name;
	/**
	 * Instrument title.
	 */
	private String title;
	/**
	 * The value of a pip, e.g. 0.0001 for the currency pair EUR/USD.
	 */
	private double pipValue;
	/**
	 * The scale of a pip.
	 */
	private int pipScale;
	/**
	 * The minimum value of a tick, e.g., 0.00001 for the currency pair EUR/USD.
	 */
	private double tickValue;
	/**
	 * The scale of a tick.
	 */
	private int tickScale;
	/**
	 * The volume scale if applicable.
	 */
	private int volumeScale;
	/**
	 * Primary currency. For the currency pair EUR/UDS it would be EUR. For the index DAX it would
	 * be EUR, while for the NASDAQ it would USD.
	 */
	private Currency primaryCurrency;
	/**
	 * Secondary currency. For the currency pair EUR/UDS it would be USD, while for an index it can
	 * be null or the primary one.
	 */
	private Currency secondaryCurrency;

	/**
	 * Default constructor.
	 */
	public Instrument() { }

	/**
	 * Returns the instrument unique identifier, normally in the context of a provider of financial
	 * data.
	 *
	 * @return The id.
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
	 * Returns instrument title.
	 *
	 * @return The title.
	 */
	public String getTitle() {
		return title;
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
	 * Returns the minimum value of a pip, e.g., 0.0001 for the currency pair EUR/USD.
	 *
	 * @return The minimum value of a pip.
	 */
	public double getPipValue() {
		return pipValue;
	}
	/**
	 * Set the pip value.
	 *
	 * @param pipValue The pip value.
	 */
	public void setPipValue(double pipValue) {
		this.pipValue = pipValue;
	}

	/**
	 * Returns the scale of a pip.
	 *
	 * @return The scale of a pip.
	 */
	public int getPipScale() {
		return pipScale;
	}
	/**
	 * Set the pip scale.
	 *
	 * @param pipScale
	 */
	public void setPipScale(int pipScale) {
		this.pipScale = pipScale;
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
	 * Set the tick value.
	 *
	 * @param tickValue
	 */
	public void setTickValue(double tickValue) {
		this.tickValue = tickValue;
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
	 * Set the tick scale.
	 *
	 * @param tickScale
	 */
	public void setTickScale(int tickScale) {
		this.tickScale = tickScale;
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
	 * Set the volume scale.
	 *
	 * @param volumeScale
	 */
	public void setVolumeScale(int volumeScale) {
		this.volumeScale = volumeScale;
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
	 * Set the primary currency.
	 *
	 * @param primaryCurrency The primary currency.
	 */
	public void setPrimaryCurrency(Currency primaryCurrency) {
		this.primaryCurrency = primaryCurrency;
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
	/**
	 * Set the secondary currency.
	 *
	 * @param secondaryCurrency The secondary currency.
	 */
	public void setSecondaryCurrency(Currency secondaryCurrency) {
		this.secondaryCurrency = secondaryCurrency;
	}
}
