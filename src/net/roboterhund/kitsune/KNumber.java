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

/**
 *
 */
public class KNumber {

	/**
	 * <code>int</code> size flag.
	 * <p>
	 * If <code>true</code>, both numerator and denominator
	 * fit in a Java int/Integer.
	 */
	boolean fitsInInt;

	/**
	 * Numerator.
	 * <p>
	 * Stores both int and long number.
	 */
	long numerator;

	/**
	 * Denominator.
	 * <p>
	 * Stores both int and long number.
	 */
	long denominator;

	/**
	 * Big values fallback.
	 */
	BigDecimal bigDecimal;

	/**
	 * Constructor.
	 */
	public KNumber () {
		setValue ();
	}

	/**
	 * Constructor.
	 *
	 * @param number
	 */
	public KNumber (KNumber number) {
		setValue (number);
	}

	/**
	 * Constructor.
	 *
	 * @param numerator
	 * @param denominator
	 */
	public KNumber (long numerator, long denominator) {
		setValue (numerator, denominator);
	}

	/**
	 * Constructor.
	 *
	 * @param bigDecimal
	 */
	public KNumber (BigDecimal bigDecimal) {
		setValue (bigDecimal);
	}

	/**
	 * Constructor.
	 *
	 * @param longValue
	 */
	public KNumber (long longValue) {
		setValue (longValue);
	}

	/**
	 * Constructor.
	 *
	 * @param doubleValue
	 */
	public KNumber (double doubleValue) {
		setValue (doubleValue);
	}

	/**
	 * Constructor.
	 *
	 * @param stringValue
	 */
	public KNumber (String stringValue) {
		setValue (stringValue);
	}

	/**
	 *
	 */
	public void setValue () {

	}

	/**
	 * @param number
	 */
	public void setValue (KNumber number) {

	}

	/**
	 * @param numerator
	 * @param denominator
	 */
	public void setValue (long numerator, long denominator) {

	}

	/**
	 * @param bigDecimal
	 */
	public void setValue (BigDecimal bigDecimal) {

	}

	/**
	 * @param longValue
	 */
	public void setValue (long longValue) {

	}

	/**
	 * @param doubleValue
	 */
	public void setValue (double doubleValue) {
	}

	/**
	 * @param stringValue
	 */
	public void setValue (String stringValue) {
	}

}
