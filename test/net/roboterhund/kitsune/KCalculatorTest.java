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

import java.math.BigDecimal;

import static net.roboterhund.kitsune.CommonTest.assertNumberEquals;

// test KCalculator
public abstract class KCalculatorTest {

	protected KNumRegister result;
	protected KNumRegister a;
	protected KNumRegister b;
	protected KCalculator calculator;
	protected KConverter converter;

	// reset objects
	protected void reset () {
		calculator = new KCalculator ();

		result = new KNumRegister ();
		a = new KNumRegister ();
		b = new KNumRegister ();

		converter = CommonTest.converter;
	}

	// check internal values of number
	protected void assertResultEquals (
		int profile,
		long numerator,
		long denominator,
		BigDecimal bigDecimal
	) {
		assertNumberEquals (
			result,
			profile,
			numerator,
			denominator,
			bigDecimal
		);
	}

}
