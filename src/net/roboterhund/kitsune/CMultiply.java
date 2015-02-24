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
 * <li>Multiplication.</li>
 * </ul>
 */
abstract class CMultiply {

	/**
	 * Rational exponent error message.
	 */
	public static final String ERR_MSG_NEGATIVE_BASE =
		"Roots of negative numbers not supported.";

	/**
	 * Multiply two numbers.
	 */
	public static void multiply (
		KCalculator calc,
		KNumRegister result,
		KNumRegister factor_1,
		KNumRegister factor_2) {

		int route = KProfile.route[factor_1.profile][factor_2.profile];

		switch (route) {
		default:
			// go to end
			break;

		case KProfile._LONG_RAT_:
			if (calc.multiply (factor_1.numerator, factor_2.numerator)) {
				long numerator = calc.intResult;

				if (calc.multiply (factor_1.denominator, factor_2.denominator)) {
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
			if (calc.multiply (factor_1.numerator, factor_2.numerator)) {
				result.setValue (
					calc.intResult,
					factor_2.denominator
				);
				return;
			}
			route = KProfile._BIG__INT1;
			break;

		case KProfile._LONG_INT2:
			if (calc.multiply (factor_1.numerator, factor_2.numerator)) {
				result.setValue (
					calc.intResult,
					factor_1.denominator
				);
				return;
			}
			route = KProfile._BIG__INT2;
			break;

		case KProfile._LONG_INT_:
			if (calc.multiply (factor_1.numerator, factor_2.numerator)) {
				result.setValue (
					calc.intResult
				);
				return;
			}
			route = KProfile._BIG__INT_;
			break;

		case KProfile._INT__RAT_:
			result.setValue (
				factor_1.numerator * factor_2.numerator,
				factor_1.denominator * factor_2.denominator
			);
			return;

		case KProfile._INT__INT_:
			result.setValue (
				factor_1.numerator * factor_2.numerator
			);
			return;
		}

		// fallback

		factor_1.setBigIntegers ();
		factor_2.setBigIntegers ();

		switch (route) {
		case KProfile._BIG__RAT_:
			result.setValue (
				factor_1.bigNumerator.multiply (factor_2.bigNumerator),
				factor_1.bigDenominator.multiply (factor_2.bigDenominator)
			);
			break;

		case KProfile._BIG__INT1:
			result.setValue (
				factor_1.bigNumerator.multiply (factor_2.bigNumerator),
				factor_2.bigDenominator
			);
			break;

		case KProfile._BIG__INT2:
			result.setValue (
				factor_1.bigNumerator.multiply (factor_2.bigNumerator),
				factor_1.bigDenominator
			);
			break;

		case KProfile._BIG__INT_:
			result.setValue (
				factor_1.bigNumerator.multiply (factor_2.bigNumerator)
			);
			break;
		}
	}

	/**
	 * Exponentiation.
	 */
	public static void power (
		KCalculator calc,
		KNumRegister result,
		KNumRegister base,
		KNumRegister exponent,
		KNumRegister maxError) {

		switch (exponent.profile) {
		case KProfile.BIG_RATIONAL:
		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			// rational exponent
			if (base.profile == KProfile.INT_RATIONAL
				&& base.numerator == 0) {
				// 0
				result.setZeroValue ();
				return;
			}

			KNumRegisterPool regPool = calc.regPool;

			KRegCont cont_1 = regPool.get ();
			KRegCont cont_2 = regPool.get ();
			KRegCont cont_3 = regPool.get ();

			KNumRegister raiseExponent = cont_1.reg;
			KNumRegister rootExponent = cont_2.reg;
			KNumRegister temp_1 = cont_3.reg;

			// TODO implement "timeout" mechanism
			CRound.split (raiseExponent, rootExponent, exponent);
			CMultiply.exponential (calc, temp_1, base, raiseExponent);
			CMultiply.principalRoot (calc, result, temp_1, rootExponent, maxError);

			regPool.discard (cont_1);
			regPool.discard (cont_2);
			regPool.discard (cont_3);
			break;

		case KProfile.BIG_INTEGER:
		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			// integer exponent
			exponential (calc, result, base, exponent);
			break;
		}
	}

	/**
	 * Raise base to integer root.
	 */
	private static void exponential (
		KCalculator calc,
		KNumRegister result,
		KNumRegister base,
		KNumRegister exponent) {

		KNumRegisterPool regPool = calc.regPool;

		KRegCont cont_1 = regPool.get ();
		KRegCont cont_2 = regPool.get ();
		KRegCont cont_3 = regPool.get ();
		KRegCont cont_4 = regPool.get ();
		KRegCont cont_5 = regPool.get ();
		KRegCont cont_6 = regPool.get ();
		KRegCont cont_7 = regPool.get ();

		KNumRegister zero = cont_1.reg;
		KNumRegister one = cont_2.reg;
		KNumRegister two = cont_3.reg;

		KNumRegister raised = cont_4.reg;
		KNumRegister exp = cont_5.reg;
		KNumRegister multiplier = cont_6.reg;
		KNumRegister expModulo = cont_7.reg;

		zero.setZeroValue ();
		one.setValue (1);
		two.setValue (2);

		raised.setValue (1);

		boolean expNegative =
			CCompare.compare (calc, exponent, zero) < 0;
		if (expNegative) {
			CInvert.negate (exp, exponent);
		} else {
			exp.copy (exponent);
		}

		multiplier.copy (base);

		// TODO check for faster way
		if (CCompare.compare (calc, exp, zero) > 0) {
			while (true) {
				CDivide.divideRemainder (calc, exp, expModulo, exp, two);
				if (CCompare.compare (calc, expModulo, zero) != 0) {
					CMultiply.multiply (calc, raised, raised, multiplier);
				}
				if (CCompare.compare (calc, exp, zero) == 0) {
					break;
				}
				CMultiply.multiply (calc, multiplier, multiplier, multiplier);
			}
		}

		if (expNegative) {
			CInvert.inverse (result, raised);
		} else {
			result.copy (raised);
		}

		regPool.discard (cont_1);
		regPool.discard (cont_2);
		regPool.discard (cont_3);
		regPool.discard (cont_4);
		regPool.discard (cont_5);
		regPool.discard (cont_6);
		regPool.discard (cont_7);
	}

	/**
	 * Compute principal root.
	 * <p>
	 * {@code rootIndex} must be a positive integer.
	 */
	private static void principalRoot (
		KCalculator calc,
		KNumRegister result,
		KNumRegister base,
		KNumRegister rootIndex,
		KNumRegister maxError) {

		if (CCompare.getSign (base) == -1) {
			throw new IllegalArgumentException (
				ERR_MSG_NEGATIVE_BASE
			);
		}

		KNumRegisterPool regPool = calc.regPool;

		KRegCont cont_1 = regPool.get ();
		KRegCont cont_2 = regPool.get ();
		KRegCont cont_3 = regPool.get ();
		KRegCont cont_4 = regPool.get ();
		KRegCont cont_5 = regPool.get ();

		KNumRegister two = cont_1.reg;
		KNumRegister lowBound = cont_2.reg;
		KNumRegister highBound = cont_3.reg;
		KNumRegister approx = cont_4.reg;
		KNumRegister temp_1 = cont_5.reg;

		// 2
		two.setValue (2);

		// 0
		lowBound.setZeroValue ();

		// 1
		highBound.setValue (1);
		if (CCompare.compare (calc, base, highBound) > 0) {
			// base > 1
			highBound.copy (base);
		}

		long sign;
		while (true) {
			// lb + hb
			CAdd.add (calc, approx, lowBound, highBound);
			// (lb + hb) / 2
			// = estimated root
			CDivide.divide (calc, approx, approx, two);
			// estimated root ^ exponent
			CMultiply.exponential (calc, temp_1, approx, rootIndex);
			// (estimated root ^ exponent) - base
			CSubtract.subtract (calc, temp_1, temp_1, base);
			sign = CCompare.getSign (temp_1);
			// |(estimated root ^ exponent) - base|
			CInvert.abs (temp_1, temp_1);
			if (CCompare.compare (calc, temp_1, maxError) < 0) {
				// |(estimated root ^ exponent) - base| < error
				break;
			}
			if (sign > 0) {
				highBound.copy (approx);
			} else {
				lowBound.copy (approx);
			}
		}

		result.copy (approx);

		regPool.discard (cont_1);
		regPool.discard (cont_2);
		regPool.discard (cont_3);
		regPool.discard (cont_4);
		regPool.discard (cont_5);
	}

}
