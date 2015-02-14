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

import static net.roboterhund.kitsune.CommonTest.BIGGER_THAN_INT;
import static net.roboterhund.kitsune.CommonTest.FITS_IN_INT;
import static net.roboterhund.kitsune.CommonTest.FITS_IN_LONG;
import static net.roboterhund.kitsune.CommonTest.IS_INTEGER;
import static org.junit.Assert.assertEquals;

public class KCalculatorTest_add extends KCalculatorTest {

	@Test
	public void testAdd () throws Exception {
		calculator = new KCalculator ();

		result = new KNumber ();
		a = new KNumber ();
		b = new KNumber ();

		/* * * * * */
		a.setValue (1);
		b.setValue (2);
		calculator.add (result, a, b);
		assertResultEquals (
			FITS_IN_INT,
			3,
			IS_INTEGER,
			FITS_IN_LONG
		);
		// continue

		calculator.add (result, result);
		assertResultEquals (
			FITS_IN_INT,
			6,
			IS_INTEGER,
			FITS_IN_LONG
		);
		// continue

		calculator.add (result, a);
		assertResultEquals (
			FITS_IN_INT,
			7,
			IS_INTEGER,
			FITS_IN_LONG
		);
		// continue

		/* * * * * */
		String aString = "0.0000000023";
		String bString = "100000000";
		a.setValue (aString);
		b.setValue (bString);
		calculator.add (result, a, b);
		assertResultEquals (
			BIGGER_THAN_INT,
			1000000000000000023L,
			10000000000L,
			FITS_IN_LONG
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
		a.setValue ("12341234.25");
		b.setValue ("67867867.89");
		calculator.add (result, a, b);
		assertResultEquals (
			BIGGER_THAN_INT,
			4010455107L,
			50,
			FITS_IN_LONG
		);


		/* * * * * */
		result.setValue (0);
		a.setValue (0.5);
		calculator.add (result, a);
		assertResultEquals (
			FITS_IN_INT,
			1,
			2,
			FITS_IN_LONG
		);
		// continue

		calculator.add (result, a);
		assertResultEquals (
			FITS_IN_INT,
			1,
			1,
			FITS_IN_LONG
		);
		// continue

		calculator.add (result, a);
		assertResultEquals (
			FITS_IN_INT,
			3,
			2,
			FITS_IN_LONG
		);

		/* * * * * */
		a.setValue (Integer.MAX_VALUE);
		result.setValue (0);
		calculator.add (result, a);
		assertResultEquals (
			FITS_IN_INT,
			Integer.MAX_VALUE,
			1,
			FITS_IN_LONG
		);
		// continue

		a.setValue (1);
		calculator.add (result, a);
		assertResultEquals (
			BIGGER_THAN_INT,
			(long) Integer.MAX_VALUE + 1,
			1,
			FITS_IN_LONG
		);
		// continue

		a.setValue (-1);
		calculator.add (result, a);
		assertResultEquals (
			FITS_IN_INT,
			Integer.MAX_VALUE,
			1,
			FITS_IN_LONG
		);
	}

}
