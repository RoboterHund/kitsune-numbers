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

	@Test
	public void testMultiply () throws Exception {
		reset ();

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
		assertMultiplyCorrect ("123.45", "24", true, true);
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

		converter.fromString (a, string_1);
		converter.fromString (b, string_2);

		/* * * * * */
		calculator.multiply (result, a, b);
		assertEquals (
			expectedProduct,
			converter.toString (result)
		);

		boolean raised;
		if (raise_1_to_2) {
			try {
				expectedPower = expectedPower (big_1, big_2);
				expectedRaised = true;
			} catch (IllegalArgumentException e) {
				if (e.getMessage ().equals (NO_RAISE)) {
					expectedPower = null;
					expectedRaised = false;
				} else {
					throw e;
				}
			}

			try {
				calculator.power (result, a, b);
				raised = true;
			} catch (IllegalArgumentException e) {
				if (e.getMessage ().equals (CMultiply.ERR_MSG_RATIONAL_EXPONENT)) {
					raised = false;
				} else {
					throw e;
				}
			}
			assertEquals (expectedRaised, raised);
			if (raised) {
				assertEquals (
					expectedPower,
					converter.toString (result)
				);
			}
		}

		/* * * * * */
		expectedProduct = expectedProduct (big_2, big_1);

		calculator.multiply (result, b, a);
		assertEquals (
			expectedProduct,
			converter.toString (result)
		);

		if (raise_2_to_1) {
			try {
				expectedPower = expectedPower (big_2, big_1);
				expectedRaised = true;
			} catch (IllegalArgumentException e) {
				if (e.getMessage ().equals (NO_RAISE)) {
					expectedPower = null;
					expectedRaised = false;
				} else {
					throw e;
				}
			}

			try {
				calculator.power (result, b, a);
				raised = true;
			} catch (IllegalArgumentException e) {
				if (e.getMessage ().equals (CMultiply.ERR_MSG_RATIONAL_EXPONENT)) {
					raised = false;
				} else {
					throw e;
				}
			}
			assertEquals (expectedRaised, raised);
			if (raised) {
				assertEquals (
					expectedPower,
					converter.toString (result)
				);
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
		int exp;
		try {
			exp = big_2.intValueExact ();
		} catch (ArithmeticException e) {
			throw new IllegalArgumentException (NO_RAISE);
		}
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
			return CommonTest.stripTrailingZeros (result).toPlainString ();
		}
	}

}
