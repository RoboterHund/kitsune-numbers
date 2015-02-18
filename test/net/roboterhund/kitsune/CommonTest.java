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

import java.io.PrintStream;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

// common test functions, constants, etc.
public abstract class CommonTest {

	// converter
	static KConverter converter = new KConverter ();

	// output print stream
	public static final PrintStream out = System.out;

	// check internal values of number
	public static void assertNumberEquals (
		KNumRegister testedNumber,
		int profile,
		long numerator,
		long denominator,
		BigDecimal bigDecimal
	) {
		if (bigDecimal != null) {
			assertEquals (
				"bigDecimal",
				bigDecimal.toPlainString (),
				converter.toBigDecimal (testedNumber).toPlainString ()
			);
		}

		assertEquals ("profile", profile, testedNumber.profile);

		assertEquals ("numerator", numerator, testedNumber.numerator);
		assertEquals ("denominator", denominator, testedNumber.denominator);
	}

	public static void validate (BigDecimal expected, KNumRegister number) {
		// TODO deal with the BigDecimal.stripTrailingZeros () bug in other places
		if (expected.compareTo (BigDecimal.ZERO) == 0) {
			expected = BigDecimal.ZERO;
		} else {
			expected = expected.stripTrailingZeros ();
		}
		assertEquals (
			"validate",
			expected.toPlainString (),
			converter.toBigDecimal (number).toPlainString ());
	}

}
