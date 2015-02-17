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
			KProfile.LONG_INTEGER,
			Long.MIN_VALUE,
			1,
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
	}

}
