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
public class KNumber {

	/**
	 * Default precision of numbers with infinite decimal expansion,
	 * unless overridden by
	 * {@link KNumber#defaultPrecision}.
	 */
	public static final int DEFAULT_PRECISION = 24;

	/**
	 * Global setting of default precision
	 * of numbers with infinite decimal expansion.
	 * <p>
	 * The default value is
	 * {@link KNumber#DEFAULT_PRECISION}.
	 */
	public static int defaultPrecision = DEFAULT_PRECISION;

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

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue()} call on existing object.
	 */
	public KNumber () {
		setValue ();
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(KNumber)} call on existing object.
	 *
	 * @param number number copied to <code>this</code>.
	 */
	public KNumber (KNumber number) {
		setValue (number);
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(long, long)} call on existing object.
	 *
	 * @param numerator numerator.
	 * @param denominator denominator.
	 * @deprecated // TODO remove
	 */
	KNumber (long numerator, long denominator) {
		setValue (numerator, denominator);
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(java.math.BigDecimal)} call on existing
	 * object.
	 *
	 * @param bigDecimal value.
	 */
	public KNumber (BigDecimal bigDecimal) {
		setValue (bigDecimal);
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(long)} call on existing object.
	 *
	 * @param longValue integer value.
	 */
	public KNumber (long longValue) {
		setValue (longValue);
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(double)} call on existing object.
	 * <p>
	 * <b>Note</b>: actual value is subject to the limitations of
	 * the <code>double</code> data type.
	 *
	 * @param doubleValue value,
	 * not guaranteed to match source code representation.
	 */
	public KNumber (double doubleValue) {
		setValue (doubleValue);
	}

	/**
	 * Constructor.
	 * <p>
	 * Equivalent to a {@link #setValue(double)} call on existing object.
	 *
	 * @param stringValue string in format
	 * <code>['+'|'-']{0..9}+['.'{0..9}+]</code>
	 * (signed or unsigned integer with optional point followed by decimals).
	 */
	public KNumber (String stringValue) {
		setValue (stringValue);
	}

	/**
	 * Set value to zero.
	 */
	public void setValue () {
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
	public void setValue (KNumber number) {
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
	void setValue (long numerator, long denominator) {
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
	 * Set value.
	 *
	 * @param bigDecimal new value, assigned directly.
	 */
	public void setValue (BigDecimal bigDecimal) {
		setValue (bigDecimal.toPlainString ());
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

			if (bigDenominator.compareTo (BigInteger.ONE) == 0) {
				bigDenominator = BigInteger.ONE;
			}
		}

		if (this.bigDenominator.compareTo (BigInteger.ZERO) < 0) {
			this.bigNumerator = bigNumerator.negate ();
			this.bigDenominator = bigDenominator.negate ();
		}

		//noinspection NumberEquality
		if (bigDenominator == BigInteger.ONE) {
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
	 * <b>Note</b>: this method involves object creation and
	 * is subject to the limitations of the <code>double</code> data type.
	 *
	 * @param doubleValue value,
	 * not guaranteed to match source code representation.
	 */
	public void setValue (double doubleValue) {
		// TODO move all setValue methods with non-essential data types elsewhere
		// TODO replace with more efficient method
		setValue (new BigDecimal (doubleValue).toPlainString ());
	}

	/**
	 * Set value from string.
	 *
	 * @param stringValue string in format
	 * <code>['+'|'-']{0..9}+['.'{0..9}+]</code>
	 * (signed or unsigned integer with optional point followed by decimals).
	 * @throws NumberFormatException unable to parse string.
	 */
	public void setValue (String stringValue) {
		String integerValue = stringValue;
		String decimalValue = null;

		int decPointPos = stringValue.indexOf ('.');
		if (decPointPos == -1) {
			// no decimals
			if (setLongIntValue (stringValue)) {
				return;
			}

		} else {
			// with decimals
			// store integer part in numerator
			integerValue = stringValue.substring (0, decPointPos);

			// get decimals
			// TODO check for invalid decimals substring
			int lastNonZero;
			for (lastNonZero = stringValue.length () - 1;
			     lastNonZero > decPointPos;
			     lastNonZero--) {

				if (stringValue.charAt (lastNonZero) != '0') {
					break;
				}
			}

			if (lastNonZero == decPointPos) {
				// decimals = 0
				if (setLongIntValue (stringValue)) {
					return;
				}

			} else {
				// parse integer and decimal part
				decimalValue =
					stringValue.substring (decPointPos + 1, lastNonZero + 1);

				if (setLongIntValue (integerValue, decimalValue)) {
					return;
				}
			}
		}

		if (decimalValue == null) {
			setValue (new BigInteger (integerValue), BigInteger.ONE);

		} else {
			BigInteger bigDenominator =
				BigInteger.TEN.pow (decimalValue.length ());

			BigInteger bigNumerator =
				new BigInteger (integerValue)
					.multiply (bigDenominator)
					.add (new BigInteger (decimalValue));

			setValue (bigNumerator, bigDenominator);
		}
	}

	/**
	 * Try to parse string and
	 * set the numeric value as a simple fraction.
	 *
	 * @param integerValue string to parse.
	 * @return <code>true</code> iff successful.
	 */
	private boolean setLongIntValue (String integerValue) {
		try {
			setValue (Long.parseLong (integerValue));
			return true;

		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Try to parse string and
	 * set the numeric value as a simple fraction.
	 *
	 * @param integerValue string to parse.
	 * @return <code>true</code> iff successful.
	 */
	private boolean setLongIntValue (
		String integerValue,
		String decimalValue) {

		try {
			// store integer part in numerator
			long numerator = Long.parseLong (integerValue);
			long denominator = 1;

			// get denominator as scale of 10
			long multiplier = 10;
			int exp = decimalValue.length ();
			long maxMultiplier = Long.MAX_VALUE / multiplier;
			while (exp != 0) {
				if ((exp & 1) != 0) {
					if (denominator > maxMultiplier) {
						return false;
					}
					denominator *= multiplier;
				}
				if (multiplier > maxMultiplier) {
					return false;
				}
				multiplier *= multiplier;
				maxMultiplier = Long.MAX_VALUE / multiplier;
				exp >>= 1;
			}

			// multiply numerator by scale of 10
			if (numerator > Long.MAX_VALUE / denominator
				|| numerator < Long.MIN_VALUE / denominator) {
				//    factor_1 * factor_2 > Long.MAX_VALUE
				// or factor_1 * factor_2 < Long.MIN_VALUE
				return false;
			}
			numerator *= denominator;

			// add decimal part to numerator
			long decimalPart = Long.parseLong (decimalValue);
			if (integerValue.charAt (0) == '-') {
				numerator -= decimalPart;
			} else {
				numerator += decimalPart;
			}

			// normalize
			setValue (numerator, denominator);
			return true;

		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Convert to BigDecimal.
	 *
	 * @return BigDecimal, with
	 * {@link KNumber#defaultPrecision}
	 * decimals if number has infinite decimal expansion.
	 */
	public BigDecimal toBigDecimal () {
		return toBigDecimal (defaultPrecision);
	}

	/**
	 * Convert to BigDecimal.
	 *
	 * @param precision number of decimals to store if the
	 * number has infinite decimal expansion.
	 * @return BigDecimal, with specified precision if the
	 * number has infinite decimal expansion.
	 */
	public BigDecimal toBigDecimal (int precision) {
		if (profile <= KProfile.BIG_INTEGER) {
			return new BigDecimal (bigNumerator)
				.divide (
					new BigDecimal (bigDenominator),
					precision,
					BigDecimal.ROUND_HALF_UP
				);

		} else {
			return new BigDecimal (numerator)
				.divide (
					new BigDecimal (denominator),
					precision,
					BigDecimal.ROUND_HALF_UP
				);
		}
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
	public void compact () {
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
