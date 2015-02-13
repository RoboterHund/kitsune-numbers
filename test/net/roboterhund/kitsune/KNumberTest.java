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
import java.math.BigInteger;

import static net.roboterhund.kitsune.CommonTest.BIGGER_THAN_INT;
import static net.roboterhund.kitsune.CommonTest.FITS_IN_INT;
import static net.roboterhund.kitsune.CommonTest.FITS_IN_LONG;
import static net.roboterhund.kitsune.CommonTest.IS_INTEGER;
import static net.roboterhund.kitsune.CommonTest.assertNumberEquals;
import static org.junit.Assert.assertEquals;

public class KNumberTest {

	private KNumber testedNumber;

	@Test
	public void testConstructor () throws Exception {
		// default value
		testedNumber = new KNumber ();
		assertTestedNumberEquals (
			FITS_IN_INT,
			0,
			IS_INTEGER,
			FITS_IN_LONG
		);

		/* * * * * */
		// 0
		testedNumber = new KNumber (0);
		assertTestedNumberEquals (
			FITS_IN_INT,
			0,
			IS_INTEGER,
			FITS_IN_LONG
		);

		/* * * * * */
		// max int
		testedNumber = new KNumber (Integer.MAX_VALUE);
		assertTestedNumberEquals (
			FITS_IN_INT,
			Integer.MAX_VALUE,
			1,
			FITS_IN_LONG
		);

		/* * * * * */
		// max int + 1
		testedNumber = new KNumber ((long) Integer.MAX_VALUE + 1);
		assertTestedNumberEquals (
			BIGGER_THAN_INT,
			(long) Integer.MAX_VALUE + 1,
			IS_INTEGER,
			FITS_IN_LONG
		);

		/* * * * * */
		// max long
		testedNumber = new KNumber (Long.MAX_VALUE);
		assertTestedNumberEquals (
			BIGGER_THAN_INT,
			Long.MAX_VALUE,
			IS_INTEGER,
			FITS_IN_LONG
		);

		/* * * * * */
		// min int
		testedNumber = new KNumber (Integer.MIN_VALUE);
		assertTestedNumberEquals (
			FITS_IN_INT,
			Integer.MIN_VALUE,
			1,
			FITS_IN_LONG
		);

		/* * * * * */
		// min int - 1
		testedNumber = new KNumber ((long) Integer.MIN_VALUE - 1);
		assertTestedNumberEquals (
			BIGGER_THAN_INT,
			(long) Integer.MIN_VALUE - 1,
			1,
			FITS_IN_LONG
		);

		/* * * * * */
		// min long
		testedNumber = new KNumber (Long.MIN_VALUE);
		assertTestedNumberEquals (
			BIGGER_THAN_INT,
			Long.MIN_VALUE,
			IS_INTEGER,
			FITS_IN_LONG
		);
	}

	@Test
	public void testSetValue () throws Exception {
		testedNumber = new KNumber ();
		String stringValue;

		/* * * * * */
		testedNumber.setValue (1, 2);
		assertTestedNumberEquals (
			FITS_IN_INT,
			1,
			2,
			FITS_IN_LONG
		);

		/* * * * * */
		testedNumber.setValue (1000, 2000);
		assertTestedNumberEquals (
			FITS_IN_INT,
			1,
			2,
			FITS_IN_LONG
		);

		/* * * * * */
		int a = -42;
		int b = 401;
		testedNumber.setValue (a * 9999, b * 9999);
		assertTestedNumberEquals (
			FITS_IN_INT,
			a,
			b,
			FITS_IN_LONG
		);

		/* * * * * */
		stringValue = String.valueOf (Long.MAX_VALUE);
		testedNumber.setValue (stringValue);
		assertTestedNumberEquals (
			BIGGER_THAN_INT,
			Long.MAX_VALUE,
			IS_INTEGER,
			FITS_IN_LONG
		);

		/* * * * * */
		stringValue = "0";
		BigDecimal maxValue = new BigDecimal (stringValue);
		testedNumber.setValue (maxValue);
		assertTestedNumberEquals (
			BIGGER_THAN_INT,
			testedNumber.numerator,
			testedNumber.denominator,
			new BigDecimal (stringValue)
		);

		/* * * * * */
		BigInteger maxValuePlusOne =
			new BigInteger (String.valueOf (Long.MAX_VALUE))
				.add (BigInteger.ONE);
		stringValue = maxValuePlusOne.toString ();
		testedNumber.setValue (stringValue);
		assertTestedNumberEquals (
			BIGGER_THAN_INT,
			testedNumber.numerator,
			testedNumber.denominator,
			new BigDecimal (maxValuePlusOne)
		);

		/* * * * * */
		double minValue = Long.MIN_VALUE;
		testedNumber.setValue (minValue);
		assertTestedNumberEquals (
			BIGGER_THAN_INT,
			Long.MIN_VALUE,
			IS_INTEGER,
			FITS_IN_LONG
		);

		/* * * * * */
		BigInteger minValueMinusOne =
			new BigInteger (String.valueOf (Long.MIN_VALUE))
				.subtract (BigInteger.ONE);
		stringValue = minValueMinusOne.toString ();
		testedNumber.setValue (stringValue);
		assertTestedNumberEquals (
			BIGGER_THAN_INT,
			testedNumber.numerator,
			testedNumber.denominator,
			new BigDecimal (minValueMinusOne)
		);

		/* * * * * */
		testedNumber.setValue ("0.2");
		assertTestedNumberEquals (
			FITS_IN_INT,
			1,
			5,
			FITS_IN_LONG
		);

		/* * * * * */
		// demonstration of problems with precision of 'double'
		boolean preciseValueSet;
		testedNumber = new KNumber ();
		testedNumber.setValue (0.2);
		try {
			assertTestedNumberEquals (
				FITS_IN_INT,
				1,
				5,
				FITS_IN_LONG
			);
			// this cannot happen
			preciseValueSet = true;

		} catch (AssertionError error) {
			assertTestedNumberEquals (
				FITS_IN_INT,
				0,
				1,
				new BigDecimal (0.2)
			);
			// this is what should happen
			preciseValueSet = false;
		}
		assertEquals (false, preciseValueSet);
	}

	// check internal values of number
	private void assertTestedNumberEquals (
		boolean fitsInInt,
		long numerator,
		long denominator,
		BigDecimal bigDecimal
	) {
		assertNumberEquals (
			testedNumber,
			fitsInInt,
			numerator,
			denominator,
			bigDecimal
		);
	}

}