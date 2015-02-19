package net.roboterhund.kitsune;

/**
 * Operations:
 * <ul>
 * <li>Division.</li>
 * <li>Modulo.</li>
 * </ul>
 */
abstract class CDivide {

	/**
	 * Divide two numbers.
	 */
	public static void divide (
		KCalculator calc,
		KNumRegister result,
		KNumRegister dividend,
		KNumRegister divisor) {

		int route = KProfile.route[dividend.profile][divisor.profile];

		switch (route) {
		default:
			// go to end
			break;

		case KProfile._LONG_RAT_:
			if (calc.multiply (dividend.numerator, divisor.denominator)) {
				long numerator = calc.intResult;

				if (calc.multiply (divisor.numerator, dividend.denominator)) {
					result.setValue (
						numerator,
						calc.intResult
					);
					return;
				}
			}
			route = KProfile._BIG__RAT_;
			break;

		case KProfile._LONG_INT1:
			if (calc.multiply (dividend.numerator, divisor.denominator)) {
				result.setValue (
					calc.intResult,
					divisor.numerator
				);
				return;
			}
			route = KProfile._BIG__INT1;
			break;

		case KProfile._LONG_INT2:
			if (calc.multiply (divisor.numerator, dividend.denominator)) {
				result.setValue (
					dividend.numerator,
					calc.intResult
				);
				return;
			}
			route = KProfile._BIG__INT2;
			break;

		case KProfile._LONG_INT_:
			result.setValue (
				dividend.numerator,
				divisor.numerator
			);
			return;

		case KProfile._INT__RAT_:
			result.setValue (
				dividend.numerator * divisor.denominator,
				divisor.numerator * dividend.denominator
			);
			return;

		case KProfile._INT__INT_:
			result.setValue (
				dividend.numerator,
				divisor.numerator
			);
			return;
		}

		// fallback

		dividend.setBigIntegers ();
		divisor.setBigIntegers ();

		switch (route) {
		case KProfile._BIG__RAT_:
			result.setValue (
				dividend.bigNumerator.multiply (divisor.bigDenominator),
				divisor.bigNumerator.multiply (dividend.bigDenominator)
			);
			break;

		case KProfile._BIG__INT1:
			result.setValue (
				dividend.bigNumerator.multiply (divisor.bigDenominator),
				divisor.bigNumerator
			);
			break;

		case KProfile._BIG__INT2:
			result.setValue (
				dividend.bigNumerator,
				divisor.bigNumerator.multiply (dividend.bigDenominator)
			);
			break;

		case KProfile._BIG__INT_:
			result.setValue (
				dividend.bigNumerator,
				divisor.bigNumerator
			);
			break;
		}
	}

	/**
	 * Get {@code dividend % divisor}.
	 */
	public static void modulo (
		KCalculator calc,
		KNumRegister result,
		KNumRegister dividend,
		KNumRegister divisor) {

		int route = KProfile.route[dividend.profile][divisor.profile];

		switch (route) {
		default:
			// go to end
			break;

		case KProfile._BIG__INT_:
			dividend.setBigIntegers ();
			divisor.setBigIntegers ();
			result.setValue (
				dividend.bigNumerator.mod (divisor.bigNumerator)
			);
			break;

		case KProfile._LONG_INT_:
		case KProfile._INT__INT_:
			result.setValue (
				dividend.numerator % divisor.numerator
			);
			return;
		}

		// fallback

		// a % b =
		// a - (b * int (a / b))
		CDivide.divide (calc, result, dividend, divisor);
		CRound.truncate (result, result);
		CMultiply.multiply (calc, result, result, divisor);

		CSubtract.subtract (calc, result, dividend, result);
	}

}
