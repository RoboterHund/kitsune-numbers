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
 * <li>Comparison.</li>
 * </ul>
 */
abstract class CCompare {

	/**
	 * Compare two numbers.
	 * <p>
	 * Result of {@code compare (n1, n2)}:
	 * <ul>
	 * <li>{@code result <  0  =>  (n1  < n2)}</li>
	 * <li>{@code result >  0  =>  (n1  > n2)}</li>
	 * <li>{@code result <= 0  =>  (n1 <= n2)}</li>
	 * <li>{@code result >= 0  =>  (n1 >= n2)}</li>
	 * <li>{@code result == 0  =>  (n1 == n2)}</li>
	 * <li>{@code result != 0  =>  (n1 != n2)}</li>
	 * </ul>
	 */
	public static long compare (
		KCalculator calc,
		KNumRegister number_1,
		KNumRegister number_2) {

		int route = KProfile.route[number_1.profile][number_2.profile];

		switch (route) {
		default:
			// go to end
			break;

		case KProfile._LONG_RAT_:
			if (calc.multiply (number_1.numerator, number_2.denominator)) {
				long n1_mul_n2 = calc.intResult;

				if (calc.multiply (number_2.numerator, number_1.denominator)) {

					if (calc.subtract (n1_mul_n2, calc.intResult)) {
						return calc.intResult;
					}
				}
			}
			route = KProfile._BIG__RAT_;
			break;

		case KProfile._LONG_INT1:
			if (calc.multiply (number_1.numerator, number_2.denominator)) {

				if (calc.subtract (calc.intResult, number_2.numerator)) {
					return calc.intResult;
				}
			}
			route = KProfile._BIG__INT1;
			break;

		case KProfile._LONG_INT2:
			if (calc.multiply (number_2.numerator, number_1.denominator)) {

				if (calc.subtract (number_1.numerator, calc.intResult)) {
					return calc.intResult;
				}
			}
			route = KProfile._BIG__INT2;
			break;

		case KProfile._INT__RAT_:
			return number_1.numerator * number_2.denominator
				- number_2.numerator * number_1.denominator;

		case KProfile._LONG_INT_:
		case KProfile._INT__INT_:
			return number_1.numerator - number_2.numerator;
		}

		// fallback

		number_1.setBigIntegers ();
		number_2.setBigIntegers ();

		if (route == KProfile._BIG__INT_) {
			return number_1.bigNumerator
				.compareTo (
					number_2.bigNumerator
				);
		}

		int signumDiff =
			number_1.bigNumerator.signum ()
				- number_2.bigNumerator.signum ();

		if (signumDiff != 0) {
			return signumDiff;
		}

		switch (route) {
		case KProfile._BIG__RAT_:
			return
				number_1.bigNumerator.multiply (number_2.bigDenominator)
					.compareTo (
						number_2.bigNumerator.multiply (number_1.bigDenominator)
					);

		case KProfile._BIG__INT1:
			return
				number_1.bigNumerator.multiply (number_2.bigDenominator)
					.compareTo (
						number_2.bigNumerator
					);

		case KProfile._BIG__INT2:
			return
				number_1.bigNumerator
					.compareTo (
						number_2.bigNumerator.multiply (number_1.bigDenominator)
					);
		}

		throw new RuntimeException ("CCompare.compareTo: Invalid route.");
	}

}
