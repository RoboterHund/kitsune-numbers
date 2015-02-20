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

import static org.junit.Assert.assertEquals;

public class KCalculatorTest_compare extends KCalculatorTest {

	@Test
	public void testInvert () throws Exception {
		reset ();

		assertCompareCorrect (
			"0",
			"0"
		);

		assertCompareCorrect (
			"1",
			"1"
		);

		assertCompareCorrect (
			"-1",
			"1"
		);

		assertCompareCorrect (
			"-1",
			"-1"
		);

		assertCompareCorrect (
			"100000.1",
			"100000.2"
		);

		assertCompareCorrect (
			"100000.1",
			"-100000.2"
		);

		assertCompareCorrect (
			"100000.0000001",
			"100000.0000002"
		);

		assertCompareCorrect (
			"100000.0000001",
			"100000"
		);

		assertCompareCorrect (
			"100000.000000000000000000000000000000000000000000000000001",
			"100000.000000000000000000000000000000000000000000000000002"
		);

		assertCompareCorrect (
			"100000.000000000000000000000000000000000000000000000000001",
			"-100000.000000000000000000000000000000000000000000000000002"
		);

		assertCompareCorrect (
			"100000.000000000000000000000000000000000000000000000000001",
			"100000"
		);

		assertCompareCorrect (
			"100000.000000000000000000000000000000000000000000000000001",
			"-100000"
		);

		assertCompareCorrect (
			String.valueOf (KEdges.MAX_LONG),
			String.valueOf (KEdges.MAX_LONG.add (BigInteger.ONE))
		);
	}

	// test inversion operations
	void assertCompareCorrect (String string_1, String string_2) {
		BigDecimal big_1 = new BigDecimal (string_1);
		BigDecimal big_2 = new BigDecimal (string_2);
		converter.fromString (a, string_1);
		converter.fromString (b, string_2);
		assertEquals (
			big_1.compareTo (big_2),
			compare (a, b));
		assertEquals (
			big_2.compareTo (big_1),
			compare (b, a));
	}

	// convert calculator comparison result
	// to BigDecimal equivalent
	int compare (KNumRegister n_1, KNumRegister n_2) {
		long result = calculator.compare (n_1, n_2);
		if (result < 0) {
			return -1;
		} else if (result > 0) {
			return 1;
		} else {
			return 0;
		}
	}

}
