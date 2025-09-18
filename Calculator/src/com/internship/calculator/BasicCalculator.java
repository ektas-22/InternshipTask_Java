package com.internship.calculator;

import java.util.Scanner;

/**
 * Calculator class provides basic arithmetic operations.
 */
class Calculator {

	/** Returns the sum of two numbers */
	public double addition(double n1, double n2) {
		return n1 + n2;
	}

	/** Returns the difference between two numbers */
	public double subtraction(double n1, double n2) {
		return n1 - n2;
	}

	/** Returns the product of two numbers */
	public double multiplication(double n1, double n2) {
		return n1 * n2;
	}

	/** Returns the division of two numbers; throws exception if dividing by zero */
	public double division(double n1, double n2) {
		if (n2 == 0) {
			throw new ArithmeticException("Division by zero is not allowed.");
		}
		return n1 / n2;
	}
}

/**
 * Main application class to interact with the user for performing calculations.
 */
public class BasicCalculator {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Calculator calculator = new Calculator();

		try {
			System.out.println("**** Simple Calculator ****");

			double num1 = getValidDouble(scanner, "Enter first number: ");
			char operator = getValidOperator(scanner, "Select the arithmetic operation [+,-,*,/]: ");
			double num2 = getValidDouble(scanner, "Enter second number: ");

			double result = switch (operator) {
			case '+' -> calculator.addition(num1, num2);
			case '-' -> calculator.subtraction(num1, num2);
			case '*' -> calculator.multiplication(num1, num2);
			case '/' -> calculator.division(num1, num2);
			default -> throw new IllegalStateException("Unexpected operator: " + operator);
			};
			System.out.println("Result = " + result);
		} catch (ArithmeticException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Invalid input! Please enter numbers only.");
		} finally {
			scanner.close();
		}
	}

	/**
	 * Prompt user and validate double input. Consumes entire line to avoid scanner
	 * issues.
	 *
	 * @param scanner Scanner object for input
	 * @param prompt  Message to display to user
	 * @return valid double entered by user
	 */
	private static double getValidDouble(Scanner scanner, String prompt) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();
			try {
				return Double.parseDouble(input);
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a valid number.");
			}
		}
	}

	/**
	 * Prompt user for an arithmetic operator and validate input.
	 *
	 * @param scanner Scanner object for input
	 * @param prompt  Message to display to user
	 * @return valid operator (+, -, *, /)
	 */
	private static char getValidOperator(Scanner scanner, String prompt) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();
			if (input.length() == 1 && "+-*/".contains(input)) {
				return input.charAt(0);
			} else {
				System.out.println("Invalid operator. Please enter one of [+,-,*,/].");
			}
		}
	}
}