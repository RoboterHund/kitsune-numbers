package net.roboterhund.kitsune;

/**
 *
 */
public abstract class KProfile {

	// number profiles

	// stored in BigDecimal
	static final int BIG = 0;

	// rational stored in long members
	static final int LONG_RATIONAL = 1;

	// integer stored in long members
	static final int LONG_INTEGER = 2;

	// rational stored in int members
	static final int INT_RATIONAL = 3;

	// integer stored in int members
	static final int INT_INTEGER = 4;

	/* */

	// calculator routes

	// use generic method
	static final int _BIGNUM___ = 0;

	// rational operands
	// check long overflow before each step
	static final int _PRE__RAT_ = 1;

	// operand 1 integer, operand 2 rational
	// check long overflow before each step
	static final int _PRE__INT1 = 2;

	// operand 1 rational, operand 2 integer
	// check long overflow before each step
	static final int _PRE__INT2 = 3;

	// integer operands
	// check long overflow before each step
	static final int _PRE__INT_ = 4;

	// rational operands
	// check int overflow after operation
	static final int _POST_RAT_ = 5;

	// integer operands
	// check int overflow after operation
	static final int _POST_INT_ = 6;

	/* */

	// routing table
	static final int[][] route = {
		// \2
		// 1\
		//      big   long rat.   long int.    int rat.    int int.
		// big
		{_BIGNUM___, _BIGNUM___, _BIGNUM___, _BIGNUM___, _BIGNUM___},
		// long rat.
		{_BIGNUM___, _PRE__RAT_, _PRE__INT2, _PRE__RAT_, _PRE__INT2},
		// long int.
		{_BIGNUM___, _PRE__INT1, _PRE__INT_, _PRE__INT1, _PRE__INT_},
		// int rat.
		{_BIGNUM___, _PRE__RAT_, _PRE__INT2, _POST_RAT_, _POST_RAT_},
		// int int.
		{_BIGNUM___, _PRE__INT1, _PRE__INT_, _POST_RAT_, _POST_INT_}
	};

}
