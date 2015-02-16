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

import static net.roboterhund.kitsune.CommonTest.assertNumberEquals;
import static org.junit.Assert.assertEquals;

// test KNumber in isolation
public class KNumberTest {

	private KNumber testedNumber;

	@Test
	public void testConstructor () throws Exception {
		// default value
		testedNumber = new KNumber ();
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			0,
			1,
			null
		);

		/* * * * * */
		// 0
		testedNumber = new KNumber (0);
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			0,
			1,
			null
		);

		/* * * * * */
		// max int
		testedNumber = new KNumber (Integer.MAX_VALUE);
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			Integer.MAX_VALUE,
			1,
			null
		);

		/* * * * * */
		// max int + 1
		testedNumber = new KNumber ((long) Integer.MAX_VALUE + 1);
		assertTestedNumberEquals (
			KProfile.LONG_INTEGER,
			(long) Integer.MAX_VALUE + 1,
			1,
			null
		);

		/* * * * * */
		// max long
		testedNumber = new KNumber (Long.MAX_VALUE);
		assertTestedNumberEquals (
			KProfile.LONG_INTEGER,
			Long.MAX_VALUE,
			1,
			null
		);

		/* * * * * */
		// min int
		testedNumber = new KNumber (Integer.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			Integer.MIN_VALUE,
			1,
			null
		);

		/* * * * * */
		// min int - 1
		testedNumber = new KNumber ((long) Integer.MIN_VALUE - 1);
		assertTestedNumberEquals (
			KProfile.LONG_INTEGER,
			(long) Integer.MIN_VALUE - 1,
			1,
			null
		);

		/* * * * * */
		// min long
		testedNumber = new KNumber (Long.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.LONG_INTEGER,
			Long.MIN_VALUE,
			1,
			null
		);
	}

	@Test
	public void testSetValue () throws Exception {
		testedNumber = new KNumber ();
		String stringValue;

		/* * * * * */
		testedNumber.setValue (1, 2);
		assertTestedNumberEquals (
			KProfile.INT_RATIONAL,
			1,
			2,
			null
		);

		/* * * * * */
		testedNumber.setValue (1000, 2000);
		assertTestedNumberEquals (
			KProfile.INT_RATIONAL,
			1,
			2,
			null
		);

		/* * * * * */
		int a = -42;
		int b = 401;
		testedNumber.setValue (a * 9999, b * 9999);
		assertTestedNumberEquals (
			KProfile.INT_RATIONAL,
			a,
			b,
			null
		);

		/* * * * * */
		stringValue = String.valueOf (Long.MAX_VALUE);
		testedNumber.setValue (stringValue);
		assertTestedNumberEquals (
			KProfile.LONG_INTEGER,
			Long.MAX_VALUE,
			1,
			null
		);

		/* * * * * */
		stringValue = "0";
		BigDecimal maxValue = new BigDecimal (stringValue);
		testedNumber.setValue (maxValue);
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			0,
			1,
			null
		);

		/* * * * * */
		BigInteger maxValuePlusOne =
			new BigInteger (String.valueOf (Long.MAX_VALUE))
				.add (BigInteger.ONE);
		stringValue = maxValuePlusOne.toString ();
		testedNumber.setValue (stringValue);
		assertTestedNumberEquals (
			KProfile.BIG,
			testedNumber.numerator,
			testedNumber.denominator,
			new BigDecimal (maxValuePlusOne)
		);

		/* * * * * */
		double minValue = Long.MIN_VALUE;
		testedNumber.setValue (minValue);
		assertTestedNumberEquals (
			KProfile.LONG_INTEGER,
			Long.MIN_VALUE,
			1,
			null
		);

		/* * * * * */
		BigInteger minValueMinusOne =
			new BigInteger (String.valueOf (Long.MIN_VALUE))
				.subtract (BigInteger.ONE);
		stringValue = minValueMinusOne.toString ();
		testedNumber.setValue (stringValue);
		assertTestedNumberEquals (
			KProfile.BIG,
			testedNumber.numerator,
			testedNumber.denominator,
			new BigDecimal (minValueMinusOne)
		);

		/* * * * * */
		testedNumber.setValue ("0.2");
		assertTestedNumberEquals (
			KProfile.INT_RATIONAL,
			1,
			5,
			null
		);

		/* * * * * */
		// demonstration of problems with precision of 'double'
		boolean preciseValueSet;
		testedNumber = new KNumber ();
		testedNumber.setValue (0.2);
		try {
			assertTestedNumberEquals (
				KProfile.INT_RATIONAL,
				1,
				5,
				null
			);
			// this cannot happen
			preciseValueSet = true;

		} catch (AssertionError error) {
			assertTestedNumberEquals (
				KProfile.BIG,
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
		int profile,
		long numerator,
		long denominator,
		BigDecimal bigDecimal
	) {
		assertNumberEquals (
			testedNumber,
			profile,
			numerator,
			denominator,
			bigDecimal
		);
	}

}
