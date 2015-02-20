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
 * <li>Subtraction.</li>
 * </ul>
 */
abstract class CSubtract {

	/**
	 * Subtract two numbers.
	 */
	public static void subtract (
		KCalculator calc,
		KNumRegister result,
		KNumRegister minuend,
		KNumRegister subtrahend) {

		int route = KProfile.route[minuend.profile][subtrahend.profile];

		switch (route) {
		default:
			// go to end
			break;

		case KProfile._LONG_RAT_:
			if (calc.multiply (minuend.numerator, subtrahend.denominator)) {
				long n1_mul_d2 = calc.intResult;

				if (calc.multiply (subtrahend.numerator, minuend.denominator)) {

					if (calc.subtract (n1_mul_d2, calc.intResult)) {
						long numerator = calc.intResult;

						if (calc.multiply (minuend.denominator, subtrahend.denominator)) {
							result.setValue (
								numerator,
								calc.intResult
							);
							return;
						}
					}
				}
			}
			route = KProfile._BIG__RAT_;
			break;

		case KProfile._LONG_INT1:
			if (calc.multiply (minuend.numerator, subtrahend.denominator)) {

				if (calc.subtract (calc.intResult, subtrahend.numerator)) {
					result.setValue (
						calc.intResult,
						subtrahend.denominator
					);
					return;
				}
			}
			route = KProfile._BIG__INT1;
			break;

		case KProfile._LONG_INT2:
			if (calc.multiply (subtrahend.numerator, minuend.denominator)) {

				if (calc.subtract (minuend.numerator, calc.intResult)) {
					result.setValue (
						calc.intResult,
						minuend.denominator
					);
					return;
				}
			}
			route = KProfile._BIG__INT2;
			break;

		case KProfile._LONG_INT_:
			if (calc.subtract (minuend.numerator, subtrahend.numerator)) {
				result.setValue (
					calc.intResult
				);
				return;
			}
			route = KProfile._BIG__INT_;
			break;

		case KProfile._INT__RAT_:
			result.setValue (
				minuend.numerator * subtrahend.denominator
					- subtrahend.numerator * minuend.denominator,
				minuend.denominator * subtrahend.denominator
			);
			return;

		case KProfile._INT__INT_:
			result.setValue (
				minuend.numerator - subtrahend.numerator
			);
			return;
		}

		// fallback

		minuend.setBigIntegers ();
		subtrahend.setBigIntegers ();

		switch (route) {
		case KProfile._BIG__RAT_:
			result.setValue (
				minuend.bigNumerator.multiply (subtrahend.bigDenominator)
					.subtract (
						subtrahend.bigNumerator.multiply (minuend.bigDenominator)),
				minuend.bigDenominator.multiply (subtrahend.bigDenominator)
			);
			break;

		case KProfile._BIG__INT1:
			result.setValue (
				minuend.bigNumerator.multiply (subtrahend.bigDenominator)
					.subtract (subtrahend.bigNumerator),
				subtrahend.bigDenominator
			);
			break;

		case KProfile._BIG__INT2:
			result.setValue (
				minuend.bigNumerator
					.subtract (
						subtrahend.bigNumerator.multiply (minuend.bigDenominator)),
				minuend.bigDenominator
			);
			break;

		case KProfile._BIG__INT_:
			result.setValue (
				minuend.bigNumerator
					.subtract (subtrahend.bigNumerator)
			);
			break;
		}
	}

}
