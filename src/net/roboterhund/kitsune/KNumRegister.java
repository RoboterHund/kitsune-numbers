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

	// TODO redesign construction to increase robustness

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setZeroValue()} call on existing object.
	 */
	public KNumRegister () {
		setZeroValue ();
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(KNumRegister)} call on existing object.
	 *
	 * @param number number copied to <code>this</code>.
	 */
	public KNumRegister (KNumRegister number) {
		setValue (number);
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(long, long)} call on existing object.
	 *
	 * @param numerator numerator.
	 * @param denominator denominator.
	 */
	public KNumRegister (long numerator, long denominator) {
		if (denominator < 0) {
			if (numerator == Long.MIN_VALUE
				|| denominator == Long.MIN_VALUE) {
				setValue (
					new BigInteger (String.valueOf (numerator)),
					new BigInteger (String.valueOf (denominator))
				);
				compact ();
				return;
			}
			numerator = -numerator;
			denominator = -denominator;
		}
		setValue (numerator, denominator);
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(long)} call on existing object.
	 *
	 * @param longValue integer value.
	 */
	public KNumRegister (long longValue) {
		setValue (longValue);
	}

	/**
	 * Set value to zero.
	 */
	public void setZeroValue () {
		profile = KProfile.INT_INTEGER;
		numerator = 0;
		denominator = 1;
		bigNumerator = null;
		bigDenominator = null;
	}

	/**
	 * Copy value.
	 *
	 * @param number copied number.
	 */
	public void setValue (KNumRegister number) {
		profile = number.profile;
		numerator = number.numerator;
		denominator = number.denominator;
		bigNumerator = number.bigNumerator;
		bigDenominator = number.bigDenominator;
	}

	/**
	 * Set value.
	 * <p>
	 * The resulting fraction is automatically simplified.
	 *
	 * @param numerator numerator.
	 * @param denominator denominator, must be positive and non-zero.
	 */
	public void setValue (long numerator, long denominator) {
		if (denominator == 1) {
			// already normalized
			setNormalizedValue (
				numerator,
				denominator
			);

		} else {
			// find greatest common divisor
			// Euclid's algorithm
			long n = numerator;
			long d = denominator;
			long modulo;
			while (n != 0) {
				modulo = d % n;
				d = n;
				n = modulo;
			}
			d = Math.abs (d);

			// normalize result
			setNormalizedValue (
				numerator / d,
				denominator / d
			);
		}
	}

	/**
	 * Set value.
	 * <p>
	 * The numerator and denominator must be already simplified.
	 *
	 * @param numerator numerator.
	 * @param denominator denominator, must be positive and non-zero.
	 */
	private void setNormalizedValue (long numerator, long denominator) {
		if (denominator < 0) {
			numerator = -numerator;
			denominator = -denominator;
		}

		// store initial values
		this.numerator = numerator;
		this.denominator = denominator;

		// check if fits in int
		if (this.denominator == 1) {
			profile = ((
				this.numerator <= Integer.MAX_VALUE
					&& this.numerator >= Integer.MIN_VALUE
			)) ?
				KProfile.INT_INTEGER :
				KProfile.LONG_INTEGER;

		} else {
			profile = ((
				this.numerator <= Integer.MAX_VALUE
					&& this.numerator >= Integer.MIN_VALUE
			) && (
				this.denominator <= Integer.MAX_VALUE
					&& this.denominator >= Integer.MIN_VALUE
			)) ?
				KProfile.INT_RATIONAL :
				KProfile.LONG_RATIONAL;
		}

		// discard old value, if any
		bigNumerator = null;
		bigDenominator = null;
	}

	/**
	 * Set integer value.
	 *
	 * @param longValue new value.
	 */
	public void setValue (long longValue) {
		// store initial values
		this.numerator = longValue;
		this.denominator = 1;

		// check if fits in int
		profile = ((
			this.numerator <= Integer.MAX_VALUE
				&& this.numerator >= Integer.MIN_VALUE
		)) ?
			KProfile.INT_INTEGER :
			KProfile.LONG_INTEGER;

		// discard old value, if any
		bigNumerator = null;
		bigDenominator = null;
	}

	/**
	 * Set value.
	 * <p>
	 * For internal use by {@link KCalculator}.
	 *
	 * @param bigNumerator new numerator.
	 * @param bigDenominator new denominator.
	 */
	public void setValue (
		BigInteger bigNumerator,
		BigInteger bigDenominator) {

		// find greatest common divisor
		BigInteger gcd = bigNumerator.gcd (bigDenominator);

		if (gcd.compareTo (BigInteger.ONE) == 0) {
			// no need to divide
			this.bigNumerator = bigNumerator;
			this.bigDenominator = bigDenominator;

		} else {
			// normalize result
			this.bigNumerator = bigNumerator.divide (gcd);
			this.bigDenominator = bigDenominator.divide (gcd);
		}

		if (this.bigDenominator.compareTo (BigInteger.ZERO) < 0) {
			this.bigNumerator = this.bigNumerator.negate ();
			this.bigDenominator = this.bigDenominator.negate ();
		}

		if (this.bigDenominator.equals (BigInteger.ONE)) {
			// make sure that the already existing BigInteger.ONE is reused
			this.bigDenominator = BigInteger.ONE;
			profile = KProfile.BIG_INTEGER;

		} else {
			profile = KProfile.BIG_RATIONAL;
		}
	}

	/**
	 * Set integer value.
	 * <p>
	 * For internal use by {@link KCalculator}.
	 *
	 * @param bigNumerator new numerator.
	 */
	public void setValue (BigInteger bigNumerator) {
		this.bigNumerator = bigNumerator;
		this.bigDenominator = BigInteger.ONE;
		profile = KProfile.BIG_INTEGER;
	}

	/**
	 * Convert current numerator, denominator to
	 * {@link java.math.BigInteger}.
	 */
	void setBigIntegers () {
		if (bigNumerator == null) {
			bigNumerator = new BigInteger (String.valueOf (numerator));
		}
		if (bigDenominator == null) {
			if (denominator == 1) {
				bigDenominator = BigInteger.ONE;
			} else {
				bigDenominator = new BigInteger (String.valueOf (denominator));
			}
		}
	}

	/**
	 * If the number is stored as
	 * {@link java.math.BigDecimal},
	 * try to revert to a simple fraction representation.
	 * <p>
	 * The conversion is actually performed only if
	 * it is estimated that it will be succesful.
	 */
	void compact () {
		if (bigNumerator.compareTo (MAX_LONG) <= 0
			&& bigNumerator.compareTo (MIN_LONG) >= 0
			&& bigDenominator.compareTo (MAX_LONG) <= 0
			&& bigDenominator.compareTo (MIN_LONG) >= 0) {

			setNormalizedValue (
				bigNumerator.longValue (),
				bigDenominator.longValue ()
			);
		}
	}

}
