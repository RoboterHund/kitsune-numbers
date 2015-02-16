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

public class KCalculatorTest_subtract extends KCalculatorTest {

	@Test
	public void testMultiply () throws Exception {
		reset ();

		/* * * * * */

		a.setValue ("100.1");
		b.setValue ("0.2");
		calculator.subtract (result, a, b);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			999,
			10,
			null
		);

		/* * * * * */

		a.setValue ("1000000000000.1");
		b.setValue ("9999999999999.9");
		calculator.subtract (result, a, b);
		assertResultEquals (
			KProfile.LONG_RATIONAL,
			-44999999999999L,
			5,
			null
		);

		/* * * * * */
		String aString;
		String bString;

		aString = "1000000000000.1111";
		bString = "9999999999999.9999";
		a.setValue (aString);
		b.setValue (bString);
		assertEquals (a.profile, KProfile.LONG_RATIONAL);
		assertEquals (b.profile, KProfile.LONG_RATIONAL);
		calculator.subtract (result, a, b);
		assertResultEquals (
			KProfile.LONG_RATIONAL,
			result.numerator,
			result.denominator,
			new BigDecimal (aString)
				.subtract (new BigDecimal (bString))
		);

		/* * * * * */
		aString = "-1.000000000000111";
		bString = "69999999999999.99999";
		a.setValue (aString);
		b.setValue (bString);
		assertEquals (KProfile.LONG_RATIONAL, a.profile);
		assertEquals (KProfile.LONG_RATIONAL, b.profile);
		calculator.subtract (result, a, b);
		BigDecimal result = new BigDecimal (aString)
			.subtract (new BigDecimal (bString));
		assertResultEquals (
			KProfile.BIG,
			this.result.numerator,
			this.result.denominator,
			result
		);
		//noinspection ConstantConditions,ConstantIfStatement
		if (false) {
			System.out.println (
				aString
					+ " - "
					+ bString
					+ " = "
					+ result.toPlainString ()
			);
		}
	}

}
