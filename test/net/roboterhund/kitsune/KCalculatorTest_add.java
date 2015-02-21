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

public class KCalculatorTest_add extends KCalculatorTest {

	@Test
	public void testAdd () throws Exception {
		reset ();

		/* * * * * */
		a.setValue (1);
		b.setValue (2);
		calculator.add (result, a, b);
		assertResultEquals (
			KProfile.INT_INTEGER,
			3,
			1,
			null
		);
		// continue

		calculator.add (result, result, result);
		assertResultEquals (
			KProfile.INT_INTEGER,
			6,
			1,
			null
		);
		// continue

		calculator.add (result, result, a);
		assertResultEquals (
			KProfile.INT_INTEGER,
			7,
			1,
			null
		);
		// continue

		/* * * * * */
		String aString = "0.0000000023";
		String bString = "100000000";
		converter.fromString (a, aString);
		converter.fromString (b, bString);
		calculator.add (result, a, b);
		assertResultEquals (
			KProfile.LONG_RATIONAL,
			1000000000000000023L,
			10000000000L,
			null
		);

		BigDecimal expectedResultBigDecimal =
			new BigDecimal (aString)
				.add (
					new BigDecimal (bString));

		// calculation specific to this case
		int actualResultPrecision = aString.length () - 2;

		BigDecimal actualResultBigDecimal =
			new BigDecimal (result.numerator)
				.divide (
					new BigDecimal (result.denominator),
					actualResultPrecision,
					BigDecimal.ROUND_HALF_UP);

		assertEquals (
			expectedResultBigDecimal.toPlainString (),
			actualResultBigDecimal.toPlainString ()
		);

		/* * * * * */
		converter.fromString (a, "12341234.25");
		converter.fromString (b, "67867867.89");
		calculator.add (result, a, b);
		assertResultEquals (
			KProfile.LONG_RATIONAL,
			4010455107L,
			50,
			null
		);


		/* * * * * */
		result.setValue (0);
		converter.fromDouble (a, 0.5);
		calculator.add (result, result, a);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			1,
			2,
			null
		);
		// continue

		calculator.add (result, result, a);
		assertResultEquals (
			KProfile.INT_INTEGER,
			1,
			1,
			null
		);
		// continue

		calculator.add (result, result, a);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			3,
			2,
			null
		);

		/* * * * * */
		a.setValue (Integer.MAX_VALUE);
		result.setValue (0);
		calculator.add (result, result, a);
		assertResultEquals (
			KProfile.INT_INTEGER,
			Integer.MAX_VALUE,
			1,
			null
		);
		// continue

		a.setValue (1);
		calculator.add (result, result, a);
		assertResultEquals (
			KProfile.LONG_INTEGER,
			(long) Integer.MAX_VALUE + 1,
			1,
			null
		);
		// continue

		a.setValue (-1);
		calculator.add (result, result, a);
		assertResultEquals (
			KProfile.INT_INTEGER,
			Integer.MAX_VALUE,
			1,
			null
		);
	}

}
