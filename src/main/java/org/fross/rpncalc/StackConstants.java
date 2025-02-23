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

public class StackConstants {

	/**
	 * cmdPI(): Add the value of PI to the stack
	 * 
	 */
	public static void cmdPI(StackObj calcStack) {
		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		calcStack.push(String.valueOf(java.lang.Math.PI));
	}

	/**
	 * cmdPHI(): Add the value PHI (Golden Ratio) to the stack
	 * 
	 */
	public static void cmdPHI(StackObj calcStack) {
		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		calcStack.push("1.61803398874989");
	}

	/**
	 * cmdEuler(): Add the Euler constant to the stack
	 * 
	 */
	public static void cmdEuler(StackObj calcStack) {
		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		calcStack.push("2.7182818284590452353602874713527");
	}

	/**
	 * cmdSpeedOfLight(): Add the speed of light in meters/second to the stack
	 * 
	 */
	public static void cmdSpeedOfLight(StackObj calcStack) {
		// Save current calcStack to the undoStack
		calcStack.saveUndo();

		calcStack.push("299792458");
	}
}
