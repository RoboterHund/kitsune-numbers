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
	 * Settings for calculations with infinite precision.
	 */
	public final MathContext exactMathContext;

	/**
	 * Settings for rounding of infinite decimal expansions.
	 */
	public final MathContext inexactMathContext;

	/**
	 * Status of last conversion.
	 *
	 * @see KConversionStatus
	 */
	public int lastConversionStatus;

	/**
	 * Constructor.
	 * <p>
	 * Set to {@link #DEFAULT_PRECISION}
	 * and {@link RoundingMode#HALF_UP}.
	 */
	public KConverter () {
		this (DEFAULT_PRECISION);
	}

	/**
	 * Constructor.
	 * <p>
	 * Set to {@link RoundingMode#HALF_UP}.
	 *
	 * @param precision {@link MathContext} {@code precision} setting.
	 */
	public KConverter (int precision) {
		this (new MathContext (precision));
	}

	/**
	 * Constructor.
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
	 * @param fromRegister number to convert.
	 * @return <code>double</code> with value as close to
	 * <code>fromRegister</code> as possible.
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
	 * @param fromRegister number to convert.
	 * @return {@link BigInteger} with value as close to
	 * <code>fromRegister</code> as possible.
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

			long longValue =
				fromRegister.numerator / fromRegister.denominator;

			return new BigInteger (String.valueOf (longValue));

		case KProfile.BIG_INTEGER:
			lastConversionStatus = KConversionStatus.OK;
			return fromRegister.bigNumerator;

		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			lastConversionStatus = KConversionStatus.OK;

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
				lastConversionStatus = KConversionStatus.OK;
				bigDecimal = new BigDecimal (fromRegister.bigNumerator)
					.divide (
						new BigDecimal (fromRegister.bigDenominator),
						exactMathContext
					);

			} catch (ArithmeticException e) {
				lastConversionStatus = KConversionStatus.INEXACT;
				bigDecimal = new BigDecimal (fromRegister.bigNumerator)
					.divide (
						new BigDecimal (fromRegister.bigDenominator),
						inexactMathContext
					);
			}
			return bigDecimal.stripTrailingZeros ();

		case KProfile.BIG_INTEGER:
			lastConversionStatus = KConversionStatus.OK;

			return new BigDecimal (fromRegister.bigNumerator);

		case KProfile.LONG_INTEGER:
		case KProfile.INT_INTEGER:
			lastConversionStatus = KConversionStatus.OK;

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
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromInt (
		KNumRegister toRegister,
		int value) {

		toRegister.setValue (value);
	}

	/**
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromLong (
		KNumRegister toRegister,
		long value) {

		toRegister.setValue (value);
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

			lastConversionStatus = KConversionStatus.OVERFLOW;
			return;
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
	 * @param toRegister register where to write number value.
	 * @param value new numeric value.
	 */
	public void fromBigInteger (
		KNumRegister toRegister,
		BigInteger value) {

		if (value.compareTo (KEdges.MAX_LONG) <= 0
			&& value.compareTo (KEdges.MIN_LONG) >= 0) {

			toRegister.setValue (value.longValue ());
			toRegister.bigNumerator = value;

		} else {
			toRegister.setValue (value);
		}
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
				if (setLongIntValue (toRegister, integerValue)) {
					return;
				}

			} else {
				// parse integer and decimal part
				decimalValue =
					value.substring (decPointPos + 1, lastNonZero + 1);

				// without this check,
				// a sign can be prepended to the decimal part
				if (!Character.isDigit (decimalValue.charAt (0))) {
					throw new NumberFormatException ();
				}

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
	 * Check the status of last conversion.
	 * <p>
	 * This is the inverse of {@link #lastConversionValid()}.
	 *
	 * @return {@code true} iff the last conversion was impossible.
	 */
	public boolean lastConversionFailed () {
		return lastConversionStatus == KConversionStatus.OVERFLOW;
	}

	/**
	 * Check the status of last conversion.
	 * <p>
	 * This is a weaker version of {@link #lastConversionExact()}.
	 *
	 * @return {@code true} iffthe last conversion yielded a valid value
	 * (but possibly inexact).
	 */
	public boolean lastConversionValid () {
		return lastConversionStatus >= KConversionStatus.OK;
	}

	/**
	 * Check the status of last conversion.
	 * <p>
	 * This is a stronger version of {@link #lastConversionValid()}.
	 *
	 * @return {@code true} iff the last conversion yielded the exact value.
	 */
	public boolean lastConversionExact () {
		return lastConversionStatus == KConversionStatus.OK;
	}

}
