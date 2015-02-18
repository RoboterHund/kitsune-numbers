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

/**
 * Collection of values related to number profiles.
 * <p>
 * Contains:
 * <ul>
 * <li>List of number profiles.</li>
 * <li>List of calculator routes.</li>
 * <li>2-dimensional table mapping operand profiles
 * to calculator routes.</li>
 * </ul>
 * <p>
 * <b>Note</b>: 'fit' or 'stored' in a data type means
 * that both the numerator and denominator can be stored
 * in two separate variables of that type.
 *
 * @see KNumRegister#profile
 */
abstract class KProfile {

	// number profiles

	/**
	 * Number profile:
	 * rational
	 * stored in BigInteger.
	 */
	static final int BIG_RATIONAL = 0;

	/**
	 * Number profile:
	 * integer
	 * stored in BigInteger.
	 */
	static final int BIG_INTEGER = 1;

	/**
	 * Number profile:
	 * rational
	 * stored in long.
	 */
	static final int LONG_RATIONAL = 2;

	/**
	 * Number profile:
	 * integer
	 * stored in long.
	 */
	static final int LONG_INTEGER = 3;

	/**
	 * Number profile:
	 * rational
	 * stored in int.
	 */
	static final int INT_RATIONAL = 4;

	/**
	 * Number profile:
	 * integer
	 * stored in int.
	 */
	static final int INT_INTEGER = 5;

	/* */

	// calculator routes

	/**
	 * Calculator route:
	 * rational operands,
	 * use BigInteger.
	 */
	static final int _BIG__RAT_ = 0;

	/**
	 * Calculator route:
	 * operand 1 integer, operand 2 rational,
	 * use BigInteger.
	 */
	static final int _BIG__INT1 = 1;

	/**
	 * Calculator route:
	 * operand 1 rational, operand 2 integer,
	 * use BigInteger.
	 */
	static final int _BIG__INT2 = 2;

	/**
	 * Calculator route:
	 * integer operands,
	 * use BigInteger.
	 */
	static final int _BIG__INT_ = 3;

	/**
	 * Calculator route:
	 * rational operands,
	 * check long overflow before each step.
	 */
	static final int _LONG_RAT_ = 4;

	/**
	 * Calculator route:
	 * operand 1 integer, operand 2 rational,
	 * check long overflow before each step.
	 */
	static final int _LONG_INT1 = 5;

	/**
	 * Calculator route:
	 * operand 1 rational, operand 2 integer,
	 * check long overflow before each step.
	 */
	static final int _LONG_INT2 = 6;

	/**
	 * Calculator route:
	 * integer operands,
	 * check long overflow before each step.
	 */
	static final int _LONG_INT_ = 7;

	/**
	 * Calculator route:
	 * one or both operands is rational,
	 * both fit in int.
	 */
	static final int _INT__RAT_ = 8;

	/**
	 * Calculator route:
	 * integer operands that fit in int.
	 */
	static final int _INT__INT_ = 9;

	/* */

	/**
	 * Routing table.
	 * Profiles are used as indices.
	 * Values are calculator routes.
	 * <p>
	 * It is *almost* symmetrical
	 * (symmetry is due to requirement to use the widest data type)
	 * (non-symmetry is due to non-commutativity of operations).
	 * <p>
	 * Routes for narrow data types towards bottom right corner.
	 * <p>
	 * Routes for wider data types towards left and top sides.
	 * <p>
	 * <i>Don't expect it to show up <b>here</b>.</i>
	 * <!-- browser users will understand -->
	 */
	static final int[][] route = {
		// columns: operand 2
		// rows: operand 1
		// big rat.   big int.  long rat.  long int.   int rat.   int int.
		// big rat.
		{_BIG__RAT_, _BIG__INT2, _BIG__RAT_, _BIG__INT2, _BIG__RAT_, _BIG__INT2},
		// big int.
		{_BIG__INT1, _BIG__INT_, _BIG__INT1, _BIG__INT_, _BIG__INT1, _BIG__INT_},
		// long rat.
		{_BIG__RAT_, _BIG__INT2, _LONG_RAT_, _LONG_INT2, _LONG_RAT_, _LONG_INT2},
		// long int.
		{_BIG__INT1, _BIG__INT_, _LONG_INT1, _LONG_INT_, _LONG_INT1, _LONG_INT_},
		// int rat.
		{_BIG__RAT_, _BIG__INT2, _LONG_RAT_, _LONG_INT2, _INT__RAT_, _INT__RAT_},
		// int int.
		{_BIG__INT1, _BIG__INT_, _LONG_INT1, _LONG_INT_, _INT__RAT_, _INT__INT_}
	};

}
