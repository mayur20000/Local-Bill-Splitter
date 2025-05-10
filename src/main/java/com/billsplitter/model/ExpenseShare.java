package com.billsplitter.model;

public class ExpenseShare {
    private int expenseId;
    private int userId;
    private double amount;
    private boolean isPaid;
    private String upiRef;

    public ExpenseShare(int expenseId, int userId, double amount, boolean isPaid, String upiRef) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.amount = amount;
        this.isPaid = isPaid;
        this.upiRef = upiRef;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public int getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public String getUpiRef() {
        return upiRef;
    }
}