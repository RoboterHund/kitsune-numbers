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
 * Pool of number registers.
 */
public class KNumRegisterPool {

	/**
	 * Stack top.
	 */
	private KRegCont top;

	/**
	 * Get register container.
	 */
	public KRegCont get () {
		if (top == null) {
			return new KRegCont ();
		} else {
			KRegCont ret = top;
			top = ret.re;
			ret.re = null;
			return ret;
		}
	}

	/**
	 * Discard register container.
	 */
	public void discard (KRegCont discarded) {
		discarded.re = top;
		top = discarded;
	}

	/**
	 * {@code KNumRegister} container.
	 */
	public class KRegCont {

		/**
		 * Reusable register.
		 */
		public final KNumRegister reg = new KNumRegister ();

		/**
		 * Next container in stack.
		 */
		private KRegCont re;

	}

}
