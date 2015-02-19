package net.roboterhund.kitsune;

import java.math.BigInteger;

/**
 * Operations:
 * <ul>
 * <li>Absolute value.</li>
 * <li>Negate.</li>
 * <li>Inverse (reciprocal).</li>
 * </ul>
 */
abstract class KCalcInvert {

	/**
	 * Get absolute value.
	 *
	 * @param result overwritten with the result.
	 * @param number the number.
	 */
	public static void abs (
		KNumRegister result,
		KNumRegister number) {

		if (result != number) {
			result.copy (number);
		}

		switch (number.profile) {
		case KProfile.BIG_RATIONAL:
		case KProfile.BIG_INTEGER:
			if (result.bigNumerator.signum () < 0) {
				result.bigNumerator = result.bigNumerator.negate ();
			}
			break;

		case KProfile.LONG_RATIONAL:
			if (result.numerator == Long.MIN_VALUE) {
				result.bigNumerator = KEdges.MIN_LONG_NEGATED;
				result.bigDenominator = BigInteger.valueOf (result.denominator);
				result.profile = KProfile.BIG_RATIONAL;
			} else {
				result.numerator = -result.numerator;
			}
			break;

		case KProfile.LONG_INTEGER:
			if (result.numerator < 0) {
				if (result.numerator == Long.MIN_VALUE) {
					result.bigNumerator = KEdges.MIN_LONG_NEGATED;
					result.bigDenominator = BigInteger.ONE;
					result.profile = KProfile.BIG_INTEGER;
				} else {
					result.numerator = Math.abs (result.numerator);
				}
			}
			break;

		case KProfile.INT_RATIONAL:
			if (result.numerator < 0) {
				result.numerator = -result.numerator;
			}
			break;

		default:
			if (result != number) {
				result.copy (number);
			}
			break;
		}
	}

	// TODO negate (additive inverse)

	// TODO multiplicative inverse (reciprocal)

}
