package net.roboterhund.kitsune;

/**
 * Operations:
 * <ul>
 * <li>Multiplication.</li>
 * </ul>
 */
abstract class KCalcMultiply {

	/**
	 * Multiply two numbers.
	 */
	public static void multiply (
		KCalculator calc,
		KNumRegister result,
		KNumRegister factor_1,
		KNumRegister factor_2) {

		int route = KProfile.route[factor_1.profile][factor_2.profile];

		switch (route) {
		default:
			// go to end
			break;

		case KProfile._LONG_RAT_:
			if (calc.multiply (factor_1.numerator, factor_2.numerator)) {
				long numerator = calc.intResult;

				if (calc.multiply (factor_1.denominator, factor_2.denominator)) {
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
			if (calc.multiply (factor_1.numerator, factor_2.numerator)) {
				result.setValue (
					calc.intResult,
					factor_2.denominator
				);
				return;
			}
			route = KProfile._BIG__INT1;
			break;

		case KProfile._LONG_INT2:
			if (calc.multiply (factor_1.numerator, factor_2.numerator)) {
				result.setValue (
					calc.intResult,
					factor_1.denominator
				);
				return;
			}
			route = KProfile._BIG__INT2;
			break;

		case KProfile._LONG_INT_:
			if (calc.multiply (factor_1.numerator, factor_2.numerator)) {
				result.setValue (
					calc.intResult
				);
				return;
			}
			route = KProfile._BIG__INT_;
			break;

		case KProfile._INT__RAT_:
			result.setValue (
				factor_1.numerator * factor_2.numerator,
				factor_1.denominator * factor_2.denominator
			);
			return;

		case KProfile._INT__INT_:
			result.setValue (
				factor_1.numerator * factor_2.numerator
			);
			return;
		}

		// fallback

		factor_1.setBigIntegers ();
		factor_2.setBigIntegers ();

		switch (route) {
		case KProfile._BIG__RAT_:
			result.setValue (
				factor_1.bigNumerator.multiply (factor_2.bigNumerator),
				factor_1.bigDenominator.multiply (factor_2.bigDenominator)
			);
			break;

		case KProfile._BIG__INT1:
			result.setValue (
				factor_1.bigNumerator.multiply (factor_2.bigNumerator),
				factor_2.bigDenominator
			);
			break;

		case KProfile._BIG__INT2:
			result.setValue (
				factor_1.bigNumerator.multiply (factor_2.bigNumerator),
				factor_1.bigDenominator
			);
			break;

		case KProfile._BIG__INT_:
			result.setValue (
				factor_1.bigNumerator.multiply (factor_2.bigNumerator)
			);
			break;
		}
	}

}
