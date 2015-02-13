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
 *
 */
public class KCalculator {

	/**
	 * Final calculation result storage.
	 * <p>
	 * Must be set before using any method.
	 */
	public KNumber result;

	/**
	 * Intermediate result.
	 */
	private long intResult;

	/**
	 * @param term_2
	 */
	public void add (KNumber term_2) {
		add (result, term_2);
	}

	/**
	 * @param term_1
	 * @param term_2
	 */
	public void add (KNumber term_1, KNumber term_2) {

	}

	/**
	 * @param subtrahend
	 */
	public void subtract (KNumber subtrahend) {
		subtract (result, subtrahend);
	}

	/**
	 * @param minuend
	 */
	public void subtractFrom (KNumber minuend) {
		subtract (minuend, result);
	}

	/**
	 * @param minuend
	 * @param subtrahend
	 */
	public void subtract (KNumber minuend, KNumber subtrahend) {

	}

	/**
	 * @param factor_2
	 */
	public void multiply (KNumber factor_2) {
		multiply (result, factor_2);
	}

	/**
	 * @param factor_1
	 * @param factor_2
	 */
	public void multiply (KNumber factor_1, KNumber factor_2) {

	}

	/**
	 * @param divisor
	 */
	public void divide (KNumber divisor) {
		divide (result, divisor);
	}

	/**
	 * @param dividend
	 */
	public void divideThat (KNumber dividend) {
		divide (dividend, result);
	}

	/**
	 * @param dividend
	 * @param divisor
	 */
	public void divide (KNumber dividend, KNumber divisor) {

	}

	/**
	 * @param term_1
	 * @param term_2
	 * @return
	 */
	private boolean add (long term_1, long term_2) {
		if (term_1 < 0) {
			if (term_1 < Integer.MIN_VALUE - term_2) {
				return false;
			}
		} else {
			if (term_1 > Integer.MAX_VALUE - term_2) {
				return false;
			}
		}
		intResult = term_1 + term_2;
		return true;
	}

	/**
	 * @param minuend
	 * @param subtrahend
	 * @return
	 */
	private boolean subtract (long minuend, long subtrahend) {
		if (minuend < 0) {
			if (minuend < Integer.MIN_VALUE + subtrahend) {
				return false;
			}
		} else {
			if (minuend > Integer.MAX_VALUE + subtrahend) {
				return false;
			}
		}
		intResult = minuend - subtrahend;
		return true;
	}

	/**
	 * @param factor_1
	 * @param factor_2
	 * @return
	 */
	private boolean multiply (long factor_1, long factor_2) {
		if (factor_2 > 0) {
			if (factor_1 > Integer.MAX_VALUE / factor_2
				|| factor_1 < Integer.MIN_VALUE / factor_2) {
				return false;
			}
		} else if (factor_2 < -1) {
			if (factor_1 < Integer.MAX_VALUE / factor_2
				|| factor_1 > Integer.MIN_VALUE / factor_2) {
				return false;
			}
		} else if (factor_2 == -1) {
			if (factor_1 == Integer.MIN_VALUE) {
				return false;
			}
		}
		intResult = factor_1 * factor_2;
		return true;
	}

}
