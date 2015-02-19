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

		if (result != number) {
			result.copy (number);
		}

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
		case KProfile.BIG_INTEGER:
			if (result.bigNumerator.signum () < 0) {
				result.bigNumerator = result.bigNumerator.negate ();
			}
			break;

		case KProfile.LONG_RATIONAL:
			if (result.numerator == Long.MIN_VALUE) {
				result.bigNumerator = KEdges.MIN_LONG_NEGATED;
				result.bigDenominator = BigInteger.valueOf (result.denominator);
				result.profile = KProfile.BIG_RATIONAL;
			} else {
				result.numerator = -result.numerator;
			}
			break;

		case KProfile.LONG_INTEGER:
			if (result.numerator < 0) {
				if (result.numerator == Long.MIN_VALUE) {
					result.bigNumerator = KEdges.MIN_LONG_NEGATED;
					result.bigDenominator = BigInteger.ONE;
					result.profile = KProfile.BIG_INTEGER;
				} else {
					result.numerator = Math.abs (result.numerator);
				}
			}
			break;

		case KProfile.INT_RATIONAL:
		case KProfile.INT_INTEGER:
			if (result.numerator < 0) {
				result.numerator = -result.numerator;
			}
			break;
		}
	}

	// TODO negate (additive inverse)

	// TODO multiplicative inverse (reciprocal)

}
