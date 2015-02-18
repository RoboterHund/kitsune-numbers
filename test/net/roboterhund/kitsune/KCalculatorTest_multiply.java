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

public class KCalculatorTest_multiply extends KCalculatorTest {

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
		calculator.multiply (result, b);
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
	}

}
