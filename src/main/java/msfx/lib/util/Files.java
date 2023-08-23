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

package msfx.lib.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * File utility functions.
 *
 * @author Miquel Sas
 */
public class Files {
	/**
	 * Returns the file extension is present.
	 *
	 * @param fileName The file name.
	 * @return The extension part.
	 */
	public static String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) return "";
		return fileName.substring(index + 1);
	}
	/**
	 * Returns the file name if an extension is present.
	 *
	 * @param fileName The file name.
	 * @return The name part.
	 */
	public static String getFileName(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) return fileName;
		return fileName.substring(0, index);
	}
	/**
	 * Return the file or null, scanning the current name as a file, and then the available path
	 * entries recursively.
	 *
	 * @param fileName The file name to search.
	 * @return The file or null.
	 * @throws FileNotFoundException If the file is not found.
	 */
	public static File getFileFromPathEntries(String fileName) throws FileNotFoundException {

		/* Check direct. */
		File file = new File(fileName);
		if (file.exists()) return file;

		/* Check path entries. */
		List<String> entries = getPathEntries();
		for (String s : entries) {
			File entry = new File(s);
			File check = getFileRecursive(entry, file);
			if (check != null) return check;
		}

		throw new FileNotFoundException(fileName);
	}
	/**
	 * Return the file composed by the parent and child, scanning recursively if the parent is a
	 * directory.
	 *
	 * @param parent The parent, file or directory.
	 * @param file   The file to search.
	 * @return The file or null if not found recursively.
	 */
	public static File getFileRecursive(File parent, File file) {
		if (parent.isFile()) {
			if (parent.getAbsolutePath().endsWith(file.getPath())) {
				return parent;
			}
		}
		if (parent.isDirectory()) {
			File[] children = parent.listFiles();
			if (children != null) {
				for (File child : children) {
					File check = getFileRecursive(child, file);
					if (check != null) return check;
				}
			}
		}
		return null;
	}
	/**
	 * Returns the localized file or the default given the locale, the file name and the extension.
	 *
	 * @param locale   The locale.
	 * @param fileName The file name.
	 * @return The localized file or null if it does not exist.
	 */
	public static File getLocalizedFile(Locale locale, String fileName) {
		File file = null;
		String name = getFileName(fileName);
		String ext = getFileExtension(fileName);
		if (!ext.isEmpty()) ext = "." + ext;

		/* First attempt: language and country. */
		if (!locale.getCountry().isEmpty()) {
			try {
				fileName = name + "_" + locale.getLanguage() + "_" + locale.getCountry() + ext;
				file = getFileFromPathEntries(fileName);
			} catch (FileNotFoundException ignore) {
			}
		}
		if (file != null) return file;

		/* Second attempt: language only. */
		if (!locale.getLanguage().isEmpty()) {
			try {
				fileName = name + "_" + locale.getLanguage() + ext;
				file = getFileFromPathEntries(fileName);
			} catch (FileNotFoundException ignore) {
			}
		}
		if (file != null) return file;

		/* Third attempt: no locale reference. */
		try {
			fileName = name + ext;
			file = getFileFromPathEntries(fileName);
		} catch (FileNotFoundException ignore) {
		}
		if (file != null) return file;

		/* Not found at all. */
		return null;
	}
	/**
	 * Returns the list of path entries parsing the class path string.
	 *
	 * @return A list of path entries.
	 */
	public static List<String> getPathEntries() {
		String classPath = System.getProperty("java.class.path");
		String pathSeparator = System.getProperty("path.separator");
		StringTokenizer tokenizer = new StringTokenizer(classPath, pathSeparator);
		List<String> entries = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			entries.add(tokenizer.nextToken());
		}
		return entries;
	}
	/**
	 * Gets the properties by loading the file.
	 *
	 * @param file The file.
	 * @return The properties.
	 * @throws IOException If an IO error occurs.
	 */
	public static Properties getProperties(File file) throws IOException {
		boolean xml = getFileExtension(file.getName()).equalsIgnoreCase("xml");
		FileInputStream fileIn = new FileInputStream(file);
		BufferedInputStream buffer = new BufferedInputStream(fileIn, 4096);
		Properties properties = getProperties(buffer, xml);
		buffer.close();
		fileIn.close();
		return properties;
	}
	/**
	 * Gets the properties from the input stream.
	 *
	 * @param stream The input stream.
	 * @param xml    A boolean that indicates if the input stream has a xml format
	 * @return The properties.
	 * @throws IOException If an IO error occurs.
	 */
	public static Properties getProperties(InputStream stream, boolean xml) throws IOException {
		Properties properties = new Properties();
		if (xml) properties.loadFromXML(stream);
		else properties.load(stream);
		return properties;
	}
}
