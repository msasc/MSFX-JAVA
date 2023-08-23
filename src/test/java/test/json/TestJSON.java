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

package test.json;

import msfx.lib.json.JSONArray;
import msfx.lib.json.JSONObject;

import java.time.LocalDate;

public class TestJSON {

	public static void main(String[] args) {
		JSONObject o1 = new JSONObject();
		o1.put("name", "Miquel Sas");
		o1.put("age", 65);
		o1.put("birthday", LocalDate.of(1958, 5, 8));
		o1.put("balance", 534000.50);

		JSONArray a1 = new JSONArray();
		a1.add(25);
		a1.add(35);
		a1.add(45);
		a1.add(55);
		o1.put("numbers", a1);

		JSONObject o2 = JSONObject.parse(o1.toString());


		System.out.println(o1);
		System.out.println(o2.toString());
		System.out.println(o1.toString(true));

		System.out.println("Wow this is fine");
	}
}
