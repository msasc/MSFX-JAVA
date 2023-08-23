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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Number utilities.
 *
 * @author Miquel Sas
 */
public class Numbers {

	/** Maximum positive double. */
	public static final double MAX_DOUBLE = Double.MAX_VALUE;
	/** Minimum negative double. */
	public static final double MIN_DOUBLE = -Double.MAX_VALUE;
	/** Maximum positive integer. */
	public static final int MAX_INTEGER = Integer.MAX_VALUE;
	/** Minimum negative integer. */
	public static final int MIN_INTEGER = -Integer.MAX_VALUE;

	/** Too small of a number. */
	public static final double TOO_SMALL = -1.0E50;
	/** Too big of a number. */
	public static final double TOO_BIG = 1.0E50;

	/**
	 * Minimum absolute value to consider two values to be equal. Absolute values
	 * less equal this minimum are considered to be equal.
	 */
	public static final double MINIMUM_EQUAL = 1.0e-12;

	/**
	 * Return the argument number bounded to the minimum and maximum accepted.
	 * @param value The value to bound.
	 * @return The bounded value.
	 */
	public static double bound(double value) { return bound(value, TOO_SMALL, TOO_BIG); }
	/**
	 * Return the argument number bounded to the minimum and maximum accepted.
	 * @param value The value to bound.
	 * @param tooSmall The smallest accepted value.
	 * @param tooBig The biggest accepted value.
	 * @return The bounded value.
	 */
	public static double bound(double value, double tooSmall, double tooBig) {
		if (value < tooSmall || value == Double.NEGATIVE_INFINITY) return tooSmall;
		else if (value > tooBig || value == Double.POSITIVE_INFINITY) return tooBig;
		else return value;
	}

	/**
	 * Compare two numbers for order.
	 * @param n1 First number to compare.
	 * @param n2 Second number to compare.
	 * @return The standard comparison integer.
	 */
	public static int compare(Number n1, Number n2) {
		BigDecimal b1 = new BigDecimal(n1.toString());
		BigDecimal b2 = new BigDecimal(n2.toString());
		return b1.compareTo(b2);
	}

	/**
	 * @param base The base.
	 * @param compare The compare.
	 * @return The unitary delta (increase/decrease) value of the base versus the
	 *         compare.
	 */
	public static double delta(double base, double compare) {
		if (compare == 0) return 0;
		return ((base / compare) - 1);
	}
	/**
	 * Compares two number for equality.
	 * @param n1 First number.
	 * @param n2 Second number.
	 * @return A boolean indicating whether they are equal.
	 */
	public static boolean equals(Number n1, Number n2) {
		return compare(n1, n2) == 0;
	}

	/**
	 * @param base The base.
	 * @param compare The compare.
	 * @return The relative value of the base versus the compare.
	 */
	public static double factor(double base, double compare) {
		if (compare == 0) return 0;
		return (base / compare);
	}
	/**
	 * Returns the floor number to the given decimal places. The decimal places can be negative.
	 * @param number The source number.
	 * @param decimals The number of decimal places.
	 * @return The floor.
	 */
	public static double floor(double number, int decimals) {
		double pow = number * Math.pow(10, decimals);
		double floor = Math.floor(pow);
		double value = floor / Math.pow(10, decimals);
		return value;
	}

	/**
	 * Returns the big decimal for the value and scale.
	 * @param value The value.
	 * @param decimals The number of decimal places.
	 * @return The big decimal.
	 */
	public static BigDecimal getBigDecimal(double value, int decimals) {
		return new BigDecimal(value).setScale(decimals, RoundingMode.HALF_UP);
	}

	/**
	 * Returns the big decimal for the value and scale.
	 * @param number The number to transform.
	 * @param decimals Decimal places.
	 * @return The big decimal.
	 */
	public static BigDecimal getBigDecimal(Number number, int decimals) {
		if (number == null) throw new NullPointerException();
		return getBigDecimal(number.doubleValue(), decimals);
	}

	/**
	 * Returns the number of integer digits of a number.
	 * @param number The number to check.
	 * @return The number of integer digits.
	 */
	public static int getDigits(double number) {
		String str = new BigDecimal(number).toPlainString();
		int index = str.indexOf('.');
		if (index <= 0) return str.length();
		return index;
	}

	/**
	 * Check in the list.
	 * @param value The value to check.
	 * @param values The list of values.
	 * @return A boolean.
	 */
	public static boolean in(double value, double... values) {
		for (double v : values) {
			if (v == value) return true;
		}
		return false;
	}
	/**
	 * Check in the list.
	 * @param value The value to check.
	 * @param values The list of values.
	 * @return A boolean.
	 */
	public static boolean in(int value, int... values) {
		for (int v : values) {
			if (v == value) return true;
		}
		return false;
	}

	/**
	 * Check if the number is even.
	 * @param d The number.
	 * @return A boolean.
	 */
	public static boolean isEven(double d) { return (d % 2 == 0); }
	/**
	 * Check if the number is even.
	 * @param l The number.
	 * @return A boolean.
	 */
	public static boolean isEven(long l) { return (l % 2 == 0); }
	/**
	 * Check if the number is odd.
	 * @param d The number.
	 * @return A boolean.
	 */
	public static boolean isOdd(double d) { return !isEven(d); }
	/**
	 * Check if the number is odd.
	 * @param l The number.
	 * @return A boolean.
	 */
	public static boolean isOdd(long l) { return !isEven(l); }
	/**
	 * Return the maximum.
	 * @param nums List of numbers.
	 * @return The maximum.
	 */
	public static double max(double... nums) {
		double max = MIN_DOUBLE;
		for (double num : nums) {
			if (num > max) max = num;
		}
		return max;
	}
	/**
	 * Return the maximum.
	 * @param nums List of numbers.
	 * @return The maximum.
	 */
	public static int max(int... nums) {
		int max = MIN_INTEGER;
		for (int num : nums) {
			if (num > max) max = num;
		}
		return max;
	}
	/**
	 * @param base The base.
	 * @param compare The compare.
	 * @return The percentage of the base versus the compare.
	 */
	public static double percentDelta(double base, double compare) {
		return 100.0 * delta(base, compare);
	}

	/**
	 * Returns the remainder of the division of two integers.
	 * @param num The numerator.
	 * @param den The denominator.
	 * @return The remainder.
	 */
	public static int remainder(int num, int den) { return num % den; }

	/**
	 * Round a number (in mode that most of us were taught in grade school).
	 * @param value The value to round.
	 * @param decimals The number of decimal places.
	 * @return The rounded value.
	 */
	public static double round(double value, int decimals) {
		return new BigDecimal(value).setScale(decimals, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Returns the sign of the evolution of a number, previous and next. If both are less equal the
	 * MINIMUM_EQUAL, the sign is considered 0, if both are positive or both are negative, the sign
	 * is 1, that is, both follow the same direction, while otherwise, when one is positive and the
	 * other is negative, the sign is -1, indicating that they follow different directions.
	 * 
	 * @param prev Previous value.
	 * @param next Next value.
	 * @return The sign of the evolution of the value.
	 */
	public static int sign(double prev, double next) {
		int sign = -1;
		if (Math.abs(prev) <= MINIMUM_EQUAL && Math.abs(next) <= MINIMUM_EQUAL) {
			sign = 0;
		} else if (prev > 0 && next > 0) {
			sign = 1;
		} else if (prev < 0 && next < 0) {
			sign = 1;
		}
		return sign;
	}
	
	/**
	 * Returns an integer array from a list of integers.
	 * @param values The list of integer values.
	 * @return The int[]
	 */
	public static int[] toIntArray(List<Integer> values) {
		int[] arr = new int[values.size()];
		for (int i = 0; i < values.size(); i++) { arr[i] = values.get(i); }
		return arr;
	}
}
