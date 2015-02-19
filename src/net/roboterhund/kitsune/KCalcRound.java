package net.roboterhund.kitsune;

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
	public static void integer (
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

}
