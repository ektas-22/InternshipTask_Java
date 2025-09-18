package com.internship.employee;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents an Employee with ID, name, and salary.
 */
class Employee {
	private int id;
	private String name;
	private double salary;
	private boolean active; // For soft delete

	public Employee(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.active = true;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public boolean isActive() {
		return active;
	}

	public void deactivate() {
		this.active = false;
	}

	@Override
	public String toString() {
		return String.format("Employee [ID=%d, Name=%s, Salary=%.2f]", id, name, salary);
	}
}

/**
 * Main class for Employee Management System. Provides CRUD operations on
 * employees with input validation and soft delete support.
 */
public class EmployeeManagement {
	private static final ArrayList<Employee> employeeList = new ArrayList<>();
	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		int choice;
		do {
			showMenu();
			choice = getValidInt("Choose an option (1-5): ", 1, 5);

			switch (choice) {
			case 1 -> addEmployee();
			case 2 -> viewEmployees();
			case 3 -> updateEmployee();
			case 4 -> deleteEmployee();
			case 5 -> System.out.println("Exiting system...");
			}
		} while (choice != 5);

		scanner.close();
	}

	/** Display the main menu options */
	private static void showMenu() {
		System.out.println("\n**** Employee Management System ****");
		System.out.println("1. Add Employee");
		System.out.println("2. View Employees");
		System.out.println("3. Update Employee");
		System.out.println("4. Delete Employee");
		System.out.println("5. Exit");
	}

	/** Add a new employee with unique ID and validated input */
	private static void addEmployee() {
		int id;
		while (true) {
			id = getValidInt("Enter employee ID: ", 1, Integer.MAX_VALUE);
			if (findEmployeeById(id) != null && findEmployeeById(id).isActive()) {
				System.out.println("Employee with ID " + id + " already exists.");
			} else
				break;
		}

		String name = getValidName("Enter employee name: ");
		double salary = getValidSalary("Enter employee salary: ");
		employeeList.add(new Employee(id, name, salary));
		System.out.println("Employee added successfully.");
	}

	/** View all active employees */
	private static void viewEmployees() {
		boolean found = false;
		System.out.println("\n---- Employee List ----");
		for (Employee emp : employeeList) {
			if (emp.isActive()) {
				System.out.println(emp);
				found = true;
			}
		}
		if (!found)
			System.out.println("No employees found.");
	}

	/** Update an employee's name and salary */
	private static void updateEmployee() {
		if (employeeList.isEmpty()) {
			System.out.println("No employees to update.");
			return;
		}

		int id = getValidInt("Enter employee ID to update: ", 1, Integer.MAX_VALUE);
		Employee emp = findEmployeeById(id);
		if (emp == null || !emp.isActive()) {
			System.out.println("Employee not found.");
			return;
		}

		String newName = getValidName("Enter new name: ");
		double newSalary = getValidSalary("Enter new salary: ");
		emp.setName(newName);
		emp.setSalary(newSalary);
		System.out.println("Employee updated successfully.");
	}

	/** Soft delete an employee (mark inactive) with confirmation */
	private static void deleteEmployee() {
		if (employeeList.isEmpty()) {
			System.out.println("No employees to delete.");
			return;
		}

		int id = getValidInt("Enter employee ID to delete: ", 1, Integer.MAX_VALUE);
		Employee emp = findEmployeeById(id);
		if (emp == null || !emp.isActive()) {
			System.out.println("Employee not found.");
			return;
		}

		System.out.print("Are you sure you want to delete this employee? (y/n): ");
		String confirm = scanner.nextLine().trim().toLowerCase();
		if (confirm.equals("y")) {
			emp.deactivate(); // soft delete
			System.out.println("Employee deleted successfully.");
		} else {
			System.out.println("Deletion cancelled.");
		}
	}

	/** Find an employee by ID */
	private static Employee findEmployeeById(int id) {
		for (Employee emp : employeeList) {
			if (emp.getId() == id)
				return emp;
		}
		return null;
	}

	/** Validate integer input within a range */
	private static int getValidInt(String prompt, int min, int max) {
		while (true) {
			try {
				System.out.print(prompt);
				int value = Integer.parseInt(scanner.nextLine().trim());
				if (value < min || value > max) {
					System.out.println("Enter a number between " + min + " and " + max + ".");
					continue;
				}
				return value;
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a valid integer.");
			}
		}
	}

	/** Validate salary input */
	private static double getValidSalary(String prompt) {
		while (true) {
			try {
				System.out.print(prompt);
				double salary = Double.parseDouble(scanner.nextLine().trim());
				if (salary <= 0 || salary > 1_000_000) {
					System.out.println("Salary must be between 1 and 1,000,000.");
					continue;
				}
				return salary;
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a valid number.");
			}
		}
	}

	/** Validate name input (letters and spaces only) */
	private static String getValidName(String prompt) {
		while (true) {
			System.out.print(prompt);
			String name = scanner.nextLine().trim();
			if (name.isEmpty()) {
				System.out.println("Name cannot be empty.");
			} else if (!name.matches("^[A-Za-z ]+$")) {
				System.out.println("Name must only contain letters and spaces.");
			} else {
				return name;
			}
		}
	}
}