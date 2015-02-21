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

public class KCalculatorTest_divide extends KCalculatorTest {

	@Test
	public void testDivide () throws Exception {
		reset ();

		/* * * * * */
		a.setValue (222);
		b.setValue (333);
		calculator.divide (result, a, b);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			2,
			3,
			null
		);
		// continue

		b.setValue (1, 6);
		calculator.divide (result, b);
		assertResultEquals (
			KProfile.INT_INTEGER,
			4,
			1,
			null
		);

		/* * * * * */
		a.setValue (Long.MIN_VALUE);
		b.setValue (-1);
		calculator.divide (result, a, b);
		assertResultEquals (
			KProfile.BIG_INTEGER,
			result.numerator,
			result.denominator,
			BigDecimal.valueOf (Long.MIN_VALUE).negate ()
		);

		/* * * * * */
		a.setValue (Long.MIN_VALUE);
		b.setValue (1);
		calculator.divide (result, a, b);
		assertResultEquals (
			KProfile.BIG_INTEGER,
			result.numerator,
			result.denominator,
			BigDecimal.valueOf (Long.MIN_VALUE)
		);


		/* * * * * */
		String divisionByZeroMessage = "???";
		try {
			//noinspection NumericOverflow,UnusedDeclaration
			int x = 1 / 0;
		} catch (ArithmeticException e) {
			divisionByZeroMessage = e.getMessage ();
		}
		try {
			a.setValue (1);
			b.setValue (0);
			calculator.divide (result, a, b);

		} catch (ArithmeticException e) {
			assertEquals (
				"division by zero exception",
				divisionByZeroMessage,
				e.getMessage ()
			);
		}

		assertDivideCorrect ("1000", "200");
		assertDivideCorrect ("0", "200");
		assertDivideCorrect ("-1.1", "10000000000.1");
		assertDivideCorrect ("-1.1", "10000000000000000000000000000000000000.1");
		assertDivideCorrect (
			"-1000000000000000000000000000000000.1",
			"10000000000000000000000000000000000000.1");
		assertDivideCorrect (
			"12345.56789",
			"-234.56");
	}

	// test division operations
	void assertDivideCorrect (String string_1, String string_2) {
		BigDecimal big_1 = new BigDecimal (string_1);
		BigDecimal big_2 = new BigDecimal (string_2);

		String big_1__div__big_2 = expectedDivide (big_1, big_2);
		String big_1__mod__big_2 = expectedModulo (big_1, big_2);
		String big_1__ind__big_2 = expectedIntDiv (big_1, big_2);

		converter.fromString (a, string_1);
		converter.fromString (b, string_2);

		calculator.divide (result, a, b);
		assertEquals (
			big_1__div__big_2,
			converter.toString (result)
		);

		calculator.modulo (result, a, b);
		assertEquals (
			big_1__mod__big_2,
			converter.toString (result)
		);

		calculator.divide (result, a, a, b);
		assertEquals (
			big_1__ind__big_2,
			converter.toString (result)
		);
		assertEquals (
			big_1__mod__big_2,
			converter.toString (a)
		);

		BigDecimal intDiv = converter.toBigDecimal (result);
		BigDecimal mod = converter.toBigDecimal (a);

		converter.fromString (a, string_1);

		assertEquals (
			CommonTest.stripTrailingZeros (big_1).toPlainString (),
			CommonTest.stripTrailingZeros (
				intDiv.multiply (big_2).add (mod)
			).toPlainString ()
		);

		if (!string_1.equals ("0")) {
			String big_2__div__big_1 = expectedDivide (big_2, big_1);
			String big_2__mod__big_1 = expectedModulo (big_2, big_1);
			String big_2__ind__big_1 = expectedIntDiv (big_2, big_1);

			calculator.divide (result, b, a);
			assertEquals (
				big_2__div__big_1,
				converter.toString (result)
			);

			calculator.modulo (result, b, a);
			assertEquals (
				big_2__mod__big_1,
				converter.toString (result)
			);

			calculator.divide (result, a, b, a);
			assertEquals (
				big_2__ind__big_1,
				converter.toString (result)
			);
			assertEquals (
				big_2__mod__big_1,
				converter.toString (a)
			);

			intDiv = converter.toBigDecimal (result);
			mod = converter.toBigDecimal (a);

			assertEquals (
				CommonTest.stripTrailingZeros (big_2).toPlainString (),
				CommonTest.stripTrailingZeros (
					intDiv.multiply (big_1).add (mod)
				).toPlainString ()
			);
		}
	}

	// expected divide
	private String expectedDivide (BigDecimal big_1, BigDecimal big_2) {
		BigDecimal result;
		try {
			result = big_1.divide (
				big_2,
				converter.exactMathContext
			);

		} catch (ArithmeticException e) {
			result = big_1.divide (
				big_2,
				converter.inexactMathContext
			);
		}
		return CommonTest.stripTrailingZeros (result).toPlainString ();
	}

	// expected modulo
	private String expectedModulo (BigDecimal big_1, BigDecimal big_2) {
		BigDecimal result;
		try {
			result = big_1.remainder (
				big_2,
				converter.exactMathContext
			);

		} catch (ArithmeticException e) {
			result = big_1.remainder (
				big_2,
				converter.inexactMathContext
			);
		}
		return CommonTest.stripTrailingZeros (result).toPlainString ();
	}

	// expected integer division
	private String expectedIntDiv (BigDecimal big_1, BigDecimal big_2) {
		BigDecimal result;
		try {
			result = big_1.divideToIntegralValue (
				big_2,
				converter.exactMathContext
			);

		} catch (ArithmeticException e) {
			result = big_1.divideToIntegralValue (
				big_2,
				converter.inexactMathContext
			);
		}
		return CommonTest.stripTrailingZeros (result).toPlainString ();
	}

}
