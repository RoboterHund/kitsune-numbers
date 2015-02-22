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

import net.roboterhund.kitsune.KConverter.KConversionStatus;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static net.roboterhund.kitsune.CommonTest.assertNumberEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KConverterTest {

	KNumRegister register;
	KConverter converter;

	// conversion from other data types to KNumRegister
	@Test
	public void testConvert_fromOther_toRegister () {
		converter = new KConverter ();

		register = new KNumRegister ();

		converter.fromInt (register, -1);
		assertRegisterEquals (
			KProfile.INT_INTEGER,
			-1,
			1,
			null
		);

		converter.fromLong (register, 42);
		assertRegisterEquals (
			KProfile.INT_INTEGER,
			42,
			1,
			null
		);

		converter.fromDouble (register, -0.1);
		assertRegisterEquals (
			KProfile.LONG_RATIONAL,
			new BigDecimal (-0.1, converter.exactMathContext)
		);

		converter.fromBigInteger (
			register,
			new BigInteger (String.valueOf ((long) Integer.MAX_VALUE + 1))
		);
		assertRegisterEquals (
			KProfile.LONG_INTEGER,
			(long) Integer.MAX_VALUE + 1,
			1,
			null
		);

		converter.fromBigDecimal (register, new BigDecimal (1f / 8));
		assertRegisterEquals (
			KProfile.INT_RATIONAL,
			1,
			8,
			null
		);

		converter.fromString (register, "4.5");
		assertRegisterEquals (
			KProfile.INT_RATIONAL,
			9,
			2,
			null
		);

		NumberFormatException thrownException = null;
		try {
			// this is expected to fail
			converter.fromString (register, "4.+5");
			assertRegisterEquals (
				register.profile,
				register.numerator,
				register.denominator,
				null
			);
		} catch (NumberFormatException e) {
			thrownException = e;
		}
		assertTrue (thrownException != null);
	}

	// conversion from KNumRegister to other data types
	@Test
	public void testConvert_fromRegister_toOther () {
		converter = new KConverter ();
		int intValue;
		long longValue;
		double doubleValue;
		BigInteger bigIntegerValue;
		BigDecimal bigDecimalValue;
		String stringValue;

		register = new KNumRegister ();
		converter.fromString (register, "4.5");

		intValue = converter.toInt (register);
		assertEquals (4, intValue);
		assertTrue (
			converter.lastConversionValid ()
				&& !converter.lastConversionExact ()
		);

		longValue = converter.toLong (register);
		assertEquals (4, longValue);
		assertConversionStatusEquals (KConversionStatus.INEXACT);

		doubleValue = converter.toDouble (register);
		assertTrue (
			Math.abs (doubleValue - 4.5) < 2.220446049250313E-16);
		assertConversionStatusEquals (KConversionStatus.INEXACT);

		bigIntegerValue = converter.toBigInteger (register);
		assertEquals (new BigInteger ("4"), bigIntegerValue);
		assertConversionStatusEquals (KConversionStatus.INEXACT);

		bigDecimalValue = converter.toBigDecimal (register);
		assertEquals (
			new BigDecimal ("4.5").stripTrailingZeros (),
			bigDecimalValue);
		assertTrue (
			converter.lastConversionValid ()
				&& converter.lastConversionExact ()
				&& !converter.lastConversionFailed ()
		);

		stringValue = converter.toString (register);
		assertEquals ("4.5", stringValue);
		assertConversionStatusEquals (KConversionStatus.OK);

		register.setValue ((long) Integer.MAX_VALUE + 1);
		// int overflow
		converter.toInt (register);
		assertTrue (
			converter.lastConversionFailed ()
				&& !converter.lastConversionValid ()
				&& !converter.lastConversionExact ()
		);

		converter.fromString (
			register,
			"0.33333333333333333333333333333333333333333333333333333333333"
		);
		doubleValue = converter.toDouble (register);
		double diff_actual_expected = Math.abs (
			doubleValue
				- 0.33333333333333333333333333333333333333333333333333333333333
		);
		assertTrue (diff_actual_expected < 2.220446049250313E-16);
		assertTrue (
			!converter.lastConversionFailed ()
				&& converter.lastConversionValid ()
				&& !converter.lastConversionExact ()
		);

		converter.fromDouble (
			register,
			Double.MAX_VALUE
		);
		new KCalculator (new KNumRegisterPool ())
			.multiply (register, register, new KNumRegister (2));
		converter.toDouble (register);
		assertTrue (
			converter.lastConversionFailed ()
				&& !converter.lastConversionValid ()
				&& !converter.lastConversionExact ()
		);
	}

	// test register value
	private void assertRegisterEquals (
		int profile,
		BigDecimal bigDecimal) {

		assertNumberEquals (
			register,
			profile,
			register.numerator,
			register.denominator,
			bigDecimal
		);
	}

	// test register value
	private void assertRegisterEquals (
		int profile,
		long numerator,
		long denominator,
		BigDecimal bigDecimal) {

		assertNumberEquals (
			register,
			profile,
			numerator,
			denominator,
			bigDecimal
		);
	}

	// test conversion status
	private void assertConversionStatusEquals (int conversionStatus) {

		assertEquals (
			conversionStatus,
			converter.lastConversionStatus
		);
	}

}
