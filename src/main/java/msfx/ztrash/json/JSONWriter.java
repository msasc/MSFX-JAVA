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

package msfx.ztrash.json;

import msfx.lib.util.Strings;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * A writer of JSON objects.
 *
 * @author Miquel Sas
 */
public class JSONWriter {

	/**
	 * Default tab size for a readable writer.
	 */
	private final static int TAB_SIZE = 3;
	/**
	 * Tab level when the writer is readable.
	 */
	private int tabLevel = 0;
	/**
	 * Readable flag.
	 */
	private boolean readable = false;

	/**
	 * Constructor of a standard, non-readable format writer.
	 */
	public JSONWriter() { }
	/**
	 * Constructor indicating the readable property.
	 *
	 * @param readable A boolean that indicates whether the output should be in a readable format.
	 */
	public JSONWriter(boolean readable) {
		this.readable = readable;
	}

	/**
	 * Write a {@link JSONObject} into a {@link Writer}.
	 * @param w The {@link Writer}.
	 * @param o The {@link JSONObject}.
	 * @throws IOException If an IO error occurs.
	 */
	public void write(Writer w, JSONObject o) throws IOException {
		w.write("{");
		if (readable) {
			tabLevel++;
		}
		Iterator<String> i = o.keys().iterator();
		while (i.hasNext()) {
			if (readable) {
				w.write("\n" + Strings.repeat(" ", tabLevel * TAB_SIZE));
			}
			String key = i.next();
			w.write("\"" + key + "\":");
			JSONEntry e = o.get(key);
			write(w, e);
			if (i.hasNext()) w.write(",");
		}
		if (readable) {
			tabLevel--;
			w.write("\n" + Strings.repeat(" ", tabLevel * TAB_SIZE));
		}
		w.write("}");
	}

	/**
	 * Write a {@link JSONArray} into a {@link Writer}.
	 * @param w The {@link Writer}.
	 * @param a The {@link JSONArray}.
	 * @throws IOException If an IO error occurs.
	 */
	public void write(Writer w, JSONArray a) throws IOException {
		w.write("[");
		if (readable) {
			tabLevel++;
		}
		Iterator<JSONEntry> i = a.iterator();
		while (i.hasNext()) {
			if (readable) {
				w.write("\n" + Strings.repeat(" ", tabLevel * TAB_SIZE));
			}
			write(w, i.next());
			if (i.hasNext()) w.write(",");
		}
		if (readable) {
			tabLevel--;
			w.write("\n" + Strings.repeat(" ", tabLevel * TAB_SIZE));
		}
		w.write("]");
	}

	/**
	 * Write a {@link JSONEntry} into a {@link Writer}.
	 * @param w The {@link Writer}.
	 * @param e The {@link JSONEntry}.
	 * @throws IOException If an IO error occurs.
	 */
	public void write(Writer w, JSONEntry e) throws IOException {
		JSONTypes type = e.getType();

		if (type == JSONTypes.NULL) {
			w.write("null");
			return;
		}

		if (type == JSONTypes.OBJECT) {
			write(w, e.getObject());
		} else if (type == JSONTypes.ARRAY) {
			write(w, e.getArray());
		} else if (type == JSONTypes.STRING) {
			w.write("\"" + e.getString() + "\"");
		} else if (type == JSONTypes.NUMBER) {
			w.write(e.getNumber().toPlainString());
		} else if (type == JSONTypes.BOOLEAN) {
			w.write(e.getBoolean().toString());
		} else if (type == JSONTypes.BINARY) {
			w.write("{\"" + type.getKey() + "\":\"");
			byte[] bytes = e.getBinary();
			for (byte b : bytes) {
				w.write(Strings.leftPad(Integer.toString(b, 16), 2, "0"));
			}
			w.write("\"}");
		} else if (type == JSONTypes.DATE) {
			w.write("{\"" + type.getKey() + "\":");
			w.write("\"" + e.getDate().toString() + "\"");
			w.write("}");
		} else if (type == JSONTypes.TIME) {
			w.write("{\"" + type.getKey() + "\":");
			w.write("\"" + e.getTime().toString() + "\"");
			w.write("}");
		} else if (type == JSONTypes.TIMESTAMP) {
			w.write("{\"" + type.getKey() + "\":");
			w.write("\"" + e.getTimestamp().toString() + "\"");
			w.write("}");
		}
	}

	/**
	 * Returns the JSON array as a string.
	 *
	 * @param arr The JSON array.
	 * @return The string representation.
	 */
	public String toString(JSONArray arr) {
		try {
			StringWriter w = new StringWriter();
			write(w, arr);
			return w.toString();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the JSON object as a string.
	 *
	 * @param obj The JSON object.
	 * @return The string representation.
	 */
	public String toString(JSONObject obj) {
		try {
			StringWriter w = new StringWriter();
			write(w, obj);
			return w.toString();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return null;
	}
}
