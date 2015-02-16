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
 *
 * @see KNumRegister#profile
 */
abstract class KProfile {

	// number profiles

	// rational
	// stored in BigInteger
	static final int BIG_RATIONAL = 0;

	// integer
	// stored in BigInteger
	static final int BIG_INTEGER = 1;

	// rational
	// stored in long
	static final int LONG_RATIONAL = 2;

	// integer
	// stored in long
	static final int LONG_INTEGER = 3;

	// rational
	// stored in int
	static final int INT_RATIONAL = 4;

	// integer
	// stored in int
	static final int INT_INTEGER = 5;

	/* */

	// calculator routes

	// rational operands
	// use BigInteger
	static final int BIG__RAT_ = 0;

	// operand 1 integer, operand 2 rational
	// use BigInteger
	static final int BIG__INT1 = 1;

	// operand 1 rational, operand 2 integer
	// use BigInteger
	static final int BIG__INT2 = 2;

	// integer operands
	// use BigInteger
	static final int BIG__INT_ = 3;

	// rational operands
	// check long overflow before each step
	static final int LONG_RAT_ = 4;

	// operand 1 integer, operand 2 rational
	// check long overflow before each step
	static final int LONG_INT1 = 5;

	// operand 1 rational, operand 2 integer
	// check long overflow before each step
	static final int LONG_INT2 = 6;

	// integer operands
	// check long overflow before each step
	static final int LONG_INT_ = 7;

	// rational operands
	// check int overflow after operation
	static final int INT__RAT_ = 8;

	// integer operands
	// check int overflow after operation
	static final int INT__INT_ = 9;

	/* */

	// routing table
	static final int[][] route = {
		// \2
		// 1\
		// big rat.   big int.  long rat.  long int.   int rat.   int int.
		// big rat.
		{BIG__RAT_, BIG__INT2, BIG__RAT_, BIG__INT2, BIG__RAT_, BIG__INT2},
		// big int.
		{BIG__INT1, BIG__INT_, BIG__INT1, BIG__INT_, BIG__INT1, BIG__INT_},
		// long rat.
		{BIG__RAT_, BIG__INT2, LONG_RAT_, LONG_INT2, LONG_RAT_, LONG_INT2},
		// long int.
		{BIG__INT1, BIG__INT_, LONG_INT1, LONG_INT_, LONG_INT1, LONG_INT_},
		// int rat.
		{BIG__RAT_, BIG__INT2, LONG_RAT_, LONG_INT2, INT__RAT_, INT__RAT_},
		// int int.
		{BIG__INT1, BIG__INT_, LONG_INT1, LONG_INT_, INT__RAT_, INT__INT_}
	};

}
