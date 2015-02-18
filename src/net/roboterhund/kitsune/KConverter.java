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
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Conversion of other data types to and from
 * {@link KNumRegister}.
 * <p>
 * The converter will always try to produce exact results.
 * However, it can have various degrees of success, which should be checked
 * by examining {@link #lastConversionStatus},
 * or the return value of
 * {@link #lastConversionValid()} and/or related methods.
 */
public class KConverter {

	/**
	 * Maximum number of decimals accepted in a string to convert.
	 */
	private static final int MAX_DECIMALS = 18;

	/**
	 * Denominator corresponding to each number of decimals.
	 */
	private static final long[] DENOMINATORS;

	// compute denominator for each number of decimals
	static {
		final int numDenominators = MAX_DECIMALS + 1;
		DENOMINATORS = new long[numDenominators];
		long exponent = 1;
		for (int i = 1; i < numDenominators; i++) {
			exponent *= 10;
			DENOMINATORS[i] = exponent;
		}
	}

	/**
	 * Default precision of numbers with infinite decimal expansion.
	 */
	public static final int DEFAULT_PRECISION = 24;

	/**
	 * Settings for exact conversions.
	 */
	public final MathContext exactMathContext;

	/**
	 * Settings for rounding of infinite decimal expansions.
	 */
	public final MathContext inexactMathContext;

	/**
	 * Status of last conversion.
	 * Set to one of the constants defined in {@link KConversionStatus}.
	 * <p>
	 * Several methods, like {@link #lastConversionValid()},
	 * are provided for convenience.
	 */
	public int lastConversionStatus;

	/**
	 * Converter with {@link #DEFAULT_PRECISION} for inexact results
	 * and {@link RoundingMode#HALF_UP HALF_UP} rounding mode.
	 *
	 * @see MathContext#precision
	 * @see #KConverter(MathContext)
	 */
	public KConverter () {
		this (DEFAULT_PRECISION);
	}

	/**
	 * Converter with specified precision for inexact results
	 * and {@link RoundingMode#HALF_UP HALF_UP} rounding mode.
	 *
	 * @param precision {@link MathContext} {@code precision} setting.
	 * @see MathContext#precision
	 * @see #KConverter(MathContext)
	 */
	public KConverter (int precision) {
		this (new MathContext (precision));
	}

	/**
	 * Converter with specified precision and rounding mode.
	 * <p>
	 * The specified rounding mode is used for all conversions.
	 * <p>
	 * The specified precision is only used when a value with finite number
	 * of digits cannot be produced
	 * (the converter will generally try to use
	 * {@link #exactMathContext}).
	 *
	 * @param mathContext {@link #inexactMathContext}.
	 */
	public KConverter (MathContext mathContext) {
		this.inexactMathContext = mathContext;
		this.exactMathContext = new MathContext (
			0,
			mathContext.getRoundingMode ()
		);
	}

	/**
	 * Convert to {@code int}.
	 * <p>
	 * Rounded.
	 * <p>
	 * May overflow.
	 *
	 * @param fromRegister number to convert.
	 * @return {@code int} with value as close to
	 * {@code fromRegister} as possible.
	 */
	public int toInt (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
			BigInteger bigValue =
				fromRegister.bigNumerator
					.divide (fromRegister.bigDenominator);

			if (bigValue.compareTo (KEdges.MAX_INT) <= 0
				&& bigValue.compareTo (KEdges.MIN_INT) >= 0) {

				lastConversionStatus = KConversionStatus.INEXACT;
				return bigValue.intValue ();

			} else {
				lastConversionStatus = KConversionStatus.OVERFLOW;
			}
			break;

		case KProfile.LONG_RATIONAL:
			long longValue =
				fromRegister.numerator / fromRegister.denominator;

			if (longValue <= Integer.MAX_VALUE
				&& longValue >= Integer.MIN_VALUE) {

				lastConversionStatus = KConversionStatus.INEXACT;
				return (int) longValue;

			} else {
				lastConversionStatus = KConversionStatus.OVERFLOW;
			}
			break;

		case KProfile.INT_RATIONAL:
			lastConversionStatus = KConversionStatus.INEXACT;
			return (int) (fromRegister.numerator / fromRegister.denominator);

		case KProfile.BIG_INTEGER:
		case KProfile.LONG_INTEGER:
			lastConversionStatus = KConversionStatus.OVERFLOW;
			break;

		case KProfile.INT_INTEGER:
			lastConversionStatus = KConversionStatus.OK;
			return (int) fromRegister.numerator;

		default:
			throw newIllegalProfileException (fromRegister);
		}

		return 0;
	}

	/**
	 * Convert to {@code long}.
	 * <p>
	 * Rounded.
	 * <p>
	 * May overflow.
	 *
	 * @param fromRegister number to convert.
	 * @return {@code long} with value as close to
	 * {@code fromRegister} as possible.
	 */
	public long toLong (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
			BigInteger bigValue =
				fromRegister.bigNumerator
					.divide (fromRegister.bigDenominator);

			if (bigValue.compareTo (KEdges.MAX_LONG) <= 0
				&& bigValue.compareTo (KEdges.MIN_LONG) >= 0) {

				lastConversionStatus = KConversionStatus.INEXACT;
				return bigValue.longValue ();

			} else {
				lastConversionStatus = KConversionStatus.OVERFLOW;
			}
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			lastConversionStatus = KConversionStatus.INEXACT;
			return
				fromRegister.numerator / fromRegister.denominator;

		case KProfile.BIG_INTEGER:
			lastConversionStatus = KConversionStatus.OVERFLOW;
			break;

		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			lastConversionStatus = KConversionStatus.OK;
			return fromRegister.numerator;

		default:
			throw newIllegalProfileException (fromRegister);
		}

		return 0;
	}

	/**
	 * Convert to {@code double}.
	 * <p>
	 * This method
	 * is subject to the limitations of the {@code double} data type.
	 *
	 * @param fromRegister number to convert.
	 * @return {@code double} with value as close to
	 * {@code fromRegister} as possible.
	 */
	public double toDouble (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
			lastConversionStatus = KConversionStatus.INEXACT;

			return fromRegister.bigNumerator
				.divide (fromRegister.bigDenominator)
				.doubleValue ();

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			lastConversionStatus = KConversionStatus.INEXACT;
			return
				(double) fromRegister.numerator / fromRegister.denominator;

		case KProfile.BIG_INTEGER:
			double doubleValue = fromRegister.bigNumerator.doubleValue ();

			if (doubleValue == Double.POSITIVE_INFINITY
				|| doubleValue == Double.NEGATIVE_INFINITY) {
				lastConversionStatus = KConversionStatus.OVERFLOW;

			} else {
				lastConversionStatus = KConversionStatus.INEXACT;
			}
			return doubleValue;

		case KProfile.LONG_INTEGER:
			lastConversionStatus = KConversionStatus.INEXACT;
			return fromRegister.numerator;

		case KProfile.INT_INTEGER:
			lastConversionStatus = KConversionStatus.OK;
			return fromRegister.numerator;

		default:
			throw newIllegalProfileException (fromRegister);
		}
	}

	/**
	 * Convert to {@code BigInteger}.
	 * <p>
	 * Rounded.
	 *
	 * @param fromRegister number to convert.
	 * @return {@link BigInteger} with value as close to
	 * {@code fromRegister} as possible.
	 */
	public BigInteger toBigInteger (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
			lastConversionStatus = KConversionStatus.INEXACT;

			return fromRegister.bigNumerator
				.divide (fromRegister.bigDenominator);

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			lastConversionStatus = KConversionStatus.INEXACT;

			return BigInteger.valueOf (
				fromRegister.numerator / fromRegister.denominator
			);

		case KProfile.BIG_INTEGER:
			lastConversionStatus = KConversionStatus.OK;
			return fromRegister.bigNumerator;

		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			lastConversionStatus = KConversionStatus.OK;

			if (fromRegister.bigNumerator != null) {
				fromRegister.bigNumerator =
					BigInteger.valueOf (fromRegister.numerator);
			}
			return fromRegister.bigNumerator;

		default:
			throw newIllegalProfileException (fromRegister);
		}
	}

	/**
	 * Convert to {@code BigDecimal}.
	 * <p>
	 * Will fail to produce exact result
	 * if {@code fromRegister} contains a number
	 * with infinite decimal expansion.
	 *
	 * @param fromRegister number to convert.
	 * @return {@link BigDecimal} with value as close to
	 * {@code fromRegister} as possible.
	 */
	public BigDecimal toBigDecimal (KNumRegister fromRegister) {
		BigDecimal bigNumerator;
		BigDecimal bigDenominator;

		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
			fromRegister.setBigIntegers ();

			bigNumerator = new BigDecimal (fromRegister.bigNumerator);
			bigDenominator = new BigDecimal (fromRegister.bigDenominator);

			return divideFraction (bigNumerator, bigDenominator);

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			bigNumerator = BigDecimal.valueOf (fromRegister.numerator);
			bigDenominator = BigDecimal.valueOf (fromRegister.denominator);

			return divideFraction (bigNumerator, bigDenominator);

		case KProfile.BIG_INTEGER:
			lastConversionStatus = KConversionStatus.OK;

			return new BigDecimal (fromRegister.bigNumerator);

		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			lastConversionStatus = KConversionStatus.OK;

			return BigDecimal.valueOf (fromRegister.numerator);

		default:
			throw newIllegalProfileException (fromRegister);
		}
	}

	/**
	 * Divide numerator and denominator
	 * to get a single {@code BigDecimal}.
	 * <p>
	 * Set {@link #lastConversionStatus}.
	 */
	private BigDecimal divideFraction (
		BigDecimal bigNumerator,
		BigDecimal bigDenominator) {

		BigDecimal bigDecimal;
		try {
			lastConversionStatus = KConversionStatus.OK;
			bigDecimal =
				bigNumerator.divide (
					bigDenominator,
					exactMathContext
				);

		} catch (ArithmeticException e) {
			lastConversionStatus = KConversionStatus.INEXACT;
			bigDecimal =
				bigNumerator.divide (
					bigDenominator,
					inexactMathContext
				);
		}
		return bigDecimal.stripTrailingZeros ();
	}

	/**
	 * Convert to {@code String}.
	 *
	 * @param fromRegister number to convert.
	 * @return String in format
	 * {@code ['+'|'-'] {0..9}+ ['.' {0..9}+] }
	 * (signed or unsigned integer, may be followed by point and decimals).
	 */
	public String toString (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			return toBigDecimal (fromRegister)
				.stripTrailingZeros ()
				.toPlainString ();

		case KProfile.BIG_INTEGER:
			lastConversionStatus = KConversionStatus.OK;

			return fromRegister.bigNumerator.toString ();

		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			lastConversionStatus = KConversionStatus.OK;

			return String.valueOf (fromRegister.numerator);

		default:
			throw newIllegalProfileException (fromRegister);
		}
	}

	/**
	 * Read value from {@code int}.
	 *
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromInt (
		KNumRegister toRegister,
		int value) {

		toRegister.setValue (value);
	}

	/**
	 * Read value from {@code long}.
	 *
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromLong (
		KNumRegister toRegister,
		long value) {

		toRegister.setValue (value);
	}

	/**
	 * Read value from {@code double}.
	 * <p>
	 * <b>Note</b>: this method involves object creation and
	 * is subject to the limitations of the {@code double} data type.
	 *
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromDouble (
		KNumRegister toRegister,
		double value) {

		if (Double.isInfinite (value)
			|| Double.isNaN (value)) {
			// TODO actually handle this values
			throw new IllegalArgumentException ("value not supported");
		}

		BigDecimal bigDecimalValue;
		try {
			bigDecimalValue = new BigDecimal (value, exactMathContext);

		} catch (ArithmeticException e) {
			bigDecimalValue = new BigDecimal (value, inexactMathContext);
		}

		fromBigDecimal (
			toRegister,
			bigDecimalValue
		);
	}

	/**
	 * Read value from {@code BigInteger}.
	 *
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromBigInteger (
		KNumRegister toRegister,
		BigInteger value) {

		toRegister.setValue (value);
	}

	/**
	 * Read value from {@code BigDecimal}.
	 *
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromBigDecimal (
		KNumRegister toRegister,
		BigDecimal value) {

		fromString (toRegister, value.toPlainString ());
	}

	/**
	 * Read value from {@code String}.
	 *
	 * @param toRegister register where to write number value.
	 * @param value string in format
	 * {@code ['+'|'-'] {0..9}+ ['.' {0..9}+] }
	 * (signed or unsigned integer, may be followed by point and decimals).
	 * @throws NumberFormatException unable to parse string.
	 */
	public void fromString (
		KNumRegister toRegister,
		String value) {

		String integerValue = value;
		String decimalValue = null;

		int decPointPos = value.indexOf ('.');
		if (decPointPos == -1) {
			// no decimals
			if (setLongIntValue (toRegister, value)) {
				return;
			}

		} else {
			// with decimals

			// get integer part
			integerValue = value.substring (0, decPointPos);

			// locate trailing zero digits
			int lastNonZero;
			for (lastNonZero = value.length () - 1;
			     lastNonZero > decPointPos;
			     lastNonZero--) {

				if (value.charAt (lastNonZero) != '0') {
					break;
				}
			}

			if (lastNonZero == decPointPos) {
				// all decimal digits are zero
				if (setLongIntValue (toRegister, integerValue)) {
					return;
				}

			} else {
				// get decimal part
				decimalValue =
					value.substring (decPointPos + 1, lastNonZero + 1);

				// without this check,
				// a sign can be prepended to the decimal part
				if (!Character.isDigit (decimalValue.charAt (0))) {
					throw new NumberFormatException ();
				}

				// parse integer and decimal part
				if (setLongIntValue (toRegister, integerValue, decimalValue)) {
					return;
				}
			}
		}

		BigInteger bigIntegerValue = new BigInteger (integerValue);

		if (decimalValue == null) {
			// set integer value
			toRegister.setValue (bigIntegerValue);

		} else {
			BigInteger bigDecimalValue = new BigInteger (decimalValue);

			// get denominator as power of 10
			BigInteger bigDenominator =
				BigInteger.TEN.pow (decimalValue.length ());

			// multiply numerator by power of 10
			BigInteger bigNumerator =
				bigIntegerValue.multiply (bigDenominator);

			// add decimal part to numerator
			if (integerValue.charAt (0) == '-') {
				bigNumerator = bigNumerator.subtract (bigDecimalValue);
			} else {
				bigNumerator = bigNumerator.add (bigDecimalValue);
			}

			// normalize fraction
			toRegister.setValue (bigNumerator, bigDenominator);
		}
	}

	/**
	 * Try to parse string as
	 * an integer numeric value.
	 *
	 * @param integerValue integer part of number.
	 * @return {@code true} iff successful.
	 */
	private boolean setLongIntValue (
		KNumRegister toRegister,
		String integerValue) {

		try {
			toRegister.setValue (Long.parseLong (integerValue));
			return true;

		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Try to parse strings
	 * as the integer part
	 * and the decimals.
	 *
	 * @param integerValue integer part of number.
	 * @param decimalValue decimals of number.
	 * @return {@code true} iff successful.
	 */
	private boolean setLongIntValue (
		KNumRegister toRegister,
		String integerValue,
		String decimalValue) {

		try {
			// store integer part in numerator
			long numerator = Long.parseLong (integerValue);

			// get denominator as power of 10
			int numDecimals = decimalValue.length ();
			if (numDecimals > 18) {
				// too many decimals
				return false;
			}
			long denominator = DENOMINATORS[numDecimals];

			// multiply numerator by power of 10
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

			// normalize fraction
			toRegister.setValue (numerator, denominator);
			return true;

		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Convenience method to throw exception.
	 *
	 * @param register number with invalid profile.
	 * @return New {@link IllegalArgumentException}.
	 */
	private static IllegalArgumentException newIllegalProfileException (
		KNumRegister register) {

		return new IllegalArgumentException (
			"Invalid KRegister profile: "
				+ register.profile
				+ ".");
	}

	/**
	 * Possible results of a conversion (whether it was exact or not, etc.)
	 */
	public static abstract class KConversionStatus {

		/**
		 * Last conversion was unsuccessful, because
		 * the numeric value is outside of the range
		 * of the destination data type.
		 */
		public static final int OVERFLOW = -1;

		/**
		 * Last conversion produced the exact value, guaranteed.
		 */
		public static final int OK = 0;

		/**
		 * Last conversion produced an value
		 * that is not guaranteed to be exact.
		 */
		public static final int INEXACT = 1;

	}

	/**
	 * Convenience method to check last conversion.
	 * <p>
	 * This is the inverse of {@link #lastConversionValid()}.
	 *
	 * @return {@code true} iff the last conversion was impossible.
	 */
	public boolean lastConversionFailed () {
		return lastConversionStatus == KConversionStatus.OVERFLOW;
	}

	/**
	 * Convenience method to check last conversion.
	 * <p>
	 * This is a weaker version of {@link #lastConversionExact()}
	 * and the inverse of {@link #lastConversionFailed()}.
	 *
	 * @return {@code true} iff the last conversion yielded
	 * an exact <b>or</b> approximate value.
	 */
	public boolean lastConversionValid () {
		return lastConversionStatus >= KConversionStatus.OK;
	}

	/**
	 * Convenience method to check last conversion.
	 * <p>
	 * This is a stronger version of {@link #lastConversionValid()}.
	 *
	 * @return {@code true} iff the last conversion yielded the exact value.
	 */
	public boolean lastConversionExact () {
		return lastConversionStatus == KConversionStatus.OK;
	}

}
