package com.internship.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Library Management System
 * 
 * This program allows managing books, users, and borrowing/return transactions.
 * Features include: - Add/View/Update/Delete books and users (soft delete) -
 * Borrow and return books with transaction history - Prevent deletion/update of
 * borrowed books - Prevent deletion of users with borrowed books - Transaction
 * safety and row locking to prevent concurrency issues
 */
public class LibraryManagement {

	private static final String URL = "jdbc:mysql://localhost:3306/library_db";
	private static final String USER = "root";
	private static final String PASSWORD = "Acer#";

	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			System.out.println("Connected to Database!");

			int choice;
			do {
				showMenu();
				choice = getValidInt("Choose an option: ", 1, 11);

				switch (choice) {
				case 1 -> addBook(conn);
				case 2 -> viewBooks(conn);
				case 3 -> updateBook(conn);
				case 4 -> deleteBook(conn);
				case 5 -> addUser(conn);
				case 6 -> viewUsers(conn);
				case 7 -> updateUser(conn);
				case 8 -> deleteUser(conn);
				case 9 -> borrowBook(conn);
				case 10 -> returnBook(conn);
				case 11 -> System.out.println("Exiting...");
				}
			} while (choice != 11);

		} catch (SQLException e) {
			System.out.println("Database connection error: " + e.getMessage());
		}
	}

	// ================= MENU =================
	/**
	 * Display the main menu options
	 */
	private static void showMenu() {
		System.out.println("\n=========== Library Management System ===========");
		System.out.println("1. Add Book");
		System.out.println("2. View Books");
		System.out.println("3. Update Book");
		System.out.println("4. Delete Book");
		System.out.println("5. Add User");
		System.out.println("6. View Users");
		System.out.println("7. Update User");
		System.out.println("8. Delete User");
		System.out.println("9. Borrow Book");
		System.out.println("10. Return Book");
		System.out.println("11. Exit");
	}

	// ================= BOOK CRUD =================
	/**
	 * Add a new book to the library
	 * 
	 * @param conn Database connection
	 */
	private static void addBook(Connection conn) {
		try {
			System.out.print("Enter book title: ");
			String title = scanner.nextLine().trim();
			if (!isValidText(title)) {
				System.out.println("Invalid title.");
				return;
			}

			System.out.print("Enter author name: ");
			String author = scanner.nextLine().trim();
			if (!isValidText(author)) {
				System.out.println("Invalid author name.");
				return;
			}

			String sql = "INSERT INTO books (title, author, available, active) VALUES (?, ?, TRUE, TRUE)";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, title);
				stmt.setString(2, author);
				stmt.executeUpdate();
				System.out.println("Book added successfully.");
			}
		} catch (SQLException e) {
			System.out.println("Error adding book: " + e.getMessage());
		}
	}

	/**
	 * View all active books
	 * 
	 * @param conn Database connection
	 */
	private static void viewBooks(Connection conn) {
		String sql = "SELECT * FROM books WHERE active=TRUE";
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			System.out.println("\nBook List:");
			boolean found = false;
			while (rs.next()) {
				found = true;
				System.out.printf("ID: %d | Title: %s | Author: %s | Available: %s%n", rs.getInt("id"),
						rs.getString("title"), rs.getString("author"), rs.getBoolean("available") ? "Yes" : "No");
			}
			if (!found)
				System.out.println("No books found.");
		} catch (SQLException e) {
			System.out.println("Error fetching books: " + e.getMessage());
		}
	}

	/**
	 * Update book details Cannot update a book if it is currently borrowed
	 * 
	 * @param conn Database connection
	 */
	private static void updateBook(Connection conn) {
		try {
			int id = getValidInt("Enter Book ID to update: ", 1, Integer.MAX_VALUE);
			if (!existsBook(conn, id)) {
				System.out.println("Book not found.");
				return;
			}
			if (!isBookAvailable(conn, id)) {
				System.out.println("Cannot update a borrowed book.");
				return;
			}

			System.out.print("Enter new title: ");
			String title = scanner.nextLine().trim();
			System.out.print("Enter new author: ");
			String author = scanner.nextLine().trim();

			String sql = "UPDATE books SET title=?, author=? WHERE id=?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, title);
				stmt.setString(2, author);
				stmt.setInt(3, id);
				stmt.executeUpdate();
				System.out.println("Book updated successfully.");
			}
		} catch (SQLException e) {
			System.out.println("Error updating book: " + e.getMessage());
		}
	}

	/**
	 * Soft delete a book Cannot delete a borrowed book
	 * 
	 * @param conn Database connection
	 */
	private static void deleteBook(Connection conn) {
		try {
			int id = getValidInt("Enter Book ID to delete: ", 1, Integer.MAX_VALUE);
			if (!existsBook(conn, id)) {
				System.out.println("Book not found.");
				return;
			}
			if (!isBookAvailable(conn, id)) {
				System.out.println("Cannot delete a borrowed book.");
				return;
			}

			String sql = "UPDATE books SET active=FALSE WHERE id=?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, id);
				stmt.executeUpdate();
				System.out.println("Book deleted (soft delete).");
			}
		} catch (SQLException e) {
			System.out.println("Error deleting book: " + e.getMessage());
		}
	}

	// ================= USER CRUD =================
	/**
	 * Add a new user
	 * 
	 * @param conn Database connection
	 */
	private static void addUser(Connection conn) {
		try {
			System.out.print("Enter user name: ");
			String name = scanner.nextLine().trim();
			if (!isValidText(name)) {
				System.out.println("Invalid name.");
				return;
			}

			String sql = "INSERT INTO users (name, active) VALUES (?, TRUE)";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, name);
				stmt.executeUpdate();
				System.out.println("User added successfully.");
			}
		} catch (SQLException e) {
			System.out.println("Error adding user: " + e.getMessage());
		}
	}

	/**
	 * View all active users
	 * 
	 * @param conn Database connection
	 */
	private static void viewUsers(Connection conn) {
		String sql = "SELECT * FROM users WHERE active=TRUE";
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			System.out.println("\nUser List:");
			boolean found = false;
			while (rs.next()) {
				found = true;
				System.out.printf("ID: %d | Name: %s%n", rs.getInt("id"), rs.getString("name"));
			}
			if (!found)
				System.out.println("No users found.");
		} catch (SQLException e) {
			System.out.println("Error fetching users: " + e.getMessage());
		}
	}

	/**
	 * Update user details
	 * 
	 * @param conn Database connection
	 */
	private static void updateUser(Connection conn) {
		try {
			int id = getValidInt("Enter User ID to update: ", 1, Integer.MAX_VALUE);
			if (!existsUser(conn, id)) {
				System.out.println("User not found.");
				return;
			}

			System.out.print("Enter new name: ");
			String name = scanner.nextLine().trim();

			String sql = "UPDATE users SET name=? WHERE id=?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, name);
				stmt.setInt(2, id);
				stmt.executeUpdate();
				System.out.println("User updated.");
			}
		} catch (SQLException e) {
			System.out.println("Error updating user: " + e.getMessage());
		}
	}

	/**
	 * Soft delete a user Cannot delete a user with borrowed books
	 * 
	 * @param conn Database connection
	 */
	private static void deleteUser(Connection conn) {
		try {
			int id = getValidInt("Enter User ID to delete: ", 1, Integer.MAX_VALUE);
			if (!existsUser(conn, id)) {
				System.out.println("User not found.");
				return;
			}
			if (hasBorrowedBooks(conn, id)) {
				System.out.println("Cannot delete user with borrowed books.");
				return;
			}

			String sql = "UPDATE users SET active=FALSE WHERE id=?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, id);
				stmt.executeUpdate();
				System.out.println("User deleted (soft delete).");
			}
		} catch (SQLException e) {
			System.out.println("Error deleting user: " + e.getMessage());
		}
	}

	// ================= TRANSACTIONS =================
	/**
	 * Borrow a book Ensures the book is available and user exists
	 * 
	 * @param conn Database connection
	 */
	private static void borrowBook(Connection conn) {
		try {
			int userId = getValidInt("Enter User ID: ", 1, Integer.MAX_VALUE);
			if (!existsUser(conn, userId)) {
				System.out.println("User not found.");
				return;
			}

			int bookId = getValidInt("Enter Book ID: ", 1, Integer.MAX_VALUE);
			if (!existsBook(conn, bookId)) {
				System.out.println("Book not found.");
				return;
			}

			conn.setAutoCommit(false);

			// Lock the book row for concurrency safety
			try (PreparedStatement lockStmt = conn
					.prepareStatement("SELECT available FROM books WHERE id=? FOR UPDATE")) {
				lockStmt.setInt(1, bookId);
				try (ResultSet rs = lockStmt.executeQuery()) {
					if (rs.next() && !rs.getBoolean("available")) {
						System.out.println("Book already borrowed.");
						conn.rollback();
						return;
					}
				}
			}

			// Mark book as borrowed
			try (PreparedStatement update = conn.prepareStatement("UPDATE books SET available=FALSE WHERE id=?")) {
				update.setInt(1, bookId);
				update.executeUpdate();
			}

			// Insert transaction record
			try (PreparedStatement insert = conn.prepareStatement(
					"INSERT INTO transactions (user_id, book_id, action, date) VALUES (?, ?, 'BORROW', NOW())")) {
				insert.setInt(1, userId);
				insert.setInt(2, bookId);
				insert.executeUpdate();
			}

			conn.commit();
			System.out.println("Book borrowed successfully.");
		} catch (SQLException e) {
			System.out.println("Error borrowing book: " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException ignored) {
			}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException ignored) {
			}
		}
	}

	/**
	 * Return a book Ensures only the user who borrowed can return
	 * 
	 * @param conn Database connection
	 */
	private static void returnBook(Connection conn) {
		try {
			int userId = getValidInt("Enter User ID: ", 1, Integer.MAX_VALUE);
			if (!existsUser(conn, userId)) {
				System.out.println("User not found.");
				return;
			}

			int bookId = getValidInt("Enter Book ID: ", 1, Integer.MAX_VALUE);
			if (!existsBook(conn, bookId)) {
				System.out.println("Book not found.");
				return;
			}

			// Check last transaction for this book
			String sqlTx = "SELECT user_id, action FROM transactions WHERE book_id=? ORDER BY id DESC LIMIT 1";
			int lastUserId = -1;
			String lastAction = null;
			try (PreparedStatement ps = conn.prepareStatement(sqlTx)) {
				ps.setInt(1, bookId);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						lastUserId = rs.getInt("user_id");
						lastAction = rs.getString("action");
					} else {
						System.out.println("Book has no borrow record.");
						return;
					}
				}
			}

			if (!"BORROW".equalsIgnoreCase(lastAction) || lastUserId != userId) {
				System.out.println("This user cannot return the book.");
				return;
			}

			conn.setAutoCommit(false);

			// Mark book as available
			try (PreparedStatement updateBook = conn.prepareStatement("UPDATE books SET available=TRUE WHERE id=?")) {
				updateBook.setInt(1, bookId);
				updateBook.executeUpdate();
			}

			// Insert return transaction
			try (PreparedStatement insertTx = conn.prepareStatement(
					"INSERT INTO transactions (user_id, book_id, action, date) VALUES (?, ?, 'RETURN', NOW())")) {
				insertTx.setInt(1, userId);
				insertTx.setInt(2, bookId);
				insertTx.executeUpdate();
			}

			conn.commit();
			System.out.println("Book returned successfully.");
		} catch (SQLException e) {
			System.out.println("Error returning book: " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException ignored) {
			}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException ignored) {
			}
		}
	}

	// ================= HELPERS =================
	/** Get a valid integer within a range from user input */
	private static int getValidInt(String prompt, int min, int max) {
		while (true) {
			try {
				System.out.print(prompt);
				int input = Integer.parseInt(scanner.nextLine());
				if (input < min || input > max) {
					System.out.println("Enter between " + min + " and " + max);
					continue;
				}
				return input;
			} catch (NumberFormatException e) {
				System.out.println("Invalid number. Try again.");
			}
		}
	}

	/** Validate text input (letters, numbers, common punctuation) */
	private static boolean isValidText(String input) {
		return input != null && !input.isEmpty() && input.matches("[a-zA-Z0-9 .,'-]+");
	}

	/** Check if user exists and is active */
	private static boolean existsUser(Connection conn, int userId) {
		String sql = "SELECT id FROM users WHERE id=? AND active=TRUE";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			return false;
		}
	}

	/** Check if book exists and is active */
	private static boolean existsBook(Connection conn, int bookId) {
		String sql = "SELECT id FROM books WHERE id=? AND active=TRUE";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, bookId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			return false;
		}
	}

	/** Check if a book is currently available */
	private static boolean isBookAvailable(Connection conn, int bookId) {
		String sql = "SELECT available FROM books WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, bookId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getBoolean("available");
			}
		} catch (SQLException e) {
			return false;
		}
	}

	/** Check if user has any borrowed books */
	private static boolean hasBorrowedBooks(Connection conn, int userId) {
		String sql = "SELECT b.id FROM books b JOIN transactions t ON b.id=t.book_id "
				+ "WHERE t.user_id=? AND b.available=FALSE";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			return false;
		}
	}
}