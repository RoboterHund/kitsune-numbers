package net.roboterhund.kitsune;

/**
 * Operations:
 * <ul>
 * <li>Addition.</li>
 * </ul>
 */
abstract class CAdd {

	/**
	 * Add two numbers.
	 */
	public static void add (
		KCalculator calc,
		KNumRegister result,
		KNumRegister term_1,
		KNumRegister term_2) {

		int route = KProfile.route[term_1.profile][term_2.profile];

		switch (route) {
		default:
			// go to end
			break;

		case KProfile._LONG_RAT_:
			if (calc.multiply (term_1.numerator, term_2.denominator)) {
				long n1_mul_d2 = calc.intResult;

				if (calc.multiply (term_2.numerator, term_1.denominator)) {
					long n2_mul_d1 = calc.intResult;

					if (calc.add (n1_mul_d2, n2_mul_d1)) {
						long numerator = calc.intResult;

						if (calc.multiply (
							term_1.denominator, term_2.denominator)
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
			if (calc.multiply (term_1.numerator, term_2.denominator)) {
				long n1_mul_d2 = calc.intResult;

				if (calc.add (n1_mul_d2, term_2.numerator)) {
					result.setValue (
						calc.intResult,
						term_2.denominator
					);
					return;
				}
			}
			route = KProfile._BIG__INT1;
			break;

		case KProfile._LONG_INT2:
			if (calc.multiply (term_2.numerator, term_1.denominator)) {
				long n2_mul_d1 = calc.intResult;

				if (calc.add (term_1.numerator, n2_mul_d1)) {
					result.setValue (
						calc.intResult,
						term_1.denominator
					);
					return;
				}
			}
			route = KProfile._BIG__INT2;
			break;

		case KProfile._LONG_INT_:
			if (calc.add (term_1.numerator, term_2.numerator)) {
				result.setValue (
					calc.intResult
				);
				return;
			}
			route = KProfile._BIG__INT_;
			break;

		case KProfile._INT__RAT_:
			result.setValue (
				term_1.numerator * term_2.denominator
					+ term_2.numerator * term_1.denominator,
				term_1.denominator * term_2.denominator
			);
			return;

		case KProfile._INT__INT_:
			result.setValue (
				term_1.numerator + term_2.numerator
			);
			return;
		}

		// fallback

		term_1.setBigIntegers ();
		term_2.setBigIntegers ();

		switch (route) {
		case KProfile._BIG__RAT_:
			result.setValue (
				term_1.bigNumerator.multiply (term_2.bigDenominator)
					.add (term_2.bigNumerator.multiply (term_1.bigDenominator)),
				term_1.bigDenominator.multiply (term_2.bigDenominator)
			);
			break;

		case KProfile._BIG__INT1:
			result.setValue (
				term_1.bigNumerator.multiply (term_2.bigDenominator)
					.add (term_2.bigNumerator),
				term_2.bigDenominator
			);
			break;

		case KProfile._BIG__INT2:
			result.setValue (
				term_1.bigNumerator
					.add (term_2.bigNumerator.multiply (term_1.bigDenominator)),
				term_1.bigDenominator
			);
			break;

		case KProfile._BIG__INT_:
			result.setValue (
				term_1.bigNumerator
					.add (term_2.bigNumerator)
			);
			break;
		}
	}

}
