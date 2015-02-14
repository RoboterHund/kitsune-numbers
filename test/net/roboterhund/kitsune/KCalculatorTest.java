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

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

import static net.roboterhund.kitsune.CommonTest.assertNumberEquals;
import static net.roboterhund.kitsune.CommonTest.out;

// test KCalculator
public class KCalculatorTest {

	protected KNumber result;
	protected KNumber a;
	protected KNumber b;
	protected KCalculator calculator;

	// check internal values of number
	protected void assertResultEquals (
		boolean fitsInInt,
		long numerator,
		long denominator,
		BigDecimal bigDecimal
	) {
		assertNumberEquals (
			result,
			fitsInInt,
			numerator,
			denominator,
			bigDecimal
		);
	}

	// some random calculations
	// change some parameters to perform various tests
	@SuppressWarnings ({"ConstantConditions", "UnusedAssignment"})
	@Test
	public void testRandomCalculations () {
		RandomTestData data = new RandomTestData ();

		// some test parameters
		//
		// see getRandomNumber(), RandomTestData.init() and getMask()
		// to know how the 'number of bits' parameters
		// affect the size of the operands
		data.init (
			// number of iterations in test loop
			10000000,
			// number of bits for random integer part
			40,
			// number of bits for random decimal part
			10,

			// these are currently not used in this package
			false,
			false,
			false,
			false,
			false,
			false
		);
		// generally, the normal test was found to run faster
		// (but not dramatically faster)
		//
		// unfortunately, there seem to be some combinations
		// (of parameters)
		// that cause the BigDecimal test to
		// consistently outperform the normal test
		// (for example, 32 in integer part and 18 for decimals)

		// this was used for a test of how large BigDecimals
		// affect performance
		// (it does, but not much, it seems)
		BigDecimal bigDecimalOperand = null;

		boolean useBigDecimal = true;
		// uncomment to avoid using the bigger number
		useBigDecimal = false;

		if (useBigDecimal) {
			bigDecimalOperand = new BigDecimal (Long.MAX_VALUE);
			bigDecimalOperand =
				bigDecimalOperand.multiply (bigDecimalOperand);
		}

		// measure time to perform test for reference
		// (does not account for extra operations in test)

		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean ();

		long startTime = threadMXBean.getCurrentThreadCpuTime ();
		// more parameters
		// see randomTest() javadoc
		randomTest (
			data,
			false,
			bigDecimalOperand,
			true,
			out
		);
		long endTime = threadMXBean.getCurrentThreadCpuTime ();

		out.printf ("\n");

		long elapsedMillis = (endTime - startTime) / 1000000;
		float elapsedMinutes = (float) elapsedMillis / 60000;

		out.printf (
			"Elapsed time:\n"
				+ "\tms: %d\n"
				+ "\tmin: %f\n",

			elapsedMillis,
			elapsedMinutes
		);

		out.printf ("\n");

		// show the data types used to store the result

		String format =
			"Results of %d iterarions:\n"

				+ "add:\n"
				+ "\tint: %d\n"
				+ "\tlong: %d\n"
				+ "\tBigDecimal: %d\n"

				+ "subtract:\n"
				+ "\tint: %d\n"
				+ "\tlong: %d\n"
				+ "\tBigDecimal: %d\n"

				+ "multiply:\n"
				+ "\tint: %d\n"
				+ "\tlong: %d\n"
				+ "\tBigDecimal: %d\n"

				+ "divide:\n"
				+ "\tint: %d\n"
				+ "\tlong: %d\n"
				+ "\tBigDecimal: %d\n";

		out.printf (
			format,

			data.numOperations,

			data.numIntResults_add,
			data.numLongResults_add,
			data.numBigDecimalResults_add,

			data.numIntResults_subtract,
			data.numLongResults_subtract,
			data.numBigDecimalResults_subtract,

			data.numIntResults_multiply,
			data.numLongResults_multiply,
			data.numBigDecimalResults_multiply,

			data.numIntResults_divide,
			data.numLongResults_divide,
			data.numBigDecimalResults_divide
		);
	}

	/**
	 * Test using random operands.
	 * Didn't have time to think of anything better.
	 * It is not very good but
	 * it shows some interesting results.
	 *
	 * @param data collection of parameters (input)
	 * and output (counters and lists of numbers)
	 * @param forceBigDecimal if true, force operands to be BigDecimals.
	 * @param bigDecimalOperand if not null,
	 * and forceBigDecimal is also true,
	 * use it as first operand for all operations.
	 * @param performMultiplications if false, do not perform multiplications.
	 * Multiplications were singled out due to
	 * their tendency to produce BigDecimals.
	 * TODO better control other tests
	 * @param out if not null, print info while in progress here
	 */
	public static void randomTest (
		RandomTestData data,
		boolean forceBigDecimal,
		BigDecimal bigDecimalOperand,
		boolean performMultiplications,
		PrintStream out) {

		// TODO extract operations other than actual calculations
		// OR increase granularity of time measurements

		KCalculator calculator = new KCalculator ();

		KNumber result = new KNumber ();
		KNumber a = new KNumber ();
		KNumber b = new KNumber ();

		Random random = new Random ();

		int showProgressEach = data.numOperations / 10;
		int untilShowProgress = showProgressEach;

		int operationsPerformed = 0;

		smallestNumber_abs = Double.POSITIVE_INFINITY;
		largestNumber_abs = 0;

		for (int i = 0; i < data.numOperations; i++) {
			String stringValue = getRandomNumber (data, random);
			if (forceBigDecimal) {
				if (bigDecimalOperand != null) {
					a.setValue (bigDecimalOperand);
				} else {
					a.setValue (new BigDecimal (stringValue));
				}
			} else {
				a.setValue (stringValue);
			}
			if (data.aOperands != null) {
				data.aOperands.add (new KNumber (a));
			}

			b.setValue (getRandomNumber (data, random));
			if (data.bOperands != null) {
				data.bOperands.add (new KNumber (b));
			}

			calculator.add (result, a, b);
			operationsPerformed++;
			if (data.results_add != null) {
				data.results_add.add (new KNumber (result));
			}
			if (result.fitsInInt) {
				data.numIntResults_add++;
			} else if (result.bigDecimal == null) {
				data.numLongResults_add++;
			} else {
				data.numBigDecimalResults_add++;
			}

			calculator.subtract (result, a, b);
			operationsPerformed++;
			if (data.results_subtract != null) {
				data.results_subtract.add (new KNumber (result));
			}
			if (result.fitsInInt) {
				data.numIntResults_subtract++;
			} else if (result.bigDecimal == null) {
				data.numLongResults_subtract++;
			} else {
				data.numBigDecimalResults_subtract++;
			}

			if (performMultiplications) {
				calculator.multiply (result, a, b);
				operationsPerformed++;
				if (data.results_multiply != null) {
					data.results_multiply.add (new KNumber (result));
				}
				if (result.fitsInInt) {
					data.numIntResults_multiply++;
				} else if (result.bigDecimal == null) {
					data.numLongResults_multiply++;
				} else {
					data.numBigDecimalResults_multiply++;
				}
			}

			calculator.divide (result, a, b);
			operationsPerformed++;
			if (data.results_divide != null) {
				data.results_divide.add (new KNumber (result));
			}
			if (result.fitsInInt) {
				data.numIntResults_divide++;
			} else if (result.bigDecimal == null) {
				data.numLongResults_divide++;
			} else {
				data.numBigDecimalResults_divide++;
			}

			if (out != null) {
				untilShowProgress--;
				if (untilShowProgress == 0) {
					out.printf (
						"Performed %d operations...\n",
						operationsPerformed
					);
					untilShowProgress = showProgressEach;
				}
			}
		}

		if (out != null) {
			out.printf (
				"Finished.\n"
					+ "Operand range: (absolute values)\n"
					+ "\tSmallest: %f\n"
					+ "\tLargest: %f\n",
				smallestNumber_abs,
				largestNumber_abs
			);
		}
	}

	private static double smallestNumber_abs;
	private static double largestNumber_abs;

	private static String getRandomNumber (
		RandomTestData data,
		Random random) {

		long intValue;
		long decValue;
		String valueString;

		do {
			long part = random.nextLong ();
			intValue = Math.abs (part & data.integerMask);
			if (part < 0) {
				intValue = -intValue;
			}

			decValue = Math.abs (random.nextLong () & data.decimalsMask);

			valueString = intValue + "." + decValue;
		} while (intValue == 0 && decValue == 0);

		double number = Math.abs (Double.parseDouble (valueString));
		if (number < smallestNumber_abs) {
			smallestNumber_abs = number;
		} else if (number > largestNumber_abs) {
			largestNumber_abs = number;
		}

		return valueString;
	}

	public static class RandomTestData {
		public int numOperations;

		public int numIntResults_add;
		public int numLongResults_add;
		public int numBigDecimalResults_add;

		public int numIntResults_subtract;
		public int numLongResults_subtract;
		public int numBigDecimalResults_subtract;

		public int numIntResults_multiply;
		public int numLongResults_multiply;
		public int numBigDecimalResults_multiply;

		public int numIntResults_divide;
		public int numLongResults_divide;
		public int numBigDecimalResults_divide;

		public long integerMask;
		public long decimalsMask;

		public ArrayList<KNumber> aOperands;
		public ArrayList<KNumber> bOperands;
		public ArrayList<KNumber> results_add;
		public ArrayList<KNumber> results_subtract;
		public ArrayList<KNumber> results_multiply;
		public ArrayList<KNumber> results_divide;

		public void init (
			int numOperations,
			int bitsIn_integer,
			int bitsIn_decimal,
			boolean storeA,
			boolean storeB,
			boolean storeResults_add,
			boolean storeResults_subtract,
			boolean storeResults_multiply,
			boolean storeResults_divide) {

			this.numOperations = numOperations;

			numIntResults_add = 0;
			numLongResults_add = 0;
			numBigDecimalResults_add = 0;
			numIntResults_subtract = 0;
			numLongResults_subtract = 0;
			numBigDecimalResults_subtract = 0;
			numIntResults_multiply = 0;
			numLongResults_multiply = 0;
			numBigDecimalResults_multiply = 0;
			numIntResults_divide = 0;
			numLongResults_divide = 0;
			numBigDecimalResults_divide = 0;

			this.integerMask = getMask (bitsIn_integer);
			this.decimalsMask = getMask (bitsIn_decimal);

			if (storeA) {
				aOperands = new ArrayList<KNumber> ();
			}
			if (storeB) {
				bOperands = new ArrayList<KNumber> ();
			}
			if (storeResults_add) {
				results_add = new ArrayList<KNumber> ();
			}
			if (storeResults_subtract) {
				results_subtract = new ArrayList<KNumber> ();
			}
			if (storeResults_multiply) {
				results_multiply = new ArrayList<KNumber> ();
			}
			if (storeResults_divide) {
				results_divide = new ArrayList<KNumber> ();
			}
		}

		private static long getMask (int numBits) {
			return ~(~0L << numBits);
		}
	}

}
