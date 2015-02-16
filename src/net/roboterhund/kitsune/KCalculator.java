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
 * Singleton object to perform calculations with {@link
 * net.roboterhund.kitsune.KNumber} objects.
 * <p>
 * Calculations try to produce, whenever possible,
 * exact, rational results.
 * <p>
 * <code>BigDecimal</code> is used as fallback,
 * but methods in this package try to avoid
 * unnecessary allocation.
 */
public class KCalculator {

	/**
	 * If a calculation produces numbers with infinite decimal expansion,
	 * and they must be converted to {@link java.math.BigDecimal},
	 * this setting determines the number of decimals stored.
	 */
	public int precision = KNumber.DEFAULT_PRECISION;

	/**
	 * Result of last primitive calculation.
	 *
	 * @see net.roboterhund.kitsune.KCalculator#add(long, long)
	 * @see net.roboterhund.kitsune.KCalculator#subtract(long, long)
	 * @see net.roboterhund.kitsune.KCalculator#multiply(long, long)
	 */
	private long intResult;

	/**
	 * Add two numbers.
	 *
	 * @param add_to number to add to.
	 * Overwritten with the result of the addition.
	 * @param term_2 number to add.
	 */
	public void add (
		KNumber add_to,
		KNumber term_2) {

		add (
			add_to,
			add_to,
			term_2);
	}

	/**
	 * Add two numbers.
	 *
	 * @param result overwritten with the result.
	 * @param term_1 first term.
	 * @param term_2 second term.
	 */
	public void add (
		KNumber result,
		KNumber term_1,
		KNumber term_2) {

		switch (KProfile.route[term_1.profile][term_2.profile]) {
		default:
			// go to end
			break;

		case KProfile._PRE__RAT_:
			if (multiply (term_1.numerator, term_2.denominator)) {
				long n1_mul_d2 = intResult;

				if (multiply (term_2.numerator, term_1.denominator)) {
					long n2_mul_d1 = intResult;

					if (add (n1_mul_d2, n2_mul_d1)) {
						long numerator = intResult;

						if (multiply (term_1.denominator, term_2.denominator)) {
							result.setValue (
								numerator,
								intResult
							);
							return;
						}
					}
				}
			}
			break;

		case KProfile._PRE__INT1:
			if (multiply (term_1.numerator, term_2.denominator)) {
				long n1_mul_d2 = intResult;

				if (add (n1_mul_d2, term_2.numerator)) {
					result.setValue (
						intResult,
						term_2.denominator
					);
					return;
				}
			}
			break;

		case KProfile._PRE__INT2:
			if (multiply (term_2.numerator, term_1.denominator)) {
				long n2_mul_d1 = intResult;

				if (add (term_1.numerator, n2_mul_d1)) {
					result.setValue (
						intResult,
						term_1.denominator
					);
					return;
				}
			}
			break;

		case KProfile._PRE__INT_:
			if (add (term_1.numerator, term_2.numerator)) {
				result.setValue (
					intResult
				);
				return;
			}
			break;

		case KProfile._POST_RAT_:
			result.setValue (
				term_1.numerator * term_2.denominator
					+ term_2.numerator * term_1.denominator,
				term_1.denominator * term_2.denominator
			);
			return;

		case KProfile._POST_INT_:
			result.setValue (
				term_1.numerator + term_2.numerator
			);
			return;
		}

		term_1.setBigIntegers ();
		term_2.setBigIntegers ();

		result.setValue (
			term_1.bigNumerator.multiply (term_2.bigDenominator)
				.add (term_2.bigNumerator.multiply (term_1.bigDenominator)),
			term_1.bigDenominator.multiply (term_2.bigDenominator)
		);
		result.compact ();
	}

	/**
	 * Subtract two numbers.
	 *
	 * @param subtract_from number to subtract from.
	 * Overwritten with the result of the subtraction.
	 * @param subtrahend number subtracted.
	 */
	public void subtract (
		KNumber subtract_from,
		KNumber subtrahend) {

		subtract (
			subtract_from,
			subtract_from,
			subtrahend);
	}

	/**
	 * Subtract two numbers.
	 *
	 * @param result overwritten with the result.
	 * @param minuend number subtracted from.
	 * @param subtrahend number to subtract.
	 */
	public void subtract (
		KNumber result,
		KNumber minuend,
		KNumber subtrahend) {

		switch (KProfile.route[minuend.profile][subtrahend.profile]) {
		default:
			// go to end
			break;

		case KProfile._PRE__RAT_:
			if (multiply (minuend.numerator, subtrahend.denominator)) {
				long n1_mul_d2 = intResult;

				if (multiply (subtrahend.numerator, minuend.denominator)) {
					long n2_mul_d1 = intResult;

					if (subtract (n1_mul_d2, n2_mul_d1)) {
						long numerator = intResult;

						if (multiply (minuend.denominator, subtrahend.denominator)) {
							result.setValue (
								numerator,
								intResult
							);
							return;
						}
					}
				}
			}
			break;

		case KProfile._PRE__INT1:
			if (multiply (minuend.numerator, subtrahend.denominator)) {
				long n1_mul_d2 = intResult;

				if (subtract (n1_mul_d2, subtrahend.numerator)) {
					result.setValue (
						intResult,
						subtrahend.denominator
					);
					return;
				}
			}
			break;

		case KProfile._PRE__INT2:
			if (multiply (subtrahend.numerator, minuend.denominator)) {
				long n2_mul_d1 = intResult;

				if (subtract (minuend.numerator, n2_mul_d1)) {
					result.setValue (
						intResult,
						minuend.denominator
					);
					return;
				}
			}
			break;

		case KProfile._PRE__INT_:
			if (subtract (minuend.numerator, subtrahend.numerator)) {
				result.setValue (
					intResult
				);
				return;
			}
			break;

		case KProfile._POST_RAT_:
			result.setValue (
				minuend.numerator * subtrahend.denominator
					- subtrahend.numerator * minuend.denominator,
				minuend.denominator * subtrahend.denominator
			);
			return;

		case KProfile._POST_INT_:
			result.setValue (
				minuend.numerator - subtrahend.numerator
			);
			return;
		}

		minuend.setBigIntegers ();
		subtrahend.setBigIntegers ();

		result.setValue (
			minuend.bigNumerator.multiply (subtrahend.bigDenominator)
				.subtract (
					subtrahend.bigNumerator.multiply (minuend.bigDenominator)
				),
			minuend.bigDenominator.multiply (subtrahend.bigDenominator)
		);
		result.compact ();
	}

	/**
	 * Multiply two numbers.
	 *
	 * @param multiplied first factor.
	 * Overwritten with the result of the multiplication.
	 * @param factor_2 second factor.
	 */
	public void multiply (
		KNumber multiplied,
		KNumber factor_2) {

		multiply (
			multiplied,
			multiplied,
			factor_2);
	}

	/**
	 * Multiply two numbers.
	 *
	 * @param result overwritten with the result.
	 * @param factor_1 first factor.
	 * @param factor_2 second factor.
	 */
	public void multiply (
		KNumber result,
		KNumber factor_1,
		KNumber factor_2) {

		switch (KProfile.route[factor_1.profile][factor_2.profile]) {
		default:
			// go to end
			break;

		case KProfile._PRE__RAT_:
			if (multiply (factor_1.numerator, factor_2.numerator)) {
				long numerator = intResult;

				if (multiply (factor_1.denominator, factor_2.denominator)) {
					result.setValue (
						numerator,
						intResult
					);
					return;
				}
			}
			break;

		case KProfile._PRE__INT1:
			if (multiply (factor_1.numerator, factor_2.numerator)) {
				result.setValue (
					intResult,
					factor_2.denominator
				);
				return;
			}
			break;

		case KProfile._PRE__INT2:
			if (multiply (factor_1.numerator, factor_2.numerator)) {
				result.setValue (
					intResult,
					factor_1.denominator
				);
				return;
			}
			break;

		case KProfile._PRE__INT_:
			if (multiply (factor_1.numerator, factor_2.numerator)) {
				result.setValue (
					intResult
				);
				return;
			}
			break;

		case KProfile._POST_RAT_:
			result.setValue (
				factor_1.numerator * factor_2.numerator,
				factor_1.denominator * factor_2.denominator
			);
			return;

		case KProfile._POST_INT_:
			result.setValue (
				factor_1.numerator * factor_2.numerator
			);
			return;
		}

		factor_1.setBigIntegers ();
		factor_2.setBigIntegers ();

		result.setValue (
			factor_1.bigNumerator.multiply (factor_2.bigNumerator),
			factor_1.bigDenominator.multiply (factor_2.bigDenominator)
		);
		result.compact ();
	}

	/**
	 * Divide two numbers.
	 *
	 * @param divided number that is divided.
	 * Overwritten with the result of the division.
	 * @param divisor number by which to divide.
	 * @throws java.lang.ArithmeticException division by zero.
	 */
	public void divide (
		KNumber divided,
		KNumber divisor) {

		divide (
			divided,
			divided,
			divisor);
	}

	/**
	 * Divide two numbers.
	 * <p>
	 * <b>Note</b>: precision is lost if
	 * the calculation cannot be performed with primitive data types,
	 * and the result has infinite decimal expansion.
	 *
	 * @param result overwritten with the result.
	 * @param dividend number that is divided.
	 * @param divisor number by which to divide.
	 * @throws java.lang.ArithmeticException division by zero.
	 */
	public void divide (
		KNumber result,
		KNumber dividend,
		KNumber divisor) {

		switch (KProfile.route[dividend.profile][divisor.profile]) {
		default:
			// go to end
			break;

		case KProfile._PRE__RAT_:
			if (multiply (dividend.numerator, divisor.denominator)) {
				long numerator = intResult;

				if (multiply (divisor.numerator, dividend.denominator)) {
					result.setValue (
						numerator,
						intResult
					);
					return;
				}
			}
			break;

		case KProfile._PRE__INT1:
			if (multiply (dividend.numerator, divisor.denominator)) {
				result.setValue (
					intResult,
					divisor.numerator
				);
				return;
			}
			break;

		case KProfile._PRE__INT2:
			if (multiply (divisor.numerator, dividend.denominator)) {
				result.setValue (
					dividend.numerator,
					intResult
				);
				return;
			}
			break;

		case KProfile._PRE__INT_:
			result.setValue (
				dividend.numerator,
				divisor.numerator
			);
			return;

		case KProfile._POST_RAT_:
			result.setValue (
				dividend.numerator * divisor.denominator,
				divisor.numerator * dividend.denominator
			);
			return;

		case KProfile._POST_INT_:
			result.setValue (
				dividend.numerator,
				divisor.numerator
			);
			return;
		}

		dividend.setBigIntegers ();
		divisor.setBigIntegers ();

		result.setValue (
			dividend.bigNumerator.multiply (divisor.bigDenominator),
			divisor.bigNumerator.multiply (dividend.bigDenominator)
		);
		result.compact ();
	}

	/**
	 * Primitive addition, guarded against <code>long</code> overflow.
	 * <p>
	 * Result stored in {@link net.roboterhund.kitsune.KCalculator#intResult}.
	 *
	 * @param term_1 first operand.
	 * @param term_2 second operand.
	 * @return <code>true</code> iff operation completed without overflow.
	 */
	private boolean add (long term_1, long term_2) {
		if (term_1 >= 0) {
			if (term_2 > 0
				&& term_1 > Long.MAX_VALUE - term_2) {
				// term_1 + term_2 > Long.MAX_VALUE
				return false;
			}

		} else {
			// term_1 < 0
			if (term_2 < 0
				&& term_1 < Long.MIN_VALUE - term_2) {
				// term_1 + term_2 < Long.MIN_VALUE
				return false;
			}
		}

		intResult = term_1 + term_2;
		return true;
	}

	/**
	 * Primitive subtraction, guarded against <code>long</code> overflow.
	 * <p>
	 * Result stored in {@link net.roboterhund.kitsune.KCalculator#intResult}.
	 *
	 * @param minuend first operand.
	 * @param subtrahend second operand.
	 * @return <code>true</code> iff operation completed without overflow.
	 */
	private boolean subtract (long minuend, long subtrahend) {
		if (minuend >= 0) {
			if (subtrahend < 0
				&& minuend > Long.MAX_VALUE + subtrahend) {
				// minuend - subtrahend > Long.MAX_VALUE
				return false;
			}

		} else {
			// minuend < 0
			if (
				subtrahend > 0
					&& minuend < Long.MIN_VALUE + subtrahend) {
				// minuend - subtrahend < Long.MIN_VALUE
				return false;
			}
		}

		intResult = minuend - subtrahend;
		return true;
	}

	/**
	 * Primitive multiplication, guarded against <code>long</code> overflow.
	 * <p>
	 * Result stored in {@link net.roboterhund.kitsune.KCalculator#intResult}.
	 *
	 * @param factor_1 first operand.
	 * @param factor_2 second operand.
	 * @return <code>true</code> iff operation completed without overflow.
	 */
	private boolean multiply (long factor_1, long factor_2) {
		if (factor_2 > 0) {
			if (factor_1 > Long.MAX_VALUE / factor_2
				|| factor_1 < Long.MIN_VALUE / factor_2) {
				//    factor_1 * factor_2 > Long.MAX_VALUE
				// or factor_1 * factor_2 < Long.MIN_VALUE
				return false;
			}

		} else if (factor_2 < -1) {
			if (factor_1 < Long.MAX_VALUE / factor_2
				|| factor_1 > Long.MIN_VALUE / factor_2) {
				//    factor_1 * factor_2 > Long.MAX_VALUE
				// or factor_1 * factor_2 < Long.MIN_VALUE
				// factor_2 is negative => order inverted
				return false;
			}

		} else if (factor_2 == -1) {
			if (factor_1 == Long.MIN_VALUE) {
				// factor_1 * factor_2 =
				// Long.MIN_VALUE * -1 = Long.MAX_VALUE + 1
				return false;
			}
		}

		intResult = factor_1 * factor_2;
		return true;
	}

}
