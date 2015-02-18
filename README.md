# kitsune-numbers
`net.roboterhund.kitsune`

_Java_ package for arbitrary-precision arithmetic.

This package was designed to provide a balanced data type for applications that:  
...process numbers that mostly fall within the range of `int` or ` long`.  
...but have to continue working with large numbers.  
...and may not result in errors due to accumulated rounding.  

---
This package provides:

- A _register_ class to store rational numbers.  
	It will always hold the exact rational number that was stored in it.  
	It will not overflow.  
	It is mutable: good for performance, but requires more care to use properly.  

- A _calculator_ class.  
	It will try to be as fast as possible.  
	It trades off the absolute optimal performance for convenience.  
	It will avoid unnecessary object allocation, unless the calculations require
	very large numbers.  

- A _converter_ class.  
	It will store values of _Java_ numeric data types into registers.  
	It will convert values of registers into _Java_ numeric data types.  

It may be slower than using hard-coded data types, but it will spare the trouble of having to choose them. Also, it will be faster than using `BigDecimal` everywhere, and will not have the unstability of `double`.  

---
It started as part of a private repo, but then it started to look as an independent module.

_RoboterHund87_  
_2015_  
