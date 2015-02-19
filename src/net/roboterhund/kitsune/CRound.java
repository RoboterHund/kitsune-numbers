/*
 Copyright 2015 RoboterHund87

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package net.roboterhund.kitsune;

import java.math.BigInteger;

/**
 * Operations:
 * <ul>
 * <li>Integer.</li>
 * <li>Floor.</li>
 * <li>Ceiling.</li>
 * </ul>
 */
abstract class CRound {

	/**
	 * Get the integer part of a number.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public static void truncate (
		KNumRegister result,
		KNumRegister number) {

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
			number.setBigIntegers ();
			result.setValue (
				number.bigNumerator.divide (number.bigDenominator)
			);
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			result.setValue (
				number.numerator / number.denominator
			);
			break;

		default:
			if (result != number) {
				result.copy (number);
			}
			break;
		}
	}

	/**
	 * Get {@code floor (number)}.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public static void floor (
		KNumRegister result,
		KNumRegister number) {

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
			number.setBigIntegers ();
			BigInteger bigValue =
				number.bigNumerator.divide (number.bigDenominator);
			if (bigValue.signum () < 0) {
				bigValue = bigValue.subtract (BigInteger.ONE);
			}
			result.setValue (
				bigValue
			);
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			long longValue = number.numerator / number.denominator;
			if (longValue < 0) {
				--longValue;
			}
			result.setValue (
				longValue
			);
			break;

		default:
			if (result != number) {
				result.copy (number);
			}
			break;
		}
	}

	/**
	 * Get {@code ceiling (number)}.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public static void ceiling (
		KNumRegister result,
		KNumRegister number) {

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
			number.setBigIntegers ();
			BigInteger bigValue =
				number.bigNumerator.divide (number.bigDenominator);
			if (bigValue.signum () > 0) {
				bigValue = bigValue.add (BigInteger.ONE);
			}
			result.setValue (
				bigValue
			);
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			long longValue = number.numerator / number.denominator;
			if (longValue > 0) {
				++longValue;
			}
			result.setValue (
				longValue
			);
			break;

		default:
			if (result != number) {
				result.copy (number);
			}
			break;
		}
	}

}
