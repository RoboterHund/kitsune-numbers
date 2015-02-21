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

public class KCalculatorTest_invert extends KCalculatorTest {

	@Test
	public void testInvert () throws Exception {
		reset ();

		String stringValue;
		BigDecimal bigValue;

		/* * * * * */
		stringValue = "-1";
		assertInvertCorrect (stringValue);

		/* * * * * */
		stringValue = "12.34";
		assertInvertCorrect (stringValue);

		/* * * * * */
		stringValue = String.valueOf (Long.MAX_VALUE);
		assertInvertCorrect (stringValue);

		/* * * * * */
		stringValue = String.valueOf (
			BigDecimal.valueOf (Long.MIN_VALUE).subtract (BigDecimal.ONE));
		assertInvertCorrect (stringValue);

		/* * * * * */
		bigValue = BigDecimal.valueOf (Long.MIN_VALUE);
		bigValue = bigValue.pow (3);
		stringValue = String.valueOf (bigValue);
		assertInvertCorrect (stringValue);

		/* * * * * */
		bigValue = BigDecimal.valueOf (Long.MIN_VALUE);
		bigValue = bigValue.pow (
			-11,
			converter.inexactMathContext
		);
		stringValue = bigValue.toPlainString ();
		assertInvertCorrect (stringValue);
	}

	// test inversion operations
	void assertInvertCorrect (String stringValue) {
		BigDecimal bigValue = new BigDecimal (stringValue);

		BigDecimal bigAbs = bigValue.abs ();
		BigDecimal bigNegate = bigValue.negate ();
		BigDecimal bigInverse;
		try {
			bigInverse = BigDecimal.ONE.divide (
				bigValue,
				converter.exactMathContext
			);
		} catch (ArithmeticException e) {
			bigInverse = BigDecimal.ONE.divide (
				bigValue,
				converter.inexactMathContext
			);
		}

		String stringAbs = bigAbs.stripTrailingZeros ().toPlainString ();
		String stringNegate = bigNegate.stripTrailingZeros ().toPlainString ();
		String stringInverse = bigInverse.stripTrailingZeros ().toPlainString ();

		converter.fromString (a, stringValue);

		// abs
		result.copy (a);
		calculator.abs (result, result);
		assertEquals (
			stringAbs,
			converter.toString (result));

		calculator.abs (result, a);
		assertEquals (
			stringAbs,
			converter.toString (result));

		// negate
		result.copy (a);
		calculator.negate (result, result);
		assertEquals (
			stringNegate,
			converter.toString (result));

		calculator.negate (result, a);
		assertEquals (
			stringNegate,
			converter.toString (result));

		// inverse
		result.copy (a);
		calculator.inverse (result, result);
		assertEquals (
			stringInverse,
			converter.toString (result));

		calculator.inverse (result, a);
		assertEquals (
			stringInverse,
			converter.toString (result));
	}

}
