/******************************************************************************
 * RPNCalc
 * 
 * RPNCalc is is an easy to use console based RPN calculator
 * 
 *  Copyright (c) 2013-2023 Michael Fross
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *           
 ******************************************************************************/
package org.fross.rpncalc;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

public class StackConversions {
	// Class Constants
	public static final Long DEFAULT_FRACTION_DENOMINATOR = 64L;  // Default Smallest Fraction Denominator

	/**
	 * cmdConvertMM(): Assumes Line1 is in inches and converts to millimeters
	 * 
	 */
	public static void cmdConvertIN2MM(StackObj calcStack) {
		// Verify at least one elements exists
		if (calcStack.size() < 1) {
			Output.printColorln(Ansi.Color.RED, "Error: There must be at least 1 element on the stack to convert");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Pop off the last value, convert, and push it back
		calcStack.push(calcStack.pop().multiply(new BigDecimal("25.4")));
	}

	/**
	 * cmdConvertIN(): Assumes Line1 is in millimeters and converts to inches
	 * 
	 */
	public static void cmdConvertMM2IN(StackObj calcStack) {
		// Verify at least one elements exists
		if (calcStack.size() < 1) {
			Output.printColorln(Ansi.Color.RED, "Error: There must be at least 1 element on the stack to convert");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Pop off the last value, convert, and push it back
		calcStack.push(calcStack.pop().divide(new BigDecimal("25.4")));
	}

	/**
	 * cmdFraction(): Display the last stack item as a fraction with a minimum base of the provided number. For example, sending
	 * 64 would produce a fraction of 1/64th but will be reduced if possible.
	 * 
	 * @param param
	 */
	public static String[] cmdFraction(StackObj calcStack, String param) {
		String[] outputString = { "", "", "", "" };
		boolean negativeNumber = false;

		// Verify we have an item on the stack
		if (calcStack.size() < 1) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  There must be at least one item on the stack");
			return outputString;
		}

		// Set the last stack item as the startingNumber
		BigDecimal startingNumber = calcStack.peek();

		// If starting number is negative, set a variable then remove the negative sign
		if (startingNumber.compareTo(BigDecimal.ZERO) < 0) {
			negativeNumber = true;
			startingNumber = startingNumber.abs();
		}

		// The base to convert the fraction to. For example, 64 = 1/64th
		Long denominator = DEFAULT_FRACTION_DENOMINATOR;

		// If a denominator is provided, use it instead of the default
		try {
			if (!param.isEmpty())
				denominator = Long.parseLong(param);
		} catch (NumberFormatException ex) {
			Output.printColorln(Ansi.Color.RED, "ERROR: '" + param + "' is not a valid denominator");
			return outputString;
		}

		// Determine the integer portion of the number
		// TODO delete
		// int integerPart = (int) java.lang.Math.floor(startingNumber);
		BigInteger integerPart = startingNumber.toBigInteger();

		// Determine the fractional portion as an double
		BigDecimal decimalPart = startingNumber.subtract(new BigDecimal(integerPart));

		// Convert to a fraction with provided base
		// TODO delete
		// long numerator = java.lang.Math.round(decimalPart * denominator);
		BigInteger numerator = decimalPart.multiply(new BigDecimal(String.valueOf(denominator))).toBigInteger();

		// Get the Greatest Common Divisor so we can simply the fraction
		long gcd = Math.GreatestCommonDivisor(numerator.longValue(), denominator);

		Output.debugPrint("Greatest Common Divisor for " + numerator.toString() + " and " + denominator + " is " + gcd);

		// Simply the fraction
		// TODO delete
		// numerator /= gcd;
		// denominator /= gcd;

		numerator = numerator.divide(new BigInteger(String.valueOf(gcd)));
		denominator /= gcd;

		// If starting number was negative, set it as negative again
		if (negativeNumber == true) {
			integerPart = integerPart.multiply(new BigInteger("-1"));
		}

		// Output the fractional display
		// If there is no fractional result, remove it so we don't see '0/1'
		String stackHeader = "-Fraction (Granularity: 1/" + (denominator * gcd) + ")";
		outputString[0] = "\n" + stackHeader + "-".repeat(Main.configProgramWidth - stackHeader.length());
		if (numerator.compareTo(BigInteger.ZERO) != 0) {
			outputString[1] = " " + calcStack.peek() + " is approximately '" + integerPart + " " + numerator + "/" + denominator + "'";
		} else {
			outputString[1] = " " + calcStack.peek() + " does not have a fractional component with a base of " + (denominator * gcd);
		}
		outputString[2] = "-".repeat(Main.configProgramWidth) + "\n";
		outputString[3] = integerPart + " " + numerator + "/" + denominator;

		return outputString;
	}

	/**
	 * cmdDegree(): Convert line1 from radians to degrees
	 * 
	 * Formula: degrees = radians * (180 / PI)
	 */
	public static void cmdRad2Deg(StackObj calcStack) {
		// Ensure we have something on the stack
		if (calcStack.isEmpty()) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  There are no items on the stack.");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Pull the value, convert and push back
		// TODO delete
		// calcStack.push(calcStack.pop() * (180 / java.lang.Math.PI));
		Double conversionFactor = 180 / java.lang.Math.PI;
		calcStack.push(calcStack.pop().multiply(new BigDecimal(String.valueOf(conversionFactor))));
	}

	/**
	 * cmdRadian(): Convert line1 from degrees to radians.
	 * 
	 * Formula: radians = degrees (PI/180)
	 */
	public static void cmdDeg2Rad(StackObj calcStack) {
		// Ensure we have something on the stack
		if (calcStack.isEmpty()) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  There are no items on the stack.");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Pull the value, convert and push back
		// TODO delete
		// calcStack.push(calcStack.pop() * (java.lang.Math.PI / 180));
		Double conversionFactor = java.lang.Math.PI / 180;
		calcStack.push(calcStack.pop().multiply(new BigDecimal(String.valueOf(conversionFactor))));
	}

	/**
	 * cmdGram2Oz(): Convert line1 from grams to ounces
	 * 
	 * There are 0.035274 ounces per gram
	 * 
	 * @param calcStack
	 */
	public static void cmdGram2Oz(StackObj calcStack) {
		// Ensure we have something on the stack
		if (calcStack.isEmpty()) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  There are no items on the stack.");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Make the conversion
		// TODO delete
		// calcStack.push(calcStack.pop() * 0.035274);
		calcStack.push(calcStack.pop().multiply(new BigDecimal("0.035274")));
	}

	/**
	 * cmdOz2Gram(): Convert line1 from grams to ounces
	 * 
	 * There are 28.349523125 grams per ounce
	 * 
	 * @param calcStack
	 */
	public static void cmdOz2Gram(StackObj calcStack) {
		// Ensure we have something on the stack
		if (calcStack.isEmpty()) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  There are no items on the stack.");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Make the conversion
		// TODO delete
		// calcStack.push(calcStack.pop() * 28.349523125);
		calcStack.push(calcStack.pop().multiply(new BigDecimal("28.349523125")));
	}

	/**
	 * cmdKg2Lb(): Convert line1 from kilograms to US pounds
	 * 
	 * @param calcStack
	 */
	public static void cmdKg2Lbs(StackObj calcStack) {
		// Ensure we have something on the stack
		if (calcStack.isEmpty()) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  There are no items on the stack.");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Make the conversion
		// TODO delete
		// calcStack.push(calcStack.pop() * 2.2046226218);
		calcStack.push(calcStack.pop().multiply(new BigDecimal("2.2046226218")));
	}

	/**
	 * cmdLb2Kg(): Convert line1 from US pounds to kilograms
	 * 
	 * @param calcStack
	 */
	public static void cmdLbs2Kg(StackObj calcStack) {
		// Ensure we have something on the stack
		if (calcStack.isEmpty()) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  There are no items on the stack.");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Make the conversion
		// TODO delete
		// calcStack.push(calcStack.pop() * 0.45359237);
		calcStack.push(calcStack.pop().multiply(new BigDecimal("0.45359237")));
	}
}
