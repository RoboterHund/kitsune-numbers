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
	 * Big values fallback.
	 */
	BigDecimal bigDecimal;

	/**
	 * Approximated value of {@link #bigDecimal}.
	 * <p>
	 * Used to compact values automatically.
	 */
	double approx;

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
		bigDecimal = null;
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
		bigDecimal = number.bigDecimal;
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
		// store initial values
		this.numerator = numerator;
		this.denominator = denominator;

		if (denominator != 1) {
			// find greatest common divisor
			// Euclid's algorithm
			long modulo;
			while (numerator != 0) {
				modulo = denominator % numerator;
				denominator = numerator;
				numerator = modulo;
			}

			// normalize result
			this.numerator /= denominator;
			this.denominator /= denominator;

		}

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
		bigDecimal = null;
	}

	/**
	 * Set value.
	 *
	 * @param bigDecimal new value, assigned directly.
	 */
	public void setValue (BigDecimal bigDecimal) {
		profile = KProfile.BIG;
		this.bigDecimal = bigDecimal;
		// other members no longer relevant
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
		bigDecimal = null;
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
		if (setLongIntValue (stringValue)) {
			return;
		}
		setValue (new BigDecimal (stringValue));
	}

	/**
	 * Try to parse string and
	 * set the numeric value as a simple fraction.
	 *
	 * @param stringValue string to parse.
	 * @return <code>true</code> iff successful.
	 */
	private boolean setLongIntValue (String stringValue) {
		try {
			int decPointPos = stringValue.indexOf ('.');
			if (decPointPos == -1) {
				// no decimals
				setValue (Long.parseLong (stringValue));

			} else {
				// with decimals
				// store integer part in numerator
				long numerator =
					Long.parseLong (stringValue.substring (0, decPointPos));
				long denominator = 1;

				// get decimals
				// TODO check for invalid decimals substring
				String decimals = stringValue.substring (decPointPos + 1);
				int lastNonZero;
				for (lastNonZero = decimals.length () - 1;
				     lastNonZero >= 0;
				     lastNonZero--) {

					if (decimals.charAt (lastNonZero) != '0') {
						break;
					}
				}
				decimals = decimals.substring (0, lastNonZero + 1);
				if (!decimals.isEmpty ()) {
					// get denominator as scale of 10
					long multiplier = 10;
					int exp = decimals.length ();
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
					if (numerator > Long.MAX_VALUE / denominator) {
						return false;
					}
					numerator *= denominator;

					// add decimal part to numerator
					numerator += Long.parseLong (decimals);
				}

				// normalize
				setValue (numerator, denominator);
			}
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
		if (bigDecimal != null) {
			return bigDecimal;

		} else {
			approx = (double) numerator / denominator;

			BigDecimal numeratorBigDecimal = new BigDecimal (numerator);
			BigDecimal denominatorBigDecimal = new BigDecimal (denominator);
			try {
				//noinspection BigDecimalMethodWithoutRoundingCalled
				return numeratorBigDecimal.divide (denominatorBigDecimal);

			} catch (ArithmeticException e) {
				// number with infinite decimal expansion
				// precision lost
				return numeratorBigDecimal.divide (
					denominatorBigDecimal,
					precision,
					BigDecimal.ROUND_HALF_UP
				);
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
		if (bigDecimal != null
			&& approx < Long.MAX_VALUE
			&& approx > Long.MIN_VALUE) {

			setValue (bigDecimal.toPlainString ());
		}
	}

}
