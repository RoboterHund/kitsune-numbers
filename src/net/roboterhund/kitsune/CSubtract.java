package net.roboterhund.kitsune;

/**
 * Operations:
 * <ul>
 * <li>Subtraction.</li>
 * </ul>
 */
abstract class CSubtract {

	/**
	 * Subtract two numbers.
	 */
	public static void subtract (
		KCalculator calc,
		KNumRegister result,
		KNumRegister minuend,
		KNumRegister subtrahend) {

		int route = KProfile.route[minuend.profile][subtrahend.profile];

		switch (route) {
		default:
			// go to end
			break;

		case KProfile._LONG_RAT_:
			if (calc.multiply (
				minuend.numerator, subtrahend.denominator)
				) {
				long n1_mul_d2 = calc.intResult;

				if (calc.multiply (
					subtrahend.numerator, minuend.denominator)
					) {
					long n2_mul_d1 = calc.intResult;

					if (calc.subtract (n1_mul_d2, n2_mul_d1)) {
						long numerator = calc.intResult;

						if (calc.multiply (
							minuend.denominator, subtrahend.denominator)
							) {
							result.setValue (
								numerator,
								calc.intResult
							);
							return;
						}
					}
				}
			}
			route = KProfile._BIG__RAT_;
			break;

		case KProfile._LONG_INT1:
			if (calc.multiply (minuend.numerator, subtrahend.denominator)) {
				long n1_mul_d2 = calc.intResult;

				if (calc.subtract (n1_mul_d2, subtrahend.numerator)) {
					result.setValue (
						calc.intResult,
						subtrahend.denominator
					);
					return;
				}
			}
			route = KProfile._BIG__INT1;
			break;

		case KProfile._LONG_INT2:
			if (calc.multiply (subtrahend.numerator, minuend.denominator)) {
				long n2_mul_d1 = calc.intResult;

				if (calc.subtract (minuend.numerator, n2_mul_d1)) {
					result.setValue (
						calc.intResult,
						minuend.denominator
					);
					return;
				}
			}
			route = KProfile._BIG__INT2;
			break;

		case KProfile._LONG_INT_:
			if (calc.subtract (minuend.numerator, subtrahend.numerator)) {
				result.setValue (
					calc.intResult
				);
				return;
			}
			route = KProfile._BIG__INT_;
			break;

		case KProfile._INT__RAT_:
			result.setValue (
				minuend.numerator * subtrahend.denominator
					- subtrahend.numerator * minuend.denominator,
				minuend.denominator * subtrahend.denominator
			);
			return;

		case KProfile._INT__INT_:
			result.setValue (
				minuend.numerator - subtrahend.numerator
			);
			return;
		}

		// fallback

		minuend.setBigIntegers ();
		subtrahend.setBigIntegers ();

		switch (route) {
		case KProfile._BIG__RAT_:
			result.setValue (
				minuend.bigNumerator.multiply (subtrahend.bigDenominator)
					.subtract (
						subtrahend.bigNumerator.multiply (minuend.bigDenominator)),
				minuend.bigDenominator.multiply (subtrahend.bigDenominator)
			);
			break;

		case KProfile._BIG__INT1:
			result.setValue (
				minuend.bigNumerator.multiply (subtrahend.bigDenominator)
					.subtract (subtrahend.bigNumerator),
				subtrahend.bigDenominator
			);
			break;

		case KProfile._BIG__INT2:
			result.setValue (
				minuend.bigNumerator
					.subtract (
						subtrahend.bigNumerator.multiply (minuend.bigDenominator)),
				minuend.bigDenominator
			);
			break;

		case KProfile._BIG__INT_:
			result.setValue (
				minuend.bigNumerator
					.subtract (subtrahend.bigNumerator)
			);
			break;
		}
	}

}
