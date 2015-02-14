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

	// kNumber.fitsInInt == true
	public static final boolean FITS_IN_INT = true;

	// kNumber.fitsInInt == false
	public static final boolean BIGGER_THAN_INT = false;

	// kNumber.denominator == 1
	public static final long IS_INTEGER = 1;

	// kNumber.bigDecimal == null
	public static final BigDecimal FITS_IN_LONG = null;

	// output print stream
	public static final PrintStream out = System.out;

	// check internal values of number
	public static void assertNumberEquals (
		KNumber testedNumber,
		boolean fitsInInt,
		long numerator,
		long denominator,
		BigDecimal bigDecimal
	) {
		assertEquals ("fitsInInt", fitsInInt, testedNumber.fitsInInt);
		assertEquals ("bigDecimal", bigDecimal, testedNumber.bigDecimal);
		assertEquals ("numerator", numerator, testedNumber.numerator);
		assertEquals ("denominator", denominator, testedNumber.denominator);
	}

}
