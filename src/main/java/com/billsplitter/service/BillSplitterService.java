package com.billsplitter.service;
import com.billsplitter.db.DatabaseHandler;
import com.billsplitter.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillSplitterService {
    private final DatabaseHandler dbHandler;

    public BillSplitterService() {
        this.dbHandler = new DatabaseHandler();
    }

    public void createGroup(String groupName, int createdBy, List<User> members) throws SQLException {
        dbHandler.addGroup(groupName, createdBy);
        int groupId = getLastInsertedGroupId();
        for (User user : members) {
            dbHandler.addGroupMember(groupId, user.getUserId());
        }
    }

    private int getLastInsertedGroupId() throws SQLException {
        String sql = "SELECT last_insert_rowid() as id";
        try (Connection conn = DriverManager.getConnection(DatabaseHandler.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    public void addExpense(int groupId, String title, double amount, int paidBy, List<User> splitBetween, String splitType, Map<Integer, Double> customAmounts) throws SQLException {
        LocalDate date = LocalDate.now();
        dbHandler.addExpense(groupId, title, amount, paidBy, date.toString());
        int expenseId = getLastInsertedExpenseId();

        if ("equal".equalsIgnoreCase(splitType)) {
            double share = amount / splitBetween.size();
            for (User user : splitBetween) {
                dbHandler.addExpenseShare(expenseId, user.getUserId(), share, false, null);
            }
        } else {
            for (User user : splitBetween) {
                double share = customAmounts.getOrDefault(user.getUserId(), 0.0);
                dbHandler.addExpenseShare(expenseId, user.getUserId(), share, false, null);
            }
        }
    }

    private int getLastInsertedExpenseId() throws SQLException {
        String sql = "SELECT last_insert_rowid() as id";
        try (Connection conn = DriverManager.getConnection(DatabaseHandler.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    public void markAsPaid(int expenseId, int userId, String upiRef) throws SQLException {
        dbHandler.updatePaymentStatus(expenseId, userId, true, upiRef);
    }

    public Map<User, Double> getUserBalances(int groupId) throws SQLException {
        Map<User, Double> balances = new HashMap<>();
        List<User> users = dbHandler.getAllUsers();
        for (User user : users) {
            balances.put(user, 0.0);
        }

        String sql = "SELECT e.paid_by, es.user_id, es.amount, es.is_paid " +
                "FROM Expenses e JOIN ExpenseShares es ON e.expense_id = es.expense_id " +
                "WHERE e.group_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseHandler.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int paidBy = rs.getInt("paid_by");
                int userId = rs.getInt("user_id");
                double amount = rs.getDouble("amount");
                boolean isPaid = rs.getBoolean("is_paid");

                if (isPaid) {
                    User paidByUser = users.stream().filter(u -> u.getUserId() == paidBy).findFirst().orElse(null);
                    User debtor = users.stream().filter(u -> u.getUserId() == userId).findFirst().orElse(null);
                    if (paidByUser != null && debtor != null) {
                        balances.put(paidByUser, balances.getOrDefault(paidByUser, 0.0) + amount);
                        balances.put(debtor, balances.getOrDefault(debtor, 0.0) - amount);
                    }
                }
            }
        }
        return balances;
    }
}