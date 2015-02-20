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
 * A mutable rational number container.
 * <p>
 * Values are stored in primitive data types.
 * A pair of {@link BigInteger} objects are used as fallback,
 * but methods in this package try to avoid
 * unnecessary allocation.
 * However, the references to the {@code BigInteger} objects
 * are retained until the {@code KNumRegister} is overwritten.
 * <p>
 * Use {@link KConverter} for input and output,
 * and {@link KCalculator} for operations.
 * <p>
 * <b>Note</b>: {@code KNumRegister} instances are mutable,
 * and therefore must be tightly controlled.
 * Allocate enough {@code KNumRegister} instances for all operations,
 * and keep their visibility to a minimum.
 */
public class KNumRegister {

	/**
	 * Number profile.
	 * <p>
	 * Determines how the numeric value is stored internally,
	 * and how to perform operations with it.
	 */
	int profile;

	/**
	 * Numerator.
	 * <p>
	 * Stores both int and long number.
	 */
	long numerator;

	/**
	 * Denominator.
	 * <p>
	 * Stores both int and long number.
	 * <p>
	 * Implementation must ensure that denominator is never negative or zero.
	 */
	long denominator;

	/**
	 * Big values fallback numerator.
	 */
	BigInteger bigNumerator;

	/**
	 * Big values fallback denominator.
	 */
	BigInteger bigDenominator;

	/**
	 * Equivalent to a {@link #setZeroValue()}
	 * call on existing object.
	 */
	public KNumRegister () {
		setZeroValue ();
	}

	/**
	 * Equivalent to a {@link #copy(KNumRegister)}
	 * call on existing object.
	 *
	 * @param register register, from where the number
	 * is copied to {@code this}.
	 */
	public KNumRegister (KNumRegister register) {
		copy (register);
	}

	/**
	 * Equivalent to a {@link #setValue(int)}
	 * call on existing object.
	 *
	 * @param intValue integer value.
	 */
	public KNumRegister (int intValue) {
		setValue (intValue);
	}

	/**
	 * Equivalent to a {@link #setValue(long)}
	 * call on existing object.
	 *
	 * @param longValue integer value.
	 */
	public KNumRegister (long longValue) {
		setValue (longValue);
	}

	/**
	 * Equivalent to a {@link #setValue(long, long)}
	 * call on existing object.
	 *
	 * @param numerator numerator.
	 * @param denominator denominator.
	 * @throws ArithmeticException denominator is zero.
	 */
	public KNumRegister (
		long numerator,
		long denominator) {

		setValue (numerator, denominator);
	}

	/**
	 * Equivalent to a {@link #setValue(BigInteger)}
	 * call on existing object.
	 *
	 * @param bigValue integer value.
	 */
	public KNumRegister (BigInteger bigValue) {
		setValue (bigValue);
	}

	/**
	 * Equivalent to a {@link #setValue(BigInteger, BigInteger)}
	 * call on existing object.
	 *
	 * @param bigNumerator new numerator.
	 * @param bigDenominator new denominator.
	 * @throws ArithmeticException denominator is zero.
	 */
	public KNumRegister (
		BigInteger bigNumerator,
		BigInteger bigDenominator) {

		setValue (bigNumerator, bigDenominator);
	}

	/**
	 * Set value to zero.
	 */
	public void setZeroValue () {
		profile = KProfile.INT_INTEGER;
		numerator = 0;
		denominator = 1;
		bigNumerator = BigInteger.ZERO;
		bigDenominator = BigInteger.ONE;
	}

	/**
	 * Copy value to {@code this} register.
	 *
	 * @param register register from where the number is copied.
	 */
	public void copy (KNumRegister register) {
		profile = register.profile;
		numerator = register.numerator;
		denominator = register.denominator;
		bigNumerator = register.bigNumerator;
		bigDenominator = register.bigDenominator;
	}

	/**
	 * Set {@code int} integer value.
	 *
	 * @param intValue new value.
	 */
	public void setValue (int intValue) {
		numerator = intValue;
		denominator = 1;

		profile = KProfile.INT_INTEGER;

		// invalidated
		bigNumerator = null;

		bigDenominator = BigInteger.ONE;
	}

	/**
	 * Set {@code long} integer value.
	 *
	 * @param longValue new value.
	 */
	public void setValue (long longValue) {
		if (longValue == Long.MIN_VALUE) {
			// Long.MIN_VALUE not allowed to avoid overflows
			setValue (
				KEdges.MIN_LONG
			);
			return;
		}

		setInteger (longValue);

		// invalidated
		bigNumerator = null;

		bigDenominator = BigInteger.ONE;
	}

	/**
	 * Set fractional value.
	 * <p>
	 * Fraction is automatically reduced and normalized.
	 *
	 * @param numerator new numerator.
	 * @param denominator new denominator.
	 * @throws ArithmeticException denominator is zero.
	 */
	public void setValue (
		long numerator,
		long denominator) {

		if (numerator == Long.MIN_VALUE
			|| denominator == Long.MIN_VALUE) {
			// Long.MIN_VALUE not allowed to avoid overflows
			setValue (
				BigInteger.valueOf (numerator),
				BigInteger.valueOf (denominator)
			);
			return;
		}

		// find greatest common divisor with Euclid's algorithm
		long gcd = numerator;
		long div = denominator;
		long modulo;
		while (div != 0) {
			modulo = gcd % div;
			gcd = div;
			div = modulo;
		}

		// simplify fraction
		numerator /= gcd;
		denominator /= gcd;

		setIrreducibleValue (numerator, denominator);
	}

	/**
	 * Set fractional value.
	 * <p>
	 * Fraction must be irreducible.
	 * <p>
	 * Fraction is automatically normalized.
	 *
	 * @param numerator new numerator.
	 * @param denominator new denominator.
	 */
	void setIrreducibleValue (
		long numerator,
		long denominator) {

		// ensure denominator positive
		if (denominator < 0) {
			numerator = -numerator;
			denominator = -denominator;
		}

		if (denominator == 1) {
			// integer
			setInteger (numerator);

		} else {
			// rational
			setIrreducibleFraction (numerator, denominator);
		}
	}

	/**
	 * Set {@code BigInteger} value.
	 *
	 * @param bigValue new value.
	 */
	public void setValue (BigInteger bigValue) {
		if (bigValue.compareTo (KEdges.MAX_LONG) <= 0
			&& bigValue.compareTo (KEdges.MIN_LONG) > 0) {
			// Long.MIN_VALUE excluded

			// compact
			setInteger (bigValue.longValue ());

		} else {
			// unable to compact
			profile = KProfile.BIG_INTEGER;
		}

		this.bigNumerator = bigValue;
		this.bigDenominator = BigInteger.ONE;
	}

	/**
	 * Set fractional value.
	 * <p>
	 * Fraction is automatically reduced and normalized.
	 *
	 * @param bigNumerator new numerator.
	 * @param bigDenominator new denominator.
	 * @throws ArithmeticException denominator is zero.
	 */
	public void setValue (
		BigInteger bigNumerator,
		BigInteger bigDenominator) {

		// find greatest common divisor
		BigInteger gcd = bigNumerator.gcd (bigDenominator);

		if (!gcd.equals (BigInteger.ONE)) {
			// simplify fraction
			bigNumerator = bigNumerator.divide (gcd);
			bigDenominator = bigDenominator.divide (gcd);
		}

		setIrreducibleValue (bigNumerator, bigDenominator, true);
	}

	/**
	 * Set fractional value.
	 * <p>
	 * Fraction must be irreducible.
	 * <p>
	 * Fraction is automatically normalized.
	 *
	 * @param bigNumerator new numerator.
	 * @param bigDenominator new denominator.
	 * @param checkSize if {@code true}, check whether
	 * the size of the numerator and denominator allow
	 * switching to another profile.
	 */
	void setIrreducibleValue (
		BigInteger bigNumerator,
		BigInteger bigDenominator,
		boolean checkSize) {

		// ensure denominator positive
		if (bigDenominator.signum () < 0) {
			bigNumerator = bigNumerator.negate ();
			bigDenominator = bigDenominator.negate ();
		}

		if (bigDenominator.equals (BigInteger.ONE)) {
			// integer
			bigDenominator = BigInteger.ONE;

			if (checkSize
				&& bigNumerator.compareTo (KEdges.MAX_LONG) <= 0
				&& bigNumerator.compareTo (KEdges.MIN_LONG) > 0) {
				// Long.MIN_VALUE excluded

				// compact
				setInteger (bigNumerator.longValue ());

			} else {
				// unable to compact
				profile = KProfile.BIG_INTEGER;
			}

		} else {
			// rational

			// denominator must be positive at this point
			if (checkSize
				&& bigNumerator.compareTo (KEdges.MAX_LONG) <= 0
				&& bigNumerator.compareTo (KEdges.MIN_LONG) > 0
				&& bigDenominator.compareTo (KEdges.MAX_LONG) <= 0) {
				// Long.MIN_VALUE excluded

				// compact
				setIrreducibleFraction (
					bigNumerator.longValue (),
					bigDenominator.longValue ()
				);

			} else {
				// unable to compact
				profile = KProfile.BIG_RATIONAL;
			}
		}

		this.bigNumerator = bigNumerator;
		this.bigDenominator = bigDenominator;
	}

	/**
	 * Set numerator and denominator, where
	 * {@code denominator = 1}.
	 */
	private void setInteger (long numerator) {
		this.numerator = numerator;
		this.denominator = 1;

		// check if fits in int
		profile = ((
			this.numerator <= Integer.MAX_VALUE
				&& this.numerator >= Integer.MIN_VALUE
		)) ?
			KProfile.INT_INTEGER :
			KProfile.LONG_INTEGER;

		// invalidated
		this.bigNumerator = null;

		this.bigDenominator = BigInteger.ONE;
	}

	/**
	 * Set value, which must not be integer.
	 * <p>
	 * The numerator and denominator must form an irreducible fraction,
	 * where {@code denominator > 1}.
	 */
	void setIrreducibleFraction (
		long numerator,
		long denominator) {

		this.numerator = numerator;
		this.denominator = denominator;

		// check if fits in int
		// denominator must be positive at this point
		profile = ((
			numerator <= Integer.MAX_VALUE
				&& numerator >= Integer.MIN_VALUE
		) && (
			denominator <= Integer.MAX_VALUE
		)) ?
			KProfile.INT_RATIONAL :
			KProfile.LONG_RATIONAL;

		// invalidated
		this.bigNumerator = null;
		this.bigDenominator = null;
	}

	/**
	 * Convert current numerator, denominator to
	 * a pair of {@link BigInteger} instances.
	 */
	void setBigIntegers () {
		if (bigNumerator == null) {
			bigNumerator = BigInteger.valueOf (numerator);
		}
		if (bigDenominator == null) {
			bigDenominator = BigInteger.valueOf (denominator);
		}
	}

}
