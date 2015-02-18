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
		assertConversionStatusEquals (KConversionStatus.INEXACT);

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
		assertConversionStatusEquals (KConversionStatus.OK);

		stringValue = converter.toString (register);
		assertEquals ("4.5", stringValue);
		assertConversionStatusEquals (KConversionStatus.OK);
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
			converter.lastOperationStatus
		);
	}

}
