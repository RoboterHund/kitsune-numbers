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
 *
 */
public class KNumber {

	public static final int DEFAULT_PRECISION = 24;

	public static int defaultPrecision = DEFAULT_PRECISION;

	/**
	 * <code>int</code> size flag.
	 * <p>
	 * If <code>true</code>, both numerator and denominator
	 * fit in a Java int/Integer.
	 */
	boolean fitsInInt;

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
	 */
	long denominator;

	/**
	 * Big values fallback.
	 */
	BigDecimal bigDecimal;

	/**
	 * Constructor.
	 */
	public KNumber () {
		setValue ();
	}

	/**
	 * Constructor.
	 *
	 * @param number
	 */
	public KNumber (KNumber number) {
		setValue (number);
	}

	/**
	 * Constructor.
	 *
	 * @param numerator
	 * @param denominator
	 */
	KNumber (long numerator, long denominator) {
		setValue (numerator, denominator);
	}

	/**
	 * Constructor.
	 *
	 * @param bigDecimal
	 */
	public KNumber (BigDecimal bigDecimal) {
		setValue (bigDecimal);
	}

	/**
	 * Constructor.
	 *
	 * @param longValue
	 */
	public KNumber (long longValue) {
		setValue (longValue);
	}

	/**
	 * Constructor.
	 *
	 * @param doubleValue
	 */
	public KNumber (double doubleValue) {
		setValue (doubleValue);
	}

	/**
	 * Constructor.
	 *
	 * @param stringValue
	 */
	public KNumber (String stringValue) {
		setValue (stringValue);
	}

	/**
	 *
	 */
	public void setValue () {
		fitsInInt = true;
		numerator = 0;
		denominator = 1;
		bigDecimal = null;
	}

	/**
	 * @param number
	 */
	public void setValue (KNumber number) {
		fitsInInt = number.fitsInInt;
		numerator = number.numerator;
		denominator = number.denominator;
		bigDecimal = number.bigDecimal;
	}

	/**
	 * @param numerator
	 * @param denominator
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

		// TODO optimize check
		// check if fits in int
		fitsInInt = (
			(
				this.numerator <= Integer.MAX_VALUE
					&& this.numerator >= Integer.MIN_VALUE
			) && (
				this.denominator <= Integer.MAX_VALUE
					&& this.denominator >= Integer.MIN_VALUE)
		);

		// discard old value, if any
		bigDecimal = null;
	}

	/**
	 * @param bigDecimal
	 */
	public void setValue (BigDecimal bigDecimal) {
		fitsInInt = false;
		this.bigDecimal = bigDecimal;
		// other members no longer relevant
	}

	/**
	 * @param longValue
	 */
	public void setValue (long longValue) {
		// store initial values
		this.numerator = longValue;
		this.denominator = 1;

		// check if fits in int
		fitsInInt = (
			this.numerator <= Integer.MAX_VALUE
				&& this.numerator >= Integer.MIN_VALUE
		);

		// discard old value, if any
		bigDecimal = null;
	}

	/**
	 * TODO move all setValue methods with non-essential data types elsewhere
	 *
	 * @param doubleValue
	 */
	public void setValue (double doubleValue) {
		// TODO replace with more efficient method
		setValue (new BigDecimal (doubleValue).toPlainString ());
	}

	/**
	 * @param stringValue
	 */
	public void setValue (String stringValue) {
		if (setLongIntValue (stringValue)) {
			return;
		}
		bigDecimal = new BigDecimal (stringValue);
	}

	/**
	 * @param stringValue
	 * @return
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

				// get decimals
				String decimals = stringValue.substring (decPointPos + 1);

				// get denominator as scale of 10
				long multiplier = 10;
				int exp = decimals.length ();
				long denominator = 1;
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
	 * @return BigDecimal, 24 decimals if infinite expansion.
	 */
	public BigDecimal toBigDecimal () {
		return toBigDecimal (defaultPrecision);
	}

	/**
	 * Convert to BigDecimal.
	 *
	 * @return BigDecimal, 24 decimals if infinite expansion.
	 */
	public BigDecimal toBigDecimal (int precision) {
		if (bigDecimal != null) {
			return bigDecimal;

		} else {
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
	 *
	 */
	public void compact () {
		if (bigDecimal != null) {
			setValue (bigDecimal.toPlainString ());
		}
	}

}
