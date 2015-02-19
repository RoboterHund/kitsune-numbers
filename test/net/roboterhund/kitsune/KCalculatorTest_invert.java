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
	public void testAbs () throws Exception {
		reset ();

		String stringValue;
		BigDecimal bigValue;

		/* * * * * */
		stringValue = "-1";
		converter.fromString (result, stringValue);
		calculator.abs (result);
		assertEquals (
			new BigDecimal (stringValue).abs (),
			converter.toBigDecimal (result));

		/* * * * * */
		stringValue = "12.34";
		converter.fromString (result, stringValue);
		calculator.abs (result);
		assertEquals (
			new BigDecimal (stringValue).abs (),
			converter.toBigDecimal (result));

		/* * * * * */
		stringValue = String.valueOf (Long.MAX_VALUE);
		converter.fromString (result, stringValue);
		calculator.abs (result);
		assertEquals (
			new BigDecimal (stringValue).abs (),
			converter.toBigDecimal (result));

		/* * * * * */
		stringValue = String.valueOf (
			BigDecimal.valueOf (Long.MIN_VALUE).subtract (BigDecimal.ONE));
		converter.fromString (result, stringValue);
		calculator.abs (result);
		assertEquals (
			new BigDecimal (stringValue).abs (),
			converter.toBigDecimal (result));

		/* * * * * */
		bigValue = BigDecimal.valueOf (Long.MIN_VALUE);
		bigValue = bigValue.pow (3);
		stringValue = String.valueOf (bigValue);
		converter.fromString (result, stringValue);
		calculator.abs (result);
		assertEquals (
			new BigDecimal (stringValue).abs (),
			converter.toBigDecimal (result));

		/* * * * * */
		bigValue = BigDecimal.valueOf (Long.MIN_VALUE);
		bigValue = bigValue.pow (
			-11,
			converter.inexactMathContext
		);
		stringValue = bigValue.toPlainString ();
		converter.fromString (result, stringValue);
		calculator.abs (result);
		assertEquals (
			new BigDecimal (stringValue).abs (),
			converter.toBigDecimal (result));
	}

}