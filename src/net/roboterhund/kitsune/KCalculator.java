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
 * Singleton object to perform calculations with
 * {@link KNumRegister} objects.
 */
public class KCalculator {

	/**
	 * Result of last primitive calculation.
	 *
	 * @see net.roboterhund.kitsune.KCalculator#add(long, long)
	 * @see net.roboterhund.kitsune.KCalculator#subtract(long, long)
	 * @see net.roboterhund.kitsune.KCalculator#multiply(long, long)
	 */
	long intResult;

	/**
	 * Add two numbers.
	 *
	 * @param add_to number to add to.
	 * Overwritten with the result of the addition.
	 * @param term_2 number to add.
	 */
	public void add (
		KNumRegister add_to,
		KNumRegister term_2) {

		add (add_to, add_to, term_2);
	}

	/**
	 * Add two numbers.
	 *
	 * @param result overwritten with the result.
	 * @param term_1 first term.
	 * @param term_2 second term.
	 */
	public void add (
		KNumRegister result,
		KNumRegister term_1,
		KNumRegister term_2) {

		CAdd.add (this, result, term_1, term_2);
	}

	/**
	 * Subtract two numbers.
	 *
	 * @param subtract_from number to subtract from.
	 * Overwritten with the result of the subtraction.
	 * @param subtrahend number subtracted.
	 */
	public void subtract (
		KNumRegister subtract_from,
		KNumRegister subtrahend) {

		subtract (subtract_from, subtract_from, subtrahend);
	}

	/**
	 * Subtract two numbers.
	 *
	 * @param result overwritten with the result.
	 * @param minuend number subtracted from (does not change).
	 * @param subtrahend number to subtract.
	 */
	public void subtract (
		KNumRegister result,
		KNumRegister minuend,
		KNumRegister subtrahend) {

		CSubtract.subtract (this, result, minuend, subtrahend);
	}

	/**
	 * Multiply two numbers.
	 *
	 * @param multiplied first factor.
	 * Overwritten with the result of the multiplication.
	 * @param factor_2 second factor.
	 */
	public void multiply (
		KNumRegister multiplied,
		KNumRegister factor_2) {

		multiply (multiplied, multiplied, factor_2);
	}

	/**
	 * Multiply two numbers.
	 *
	 * @param result overwritten with the result.
	 * @param factor_1 first factor.
	 * @param factor_2 second factor.
	 */
	public void multiply (
		KNumRegister result,
		KNumRegister factor_1,
		KNumRegister factor_2) {

		CMultiply.multiply (this, result, factor_1, factor_2);
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
		KNumRegister divided,
		KNumRegister divisor) {

		divide (divided, divided, divisor);
	}

	/**
	 * Divide two numbers.
	 *
	 * @param result overwritten with the result.
	 * @param dividend number that is divided (does not change).
	 * @param divisor number by which to divide.
	 * @throws java.lang.ArithmeticException division by zero.
	 */
	public void divide (
		KNumRegister result,
		KNumRegister dividend,
		KNumRegister divisor) {

		CDivide.divide (this, result, dividend, divisor);
	}

	/**
	 * Get {@code dividend % divisor}.
	 *
	 * @param result overwritten with the result.
	 * @param dividend number that is divided (does not change).
	 * @param divisor number by which to divide.
	 * @throws java.lang.ArithmeticException divisor is zero.
	 */
	public void modulo (
		KNumRegister result,
		KNumRegister dividend,
		KNumRegister divisor) {

		CDivide.modulo (this, result, dividend, divisor);
	}

	/**
	 * Leave only the integer part of a number.
	 *
	 * @param number the number.
	 * Overwritten, decimal part is discarded.
	 */
	public void truncate (
		KNumRegister number) {

		truncate (number, number);
	}

	/**
	 * Get the integer part of a number.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public void truncate (
		KNumRegister result,
		KNumRegister number) {

		CRound.truncate (result, number);
	}

	/**
	 * Get {@code floor (number)}.
	 *
	 * @param number the number.
	 * Overwritten, decimal part is discarded.
	 */
	public void floor (
		KNumRegister number) {

		floor (number, number);
	}

	/**
	 * Get {@code floor (number)}.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public void floor (
		KNumRegister result,
		KNumRegister number) {

		CRound.floor (result, number);
	}

	/**
	 * Get {@code ceiling (number)}.
	 *
	 * @param number the number.
	 * Overwritten, decimal part is discarded.
	 */
	public void ceiling (
		KNumRegister number) {

		ceiling (number, number);
	}

	/**
	 * Get {@code ceiling (number)}.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public void ceiling (
		KNumRegister result,
		KNumRegister number) {

		CRound.ceiling (result, number);
	}

	/**
	 * Get absolute value of number.
	 *
	 * @param number the number.
	 */
	public void abs (
		KNumRegister number) {

		abs (number, number);
	}

	/**
	 * Get absolute value of number.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public void abs (
		KNumRegister result,
		KNumRegister number) {

		CInvert.abs (result, number);
	}

	/**
	 * Get negated number.
	 *
	 * @param number the number.
	 */
	public void negate (
		KNumRegister number) {

		negate (number, number);
	}

	/**
	 * Get negated number.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public void negate (
		KNumRegister result,
		KNumRegister number) {

		CInvert.negate (result, number);
	}

	/**
	 * Get multiplicative inverse of number.
	 *
	 * @param number the number.
	 */
	public void inverse (
		KNumRegister number) {

		inverse (number, number);
	}

	/**
	 * Get multiplicative inverse of number.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public void inverse (
		KNumRegister result,
		KNumRegister number) {

		CInvert.inverse (result, number);
	}

	/**
	 * Primitive addition, guarded against {@code long} overflow.
	 * <p>
	 * Result stored in {@link net.roboterhund.kitsune.KCalculator#intResult}.
	 *
	 * @param term_1 first operand.
	 * @param term_2 second operand.
	 * @return {@code true} iff operation completed without overflow.
	 */
	boolean add (long term_1, long term_2) {
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
	 * Primitive subtraction, guarded against {@code long} overflow.
	 * <p>
	 * Result stored in {@link net.roboterhund.kitsune.KCalculator#intResult}.
	 *
	 * @param minuend first operand.
	 * @param subtrahend second operand.
	 * @return {@code true} iff operation completed without overflow.
	 */
	boolean subtract (long minuend, long subtrahend) {
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
	 * Primitive multiplication, guarded against {@code long} overflow.
	 * <p>
	 * Result stored in {@link net.roboterhund.kitsune.KCalculator#intResult}.
	 *
	 * @param factor_1 first operand.
	 * @param factor_2 second operand.
	 * @return {@code true} iff operation completed without overflow.
	 */
	boolean multiply (long factor_1, long factor_2) {
		if (factor_2 > 1) {
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

		}
		// multiplying by -1 is always valid because
		// Long.MIN_VALUE is not allowed here

		intResult = factor_1 * factor_2;
		return true;
	}

}
