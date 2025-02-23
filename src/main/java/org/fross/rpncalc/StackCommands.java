/******************************************************************************
 * RPNCalc
 * 
 * RPNCalc is is an easy to use console based RPN calculator
 * 
 *  Copyright (c) 2011-2024 Michael Fross
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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

public class StackCommands {
	/**
	 * cmdAddAll(): Add everything on the stack together and return the result to the stack
	 * 
	 * @param arg
	 */
	public static void cmdAddAll(StackObj calcStack, String arg) {
		// Ensure we have enough numbers on the stack
		if (calcStack.size() < 2) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  This operation requires at least two items on the stack");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Determine if we should keep or clear the stack upon adding
		boolean keepFlag = false;
		try {
			// Just check if the provided command starts with 'k'. That should be enough
			if (arg.toLowerCase().charAt(0) == 'k') {
				keepFlag = true;
			}
		} catch (StringIndexOutOfBoundsException ex) {
			keepFlag = false;
		}

		// Counter to hold the accumulating total
		BigDecimal totalCounter = BigDecimal.ZERO;

		// If the 'keep' flag was sent, get the stack items instead of using pop
		if (keepFlag == true) {
			for (int i = 0; i < calcStack.size(); i++) {
				totalCounter = totalCounter.add(calcStack.get(i));
			}
		} else {
			// Loop through the stack items popping them off until there is nothing left
			while (calcStack.isEmpty() == false) {
				totalCounter = totalCounter.add(calcStack.pop());
			}
		}

		// Add result back to the stack
		calcStack.push(totalCounter);
	}

	/**
	 * cmdAbsoluteValue(): Take the absolute value of the top stack item
	 */
	public static void cmdAbsoluteValue(StackObj calcStack) {
		// Ensure we have enough numbers on the stack
		if (calcStack.size() < 1) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  This operation requires at least one item on the stack");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		Output.debugPrintln("Taking the absolute value of " + calcStack.peek());

		// Pop the stack item and push back the absolute value
		calcStack.push(calcStack.pop().abs());

	}

	/**
	 * cmdAverage(): Calculate the average of the stack items
	 * 
	 * @param arg
	 */
	public static void cmdAverage(StackObj calcStack, String arg) {
		// Ensure we have enough numbers on the stack
		if (calcStack.size() < 2) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  Average requires at least two items on the stack");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Determine if we should keep or clear the stack
		boolean keepFlag = false;
		try {
			// Just check if the provided command starts with 'k'. That should be enough
			if (arg.toLowerCase().charAt(0) == 'k') {
				keepFlag = true;
			}
		} catch (StringIndexOutOfBoundsException ex) {
			keepFlag = false;
		}

		// Calculate the mean
		BigDecimal mean = Math.mean(calcStack);

		// If we are not going to keep the stack (the default) clear it
		if (keepFlag == false)
			calcStack.clear();

		// Add the average to the stack
		calcStack.push(mean);
	}

	/**
	 * cmdClean(): Clean the screen by clearing it and then showing existing stack
	 */
	public static void cmdClean() {
		// Rather than printing several hundred new lines, use the JANSI clear screen
		Output.clearScreen();
	}

	/**
	 * cmdClear(): Clear the current stack and the screen
	 */
	public static void cmdClear(StackObj calcStack) {
		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Empty the stack
		calcStack.clear();

		// Use JANSI to clear the screen
		Output.clearScreen();
	}

	/**
	 * cmdCopy(): Copy the item at the top of the stack or the line number provided
	 * 
	 */
	public static void cmdCopy(StackObj calcStack, String arg) {
		// Line number to copy
		int lineNumToCopy = 1;

		// Ensure we have at least one number to copy
		if (calcStack.size() < 1) {
			Output.printColorln(Ansi.Color.RED, "Error: The stack must contain at least one number to copy");
			return;
		}

		// Determine line number to copy
		try {
			lineNumToCopy = Integer.parseInt(arg);
		} catch (NumberFormatException ex) {
			if (!arg.isBlank()) {
				Output.printColorln(Ansi.Color.RED, "ERROR:  '" + arg + "' is not a valid line number");
				return;
			}
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		Output.debugPrintln("Copying line" + lineNumToCopy + " to line1");

		// Copy the provided number if it's valid
		try {
			// Ensure the number entered is is valid
			if (lineNumToCopy < 1 || lineNumToCopy > calcStack.size()) {
				Output.printColorln(Ansi.Color.RED, "Invalid line number entered: " + lineNumToCopy);
			} else {
				// Perform the copy
				calcStack.push(calcStack.get(calcStack.size() - lineNumToCopy));
			}
		} catch (Exception e) {
			Output.printColorln(Ansi.Color.RED, "Error parsing line number for element copy: '" + lineNumToCopy + "'");
			Output.debugPrintln(e.getMessage());
		}
	}

	/**
	 * cmdDelete(): Delete the provided item from the stack
	 * 
	 * @param item
	 */
	public static void cmdDelete(StackObj calcStack, String arg) {
		int startLine = -1;
		int endLine = -1;

		// Ensure we have at least one item on the stack
		if (calcStack.size() < 1) {
			Output.printColorln(Ansi.Color.RED, "There must be at least one item on the stack to delete");
			return;
		}

		// Remove any spaces from arg
		arg = arg.replace(" ", "");

		// Determine line to delete by looking at arg
		try {
			// If this doesn't have an exception, user entered in a single number integer
			// Simple delete that line
			startLine = Integer.parseInt(arg);
			endLine = startLine;

		} catch (NumberFormatException ex) {
			// If a dash is present, user entered in a range
			if (arg.contains("-")) {
				try {
					startLine = Integer.parseInt(arg.split("-")[0]);
					endLine = Integer.parseInt(arg.split("-")[1]);
				} catch (Exception e) {
					Output.printColorln(Ansi.Color.RED, "Invalid range provided: '" + arg + "'");
					return;
				}
			}

			// An invalid argument was provided
			if (!arg.isBlank() && startLine == -1) {
				Output.printColorln(Ansi.Color.RED, "Invalid line number provided: '" + arg + "'");
				return;

			} else if (startLine == -1) {
				// No argument was provided - delete the item on the top of the stack
				startLine = 1;
				endLine = 1;
			}
		}

		// If the start line is great than the end line, swap them as they were entered in the wrong order
		if (startLine > endLine) {
			int temp = startLine;
			startLine = endLine;
			endLine = temp;
		}

		Output.debugPrintln("Range to Delete: Line" + startLine + " to Line" + endLine);

		try {
			// Ensure the number entered is is valid
			if (startLine < 1 || endLine > calcStack.size()) {
				Output.printColorln(Ansi.Color.RED, "Deletion range must be between 1 and " + calcStack.size());
				return;

			} else {
				int counter = 0;	// Account for a shrinking calcStack.size() as items are removed
				// Save current calcStack to the undoStack
				calcStack.saveUndo();

				// Lets finally delete the lines from the provided range startLine -> endLine inclusive
				for (int i = startLine; i <= endLine; i++) {
					calcStack.remove(calcStack.size() - i + counter);
					counter++;
				}

			}

		} catch (Exception e) {
			Output.printColorln(Ansi.Color.RED, "Error parsing line number for element delete: '" + arg + "'");
			Output.debugPrintln(e.getMessage());
		}
	}

	/**
	 * cmdDice(XdY): Roll a Y sided die X times and add the result to the stack.
	 * 
	 * @param param
	 */
	public static void cmdDice(StackObj calcStack, String param) {
		int die = 6;
		int rolls = 1;

		// Parse out the die sides and rolls
		try {
			if (!param.isEmpty()) {
				rolls = Integer.parseInt(param.substring(0).trim().split("[Dd]")[0]);
				die = Integer.parseInt(param.substring(0).trim().split("[Dd]")[1]);
			}
		} catch (Exception e) {
			Output.printColorln(Ansi.Color.RED, "Error parsing dice parameter('" + param + "').  Format: 'dice xdy' where x=rolls, y=sides");
			return;
		}

		// Display Debug Output
		Output.debugPrintln("Rolls: '" + rolls + "'  |  Die: '" + die + "'");

		// Verify that the entered numbers are valid
		if (die <= 0) {
			Output.printColorln(Ansi.Color.RED, "ERROR: die must have greater than zero sides");
			return;
		} else if (rolls < 1) {
			Output.printColorln(Ansi.Color.RED, "ERROR: You have to specify at least 1 roll");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Roll them bones
		for (int i = 0; i < rolls; i++) {
			long randomNum = new java.util.Random().nextInt(die) + 1;
			calcStack.push(new BigDecimal(String.valueOf(randomNum)));
		}
	}

	/**
	 * cmdFactorial(): Take the factorial of the top of stack item dropping decimals if present
	 * 
	 * @param calcStack
	 */
	public static void cmdFactorial(StackObj calcStack) {
		// Ensure we have an item on the stack
		if (calcStack.size() < 1) {
			Output.printColorln(Ansi.Color.RED, "Error: There must be at least one item on the stack to perform a factorial");
			return;
		}

		// Ensure the provided number is not zero or negative
		if (calcStack.peek().compareTo(BigDecimal.ZERO) < 1) {
			Output.printColorln(Ansi.Color.RED, "ERROR: Factorial requires a number greater than zero");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Warn user the decimal has been dropped
		// TODO: Should make this more international at some point
		if (calcStack.peek().toPlainString().contains(".")) {
			Output.printColorln(Ansi.Color.CYAN, "Warning: The decimal portion of '" + calcStack.peek().toString() + "' has been dropped for the calculation");
		}

		BigDecimal result = Math.Factorial(calcStack.pop());
		calcStack.push(result);
	}

	/**
	 * cmdFlipSign(): Change the sign of the last element in the stack
	 * 
	 */
	public static void cmdFlipSign(StackObj calcStack) {
		if (calcStack.size() < 1) {
			Output.printColorln(Ansi.Color.RED, "Error: There must be at least one item on the stack to flip it's sign");

		} else {
			// Save current calcStack to the undoStack
			calcStack.saveUndo();

			Output.debugPrintln("Changing sign of last stack element");
			calcStack.push(calcStack.pop().multiply(new BigDecimal("-1")));
		}
	}

	/**
	 * cmdInteger(): Take the integer value of the top stack item
	 */
	public static void cmdInteger(StackObj calcStack) {
		if (calcStack.size() >= 1) {
			// Save current calcStack to the undoStack
			calcStack.saveUndo();

			Output.debugPrintln("Taking the integer of " + calcStack.peek().toEngineeringString());

			calcStack.push(new BigDecimal(calcStack.pop().toBigInteger()).toEngineeringString());

		} else {
			Output.printColorln(Ansi.Color.RED, "ERROR: Must be at least one item on the stack");
		}
	}

	/**
	 * cmdLinearRegression(): Based on the values in the stack (bottom to top) calculate the next predicted value via linear
	 * regression
	 * 
	 * Formula: y = b0 + (b1 * x)
	 * 
	 * Reference:
	 * https://www.statisticshowto.com/probability-and-statistics/regression-analysis/find-a-linear-regression-equation/#FindaLinear
	 * https://www.graphpad.com/quickcalcs/linear1
	 * 
	 * @param calcStack
	 */
	public static void cmdLinearRegression(StackObj calcStack) {
		// Ensure we have at least 2 values on the stack
		if (calcStack.size() < 2) {
			Output.printColorln(Ansi.Color.RED, "Error: There must be at least two items on the stack to calculate a linear regression");
			return;
		}

		// X is the number of stack items
		BigDecimal n = new BigDecimal(String.valueOf(calcStack.size()));
		BigDecimal sumX = BigDecimal.ZERO;		// X values are the stack numbers
		BigDecimal sumY = BigDecimal.ZERO;		// Sum of the stack values
		BigDecimal sumXY = BigDecimal.ZERO;		// Sum of X times Y
		BigDecimal sumX2 = BigDecimal.ZERO;		// Sum of X Squared
		BigDecimal sumY2 = BigDecimal.ZERO;		// Sum of Y Squared

		// Loop through the items to calculate the needed sums
		for (int i = 0; i < calcStack.size(); i++) {
			int x = i + 1;
			BigDecimal y = calcStack.get(i);

			// Calculate the sums:
			// sumX += x;
			// sumY += y;
			// sumXY += x * y;
			// sumX2 += x * x;
			// sumY2 += y * y;

			// sumX & sumY
			sumX = sumX.add(new BigDecimal(String.valueOf(x)));
			sumY = sumY.add(new BigDecimal(String.valueOf(y)));

			// sumXY
			sumXY = sumXY.add(new BigDecimal(String.valueOf(x)).multiply(y));

			// sumX2
			sumX2 = sumX2.add(new BigDecimal(String.valueOf(x)).pow(2));

			// sumY2
			sumY2 = sumY2.add(new BigDecimal(String.valueOf(y)).pow(2));

			// Line by line debug output
			Output.debugPrintln(
					"#" + i + ":\tx:" + x + "\ty:" + y + "\tXY:" + y.multiply(new BigDecimal(String.valueOf(x))) + "\tX2:" + (x * x) + "\tY2:" + y.pow(2));
		}

		// Calculate the remaining values
		// a = ((sumY * sumX2) - (sumX * sumXY)) / ((n * sumX2) - (sumX * sumX));
		// b = ((n * sumXY) - (sumX * sumY)) / ((n * sumX2) - (sumX * sumX));

		BigDecimal a_top = sumY.multiply(sumX2).subtract(sumX.multiply(sumXY));
		BigDecimal a_bottom = n.multiply(sumX2).subtract(sumX.pow(2));
		BigDecimal a = a_top.divide(a_bottom, MathContext.DECIMAL128);

		BigDecimal b_top = n.multiply(sumXY).subtract(sumX.multiply(sumY));
		BigDecimal b_bottom = n.multiply(sumX2).subtract(sumX.pow(2));
		BigDecimal b = b_top.divide(b_bottom, MathContext.DECIMAL128);

		// Output details if debug is enabled
		Output.debugPrintln("n:     " + n.toPlainString());
		Output.debugPrintln("sumX:  " + sumX.toPlainString());
		Output.debugPrintln("sumY:  " + sumY.toPlainString());
		Output.debugPrintln("sumXY: " + sumXY.toPlainString());
		Output.debugPrintln("sumX2: " + sumX2.toPlainString());
		Output.debugPrintln("sumY2: " + sumY2.toPlainString());
		Output.debugPrintln("a:     " + a.toPlainString());
		Output.debugPrintln("b:     " + b.toPlainString());

		// Rounded values are just for the display
		BigDecimal nextValue = a.add(b.multiply(n.add(BigDecimal.ONE)));
		BigDecimal aRounded = a.setScale(4, RoundingMode.HALF_UP);
		BigDecimal bRounded = b.setScale(4, RoundingMode.HALF_UP);
		BigDecimal nextValueRounded = nextValue.setScale(4, RoundingMode.HALF_UP);

		// Display the LR formula
		Output.printColorln(Ansi.Color.CYAN, "Slope Equation: y = " + bRounded + "x + " + aRounded);
		Output.printColorln(Ansi.Color.CYAN, "Slope: " + bRounded + "   Y-Intercept: " + aRounded);
		Output.printColorln(Ansi.Color.CYAN, "Predicted next value (" + nextValueRounded + ") added to the top of the stack");

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Add the next predicted value to the stack
		calcStack.push(nextValue);

	}

	/**
	 * cmdLog(): Take the natural (base e) logarithm
	 */
	public static void cmdLog(StackObj calcStack) {
		if (calcStack.size() >= 1) {
			// Save current calcStack to the undoStack
			calcStack.saveUndo();

			Output.debugPrintln("Taking the natural logarithm of " + calcStack.peek().toString());
			calcStack.push(java.lang.Math.log(calcStack.pop().doubleValue()));

		} else {
			Output.printColorln(Ansi.Color.RED, "ERROR: Must be at least one item on the stack");
		}
	}

	/**
	 * cmdLog10(): Take base10 logarithm
	 */
	public static void cmdLog10(StackObj calcStack) {
		if (calcStack.size() >= 1) {
			// Save current calcStack to the undoStack
			calcStack.saveUndo();

			Output.debugPrintln("Taking the base 10 logarithm of " + calcStack.peek());
			calcStack.push(java.lang.Math.log10(calcStack.pop().doubleValue()));

		} else {
			Output.printColorln(Ansi.Color.RED, "ERROR: Must be at least one item on the stack");
		}
	}

	/**
	 * cmdMaximum(): Add minimum value in the stack to the top of the stack
	 */
	public static boolean cmdMaximum(StackObj calcStack) {
		BigDecimal largestValue = new BigDecimal(Double.MIN_VALUE);

		// Ensure we have enough numbers on the stack
		if (calcStack.size() < 1) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  This operation requires at least one item on the stack");
			return false;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Loop through the stack and look for the largest value
		for (int i = 0; i < calcStack.size(); i++) {
			if (calcStack.get(i).compareTo(largestValue) > 0)
				largestValue = calcStack.get(i);
		}

		// Add lowest value to the stack
		calcStack.push(largestValue);

		return true;
	}

	/**
	 * cmdMedian(): Replace the stack with the median value. If `keep` is specified, retain the stack
	 */
	public static void cmdMedian(StackObj calcStack, String arg) {
		// Ensure we have enough numbers on the stack
		if (calcStack.size() < 2) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  Median requires at least two items on the stack");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Determine if we should keep or clear the stack
		boolean keepFlag = false;
		try {
			// Just check if the provided command starts with 'k'. That should be enough
			if (arg.toLowerCase().charAt(0) == 'k') {
				keepFlag = true;
			}
		} catch (StringIndexOutOfBoundsException ex) {
			keepFlag = false;
		}

		// Calculate the median
		BigDecimal median = Math.median(calcStack);

		// If we are not going to keep the stack (the default) clear it
		if (keepFlag == false)
			calcStack.clear();

		// Add the average to the stack
		calcStack.push(median);

	}

	/**
	 * cmdMinimum(): Add minimum value in the stack to the top of the stack
	 */
	public static boolean cmdMinimum(StackObj calcStack) {
		BigDecimal lowestValue = new BigDecimal(Double.MAX_VALUE);

		// Ensure we have enough numbers on the stack
		if (calcStack.size() < 1) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  This operation requires at least one item on the stack");
			return false;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Loop through the stack and look for the lowest value
		for (int i = 0; i < calcStack.size(); i++) {
			if (calcStack.get(i).compareTo(lowestValue) < 1)
				lowestValue = calcStack.get(i);
		}

		// Add lowest value to the stack
		calcStack.push(lowestValue);

		return true;
	}

	/**
	 * cmdMod(): Divide and place the modulus onto the stack
	 */
	public static void cmdModulus(StackObj calcStack) {
		// Ensure we have at least 2 items on the stack
		if (calcStack.size() < 2) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  There must be at least two items on the stack");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Perform the division and push the result onto the stack
		BigDecimal b = calcStack.pop();
		BigDecimal a = calcStack.pop();

		// Calculate the result. Negative numbers can cause problems - see the following for the result calculation
		BigDecimal remainder = a.remainder(b, MathContext.DECIMAL128);

		Output.debugPrintln("Modulus: " + a + " % " + b + " = " + remainder.toPlainString());
		calcStack.push(remainder);
	}

	/**
	 * cmdOperand(): An operand was entered such as + or -
	 * 
	 */
	public static void cmdOperand(StackObj calcStack, String op) {
		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		Output.debugPrintln("CalcStack has " + calcStack.size() + " elements");
		Output.debugPrintln("Operand entered: '" + op + "'");

		// Verify stack contains at least two elements
		if (calcStack.size() >= 2) {
			Math.Parse(op, calcStack);
		} else {
			Output.printColorln(Ansi.Color.RED, "Two numbers are required for this operation");
		}

	}

	/**
	 * cmdRandom(): Produce a random number between the Low and High values provided. If there are no parameters, produce the
	 * number between 1 and 100.
	 * 
	 * @param param
	 */
	public static void cmdRandom(StackObj calcStack, String param) {
		Long low = 1L;
		Long high = 100L;
		Long randomNumber = 0L;

		// Parse out the low and high numbers
		try {
			if (!param.isEmpty()) {
				low = Long.parseLong(param.substring(0).trim().split("\\s")[0]);
				high = Long.parseLong(param.substring(0).trim().split("\\s")[1]);
			}
		} catch (Exception e) {
			Output.printColorln(Ansi.Color.RED, "Error parsing low and high parameters.  Low: '" + low + "' High: '" + high + "'");
			Output.printColorln(Ansi.Color.RED, "See usage information in the help page");
			return;
		}

		// Display Debug Output
		Output.debugPrintln("Generating Random number between " + low + " and " + high + "(inclusive of both)");

		// Verify that the low number <= the high number
		if (low > high) {
			Output.printColorln(Ansi.Color.RED, "ERROR: the first number much be less than or equal to the high number");
			return;
		}

		// Generate the random number. This is inclusive to BOTH low and high values (hence the +1)
		randomNumber = ThreadLocalRandom.current().nextLong(low, high + 1);

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Add result to the calculator stack
		calcStack.push(new BigDecimal(randomNumber));
	}

	/**
	 * cmdRound(): Round to the provided decimal place. If none is provided round to the nearest integer
	 * 
	 * Reference: https://www.baeldung.com/java-round-decimal-number
	 * 
	 * @param arg
	 */
	public static void cmdRound(StackObj calcStack, String arg) {
		int decimalPlaces = 0;
		BigDecimal result;

		// Ensure we have something on the stack
		if (calcStack.isEmpty()) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  There must be at least one item on the stack");
			return;
		}

		// Convert the arg to the number of decimal places
		try {
			decimalPlaces = Integer.parseInt(arg);
			// Ensure a negative number is not provided for decimal points to round
			if (decimalPlaces < 0) {
				Output.printColorln(Ansi.Color.RED, "ERROR:  '" + arg + "' number of decimal places must be >= 0");
				return;
			}

		} catch (NumberFormatException ex) {
			if (arg.isBlank()) {
				decimalPlaces = 0;
			} else {
				// Error out for any non-valid characters
				Output.printColorln(Ansi.Color.RED, "ERROR:  '" + arg + "' not a valid number of decimal places");
				return;
			}
		}

		// Round the top of stack item and return that result to the stack
		result = calcStack.pop();
		result = result.setScale(decimalPlaces, RoundingMode.HALF_UP);

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		calcStack.push(result);
	}

	/**
	 * cmdSort(): Sort the stack in ascending or descending order
	 * 
	 * @param param
	 */
	public static void cmdSort(StackObj calcStack, String param) {
		// Ensure we have enough numbers on the stack
		if (calcStack.size() < 2) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  Sort requires at least two items on the stack");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Determine if we are sorting in ascending or descender mode
		try {
			if (param.toLowerCase().charAt(0) == 'a') {
				calcStack.sort("ascending");
			} else if (param.toLowerCase().charAt(0) == 'd') {
				calcStack.sort("descending");
			} else {
				throw new IllegalArgumentException();
			}
		} catch (Exception ex) {
			Output.printColorln(Ansi.Color.RED, "ERROR: Sort requires an (a)scending or (d)escending argument");
			return;
		}

	}

	/**
	 * cmdSwapElements(): Swap the provided elements within the stack
	 * 
	 * @param param
	 */
	public static void cmdSwapElements(StackObj calcStack, String param) {
		// Default is to swap last two stack items
		int item1 = 1;
		int item2 = 2;

		// Verify at least two elements exist
		if (calcStack.size() < 2) {
			Output.printColorln(Ansi.Color.RED, "Error: There must be at least 2 elements on the stack to swap");
			return;
		}

		// Determine the source and destination elements
		try {
			if (!param.isEmpty()) {
				item1 = Integer.parseInt(param.substring(0).trim().split("\\s")[0]);
				item2 = Integer.parseInt(param.substring(0).trim().split("\\s")[1]);
			}

		} catch (NumberFormatException e) {
			Output.printColorln(Ansi.Color.RED, "Error parsing line number for stack swap: '" + item1 + "' and '" + item2 + "'");
			return;

		} catch (Exception e) {
			Output.printColorln(Ansi.Color.RED, "ERROR:\n" + e.getMessage());
		}

		// Make sure the numbers are valid
		if (item1 < 1 || item1 > calcStack.size() || item2 < 1 || item2 > calcStack.size()) {
			Output.printColorln(Ansi.Color.RED, "Invalid element entered.  Must be between 1 and " + calcStack.size());

		} else {
			// Save current calcStack to the undoStack
			calcStack.saveUndo();

			// We're good - make the swap
			Output.debugPrintln("Swapping line" + item1 + " and line" + item2 + " stack items");
			StackOperations.StackSwapItems(calcStack, (item1 - 1), (item2) - 1);
		}
	}

	/**
	 * cmdSqrt(): Take the square root of the number at the top of the stack
	 * 
	 */
	public static void cmdSqrt(StackObj calcStack) {
		// Verify we have an item on the stack
		if (calcStack.isEmpty()) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  There must be at least one item on the stack");
			return;
		}

		// If the number to take the square root of is negative, return an error
		if (calcStack.peek().compareTo(BigDecimal.ZERO) < 0) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  You can not take the square root of a negative number");
			return;
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		Output.debugPrintln("Taking the square root of the last stack item");
		calcStack.push(calcStack.pop().sqrt(MathContext.DECIMAL128));
	}

	/**
	 * cmdStdDeviation(): Calculate the Standard Deviation of the stack items
	 * 
	 * Reference: https://www.mathsisfun.com/data/standard-deviation-formulas.html
	 * 
	 * @param arg
	 */
	public static void cmdStdDeviation(StackObj calcStack, String arg) {
		// Ensure we have enough numbers on the stack
		if (calcStack.size() < 2) {
			Output.printColorln(Ansi.Color.RED, "ERROR:  Standard Deviation requires at least two items on the stack");
			return;
		}

		// Determine if we should keep or clear the stack
		boolean keepFlag = false;
		try {
			// Just check if the provided command starts with 'k'. That should be enough
			if (arg.toLowerCase().charAt(0) == 'k') {
				keepFlag = true;
			}
		} catch (StringIndexOutOfBoundsException ex) {
			keepFlag = false;
		}

		// Step1: Get the mean
		BigDecimal mean1 = Math.mean(calcStack);
		Output.debugPrintln("Initial mean of the numbers: " + mean1.toPlainString());

		// Step2: For each number: subtract the mean from the number and square the result
		BigDecimal[] stdArray = new BigDecimal[calcStack.size()];

		// Zero out the array
		for (int i = 0; i < calcStack.size(); i++)
			stdArray[i] = BigDecimal.ZERO;

		for (int i = 0; i < calcStack.size(); i++) {
			stdArray[i] = calcStack.get(i).subtract(mean1);
			stdArray[i] = stdArray[i].pow(2);
		}

		// Step3: Work out the mean of those squared differences
		BigDecimal mean2 = Math.mean(stdArray);
		Output.debugPrintln("Secondary mean of (number-mean)^2: " + mean2);

		// Clear the stack if no 'keep' parameter sent
		if (keepFlag == false) {
			calcStack.clear();
		}

		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		// Step4: Take the square root of that result and push onto the stack
		calcStack.push(mean2.sqrt(MathContext.DECIMAL128));
	}

	/**
	 * cmdUndo(): Undo last change be restoring the last stack from the undo stack
	 * 
	 * The approach is to determine the index of the undo stack we'll be "undoing" back to and then restore the undo stack from
	 * that location to the main stack
	 * 
	 */
	public static void cmdUndo(StackObj calcStack, String arg) {
		Output.debugPrintln("Undoing command");
		int lineNum = 0;	// Undo line number NOT the position on the stack. That is one less.

		// Determine if an "undo back to" line number was provided. If not use the last entry
		try {
			lineNum = Integer.parseInt(arg);  // This is a line number so is one more than the index number

			// Ensure number provided as > 0 and less than the size of the undo stack
			if (lineNum <= 0 || lineNum > calcStack.undoSize()) {
				Output.printColorln(Ansi.Color.RED, "An invalid undo line number entered: '" + arg + "'");
				return;
			}
		} catch (NumberFormatException ex) {
			if (arg.isEmpty()) {
				// No number was provided, use the top stack item
				lineNum = calcStack.undoSize();
			} else {
				Output.printColorln(Ansi.Color.RED, "An invalid undo line number entered: '" + arg + "'");
				return;
			}
		}

		Output.debugPrintln("  - Restoring back to line number: " + lineNum + "  |  index number: " + (lineNum - 1));

		if (calcStack.undoSize() >= 1) {
			// Replace the calcStack items with the correct undo stack ones
			Stack<Stack<BigDecimal>> currentUndoStack = calcStack.undoGet();
			calcStack.replaceStack(currentUndoStack.get(lineNum - 1));

			// Discard the items in the Undo stack after the selected index
			for (int i = calcStack.undoSize(); i > lineNum - 1; i--) {
				Output.debugPrintln("  - Removing unneeded undo stack item at line " + (i) + " / index: " + (i - 1) + ":  " + calcStack.undoGet(i - 1));
				calcStack.undoRemove(i - 1);
			}

		} else {
			Output.printColorln(Ansi.Color.RED, "Error: Already at oldest change");
		}
	}

}
