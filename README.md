# Codveda Internship Projects

This repository contains all the **Java projects** developed during my **Codveda Internship**.  
Each project is organised into its own folder and can be opened/run independently.

---

## 📂 Repository Structure
Codveda/
│
├─ Calculator/ # Console-based arithmetic calculator
├─ EmployeeManagementSystem/ # Employee CRUD operations using ArrayList
└─ LibraryManagementSystem/ # Library management with MySQL database

sql
Copy code

---

## 🔢 1. Calculator
A simple **console-based calculator** that performs:
- ➕ Addition
- ➖ Subtraction
- ✖️ Multiplication
- ➗ Division (with division-by-zero protection)

### How to Run
1. Open the `Calculator` project in your IDE (Eclipse/IntelliJ).
2. Run the `CalculatorApp` class.
3. Enter two numbers and an operator (`+ - * /`) when prompted.

---

## 👥 2. Employee Management System
A **CRUD application** to manage employees **in-memory** using an `ArrayList`.  
No database required.

### Features
- **Add** a new employee (ID, Name, Salary) — validates unique IDs.
- **View** all employees.
- **Update** employee name and salary.
- **Delete** employee by ID.
- Strong input validation for IDs, names, and salary range.

### How to Run
1. Open the `EmployeeManagementSystem` project in your IDE.
2. Run the `EmployeeManagementSystem` main class.
3. Follow the on-screen menu for operations.

---

## 📚 3. Library Management System
A console-based library application with a **MySQL database** for storing books, users, and transactions.

### Key Features
- **Book Management**: Add, view, update, and delete books.
- **User Management**: Add, view, update, and delete users.
- **Transactions**:
  - Borrow books (checks availability).
  - Return books (ensures correct borrower).
- Proper transaction handling with `commit`/`rollback` to prevent data corruption.

### Database Setup
Run these SQL commands in MySQL:
```sql
CREATE DATABASE library_db;
USE library_db;

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    available BOOLEAN DEFAULT TRUE
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    book_id INT,
    action ENUM('BORROW','RETURN'),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

🛠️ Tech Stack

Language: Java 17 (or compatible version)

Database: MySQL (only for Library project)

Build Tool: None (pure Java/Eclipse projects)

Version Control: Git & GitHub
