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

import java.math.BigInteger;

/**
 * Edge case numbers.
 */
public abstract class KEdges {

	/**
	 * {@link Integer#MAX_VALUE}.
	 */
	public static final BigInteger MAX_INT =
		BigInteger.valueOf (Integer.MAX_VALUE);

	/**
	 * {@link Integer#MIN_VALUE}.
	 */
	public static final BigInteger MIN_INT =
		BigInteger.valueOf (Integer.MIN_VALUE);

	/**
	 * {@link Long#MAX_VALUE}.
	 */
	public static final BigInteger MAX_LONG =
		BigInteger.valueOf (Long.MAX_VALUE);

	/**
	 * {@link Long#MIN_VALUE}.
	 */
	public static final BigInteger MIN_LONG =
		BigInteger.valueOf (Long.MIN_VALUE);

}
