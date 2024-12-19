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

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * JSON parser.
 *
 * @author Miquel Sas
 */
public class JSONParser {

	/**
	 * Lists the kinds of tokens.
	 */
	private enum Kind { EOF, STRUCT, VALUE }

	/**
	 * Tokens retrieved by the parser.
	 *
	 * @param kind  Kind of token.
	 * @param type  Type of the value.
	 * @param value Value.
	 */
	private record Token(Kind kind, JSONTypes type, Object value) {

		/**
		 * Return a string representation.
		 *
		 * @return The string representation.
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(kind);
			b.append(", ");
			b.append(type);
			b.append(", ");
			b.append(value);
			return b.toString();
		}
	}

	/** Format error message. */
	private static final String FMT_ERR = "Format error";

	/** Reader. */
	private Reader reader;
	/** Document to fill with parsed data. */
	private Token nextToken;

	/**
	 * Constructor.
	 */
	public JSONParser() { }

	/**
	 * Parse the source reader and return the result JSON object.
	 *
	 * @param r The source reader.
	 * @return The parsed document.
	 * @throws IOException If an IO or format error occurs.
	 */
	public JSONObject parse(Reader r) throws IOException {
		reader = r;
		nextToken = null;

		/* Read the first token, it must be the doc start structural char. */
		Token token = nextToken();
		if (token.kind == Kind.EOF) return new JSONObject();
		if (token.kind != Kind.STRUCT) throw new IOException(FMT_ERR);
		if (!token.value.equals("{")) throw new IOException(FMT_ERR);

		/* Parse and return the document. */
		return parseObject();
	}

	/**
	 * Parse and return the incoming JSON object.
	 *
	 * @return The parsed document.
	 * @throws IOException If an error occurs.
	 */
	private JSONObject parseObject() throws IOException {

		JSONObject obj = new JSONObject();
		Token token;

		while (true) {

			/* Read the key. Must be VALUE and STRING. */
			token = nextToken();
			if (token.kind != Kind.VALUE) throw new IOException(FMT_ERR);
			if (token.type != JSONTypes.STRING) throw new IOException(FMT_ERR);
			String key = token.value.toString();

			/* Read the structural char ':'. Any other is an error. */
			token = nextToken();
			if (token.kind != Kind.STRUCT) throw new IOException(FMT_ERR);
			if (!token.value.equals(":")) throw new IOException(FMT_ERR);

			/*
			 * Read next token and analyze. Accepted tokens are values or the structural
			 * chars '{' or '['.
			 */
			token = nextToken();
			if (token.kind == Kind.EOF) throw new IOException(FMT_ERR);

			/* A value, put it into the object. */
			if (token.kind == Kind.VALUE) {
				obj.put(key, new JSONEntry(token.type, token.value));
			} else if (token.kind == Kind.STRUCT) {
				/* Start of a JSON object. */
				if (token.value.equals("{")) {
					JSONObject nextObj = parseObject();
					JSONEntry entry_ext = getExtendedTypeEntry(nextObj);
					if (entry_ext != null)
						obj.put(key, entry_ext);
					else
						obj.put(key, nextObj);
				}
				/* Start of a JSON array. */
				if (token.value.equals("[")) {
					JSONArray arr = parseArray();
					obj.put(key, arr);
				}
			}

			/* Read next token. */
			token = nextToken();
			if (token.kind == Kind.EOF) throw new IOException(FMT_ERR);
			if (token.value.equals(",")) continue;
			if (token.value.equals("}")) break;
		}

		return obj;
	}

	/**
	 * Parse the incoming JSON array.
	 *
	 * @return The array.
	 * @throws IOException If an error occurs.
	 */
	private JSONArray parseArray() throws IOException {
		JSONArray arr = new JSONArray();
		Token token;

		while (true) {

			/* Read the next token. Must be STRUCT or value. */
			token = nextToken();
			if (token.kind == Kind.EOF) throw new IOException(FMT_ERR);

			/* Token is a value. */
			if (token.kind == Kind.VALUE) {
				arr.add(new JSONEntry(token.type, token.value));
			}

			/* Token is a structural character. */
			if (token.kind == Kind.STRUCT) {
				/* Start of a JSON object. */
				if (token.value.equals("{")) {
					JSONObject nextObj = parseObject();
					JSONEntry entry_ext = getExtendedTypeEntry(nextObj);
					if (entry_ext != null)
						arr.add(entry_ext);
					else
						arr.add(nextObj);
				}
				/* Start of a JSON array. */
				if (token.value.equals("[")) {
					JSONArray nextArr = parseArray();
					arr.add(nextArr);
				}
			}

			/* Read next token. */
			token = nextToken();
			if (token.kind == Kind.EOF) throw new IOException(FMT_ERR);
			if (token.value.equals(",")) continue;
			if (token.value.equals("]")) break;
		}

		return arr;
	}

	/**
	 * If required reads tokens from the stream and return the next token.
	 *
	 * @return The next token.
	 * @throws IOException If an error occurs.
	 */
	private Token nextToken() throws IOException {

		/* If the queue of tokens is not empty, return the first one. */
		if (nextToken != null) {
			Token token = nextToken;
			nextToken = null;
			return token;
		}

		/* Read the next neat char. */
		int c = nextChar(true);

		/* -1, end of stream. */
		if (c < 0) {
			return new Token(Kind.EOF, null, null);
		}

		/* Structural char. */
		if ("[]{}:,".indexOf(c) >= 0) {
			return new Token(Kind.STRUCT, JSONTypes.STRING, String.valueOf((char) c));
		}

		/*
		 * '"', start of a string or key. Read chars up to the end of the string and
		 * then check whether it could be a date, time, timestamp or a string.
		 */
		if (c == '\"') {
			String str = readString();
			return new Token(Kind.VALUE, JSONTypes.STRING, str);
		}

		/*
		 * Analyze true, false, null or number. Although at this point we do not know if
		 * the token is a KEY or a VALUE, we may infer it is a VALUE if it is not a
		 * format error.
		 */

		/* 't' possible start of the token 'true'. */
		if (c == 't') {
			StringBuilder b = new StringBuilder();
			b.append((char) c);
			for (int i = 0; i < 3; i++) {
				b.append((char) nextChar(false));
			}
			if (b.toString().equals("true")) {
				return new Token(Kind.VALUE, JSONTypes.BOOLEAN, true);
			}
			throw new IOException(FMT_ERR);
		}

		/* 'f' possible start of the token false. */
		if (c == 'f') {
			StringBuilder b = new StringBuilder();
			b.append((char) c);
			for (int i = 0; i < 4; i++) {
				b.append((char) nextChar(false));
			}
			if (b.toString().equals("false")) {
				return new Token(Kind.VALUE, JSONTypes.BOOLEAN, false);
			}
			throw new IOException(FMT_ERR);
		}

		/* 'n' possible start of the token null. */
		if (c == 'n') {
			StringBuilder b = new StringBuilder();
			b.append((char) c);
			for (int i = 0; i < 3; i++) {
				b.append((char) nextChar(false));
			}
			if (b.toString().equals("null")) {
				return new Token(Kind.VALUE, JSONTypes.NULL, null);
			}
			throw new IOException(FMT_ERR);
		}

		/* Next token must be a number. */
		if ("-0123456789".indexOf(c) >= 0) {
			StringBuilder b = new StringBuilder();
			b.append((char) c);
			while (true) {
				c = nextChar(true);
				if (c <= 32) throw new IOException(FMT_ERR);
				if ("+-.0123456789eE".indexOf(c) >= 0) {
					b.append((char) c);
					continue;
				}
				if (":,]}".indexOf(c) >= 0) {
					try {
						BigDecimal dec = new BigDecimal(b.toString());
						nextToken = new Token(Kind.STRUCT, JSONTypes.STRING,
								String.valueOf((char) c));
						return new Token(Kind.VALUE, JSONTypes.NUMBER, dec);
					} catch (NumberFormatException exc) {
						throw new IOException(FMT_ERR);
					}
				}
			}
		}
		throw new IOException(FMT_ERR);
	}

	/**
	 * Check whether the object is an extended type entry.
	 *
	 * @param obj The JSON object.
	 * @return The extended type entry or null.
	 */
	private JSONEntry getExtendedTypeEntry(JSONObject obj) {

		/* One and only one entry. */
		if (obj.size() != 1) return null;

		/* Check whether the key is the key of an extended type. */
		String key = obj.keys().iterator().next();
		JSONTypes type = null;
		for (JSONTypes t : JSONTypes.EXTENDED_TYPES) {
			if (t.getKey().equals(key)) {
				type = t;
				break;
			}
		}
		if (type == null) return null;

		/* The object entry must be string or null. */
		JSONEntry entry_obj = obj.get(key);
		JSONTypes type_obj = entry_obj.getType();
		if (type_obj != JSONTypes.STRING && type_obj != JSONTypes.NULL) return null;

		/* If it is null, it is correct, extended types accept null values. */
		if (type_obj == JSONTypes.NULL) return new JSONEntry();

		/* Must validate that the string value complies with the type. */
		String str = entry_obj.getString();

		/*
		 * Type binary, the string must have an even number of characters and each pair
		 * must be parsed base 16 to a number from 0 to 127.
		 */
		if (type == JSONTypes.BINARY) {
			if (str.length() % 2 != 0) return null;
			int size = str.length() / 2;
			byte[] bytes = new byte[size];
			for (int i = 0; i < size; i++) {
				int index = i * 2;
				String pair = str.substring(index, index + 2);
				int value = -1;
				try {
					value = Integer.parseInt(pair, 16);
				} catch (NumberFormatException ignore) {
				}
				if (value < 0 || value > 127) return null;
				bytes[i] = (byte) value;
			}
			return new JSONEntry(bytes);
		}

		/* Type date, let LocalDate.parse do the job. */
		if (type == JSONTypes.DATE) {
			LocalDate date = null;
			try {
				date = LocalDate.parse(str);
			} catch (DateTimeParseException ignore) {
			}
			if (date == null) return null;
			return new JSONEntry(date);
		}
		/* Type time, let LocalTime.parse do the job. */
		if (type == JSONTypes.TIME) {
			LocalTime time = null;
			try {
				time = LocalTime.parse(str);
			} catch (DateTimeParseException ignore) {
			}
			if (time == null) return null;
			return new JSONEntry(time);
		}
		/* Type timestamp, let LocalDateTime.parse do the job. */
		if (type == JSONTypes.TIMESTAMP) {
			LocalDateTime timestamp = null;
			try {
				timestamp = LocalDateTime.parse(str);
			} catch (DateTimeParseException ignore) {
			}
			if (timestamp == null) return null;
			return new JSONEntry(timestamp);
		}

		/* The string does not comply with the type. */
		return null;
	}

	/**
	 * Returns the next char. If the argument neat is set to true, then the next
	 * char GT 32 will be returned. Returns -1 if the end of the stream is reached.
	 *
	 * @param neat A boolean that indicates whether next char should be neat, that
	 *             is, GT 32.
	 * @return The next char, whether neat or not, -1 if the end of the stream is
	 * reached.
	 * @throws IOException If an error occurs.
	 */
	private int nextChar(boolean neat) throws IOException {
		while (true) {
			int c = reader.read();
			if (!neat) return c;
			if (c < 0 || c > 32) return c;
		}
	}

	/**
	 * Reads the next string, may be empty.
	 *
	 * @return The read string.
	 * @throws IOException If an error occurs.
	 */
	private String readString() throws IOException {
		StringBuilder b = new StringBuilder();
		while (true) {
			int c = nextChar(false);

			/*
			 * -1, reached end of stream before the string is closed, it is a format error.
			 * Can not be 0 to 31, these must be unicode escaped.
			 */
			if (c <= 31) throw new IOException(FMT_ERR);

			/* '"' The end of the string has been reached. */
			if (c == '\"') break;

			/*
			 * '\' escape indicator, read the next char to see whether it is a two-character
			 * escape sequence or the start (u) of a potential unicode escape.
			 */
			if (c == '\\') {
				int n = nextChar(false);
				if (n < 0) throw new IOException(FMT_ERR);

				/* Two-character escape sequences. */
				switch (n) {
				case '\"', '\\', '\b', '\f', '\n', '\r', '\t' -> {
					b.append((char) c);
					continue;
				}
				}

				/*
				 * Potential unicode escape. Read 4 more chars, that must be digits or letters a
				 * to f, or A to F. Any other char is a format error.
				 */
				if (n == 'u') {
					StringBuilder u = new StringBuilder();
					for (int i = 0; i < 4; i++) {
						int m = nextChar(false);
						if ("0123456789abcdefABCDEF".indexOf(m) >= 0) u.append((char) m);
						else throw new IOException(FMT_ERR);
					}
					b.append((char) Integer.parseInt(u.toString(), 16));
					continue;
				}
				throw new IOException(FMT_ERR);
			}

			/* Valid non escaped char, just accept. */
			b.append((char) c);
		}
		return b.toString();
	}
}
