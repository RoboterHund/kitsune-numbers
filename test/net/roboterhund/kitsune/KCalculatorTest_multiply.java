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

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class KCalculatorTest_multiply extends KCalculatorTest {

	public static final String NO_RAISE = "no raise";

	KNumRegister maxError;

	@Test
	public void testMultiply () throws Exception {
		reset ();

		converter = new KConverter (100);

		maxError = new KNumRegister ();
		converter.fromString (
			maxError,
			"0.00000000000000000000000000000000000000000000000001"
		);

		/* * * * * */
		converter.fromString (a, "12.34");
		b.setValue (10);
		calculator.multiply (result, a, b);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			617,
			5,
			null
		);

		/* * * * * */
		converter.fromDouble (a, -2.5);
		b.setValue (4);
		calculator.multiply (result, a, b);
		assertResultEquals (
			KProfile.INT_INTEGER,
			-10,
			1,
			null
		);

		/* * * * * */
		a.setValue (-1);
		b.setValue (Long.MIN_VALUE);
		calculator.multiply (result, a, b);
		assertResultEquals (
			KProfile.BIG_INTEGER,
			result.numerator,
			result.denominator,
			new BigDecimal (Long.MAX_VALUE).add (BigDecimal.ONE)
		);
		// continue

		b.setValue (1, 2);
		calculator.multiply (result, result, b);
		assertResultEquals (
			KProfile.LONG_INTEGER,
			result.numerator,
			result.denominator,
			new BigDecimal (Long.MAX_VALUE)
				.add (BigDecimal.ONE)
				.divide (
					new BigDecimal (2),
					converter.exactMathContext
				)
		);

		/* * * * * */
		assertMultiplyCorrect ("2", "4", true, true);
		assertMultiplyCorrect ("2", "0.5", true, true);
		assertMultiplyCorrect ("0.01", "0.5", true, true);
		assertMultiplyCorrect ("1.45", "1000", true, true);
		assertMultiplyCorrect ("1.45", "24", true, true);
		assertMultiplyCorrect ("123.45", "24", true, false);
		assertMultiplyCorrect ("123.45", "2.5", true, false);
		assertMultiplyCorrect ("1000000000.1", "2", true, false);
		assertMultiplyCorrect ("1000000000.1", "-1000000000.1", false, false);
		assertMultiplyCorrect (
			"10000000000000000000000000000000000000000000000000000000.23",
			"-4",
			true,
			false);
		assertMultiplyCorrect (
			"-2",
			"-31",
			true,
			false);
	}

	// test multiplication operations
	void assertMultiplyCorrect (
		String string_1,
		String string_2,
		boolean raise_1_to_2,
		boolean raise_2_to_1) {

		BigDecimal big_1 = new BigDecimal (string_1);
		BigDecimal big_2 = new BigDecimal (string_2);

		String expectedProduct = expectedProduct (big_1, big_2);
		String expectedPower;
		boolean expectedRaised;

		String actual;

		converter.fromString (a, string_1);
		converter.fromString (b, string_2);

		/* * * * * */
		calculator.multiply (result, a, b);
		actual = converter.toString (result);
		assertEquals (
			expectedProduct,
			actual
		);

		boolean raised;
		if (raise_1_to_2) {
			try {
				expectedPower = expectedPower (big_1, big_2);
				expectedRaised = true;
			} catch (IllegalArgumentException e) {
				if (NO_RAISE.equals (e.getMessage ())) {
					expectedPower = null;
					expectedRaised = false;
				} else {
					throw e;
				}
			}

			try {
				calculator.power (result, a, b, maxError);
				actual = converter.toString (result);
				raised = true;
			} catch (IllegalArgumentException e) {
				if (CMultiply.ERR_MSG_NEGATIVE_BASE.equals (e.getMessage ())) {
					raised = false;
				} else {
					throw e;
				}
			}
			assertEquals (expectedRaised, raised);
			if (raised) {
				try {
					assertEquals (
						expectedPower,
						actual
					);
				} catch (AssertionError error) {
					//noinspection ConstantConditions
					assertEquals (
						expectedPower.substring (0, expectedPower.length () - 1),
						actual.substring (0, expectedPower.length () - 1)
					);
					CommonTest.out.printf (
						"%s ^ %s\n"
							+ " = %s (KCalculator)\n"
							+ " = %s (Math.pow)\n",
						string_1,
						string_2,
						actual,
						expectedPower
					);
				}
			}
		}

		/* * * * * */
		expectedProduct = expectedProduct (big_2, big_1);

		calculator.multiply (result, b, a);
		actual = converter.toString (result);
		assertEquals (
			expectedProduct,
			actual
		);

		if (raise_2_to_1) {
			try {
				expectedPower = expectedPower (big_2, big_1);
				expectedRaised = true;
			} catch (IllegalArgumentException e) {
				if (NO_RAISE.equals (e.getMessage ())) {
					expectedPower = null;
					expectedRaised = false;
				} else {
					throw e;
				}
			}

			try {
				calculator.power (result, b, a, maxError);
				actual = converter.toString (result);
				raised = true;
			} catch (IllegalArgumentException e) {
				if (CMultiply.ERR_MSG_NEGATIVE_BASE.equals (e.getMessage ())) {
					raised = false;
				} else {
					throw e;
				}
			}
			assertEquals (expectedRaised, raised);
			if (raised) {
				try {
					assertEquals (
						expectedPower,
						actual
					);
				} catch (AssertionError error) {
					//noinspection ConstantConditions
					assertEquals (
						expectedPower.substring (0, expectedPower.length () - 1),
						actual.substring (0, expectedPower.length () - 1)
					);
					CommonTest.out.printf (
						"%s ^ %s\n"
							+ " = %s (KCalculator)\n"
							+ " = %s (Math.pow)\n",
						string_2,
						string_1,
						actual,
						expectedPower
					);
				}
			}
		}
	}

	// expected product
	private String expectedProduct (BigDecimal big_1, BigDecimal big_2) {
		BigDecimal result;
		result = big_1.multiply (big_2, converter.exactMathContext);
		return CommonTest.stripTrailingZeros (result).toPlainString ();
	}

	// expected power
	private String expectedPower (BigDecimal big_1, BigDecimal big_2) {
		String power;
		try {
			int exp = big_2.intValueExact ();
			if (exp > 0) {
				BigDecimal result;
				result = big_1.pow (exp, converter.exactMathContext);
				return CommonTest.stripTrailingZeros (result).toPlainString ();
			} else {
				// Integer.MIN_VALUE not supported
				BigDecimal result;
				BigDecimal pow = big_1.pow (-exp, converter.exactMathContext);
				try {
					result = BigDecimal.ONE.divide (
						pow,
						converter.exactMathContext
					);
				} catch (ArithmeticException e) {
					result = BigDecimal.ONE.divide (
						pow,
						converter.inexactMathContext
					);
				}
				power = CommonTest.stripTrailingZeros (result).toPlainString ();
			}

		} catch (ArithmeticException e) {
			double base = big_1.doubleValue ();
			if (base < 0) {
				throw new IllegalArgumentException (NO_RAISE);
			}
			double exp = big_2.doubleValue ();
			BigDecimal result;
			result = BigDecimal.valueOf (Math.pow (base, exp));
			power = CommonTest.stripTrailingZeros (result).toPlainString ();
		}

		return power;
	}

}
