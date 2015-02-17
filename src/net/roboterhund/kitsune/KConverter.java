package net.roboterhund.kitsune;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Conversion of other data types to and from
 * {@link KNumRegister}.
 */
public class KConverter {

	/**
	 * {@link Integer#MAX_VALUE} as {@link java.math.BigInteger}.
	 */
	public static final BigInteger MAX_INT =
		new BigInteger (String.valueOf (Integer.MAX_VALUE));

	/**
	 * {@link Integer#MIN_VALUE} as {@link java.math.BigInteger}.
	 */
	public static final BigInteger MIN_INT =
		new BigInteger (String.valueOf (Integer.MIN_VALUE));

	/**
	 *
	 */
	public static abstract class KConversionStatus {

		/**
		 * Last conversion produced the exact value.
		 */
		public static final int OK = 0;

		/**
		 * Last conversion produced an inexact value.
		 */
		public static final int INEXACT = 1;

		/**
		 * Last conversion was unsuccessful, because
		 * the numeric value is outside of the range
		 * of the destination data type.
		 */
		public static final int OVERFLOW = 2;

	}

	/**
	 * Default precision of numbers with infinite decimal expansion,
	 * unless overridden by
	 * {@link net.roboterhund.kitsune.KConverter#precision}.
	 */
	public static final int DEFAULT_PRECISION = 24;

	/**
	 * Precision of numbers with infinite decimal expansion.
	 * <p>
	 * The default value is
	 * {@link net.roboterhund.kitsune.KConverter#DEFAULT_PRECISION}.
	 */
	public int precision = DEFAULT_PRECISION;

	/**
	 * Status of last conversion.
	 *
	 * @see KConversionStatus
	 */
	public int lastOperationStatus;

	/**
	 * @param fromRegister number to convert.
	 * @return <code>int</code> with value as close to
	 * <code>fromRegister</code> as possible.
	 */
	public int toInt (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
			BigInteger bigValue =
				fromRegister.bigNumerator
					.divide (fromRegister.bigDenominator);

			if (bigValue.compareTo (KConverter.MAX_INT) <= 0
				&& bigValue.compareTo (KConverter.MIN_INT) >= 0) {

				lastOperationStatus = KConversionStatus.INEXACT;
				return bigValue.intValue ();

			} else {
				lastOperationStatus = KConversionStatus.OVERFLOW;
			}
			break;

		case KProfile.LONG_RATIONAL:
			long longValue =
				fromRegister.numerator / fromRegister.denominator;

			if (longValue <= Integer.MAX_VALUE
				&& longValue >= Integer.MIN_VALUE) {

				lastOperationStatus = KConversionStatus.INEXACT;
				return (int) longValue;

			} else {
				lastOperationStatus = KConversionStatus.OVERFLOW;
			}
			break;

		case KProfile.INT_RATIONAL:
			lastOperationStatus = KConversionStatus.INEXACT;
			return (int) (fromRegister.numerator / fromRegister.denominator);

		case KProfile.BIG_INTEGER:
		case KProfile.LONG_INTEGER:
			lastOperationStatus = KConversionStatus.OVERFLOW;
			break;

		case KProfile.INT_INTEGER:
			lastOperationStatus = KConversionStatus.OK;
			return (int) fromRegister.numerator;

		default:
			throw newIllegalProfileException (fromRegister);
		}

		return 0;
	}

	/**
	 * @param fromRegister number to convert.
	 * @return <code>long</code> with value as close to
	 * <code>fromRegister</code> as possible.
	 */
	public long toLong (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
			BigInteger bigValue =
				fromRegister.bigNumerator
					.divide (fromRegister.bigDenominator);

			if (bigValue.compareTo (KNumRegister.MAX_LONG) <= 0
				&& bigValue.compareTo (KNumRegister.MIN_LONG) >= 0) {

				lastOperationStatus = KConversionStatus.INEXACT;
				return bigValue.longValue ();

			} else {
				lastOperationStatus = KConversionStatus.OVERFLOW;
			}
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			lastOperationStatus = KConversionStatus.INEXACT;
			return
				fromRegister.numerator / fromRegister.denominator;

		case KProfile.BIG_INTEGER:
			lastOperationStatus = KConversionStatus.OVERFLOW;
			break;

		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			lastOperationStatus = KConversionStatus.OK;
			return fromRegister.numerator;

		default:
			throw newIllegalProfileException (fromRegister);
		}

		return 0;
	}

	/**
	 * @param fromRegister number to convert.
	 * @return <code>double</code> with value as close to
	 * <code>fromRegister</code> as possible.
	 */
	public double toDouble (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
			lastOperationStatus = KConversionStatus.INEXACT;

			return fromRegister.bigNumerator
				.divide (fromRegister.bigDenominator)
				.doubleValue ();

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			lastOperationStatus = KConversionStatus.INEXACT;
			return
				(double) fromRegister.numerator / fromRegister.denominator;

		case KProfile.BIG_INTEGER:
			double doubleValue = fromRegister.bigNumerator.doubleValue ();

			if (doubleValue != Double.POSITIVE_INFINITY
				&& doubleValue != Double.NEGATIVE_INFINITY) {

				lastOperationStatus = KConversionStatus.INEXACT;

			} else {
				lastOperationStatus = KConversionStatus.OVERFLOW;
			}
			return doubleValue;

		case KProfile.LONG_INTEGER:
			lastOperationStatus = KConversionStatus.INEXACT;
			return fromRegister.numerator;

		case KProfile.INT_INTEGER:
			lastOperationStatus = KConversionStatus.OK;
			return fromRegister.numerator;

		default:
			throw newIllegalProfileException (fromRegister);
		}
	}

	/**
	 * @param fromRegister number to convert.
	 * @return {@link BigInteger} with value as close to
	 * <code>fromRegister</code> as possible.
	 */
	public BigInteger toBigInteger (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
			lastOperationStatus = KConversionStatus.INEXACT;

			return fromRegister.bigNumerator
				.divide (fromRegister.bigDenominator);

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			lastOperationStatus = KConversionStatus.INEXACT;

			long longValue =
				fromRegister.numerator / fromRegister.denominator;

			return new BigInteger (String.valueOf (longValue));

		case KProfile.BIG_INTEGER:
			lastOperationStatus = KConversionStatus.OK;
			return fromRegister.bigNumerator;

		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			lastOperationStatus = KConversionStatus.OK;

			if (fromRegister.bigNumerator != null) {
				fromRegister.bigNumerator =
					new BigInteger (String.valueOf (fromRegister.numerator));
			}
			return fromRegister.bigNumerator;

		default:
			throw newIllegalProfileException (fromRegister);
		}
	}

	/**
	 * @param fromRegister number to convert.
	 * @return {@link BigDecimal} with value as close to
	 * <code>fromRegister</code> as possible.
	 */
	public BigDecimal toBigDecimal (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			fromRegister.setBigIntegers ();
			BigDecimal bigDecimal;
			try {
				lastOperationStatus = KConversionStatus.OK;
				bigDecimal = new BigDecimal (fromRegister.bigNumerator)
					.divide (
						new BigDecimal (fromRegister.bigDenominator),
						precision,
						RoundingMode.UNNECESSARY
					);

			} catch (ArithmeticException e) {
				lastOperationStatus = KConversionStatus.INEXACT;
				bigDecimal = new BigDecimal (fromRegister.bigNumerator)
					.divide (
						new BigDecimal (fromRegister.bigDenominator),
						precision,
						RoundingMode.HALF_UP
					);
			}
			return bigDecimal.stripTrailingZeros ();

		case KProfile.BIG_INTEGER:
			lastOperationStatus = KConversionStatus.OK;

			return new BigDecimal (fromRegister.bigNumerator);

		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			lastOperationStatus = KConversionStatus.OK;

			if (fromRegister.bigNumerator != null) {
				return new BigDecimal (fromRegister.bigNumerator);

			} else {
				return new BigDecimal (fromRegister.numerator);
			}

		default:
			throw newIllegalProfileException (fromRegister);
		}
	}

	/**
	 * @param fromRegister number to convert.
	 * @return new numeric value.
	 */
	public String toString (KNumRegister fromRegister) {
		switch (fromRegister.profile) {
		case KProfile.BIG_RATIONAL:
		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			fromRegister.setBigIntegers ();

			BigDecimal bigValue;
			try {
				lastOperationStatus = KConversionStatus.OK;
				bigValue = new BigDecimal (fromRegister.bigNumerator)
					.divide (
						new BigDecimal (fromRegister.bigDenominator),
						precision,
						RoundingMode.UNNECESSARY
					);

			} catch (ArithmeticException e) {
				lastOperationStatus = KConversionStatus.INEXACT;
				bigValue = new BigDecimal (fromRegister.bigNumerator)
					.divide (
						new BigDecimal (fromRegister.bigDenominator),
						precision,
						RoundingMode.HALF_UP
					);
			}

			return bigValue.stripTrailingZeros ().toPlainString ();

		case KProfile.BIG_INTEGER:
			lastOperationStatus = KConversionStatus.OK;

			return fromRegister.bigNumerator.toString ();

		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			lastOperationStatus = KConversionStatus.OK;

			return String.valueOf (fromRegister.numerator);

		default:
			throw newIllegalProfileException (fromRegister);
		}
	}

	/**
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromInt (
		KNumRegister toRegister,
		int value) {

		toRegister.profile = KProfile.INT_INTEGER;

		toRegister.numerator = value;
		toRegister.denominator = 1;

		toRegister.bigNumerator = null;
		toRegister.bigDenominator = null;
	}

	/**
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromLong (
		KNumRegister toRegister,
		long value) {

		toRegister.profile = ((
			value <= Integer.MAX_VALUE
				&& value >= Integer.MIN_VALUE
		)) ?
			KProfile.INT_INTEGER :
			KProfile.LONG_INTEGER;

		toRegister.numerator = value;
		toRegister.denominator = 1;

		toRegister.bigNumerator = null;
		toRegister.bigDenominator = null;
	}

	/**
	 * <p>
	 * <b>Note</b>: this method involves object creation and
	 * is subject to the limitations of the <code>double</code> data type.
	 *
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromDouble (
		KNumRegister toRegister,
		double value) {

		if (Double.isInfinite (value)
			|| Double.isNaN (value)) {

			lastOperationStatus = KConversionStatus.OVERFLOW;
			return;
		}

		fromBigDecimal (
			toRegister,
			new BigDecimal (value, new MathContext (precision))
		);
	}

	/**
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromBigInteger (
		KNumRegister toRegister,
		BigInteger value) {

		if (value.compareTo (MAX_INT) <= 0
			&& value.compareTo (MIN_INT) >= 0) {

			toRegister.profile = KProfile.INT_INTEGER;

			toRegister.numerator = value.intValue ();
			toRegister.denominator = 1;

		} else if (value.compareTo (KNumRegister.MAX_LONG) <= 0
			&& value.compareTo (KNumRegister.MIN_LONG) >= 0) {

			toRegister.profile = KProfile.LONG_INTEGER;

			toRegister.numerator = value.longValue ();
			toRegister.denominator = 1;

		} else {
			toRegister.profile = KProfile.BIG_INTEGER;
		}

		toRegister.bigNumerator = value;
		toRegister.bigDenominator = null;
	}

	/**
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromBigDecimal (
		KNumRegister toRegister,
		BigDecimal value) {

		fromString (toRegister, value.toPlainString ());
	}

	/**
	 * @param toRegister register where to write number value.
	 * @param value string in format
	 * <code>['+'|'-']{0..9}+['.'{0..9}+]</code>
	 * (signed or unsigned integer with optional point followed by decimals).
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
			// store integer part in numerator
			integerValue = value.substring (0, decPointPos);

			// get decimals
			// TODO check for invalid decimals substring
			int lastNonZero;
			for (lastNonZero = value.length () - 1;
			     lastNonZero > decPointPos;
			     lastNonZero--) {

				if (value.charAt (lastNonZero) != '0') {
					break;
				}
			}

			if (lastNonZero == decPointPos) {
				// decimals = 0
				if (setLongIntValue (toRegister, value)) {
					return;
				}

			} else {
				// parse integer and decimal part
				decimalValue =
					value.substring (decPointPos + 1, lastNonZero + 1);

				if (setLongIntValue (toRegister, integerValue, decimalValue)) {
					return;
				}
			}
		}

		if (decimalValue == null) {
			toRegister.setValue (
				new BigInteger (integerValue),
				BigInteger.ONE
			);

		} else {
			BigInteger bigDenominator =
				BigInteger.TEN.pow (decimalValue.length ());

			BigInteger bigNumerator =
				new BigInteger (integerValue)
					.multiply (bigDenominator);

			if (integerValue.charAt (0) == '-') {
				bigNumerator =
					bigNumerator.subtract (new BigInteger (decimalValue));

			} else {
				bigNumerator =
					bigNumerator.add (new BigInteger (decimalValue));
			}

			toRegister.setValue (bigNumerator, bigDenominator);
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
	 * Try to parse string and
	 * set the numeric value as a simple fraction.
	 *
	 * @param integerValue string to parse.
	 * @return <code>true</code> iff successful.
	 */
	private boolean setLongIntValue (
		KNumRegister toRegister,
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
			toRegister.setValue (numerator, denominator);
			return true;

		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * @param register number with invalid profile.
	 * @return New {@link IllegalArgumentException}.
	 */
	private static IllegalArgumentException
	newIllegalProfileException (KNumRegister register) {
		return new IllegalArgumentException (
			"Invalid KRegister profile: "
				+ register.profile
				+ ".");
	}

}
