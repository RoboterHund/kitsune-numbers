package net.roboterhund.kitsune;

import java.math.BigInteger;

/**
 * Edge case numbers.
 */
public abstract class KEdges {

	/**
	 * {@link Integer#MAX_VALUE} as {@link BigInteger}.
	 */
	public static final BigInteger MAX_INT =
		new BigInteger (String.valueOf (Integer.MAX_VALUE));

	/**
	 * {@link Integer#MIN_VALUE} as {@link BigInteger}.
	 */
	public static final BigInteger MIN_INT =
		new BigInteger (String.valueOf (Integer.MIN_VALUE));

	/**
	 * {@link Long#MAX_VALUE} as {@link java.math.BigInteger}.
	 */
	public static final BigInteger MAX_LONG =
		new BigInteger (String.valueOf (Long.MAX_VALUE));

	/**
	 * {@link Long#MIN_VALUE} as {@link BigInteger}.
	 */
	public static final BigInteger MIN_LONG =
		new BigInteger (String.valueOf (Long.MIN_VALUE));

	/**
	 * <code>(-{@link Long#MIN_VALUE})</code> as {@link BigInteger}
	 */
	public static final BigInteger MIN_LONG_NEGATED
		= MIN_LONG.negate ();

}
