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
 * <p>
 * <code>BigDecimal</code> is used as fallback,
 * but methods in this package try to avoid
 * unnecessary allocation.
 */
public class KNumRegister {

	/**
	 * {@link Long#MAX_VALUE} as {@link java.math.BigInteger}.
	 */
	public static final BigInteger MAX_LONG =
		new BigInteger (String.valueOf (Long.MAX_VALUE));

	/**
	 * {@link Long#MIN_VALUE} as {@link java.math.BigInteger}.
	 */
	public static final BigInteger MIN_LONG =
		new BigInteger (String.valueOf (Long.MIN_VALUE));

	/**
	 * <code>(-{@link Long#MIN_VALUE})</code> as {@link java.math.BigInteger}
	 * (lazily initialized).
	 */
	static BigInteger MIN_LONG_NEGATED;

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
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setZeroValue()}
	 * call on existing object.
	 */
	public KNumRegister () {
		setZeroValue ();
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #copy(KNumRegister)}
	 * call on existing object.
	 *
	 * @param number number copied to <code>this</code>.
	 */
	public KNumRegister (KNumRegister number) {
		copy (number);
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(int)}
	 * call on existing object.
	 *
	 * @param intValue integer value.
	 */
	public KNumRegister (int intValue) {
		setValue (intValue);
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(long)}
	 * call on existing object.
	 *
	 * @param longValue integer value.
	 */
	public KNumRegister (long longValue) {
		setValue (longValue);
	}

	/**
	 * Constructor.
	 * <p>
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
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(BigInteger)}
	 * call on existing object.
	 *
	 * @param bigValue integer value.
	 */
	public KNumRegister (BigInteger bigValue) {
		setValue (bigValue);
	}

	/**
	 * Constructor.
	 * <p>
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
	 * Copy value.
	 *
	 * @param number copied number.
	 */
	public void copy (KNumRegister number) {
		profile = number.profile;
		numerator = number.numerator;
		denominator = number.denominator;
		bigNumerator = number.bigNumerator;
		bigDenominator = number.bigDenominator;
	}

	/**
	 * Set <code>int</code> integer value.
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
	 * Set <code>long</code> integer value.
	 *
	 * @param longValue new value.
	 */
	public void setValue (long longValue) {
		setInteger (longValue);

		// invalidated
		bigNumerator = null;

		bigDenominator = BigInteger.ONE;
	}

	/**
	 * Set fractional value.
	 * <p>
	 * Fraction is automatically normalized.
	 *
	 * @param numerator numerator.
	 * @param denominator denominator.
	 * @throws ArithmeticException denominator is zero.
	 */
	public void setValue (
		long numerator,
		long denominator) {

		// BigIntegers invalidated
		bigNumerator = null;
		bigDenominator = null;

		if (denominator < 0) {
			// negative denominator

			if (numerator == Long.MIN_VALUE
				|| denominator == Long.MIN_VALUE) {
				// negation would overflow
				// unable to normalize signs

				// try to simplify
				long gcd = gcd (numerator, denominator);
				// don't use Math.abs (gcd), it might overflow
				if (gcd == 1 || gcd == -1) {
					// unable to simplify
					// unable to normalize signs

					// use fallback

					if (MIN_LONG_NEGATED == null) {
						MIN_LONG_NEGATED = MIN_LONG.negate ();
					}

					// only one is true:
					// (numerator == Long.MIN_VALUE)
					// or
					// (denominator == Long.MIN_VALUE)
					// it can't be both (otherwise: abs(gcd) == Long.MIN_VALUE)
					// so, the other number can be negated safely
					if (numerator == Long.MIN_VALUE) {
						bigNumerator = MIN_LONG_NEGATED;
						bigDenominator = BigInteger.valueOf (-denominator);

					} else {
						bigNumerator = BigInteger.valueOf (-numerator);
						bigDenominator = MIN_LONG_NEGATED;
					}

					// denominator cannot be positive
					if (denominator == -1) {
						// integer
						profile = KProfile.BIG_INTEGER;
						// if you get here you get a cookie
						// TODO replace this whole branch with something simpler
						// (I didn't know it could get this complex)

					} else {
						// rational
						profile = KProfile.BIG_RATIONAL;
					}
					return;

				} else {
					// simplify fraction
					numerator /= gcd;
					denominator /= gcd;

					// normalize signs
					if (denominator < 0) {
						numerator = -numerator;
						denominator = -denominator;
					}

					// set value and profile
					setIrreducibleFraction (
						numerator,
						denominator
					);
					return;
				}

			} else {
				// normalize signs
				numerator = -numerator;
				denominator = -denominator;
			}
		}

		// denominator must be positive at this point

		long gcd = gcd (numerator, denominator);
		// prevent denominator from becoming negative
		gcd = Math.abs (gcd);
		// the previous operation cannot overflow:
		// it would overflow if gcd == Long.MIN_VALUE
		// but that would imply that
		// the denominator is a multiple of Long.MIN_VALUE
		// the only multiple of Long.MIN_VALUE that fits in Long
		// is Long.MIN_VALUE itself
		// and it is negative
		// but denominator must be positive
		// which is a contradiction

		// simplify fraction
		numerator /= gcd;
		denominator /= gcd;

		// set value and profile
		setIrreducibleFraction (
			numerator,
			denominator
		);
	}

	/**
	 * Set <code>BigInteger</code> value.
	 *
	 * @param bigValue new value.
	 */
	public void setValue (BigInteger bigValue) {
		this.bigNumerator = bigValue;
		this.bigDenominator = null;

		if (bigValue.compareTo (MAX_LONG) <= 0
			&& bigValue.compareTo (MIN_LONG) >= 0) {
			// compact
			setInteger (bigValue.longValue ());

		} else {
			// unable to compact
			profile = KProfile.BIG_INTEGER;
		}
	}

	/**
	 * Set fractional value.
	 * <p>
	 * Fraction is automatically normalized.
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

		if (gcd.equals (BigInteger.ONE)) {
			// no need to divide
			this.bigNumerator = bigNumerator;
			this.bigDenominator = bigDenominator;

		} else {
			// simplify to irreducible fraction
			this.bigNumerator = bigNumerator.divide (gcd);
			this.bigDenominator = bigDenominator.divide (gcd);
		}

		if (this.bigDenominator.signum () < 0) {
			// negative denominator
			this.bigNumerator = this.bigNumerator.negate ();
			this.bigDenominator = this.bigDenominator.negate ();
		}

		if (this.bigDenominator.equals (BigInteger.ONE)) {
			// integer
			this.bigDenominator = null;

			if (this.bigNumerator.compareTo (MAX_LONG) <= 0
				&& this.bigNumerator.compareTo (MIN_LONG) >= 0) {
				// compact
				setInteger (this.bigNumerator.longValue ());

			} else {
				// unable to compact
				profile = KProfile.BIG_INTEGER;
			}

		} else {
			// rational

			// denominator must be positive at this point
			if (this.bigNumerator.compareTo (MAX_LONG) <= 0
				&& this.bigNumerator.compareTo (MIN_LONG) >= 0
				&& this.bigDenominator.compareTo (MAX_LONG) <= 0) {
				// compact
				setIrreducibleRationalFraction (
					this.bigNumerator.longValue (),
					this.bigDenominator.longValue ()
				);

			} else {
				// unable to compact
				profile = KProfile.BIG_RATIONAL;
			}
		}
	}

	/**
	 * Set numerator and denominator, where
	 * <code>denominator = 1</code>
	 *
	 * @param numerator new numerator.
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
	}

	/**
	 * Set value.
	 * <p>
	 * The numerator and denominator must form an irreducible fraction.
	 *
	 * @param numerator numerator, must be coprime with the denominator.
	 * @param denominator denominator, must be positive and non-zero.
	 */
	private void setIrreducibleFraction (
		long numerator,
		long denominator) {

		// denominator must be positive at this point
		if (denominator == 1) {
			// integer
			this.numerator = numerator;
			this.denominator = 1;

			// check if fits in int
			// denominator must be positive at this point
			profile = ((
				numerator <= Integer.MAX_VALUE
					&& numerator >= Integer.MIN_VALUE
			)) ?
				KProfile.INT_INTEGER :
				KProfile.LONG_INTEGER;

		} else {
			// rational
			setIrreducibleRationalFraction (numerator, denominator);
		}
	}

	/**
	 * Set value, which must not be integer.
	 * <p>
	 * The numerator and denominator must form an irreducible fraction.
	 *
	 * @param numerator numerator, must be coprime with the denominator.
	 * @param denominator denominator, must be greater than 1.
	 */
	private void setIrreducibleRationalFraction (
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
	}

	/**
	 * Find greatest common divisor.
	 */
	private static long gcd (long a, long b) {
		// Euclid's algorithm
		long modulo;
		while (b != 0) {
			modulo = a % b;
			a = b;
			b = modulo;
		}
		return a;
	}

	/**
	 * Convert current numerator, denominator to
	 * {@link java.math.BigInteger}.
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
