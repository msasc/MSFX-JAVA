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

package msfx.ztrash.db.converters;

import msfx.ztrash.db.Value;
import msfx.ztrash.res.StringRes;

import java.util.Locale;

/**
 * String converter for values of type boolean.
 *
 * @author Miquel Sas
 */
public class BooleanStringConverter implements ValueStringConverter {

	/** String true. */
	private final String tokenTrue;
	/** String false. */
	private final String tokenFalse;

	/**
	 * Constructor.
	 */
	public BooleanStringConverter() {
		this(Locale.getDefault());
	}
	/**
	 * Constructor.
	 *
	 * @param locale Locale.
	 */
	public BooleanStringConverter(Locale locale) {
		this.tokenTrue = StringRes.get("TOKEN.YES", "Yes", locale);
		this.tokenFalse = StringRes.get("TOKEN.NO", "No", locale);
	}
	/**
	 * Constructor assigning the true and false tokens.
	 *
	 * @param tokenTrue  Token for true values.
	 * @param tokenFalse Token for false values.
	 */
	public BooleanStringConverter(String tokenTrue, String tokenFalse) {
		this.tokenTrue = tokenTrue;
		this.tokenFalse = tokenFalse;
	}
	/**
	 * Return the value given the string.
	 */
	public Value fromString(String string) {
		if (string == null) return new Value(false);
		return new Value(string.equals(tokenTrue));
	}
	/**
	 * Return the string given the value.
	 */
	public String toString(Value value) {
		return value.getBoolean() ? tokenTrue : tokenFalse;
	}
}