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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.TimeZone;

public class TestDateTime {
	public static void main(String[] args) {
		LocalDateTime dt1 = LocalDateTime.of(2023, 5, 8, 0, 30);
		LocalDateTime dt2 = LocalDateTime.of(2023, 9, 24, 12, 25);
		Period period = Period.between(dt1.toLocalDate(), dt2.toLocalDate());
		Duration duration = Duration.between(dt1.toLocalTime(), dt2.toLocalTime());
		System.out.println(period + " - " + duration);
		System.out.println(TimeZone.getDefault().getRawOffset());
		System.out.println(TimeZone.getDefault().getOffset(System.currentTimeMillis()));
		System.out.println(ZoneOffset.systemDefault());
	}
}
