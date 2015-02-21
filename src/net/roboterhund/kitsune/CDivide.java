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

import net.roboterhund.kitsune.KNumRegisterPool.KRegCont;

/**
 * Operations:
 * <ul>
 * <li>Division.</li>
 * <li>Modulo.</li>
 * </ul>
 */
abstract class CDivide {

	/**
	 * Divide two numbers.
	 */
	public static void divide (
		KCalculator calc,
		KNumRegister result,
		KNumRegister dividend,
		KNumRegister divisor) {

		int route = KProfile.route[dividend.profile][divisor.profile];

		switch (route) {
		default:
			// go to end
			break;

		case KProfile._LONG_RAT_:
			if (calc.multiply (dividend.numerator, divisor.denominator)) {
				long numerator = calc.intResult;

				if (calc.multiply (divisor.numerator, dividend.denominator)) {
					result.setValue (
						numerator,
						calc.intResult
					);
					return;
				}
			}
			route = KProfile._BIG__RAT_;
			break;

		case KProfile._LONG_INT1:
			if (calc.multiply (dividend.numerator, divisor.denominator)) {
				result.setValue (
					calc.intResult,
					divisor.numerator
				);
				return;
			}
			route = KProfile._BIG__INT1;
			break;

		case KProfile._LONG_INT2:
			if (calc.multiply (divisor.numerator, dividend.denominator)) {
				result.setValue (
					dividend.numerator,
					calc.intResult
				);
				return;
			}
			route = KProfile._BIG__INT2;
			break;

		case KProfile._LONG_INT_:
			result.setValue (
				dividend.numerator,
				divisor.numerator
			);
			return;

		case KProfile._INT__RAT_:
			result.setValue (
				dividend.numerator * divisor.denominator,
				divisor.numerator * dividend.denominator
			);
			return;

		case KProfile._INT__INT_:
			result.setValue (
				dividend.numerator,
				divisor.numerator
			);
			return;
		}

		// fallback

		dividend.setBigIntegers ();
		divisor.setBigIntegers ();

		switch (route) {
		case KProfile._BIG__RAT_:
			result.setValue (
				dividend.bigNumerator.multiply (divisor.bigDenominator),
				divisor.bigNumerator.multiply (dividend.bigDenominator)
			);
			break;

		case KProfile._BIG__INT1:
			result.setValue (
				dividend.bigNumerator.multiply (divisor.bigDenominator),
				divisor.bigNumerator
			);
			break;

		case KProfile._BIG__INT2:
			result.setValue (
				dividend.bigNumerator,
				divisor.bigNumerator.multiply (dividend.bigDenominator)
			);
			break;

		case KProfile._BIG__INT_:
			result.setValue (
				dividend.bigNumerator,
				divisor.bigNumerator
			);
			break;
		}
	}

	/**
	 * Get {@code dividend % divisor}.
	 */
	public static void modulo (
		KCalculator calc,
		KNumRegister result,
		KNumRegister dividend,
		KNumRegister divisor) {

		int route = KProfile.route[dividend.profile][divisor.profile];

		switch (route) {
		default:
			// go to end
			break;

		case KProfile._BIG__INT_:
			dividend.setBigIntegers ();
			divisor.setBigIntegers ();
			result.setValue (
				dividend.bigNumerator.mod (divisor.bigNumerator)
			);
			break;

		case KProfile._LONG_INT_:
		case KProfile._INT__INT_:
			result.setValue (
				dividend.numerator % divisor.numerator
			);
			return;
		}

		// fallback

		KNumRegisterPool regPool = calc.regPool;

		KRegCont cont_1 = regPool.get ();
		KNumRegister temp_1 = cont_1.reg;

		// a % b
		// = a - (b * int (a / b))
		CDivide.divide (calc, temp_1, dividend, divisor);
		CRound.truncate (temp_1, temp_1);
		CMultiply.multiply (calc, temp_1, temp_1, divisor);
		CSubtract.subtract (calc, result, dividend, temp_1);

		regPool.discard (cont_1);
	}

	/**
	 * Integer division.
	 * <p>
	 * Optionally store remainder.
	 */
	public static void divideRemainder (
		KCalculator calc,
		KNumRegister result,
		KNumRegister remainder,
		KNumRegister dividend,
		KNumRegister divisor) {

		KNumRegisterPool regPool = calc.regPool;

		KRegCont cont_1 = regPool.get ();
		KNumRegister temp_1 = cont_1.reg;

		KRegCont cont_2 = regPool.get ();
		KNumRegister temp_2 = cont_2.reg;

		// a // b
		// = int (a / b)
		CDivide.divide (calc, temp_1, dividend, divisor);
		CRound.truncate (temp_1, temp_1);

		if (remainder != null) {
			// a % b
			// = a - (b * int (a / b))
			// = a - (b * a // b)
			CMultiply.multiply (calc, temp_2, temp_1, divisor);
			CSubtract.subtract (calc, remainder, dividend, temp_2);
		}

		result.copy (temp_1);

		regPool.discard (cont_1);
		regPool.discard (cont_2);
	}

}
