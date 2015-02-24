# kitsune-numbers
`net.roboterhund.kitsune`  
_Java_ package for arbitrary-precision arithmetic.  

## Overview

This package was designed to provide a balanced data type for applications that:  
...process numbers that fall often within the range of `int` or `long`  
...but have to continue working with large numbers  
...and may not result in errors due to accumulated rounding.  

## Operations

The available operations are:

	add subtract multiply divide modulo
	power*
	truncate floor ceiling
	abs negate inverse
	compare

*Rational exponents: limited precision, negative base not supported, not suited for large degree roots.

Possible conversion (to and from) are:

	String
	int long BigInteger
	double BigDecimal

Also, direct assignment of rational values is possible.

## Classes

The package contains:

- A _register_ class to store rational numbers.  
	It will always hold the exact rational number that was stored in it.  
	It will not overflow.  
	It is mutable: good for performance, but requires more care to use properly.  

- A _calculator_ class.  
	It will take the fastest route to complete the calculation.  
	It will avoid unnecessary object allocation, unless the calculations require
	very large numbers.  

- A _converter_ class.  
	It will store values of _Java_ numeric data types into registers.  
	It will convert values of registers into _Java_ numeric data types.  

- A _register pool_ class.  
	Implements the _object pool_ pattern.  

## Pros

Advantages of using this package:

- No overflows.
- No precision loss.
- For sufficiently small operands, less object allocation.

## Cons

Pitfalls to avoid:

- May fail to give a performance advantage.  
See **Overview** for a list of conditions that a program should satisfy to fully benefit from this package.  
Otherwise, data types like `BigDecimal`, `long` or `double` would give better performance.
- The registers are ***mutable***.  
The next section discusses this.

## Handling mutability

It's true that immutable data types are easier to deal with, but I consider that it was a mistake that there are only immutable _public_ versions of `BigInteger` and `BigDecimal`.  
These classes should have been implemented as wrappers of mutable, _public_ classes. To prevent errors, immutable classes should be recommended for general use. The mutable classes could be less advertised. But they *should be there*.  

Anyway, the registers of this package are mutable. They are an abstraction of a _computer register_ (the name tries to point this out), not an abstraction of a numeric value.  
This means that extra care must be taken when using them.  
This is an example of problematic code:

	KCalculator calc;
	// result = n_1 - (int(n_1 / n_2) * n_2)
	void doStuff (KNumRegister result, KNumRegister n_1, KNumRegister n_2) {
		calc.divide (result, n_1, n_2);
		calc.truncate (result, result);
		calc.multiply (result, result, n_2);
		calc.subtract (result, n_1, result);
	}

The problem is that `result` could be the same object as `n_1` or `n_2`.  
Then, the last two operations would be operating with wrong values.  
A good implementation would do this:

	void doStuff (KNumRegister result, KNumRegister n_1, KNumRegister n_2) {
		// reserve temporal registers from pool
		KNumRegisterPool regPool = calc.regPool;
		KRegCont cont_1 = regPool.get ();
		KNumRegister temp_1 = cont_1.reg;

		// operate
		// none of the registers provided as arguments are written to
		calc.divide (temp_1, n_1, n_2);
		calc.truncate (temp_1, temp_1);
		calc.multiply (temp_1, temp_1, n_2);
		// output registers are only overwritten when input is no longer needed
		calc.subtract (result, n_1, temp_1);

		// release temporal registers (back to pool)
		regPool.discard (cont_1);
	}

It's the responsibility of the caller to provide output registers that can be safely overwritten.  
The function in the example does this, by reserving `temp_1` for itself.  
Thus, it saves space (by using only one temporal register) and time (by allocating it only once, and also _possibly_ by using a pool).  
Saving resources is important if the code must run on a small device.

A further consideration is to avoid returning registers, and passing them to functions that would in turn pass them to external code.  
The component that reserves a register must be the only one that can write in it.  
A copy must be created if the content of the register must be shared.

The general principle is:  
Do not overwrite a register that contains input data (until that data is no longer needed).

---

This package began as part of a private repo, but then it started to look as an independent module.

An enhancement I want to add in the future is inlining the `BigInteger` calculations,
to reduce object allocation to a minimum.  
But currently I have no time for this.

_RoboterHund87_  
_2015_  