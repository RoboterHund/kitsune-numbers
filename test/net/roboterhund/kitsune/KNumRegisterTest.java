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
import static net.roboterhund.kitsune.CommonTest.converter;

// test KNumber in isolation
public class KNumRegisterTest {

	private KNumRegister testedNumber;

	@Test
	public void testConstructor () throws Exception {
		// default value
		testedNumber = new KNumRegister ();
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			0,
			1,
			null
		);

		/* * * * * */
		// 0
		testedNumber = new KNumRegister (0);
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			0,
			1,
			null
		);

		/* * * * * */
		// max int
		testedNumber = new KNumRegister (Integer.MAX_VALUE);
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			Integer.MAX_VALUE,
			1,
			null
		);

		/* * * * * */
		// max int + 1
		testedNumber = new KNumRegister ((long) Integer.MAX_VALUE + 1);
		assertTestedNumberEquals (
			KProfile.LONG_INTEGER,
			(long) Integer.MAX_VALUE + 1,
			1,
			null
		);

		/* * * * * */
		// max long
		testedNumber = new KNumRegister (Long.MAX_VALUE);
		assertTestedNumberEquals (
			KProfile.LONG_INTEGER,
			Long.MAX_VALUE,
			1,
			null
		);

		/* * * * * */
		// min int
		testedNumber = new KNumRegister (Integer.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			Integer.MIN_VALUE,
			1,
			null
		);

		/* * * * * */
		// min int - 1
		testedNumber = new KNumRegister ((long) Integer.MIN_VALUE - 1);
		assertTestedNumberEquals (
			KProfile.LONG_INTEGER,
			(long) Integer.MIN_VALUE - 1,
			1,
			null
		);

		/* * * * * */
		// min long
		testedNumber = new KNumRegister (Long.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.BIG_INTEGER,
			testedNumber.numerator,
			testedNumber.denominator,
			BigDecimal.valueOf (Long.MIN_VALUE)
		);


		/* * * * * */
		testedNumber = new KNumRegister (10, -20);
		assertTestedNumberEquals (
			KProfile.INT_RATIONAL,
			-1,
			2,
			null
		);

		/* * * * * */
		testedNumber = new KNumRegister (Long.MIN_VALUE, -1);
		assertTestedNumberEquals (
			KProfile.BIG_INTEGER,
			testedNumber.numerator,
			testedNumber.denominator,
			new BigDecimal (Long.MAX_VALUE).add (BigDecimal.ONE)
		);

		/* * * * * */
		testedNumber = new KNumRegister (Long.MIN_VALUE, -2);
		assertTestedNumberEquals (
			KProfile.LONG_INTEGER,
			testedNumber.numerator,
			testedNumber.denominator,
			new BigDecimal (Long.MIN_VALUE).divide (
				new BigDecimal (-2),
				converter.exactMathContext
			)
		);

		/* * * * * */
		testedNumber = new KNumRegister (Long.MIN_VALUE, -3);
		assertTestedNumberEquals (
			KProfile.BIG_RATIONAL,
			testedNumber.numerator,
			testedNumber.denominator,
			new BigDecimal (Long.MIN_VALUE).divide (
				new BigDecimal (-3),
				converter.inexactMathContext
			)
		);

		/* * * * * */
		int d = 5;
		testedNumber = new KNumRegister (Long.MIN_VALUE, d);
		assertTestedNumberEquals (
			KProfile.BIG_RATIONAL,
			testedNumber.numerator,
			testedNumber.denominator,
			new BigDecimal (Long.MIN_VALUE).divide (
				new BigDecimal (d),
				converter.exactMathContext
			)
		);

		/* * * * * */
		testedNumber = new KNumRegister (-3, Long.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.BIG_RATIONAL,
			testedNumber.numerator,
			testedNumber.denominator,
			BigDecimal.valueOf (-3).divide (
				new BigDecimal (Long.MIN_VALUE),
				converter.exactMathContext
			)
		);


		/* * * * * */
		testedNumber = new KNumRegister (-2, Long.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.LONG_RATIONAL,
			testedNumber.numerator,
			testedNumber.denominator,
			BigDecimal.valueOf (-2).divide (
				new BigDecimal (Long.MIN_VALUE),
				converter.exactMathContext
			)
		);

		/* * * * * */
		testedNumber = new KNumRegister (-1, Long.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.BIG_RATIONAL,
			testedNumber.numerator,
			testedNumber.denominator,
			BigDecimal.valueOf (-1).divide (
				new BigDecimal (Long.MIN_VALUE),
				converter.exactMathContext
			)
		);

		/* * * * * */
		testedNumber = new KNumRegister (0, Long.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			testedNumber.numerator,
			testedNumber.denominator,
			BigDecimal.ZERO
		);

		/* * * * * */
		testedNumber = new KNumRegister (1, Long.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.BIG_RATIONAL,
			testedNumber.numerator,
			testedNumber.denominator,
			BigDecimal.ONE.divide (
				new BigDecimal (Long.MIN_VALUE),
				converter.exactMathContext
			)
		);

		/* * * * * */
		testedNumber = new KNumRegister (2, Long.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.LONG_RATIONAL,
			testedNumber.numerator,
			testedNumber.denominator,
			new BigDecimal (2).divide (
				new BigDecimal (Long.MIN_VALUE),
				converter.exactMathContext
			)
		);

		/* * * * * */
		testedNumber = new KNumRegister (Long.MIN_VALUE, Long.MIN_VALUE);
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			testedNumber.numerator,
			testedNumber.denominator,
			BigDecimal.ONE
		);

		/* * * * * */
		testedNumber = new KNumRegister (BigInteger.valueOf (42));
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			42,
			1,
			BigDecimal.valueOf (42)
		);

		/* * * * * */
		testedNumber = new KNumRegister (
			BigInteger.valueOf (42),
			BigInteger.valueOf (-2));
		assertTestedNumberEquals (
			KProfile.INT_INTEGER,
			-21,
			1,
			BigDecimal.valueOf (-21)
		);
	}

	@Test
	public void testSetValue () throws Exception {
		testedNumber = new KNumRegister ();

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
