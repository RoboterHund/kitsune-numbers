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

public class KCalculatorTest_result extends KCalculatorTest {

	@Test
	public void testNumberResult () throws Exception {
		calculator = new KCalculator ();

		result = new KNumber ();
		a = new KNumber ();
		b = new KNumber ();

		calculator.result = result;

		/* * * * * */
		a.setValue (222);
		b.setValue (333);
		calculator.divide (a, b);
		assertResultEquals (
			FITS_IN_INT,
			2,
			3,
			FITS_IN_LONG
		);
		// continue

		a.setValue (calculator.result);
		calculator.add (a);
		assertResultEquals (
			FITS_IN_INT,
			4,
			3,
			FITS_IN_LONG
		);
		// continue

		calculator.add (a);
		assertResultEquals (
			FITS_IN_INT,
			2,
			1,
			FITS_IN_LONG
		);
		// continue

		calculator.add (a);
		assertResultEquals (
			FITS_IN_INT,
			8,
			3,
			FITS_IN_LONG
		);

		/* * * * * */
		a.setValue (Long.MAX_VALUE);
		b.setValue (1);
		calculator.add (a, b);
		BigDecimal maxLongBigDecimal = new BigDecimal (Long.MAX_VALUE);
		assertResultEquals (
			BIGGER_THAN_INT,
			result.numerator,
			result.denominator,
			maxLongBigDecimal.add (BigDecimal.ONE)
		);
		// continue

		// the result does not revert to simple fraction representation
		calculator.subtract (b);
		assertResultEquals (
			BIGGER_THAN_INT,
			result.numerator,
			result.denominator,
			maxLongBigDecimal
		);
		// continue

		result.compact ();
		assertResultEquals (
			BIGGER_THAN_INT,
			Long.MAX_VALUE,
			1,
			FITS_IN_LONG
		);
	}

}
