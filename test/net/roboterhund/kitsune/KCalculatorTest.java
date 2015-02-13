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
import static net.roboterhund.kitsune.CommonTest.assertNumberEquals;
import static org.junit.Assert.assertEquals;

public class KCalculatorTest {

	private KNumber result;
	private KNumber a;
	private KNumber b;
	private KCalculator calculator;

	@Test
	public void testAdd () throws Exception {
		calculator = new KCalculator ();

		result = new KNumber ();
		a = new KNumber ();
		b = new KNumber ();

		boolean operationFailed = false;
		try {
			calculator.add (a, b);
		} catch (NullPointerException e) {
			operationFailed = true;
		}
		assertEquals (true, operationFailed);

		calculator.result = result;

		/* * * * * */
		a.setValue (1);
		b.setValue (2);
		calculator.add (a, b);
		assertResultEquals (
			FITS_IN_INT,
			3,
			IS_INTEGER,
			FITS_IN_LONG
		);
		// continue

		calculator.add (calculator.result);
		assertResultEquals (
			FITS_IN_INT,
			6,
			IS_INTEGER,
			FITS_IN_LONG
		);
		// continue

		calculator.add (a);
		assertResultEquals (
			FITS_IN_INT,
			7,
			IS_INTEGER,
			FITS_IN_LONG
		);
		// continue

		/* * * * * */
		a.setValue ("0.000000023");
		b.setValue (100000000L);
		calculator.add (a, b);
		assertResultEquals (
			BIGGER_THAN_INT,
			100000000000000023L,
			1000000000,
			FITS_IN_LONG
		);

		/* * * * * */
		a.setValue ("12341234.25");
		b.setValue ("67867867.89");
		calculator.add (a, b);
		assertResultEquals (
			BIGGER_THAN_INT,
			4010455107L,
			50,
			FITS_IN_LONG
		);


		/* * * * * */
		calculator.result.setValue (0);
		a.setValue (0.5);
		calculator.add (a);
		assertResultEquals (
			FITS_IN_INT,
			1,
			2,
			FITS_IN_LONG
		);
		// continue

		calculator.add (a);
		assertResultEquals (
			FITS_IN_INT,
			1,
			1,
			FITS_IN_LONG
		);
		// continue

		calculator.add (a);
		assertResultEquals (
			FITS_IN_INT,
			3,
			2,
			FITS_IN_LONG
		);

		/* * * * * */
		a.setValue (Integer.MAX_VALUE);
		calculator.result.setValue (0);
		calculator.add (a);
		assertResultEquals (
			FITS_IN_INT,
			Integer.MAX_VALUE,
			1,
			FITS_IN_LONG
		);
		// continue

		a.setValue (1);
		calculator.add (a);
		assertResultEquals (
			BIGGER_THAN_INT,
			(long) Integer.MAX_VALUE + 1,
			1,
			FITS_IN_LONG
		);
		// continue

		a.setValue (-1);
		calculator.add (a);
		assertResultEquals (
			FITS_IN_INT,
			Integer.MAX_VALUE,
			1,
			FITS_IN_LONG
		);
	}

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
	}

	@Test
	public void testDivide () throws Exception {
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
	}

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
	}

	// check internal values of number
	private void assertResultEquals (
		boolean fitsInInt,
		long numerator,
		long denominator,
		BigDecimal bigDecimal
	) {
		assertNumberEquals (
			result,
			fitsInInt,
			numerator,
			denominator,
			bigDecimal
		);
	}

}