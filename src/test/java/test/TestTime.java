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

package test;

import msfx.lib.util.Strings;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;

public class TestTime {
	public static void main(String[] args) {
		LocalDateTime time = LocalDateTime.ofEpochSecond(
				System.currentTimeMillis() / 1000, 0, ZoneOffset.ofHours(2));
		System.out.println(time);

		Duration duration = Duration.ofSeconds(900);
//		System.out.println(duration);
//		System.out.println(duration.toDaysPart());
//		System.out.println(duration.toHoursPart());
//		System.out.println(duration.toMinutesPart());
//		System.out.println(duration.toSecondsPart());
		System.out.println(Strings.toString(duration, false, true));
		System.out.println(System.currentTimeMillis());

		System.out.println(new BigDecimal("0.0001").scale());

	}
}
