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

public class KCalculatorTest_multiply extends KCalculatorTest {

	@Test
	public void testMultiply () throws Exception {
		calculator = new KCalculator ();

		result = new KNumber ();
		a = new KNumber ();
		b = new KNumber ();

		calculator.result = result;

		/* * * * * */
		a.setValue ("12.34");
		b.setValue (10);
		calculator.multiply (a, b);
		assertResultEquals (
			FITS_IN_INT,
			617,
			5,
			FITS_IN_LONG
		);

		/* * * * * */
		a.setValue (-2.5);
		b.setValue (4);
		calculator.multiply (a, b);
		assertResultEquals (
			FITS_IN_INT,
			-6,
			1,
			FITS_IN_LONG
		);

		/* * * * * */
		a.setValue (-1);
		b.setValue (Long.MIN_VALUE);
		calculator.multiply (a, b);
		assertResultEquals (
			BIGGER_THAN_INT,
			result.numerator,
			result.denominator,
			new BigDecimal (Long.MAX_VALUE).add (BigDecimal.ONE)
		);
	}

}
