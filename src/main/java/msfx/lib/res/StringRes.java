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

package msfx.lib.res;

import java.util.Locale;

/**
 * Provider of localized string resources.
 *
 * @author Miquel Sas
 */
public abstract class StringRes {

	/**
	 * Default resources installed when starting a desktop application. Recall that in a WEB
	 * application resources will be required on a session basis depending on the session locale.
	 */
	private static StringRes res;
	/**
	 * Set the default string resource provider within the application.
	 *
	 * @param res The string resource provider.
	 */
	public static void setDefault(StringRes res) {
		StringRes.res = res;
	}

	/**
	 * Return the string given the default string, using it as the key, and applying the default
	 * locale. This method is aimed to be used all around the development stage, and then searching
	 * usages of this method and applying the proper key and locale.
	 *
	 * @param key The key to search the string withing the localized source.
	 * @param def The default string.
	 * @return The string.
	 */
	public static String get(String key, String def) {
		return get(key, def, Locale.getDefault());
	}

	/**
	 * Return the string using the default provider.
	 *
	 * @param key The key to search the string withing the localized source.
	 * @param def The default string.
	 * @param loc The {@link Locale} used to look up the source of localized strings.
	 * @return The localized string or the default one.
	 */
	public static String get(String key, String def, Locale loc) {
		if (res == null) return def;
		return res.getString(key, def, loc);
	}

	/**
	 * Look up the string given the key within the localized source and, if not found, return the
	 * default string passed as an argument.
	 * <p>
	 * This is the method that any string provider must implement.
	 *
	 * @param key The key to search the string withing the localized source.
	 * @param def The default string.
	 * @param loc The {@link Locale} used to look up the source of localized strings.
	 * @return The localized string or the default one.
	 */
	public abstract String getString(String key, String def, Locale loc);
}
