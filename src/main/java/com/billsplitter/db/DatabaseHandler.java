package com.billsplitter.db;

import com.billsplitter.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/db/billsplitter.db";

    public DatabaseHandler() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "phone TEXT NOT NULL)";
            String createGroupsTable = "CREATE TABLE IF NOT EXISTS Groups (" +
                    "group_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "group_name TEXT NOT NULL, " +
                    "created_by INTEGER, " +
                    "FOREIGN KEY(created_by) REFERENCES Users(user_id))";
            String createGroupMembersTable = "CREATE TABLE IF NOT EXISTS GroupMembers (" +
                    "group_id INTEGER, " +
                    "user_id INTEGER, " +
                    "PRIMARY KEY(group_id, user_id), " +
                    "FOREIGN KEY(group_id) REFERENCES Groups(group_id), " +
                    "FOREIGN KEY(user_id) REFERENCES Users(user_id))";
            String createExpensesTable = "CREATE TABLE IF NOT EXISTS Expenses (" +
                    "expense_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "group_id INTEGER, " +
                    "title TEXT NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "paid_by INTEGER, " +
                    "date TEXT NOT NULL, " +
                    "FOREIGN KEY(group_id) REFERENCES Groups(group_id), " +
                    "FOREIGN KEY(paid_by) REFERENCES Users(user_id))";
            String createExpenseSharesTable = "CREATE TABLE IF NOT EXISTS ExpenseShares (" +
                    "expense_id INTEGER, " +
                    "user_id INTEGER, " +
                    "amount REAL NOT NULL, " +
                    "is_paid BOOLEAN NOT NULL, " +
                    "upi_ref TEXT, " +
                    "PRIMARY KEY(expense_id, user_id), " +
                    "FOREIGN KEY(expense_id) REFERENCES Expenses(expense_id), " +
                    "FOREIGN KEY(user_id) REFERENCES Users(user_id))";

            conn.createStatement().execute(createUsersTable);
            conn.createStatement().execute(createGroupsTable);
            conn.createStatement().execute(createGroupMembersTable);
            conn.createStatement().execute(createExpensesTable);
            conn.createStatement().execute(createExpenseSharesTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // User CRUD
    public void addUser(String name, String phone) throws SQLException {
        String sql = "INSERT INTO Users(name, phone) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.executeUpdate();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("phone")));
            }
        }
        return users;
    }

    // Group CRUD
    public void addGroup(String groupName, int createdBy) throws SQLException {
        String sql = "INSERT INTO Groups(group_name, created_by) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            pstmt.setInt(2, createdBy);
            pstmt.executeUpdate();
        }
    }

    public void addGroupMember(int groupId, int userId) throws SQLException {
        String sql = "INSERT INTO GroupMembers(group_id, user_id) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    // Expense CRUD
    public void addExpense(int groupId, String title, double amount, int paidBy, String date) throws SQLException {
        String sql = "INSERT INTO Expenses(group_id, title, amount, paid_by, date) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setString(2, title);
            pstmt.setDouble(3, amount);
            pstmt.setInt(4, paidBy);
            pstmt.setString(5, date);
            pstmt.executeUpdate();
        }
    }

    public void addExpenseShare(int expenseId, int userId, double amount, boolean isPaid, String upiRef) throws SQLException {
        String sql = "INSERT INTO ExpenseShares(expense_id, user_id, amount, is_paid, upi_ref) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, expenseId);
            pstmt.setInt(2, userId);
            pstmt.setDouble(3, amount);
            pstmt.setBoolean(4, isPaid);
            pstmt.setString(5, upiRef);
            pstmt.executeUpdate();
        }
    }

    public void updatePaymentStatus(int expenseId, int userId, boolean isPaid, String upiRef) throws SQLException {
        String sql = "UPDATE ExpenseShares SET is_paid = ?, upi_ref = ? WHERE expense_id = ? AND user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isPaid);
            pstmt.setString(2, upiRef);
            pstmt.setInt(3, expenseId);
            pstmt.setInt(4, userId);
            pstmt.executeUpdate();
        }
    }
}