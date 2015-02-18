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

import static org.junit.Assert.*;

public class KCalculatorTest_modulo extends KCalculatorTest {

	@Test
	public void testModulo () throws Exception {
		reset ();

		String aString;
		String bString;

		BigDecimal bigA;
		BigDecimal bigB;

		/* * * * * */
		a.setValue (11);
		b.setValue (3);
		calculator.modulo (result, a, b);
		assertResultEquals (
			KProfile.INT_INTEGER,
			2,
			1,
			null
		);

		/* * * * * */
		a.setValue (-11);
		b.setValue (3);
		calculator.modulo (result, a, b);
		assertResultEquals (
			KProfile.INT_INTEGER,
			-2,
			1,
			null
		);

		/* * * * * */
		a.setValue (-11);
		b.setValue (-3);
		calculator.modulo (result, a, b);
		assertResultEquals (
			KProfile.INT_INTEGER,
			-2,
			1,
			null
		);

		/* * * * * */
		a.setValue (11);
		b.setValue (-3);
		calculator.modulo (result, a, b);
		assertResultEquals (
			KProfile.INT_INTEGER,
			2,
			1,
			null
		);

		/* * * * * */
		aString = "1100000000000000000000";
		bString = "300000000000000000000";
		converter.fromString (a, aString);
		converter.fromString (b, bString);
		calculator.modulo (result, a, b);
		assertResultEquals (
			KProfile.BIG_INTEGER,
			result.numerator,
			result.denominator,
			new BigDecimal (aString)
				.remainder (
					new BigDecimal (bString),
					converter.exactMathContext
				)
		);

		/* * * * * */
		aString = "-1100000000000000000000";
		bString = "300000000000000000000";
		converter.fromString (a, aString);
		converter.fromString (b, bString);
		calculator.modulo (result, a, b);
		assertResultEquals (
			KProfile.BIG_INTEGER,
			result.numerator,
			result.denominator,
			new BigDecimal (aString)
				.remainder (
					new BigDecimal (bString),
					converter.exactMathContext
				)
		);


		/* * * * * */
		aString = "12.34";
		bString = "5.6";
		converter.fromString (a, aString);
		converter.fromString (b, bString);
		calculator.modulo (result, a, b);
		assertEquals ("1.14", converter.toString (result));

		/* * * * * */
		aString = "-12.34";
		bString = "5.6";
		converter.fromString (a, aString);
		converter.fromString (b, bString);
		calculator.modulo (result, a, b);
		assertEquals ("-1.14", converter.toString (result));

		/* * * * * */
		aString = "120000000000000000000000.34";
		bString = "5.6";
		converter.fromString (a, aString);
		converter.fromString (b, bString);
		calculator.modulo (result, a, b);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			177,
			50,
			new BigDecimal (aString)
				.remainder (
					new BigDecimal (bString),
					converter.exactMathContext
				)
		);
		assertEquals ("3.54", converter.toString (result));

		/* * * * * */
		aString = "-120000000000000000000000.34";
		bString = "5.6";
		converter.fromString (a, aString);
		converter.fromString (b, bString);
		calculator.modulo (result, a, b);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			-177,
			50,
			new BigDecimal (aString)
				.remainder (
					new BigDecimal (bString),
					converter.exactMathContext
				)
		);
		assertEquals ("-3.54", converter.toString (result));

		/* * * * * */
		aString = "-120000000000000000000000.34";
		bString = "-5.6";
		bigA = new BigDecimal (aString);
		bigB = new BigDecimal (bString);
		converter.fromString (a, aString);
		converter.fromString (b, bString);
		calculator.modulo (result, a, b);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			-177,
			50,
			bigA
				.remainder (
					bigB,
					converter.exactMathContext
				)
		);
		assertEquals ("-3.54", converter.toString (result));
		assertEquals (
			aString,
			bigA
				.divideToIntegralValue (
					bigB,
					converter.exactMathContext
				)
				.multiply (bigB)
				.add (converter.toBigDecimal (result))
				.stripTrailingZeros ()
				.toPlainString ()
		);

		/* * * * * */
		aString = "120000000000000000000000.34";
		bString = "-5.6";
		converter.fromString (a, aString);
		converter.fromString (b, bString);
		calculator.modulo (result, a, b);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			177,
			50,
			new BigDecimal (aString)
				.remainder (
					new BigDecimal (bString),
					converter.exactMathContext
				)
		);
		assertEquals ("3.54", converter.toString (result));

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
			calculator.modulo (result, a, b);

		} catch (ArithmeticException e) {
			assertEquals (
				"division by zero exception",
				divisionByZeroMessage,
				e.getMessage ()
			);
		}
	}

}