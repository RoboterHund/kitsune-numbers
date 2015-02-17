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
	private static BigInteger MIN_LONG_NEGATED;

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

		// find greatest common divisor with Euclid's algorithm
		long gcd = numerator;
		long div = denominator;
		long modulo;
		while (div != 0) {
			modulo = gcd % div;
			gcd = div;
			div = modulo;
		}

		if (denominator < 0) {
			// negative denominator
			if ((
				numerator == Long.MIN_VALUE || denominator == Long.MIN_VALUE
			) && (
				gcd == 1 || gcd == -1
			)) {
				// cannot make denominator positive
				// without overflowing either the numerator or the denominator
				// and the fraction cannot be simplified

				if (MIN_LONG_NEGATED == null) {
					MIN_LONG_NEGATED = MIN_LONG.negate ();
				}

				if (numerator == Long.MIN_VALUE) {
					// denominator != Long.MIN_VALUE
					denominator = -denominator;

					bigNumerator = MIN_LONG_NEGATED;
					bigDenominator = BigInteger.valueOf (denominator);

					profile =
						(denominator == 1) ?
							KProfile.BIG_INTEGER :
							KProfile.BIG_RATIONAL;

				} else {
					// denominator == Long.MIN_VALUE
					bigNumerator = BigInteger.valueOf (-numerator);
					bigDenominator = MIN_LONG_NEGATED;

					profile = KProfile.BIG_RATIONAL;
				}
				return;

			} else {
				// simplify fraction
				numerator /= gcd;
				denominator /= gcd;

				// ensure denominator positive
				if (denominator < 0) {
					numerator = -numerator;
					denominator = -denominator;
				}
			}

		} else {
			// denominator positive
			// GCD != Long.MIN_VALUE
			gcd = Math.abs (gcd);
			numerator /= gcd;
			denominator /= gcd;
		}
		// apparently, the last two branches can be merged into one
		// but that would be a hack

		if (denominator == 1) {
			// integer
			setInteger (numerator);

		} else {
			// rational
			setIrreducibleFraction (numerator, denominator);
		}
	}

	/**
	 * Set <code>BigInteger</code> value.
	 *
	 * @param bigValue new value.
	 */
	public void setValue (BigInteger bigValue) {
		this.bigNumerator = bigValue;
		this.bigDenominator = BigInteger.ONE;

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

		if (!gcd.equals (BigInteger.ONE)) {
			// simplify fraction
			bigNumerator = bigNumerator.divide (gcd);
			bigDenominator = bigDenominator.divide (gcd);
		}

		// ensure denominator positive
		if (bigDenominator.signum () < 0) {
			bigNumerator = bigNumerator.negate ();
			bigDenominator = bigDenominator.negate ();
		}

		if (bigDenominator.equals (BigInteger.ONE)) {
			// integer
			bigDenominator = BigInteger.ONE;

			if (bigNumerator.compareTo (MAX_LONG) <= 0
				&& bigNumerator.compareTo (MIN_LONG) >= 0) {
				// compact
				setInteger (bigNumerator.longValue ());

			} else {
				// unable to compact
				profile = KProfile.BIG_INTEGER;
			}

		} else {
			// rational

			// denominator must be positive at this point
			if (bigNumerator.compareTo (MAX_LONG) <= 0
				&& bigNumerator.compareTo (MIN_LONG) >= 0
				&& bigDenominator.compareTo (MAX_LONG) <= 0) {
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
	 * {@code denominator = 1}
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
	 * Set value, which must not be integer.
	 * <p>
	 * The numerator and denominator must form an irreducible fraction,
	 * where {@code denominator > 1}.
	 */
	private void setIrreducibleFraction (
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
