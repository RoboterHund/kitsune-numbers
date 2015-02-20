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

/**
 * Operations:
 * <ul>
 * <li>Absolute value.</li>
 * <li>Negate.</li>
 * <li>Inverse (reciprocal).</li>
 * </ul>
 */
abstract class CInvert {

	/**
	 * Get absolute value.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public static void abs (
		KNumRegister result,
		KNumRegister number) {

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
		case KProfile.BIG_INTEGER:
			result.profile = number.profile;
			result.bigNumerator = number.bigNumerator.abs ();
			result.bigDenominator = number.bigDenominator;
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.LONG_INTEGER:
		case KProfile.INT_RATIONAL:
		case KProfile.INT_INTEGER:
			result.setIrreducibleFraction (
				Math.abs (number.numerator),
				number.denominator
			);
			break;
		}
	}

	/**
	 * Negate (additive inverse).
	 */
	static void negate (
		KNumRegister result,
		KNumRegister number) {

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
		case KProfile.BIG_INTEGER:
			result.profile = number.profile;
			result.bigNumerator = number.bigNumerator.negate ();
			result.bigDenominator = number.bigDenominator;
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.LONG_INTEGER:
		case KProfile.INT_RATIONAL:
		case KProfile.INT_INTEGER:
			result.setIrreducibleFraction (
				-number.numerator,
				number.denominator
			);
			break;
		}
	}

	/**
	 * Multiplicative inverse (reciprocal).
	 */
	static void inverse (
		KNumRegister result,
		KNumRegister number) {

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
		case KProfile.BIG_INTEGER:
			result.setIrreducibleValue (
				number.bigDenominator,
				number.bigNumerator,
				false
			);
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.LONG_INTEGER:
		case KProfile.INT_RATIONAL:
		case KProfile.INT_INTEGER:
			result.setIrreducibleValue (
				number.denominator,
				number.numerator
			);
			break;
		}
	}

}
