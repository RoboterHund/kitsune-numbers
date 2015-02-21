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

public class KCalculatorTest_result extends KCalculatorTest {

	@Test
	public void testNumberResult () throws Exception {
		reset ();

		/* * * * * */
		a.setValue (222);
		b.setValue (333);
		calculator.divide (result, a, b);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			2,
			3,
			null
		);
		// continue

		a.copy (result);
		calculator.add (result, result, a);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			4,
			3,
			null
		);
		// continue

		calculator.add (result, result, a);
		assertResultEquals (
			KProfile.INT_INTEGER,
			2,
			1,
			null
		);
		// continue

		calculator.add (result, result, a);
		assertResultEquals (
			KProfile.INT_RATIONAL,
			8,
			3,
			null
		);

		/* * * * * */
		a.setValue (Long.MAX_VALUE);
		b.setValue (1);
		calculator.add (result, a, b);
		BigDecimal maxLongBigDecimal = new BigDecimal (Long.MAX_VALUE);
		assertResultEquals (
			KProfile.BIG_INTEGER,
			result.numerator,
			result.denominator,
			maxLongBigDecimal.add (BigDecimal.ONE)
		);
		// continue

		// the result reverts to simple fraction representation
		calculator.subtract (result, result, b);
		assertResultEquals (
			KProfile.LONG_INTEGER,
			Long.MAX_VALUE,
			1,
			null
		);
	}

	@Test
	public void testRound () throws Exception {
		reset ();

		String aString;
		BigDecimal bigA;

		/* * * * * */
		aString = "-2.5";
		converter.fromString (result, aString);
		calculator.truncate (result, result);
		assertResultEquals (
			KProfile.INT_INTEGER,
			-2,
			1,
			null
		);
		// continue

		converter.fromString (result, aString);
		calculator.floor (result, result);
		assertResultEquals (
			KProfile.INT_INTEGER,
			-3,
			1,
			null
		);
		// continue

		converter.fromString (result, aString);
		calculator.ceiling (result, result);
		assertResultEquals (
			KProfile.INT_INTEGER,
			-2,
			1,
			null
		);

		/* * * * * */
		aString = "+2.5";
		converter.fromString (result, aString);
		calculator.truncate (result, result);
		assertResultEquals (
			KProfile.INT_INTEGER,
			2,
			1,
			null
		);
		// continue

		converter.fromString (result, aString);
		calculator.floor (result, result);
		assertResultEquals (
			KProfile.INT_INTEGER,
			2,
			1,
			null
		);
		// continue

		converter.fromString (result, aString);
		calculator.ceiling (result, result);
		assertResultEquals (
			KProfile.INT_INTEGER,
			3,
			1,
			null
		);

		/* * * * * */
		aString = "-2000000000000000000000000000.5";
		converter.fromString (result, aString);
		calculator.truncate (result, result);
		bigA = new BigDecimal (aString);
		assertResultEquals (
			KProfile.BIG_INTEGER,
			result.numerator,
			result.denominator,
			bigA.divide (
				BigDecimal.ONE,
				0,
				BigDecimal.ROUND_DOWN
			)
		);
		// continue

		converter.fromString (result, aString);
		calculator.floor (result, result);
		assertResultEquals (
			KProfile.BIG_INTEGER,
			result.numerator,
			result.denominator,
			bigA.divide (
				BigDecimal.ONE,
				0,
				BigDecimal.ROUND_FLOOR
			)
		);
		// continue

		converter.fromString (result, aString);
		calculator.ceiling (result, result);
		assertResultEquals (
			KProfile.BIG_INTEGER,
			result.numerator,
			result.denominator,
			bigA.divide (
				BigDecimal.ONE,
				0,
				BigDecimal.ROUND_CEILING
			)
		);
	}

}
