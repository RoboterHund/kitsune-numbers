package net.roboterhund.kitsune;

import java.math.BigInteger;

/**
 * Operations:
 * <ul>
 * <li>Integer.</li>
 * <li>TODO Ceiling.</li>
 * <li>TODO Floor.</li>
 * </ul>
 */
public abstract class KCalcRound {

	/**
	 * Get the integer part of a number.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public static void truncate (
		KNumRegister result,
		KNumRegister number) {

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
			number.setBigIntegers ();
			result.setValue (
				number.bigNumerator.divide (number.bigDenominator)
			);
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			result.setValue (
				number.numerator / number.denominator
			);
			break;

		default:
			if (result != number) {
				result.copy (number);
			}
			break;
		}
	}

	/**
	 * Get {@code floor (number)}.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public static void floor (
		KNumRegister result,
		KNumRegister number) {

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
			number.setBigIntegers ();
			BigInteger bigValue =
				number.bigNumerator.divide (number.bigDenominator);
			if (bigValue.signum () < 0) {
				bigValue = bigValue.subtract (BigInteger.ONE);
			}
			result.setValue (
				bigValue
			);
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			long longValue = number.numerator / number.denominator;
			if (longValue < 0) {
				--longValue;
			}
			result.setValue (
				longValue
			);
			break;

		default:
			if (result != number) {
				result.copy (number);
			}
			break;
		}
	}

	/**
	 * Get {@code ceiling (number)}.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public static void ceiling (
		KNumRegister result,
		KNumRegister number) {

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
			number.setBigIntegers ();
			BigInteger bigValue =
				number.bigNumerator.divide (number.bigDenominator);
			if (bigValue.signum () > 0) {
				bigValue = bigValue.add (BigInteger.ONE);
			}
			result.setValue (
				bigValue
			);
			break;

		case KProfile.LONG_RATIONAL:
		case KProfile.INT_RATIONAL:
			long longValue = number.numerator / number.denominator;
			if (longValue > 0) {
				++longValue;
			}
			result.setValue (
				longValue
			);
			break;

		default:
			if (result != number) {
				result.copy (number);
			}
			break;
		}
	}

}
