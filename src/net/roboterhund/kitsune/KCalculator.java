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

import java.math.BigDecimal;

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

		if (term_1.fitsInInt
			&& term_2.fitsInInt) {
			// int operation

			// TODO complete overflow test
			result.setValue (
				term_1.numerator * term_2.denominator
					+ term_2.numerator * term_1.denominator,
				term_1.denominator * term_2.denominator
			);
			return;

		} else if (term_1.bigDecimal == null
			&& term_2.bigDecimal == null) {
			// long operation

			if (term_1.denominator == 1) {
				if (term_2.denominator == 1) {
					// both terms integers
					if (add (term_1.numerator, term_2.numerator)) {
						result.setValue (
							intResult
						);
						return;
					}

				} else {
					// term 1 integer
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
				}

			} else {
				if (term_2.denominator == 1) {
					// term 2 integer
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

				} else {
					// both terms not integers
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
				}
			}
		}

		// BigDecimal operation

		result.setValue (
			term_1.toBigDecimal ()
				.add (term_2.toBigDecimal ())
		);
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

		if (minuend.fitsInInt
			&& subtrahend.fitsInInt) {
			// int operation

			result.setValue (
				minuend.numerator * subtrahend.denominator
					- subtrahend.numerator * minuend.denominator,
				minuend.denominator * subtrahend.denominator
			);
			return;

		} else if (minuend.bigDecimal == null
			&& subtrahend.bigDecimal == null) {
			// long operation

			if (minuend.denominator == 1) {
				if (subtrahend.denominator == 1) {
					// minuend, subtrahend integers
					if (subtract (minuend.numerator, subtrahend.numerator)) {
						result.setValue (
							intResult
						);
						return;
					}

				} else {
					// minuend integer
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
				}

			} else {
				if (subtrahend.denominator == 1) {
					// subtrahend integer
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

				} else {
					// minuend, subtrahend not integers
					if (multiply (minuend.numerator, subtrahend.denominator)) {
						long n1_mul_d2 = intResult;

						if (multiply (subtrahend.numerator, minuend.denominator)) {
							long n2_mul_d1 = intResult;

							if (subtract (n1_mul_d2, n2_mul_d1)) {
								long numerator = intResult;

								if (multiply (
									minuend.denominator, subtrahend.denominator)) {

									result.setValue (
										numerator,
										intResult
									);
									return;
								}
							}
						}
					}
				}
			}
		}

		// BigDecimal operation

		result.setValue (
			minuend.toBigDecimal ()
				.subtract (subtrahend.toBigDecimal ())
		);
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

		if (factor_1.fitsInInt
			&& factor_2.fitsInInt) {
			// int operation

			result.setValue (
				factor_1.numerator * factor_2.numerator,
				factor_1.denominator * factor_2.denominator
			);
			return;

		} else if (factor_1.bigDecimal == null
			&& factor_2.bigDecimal == null) {
			// long operation

			if (multiply (factor_1.numerator, factor_2.numerator)) {
				long numerator = intResult;

				if (factor_1.denominator == 1) {
					if (factor_2.denominator == 1) {
						// both factors integers
						result.setValue (numerator);
						return;

					} else {
						// factor 1 integer
						result.setValue (numerator, factor_2.denominator);
						return;
					}

				} else {
					if (factor_2.denominator == 1) {
						// factor 2 integer
						result.setValue (numerator, factor_1.denominator);
						return;

					} else {
						// both factors not integers
						if (multiply (factor_1.denominator, factor_2.denominator)) {
							result.setValue (numerator, intResult);
							return;
						}
					}
				}
			}
		}

		// BigDecimal operation

		result.setValue (
			factor_1.toBigDecimal ()
				.multiply (factor_2.toBigDecimal ())
		);
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

		if (dividend.bigDecimal == null
			&& divisor.bigDecimal == null) {

			if (dividend.denominator == 1) {
				if (divisor.denominator == 1) {
					// dividend, divisor integers
					result.setValue (
						dividend.numerator,
						divisor.numerator
					);
					return;

				} else {
					// dividend integer
					if (dividend.fitsInInt
						&& divisor.fitsInInt) {
						// int operation

						result.setValue (
							dividend.numerator * divisor.denominator,
							divisor.numerator
						);
						return;

					} else {
						// long operation

						if (multiply (dividend.numerator, divisor.denominator)) {
							result.setValue (
								intResult,
								divisor.numerator
							);
							return;
						}
					}
				}

			} else {
				if (dividend.fitsInInt
					&& divisor.fitsInInt) {
					// int operation

					result.setValue (
						dividend.numerator * divisor.denominator,
						dividend.denominator * divisor.numerator
					);
					return;

				} else {
					// long operation

					if (divisor.denominator == 1) {
						// divisor integer
						if (multiply (dividend.denominator, divisor.numerator)) {
							result.setValue (
								dividend.numerator,
								intResult
							);
							return;
						}

					} else {
						// dividend, divisor not integers
						if (multiply (dividend.numerator, divisor.denominator)) {
							long numerator = intResult;

							if (multiply (dividend.denominator, divisor.numerator)) {
								result.setValue (
									numerator,
									intResult
								);
								return;
							}
						}
					}
				}
			}
		}

		// BigDecimal operation

		result.setValue (
			dividend.toBigDecimal ()
				.divide (
					divisor.toBigDecimal (),
					precision,
					BigDecimal.ROUND_HALF_UP
				)
		);
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
