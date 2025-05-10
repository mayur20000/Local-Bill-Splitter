package com.billsplitter.model;

import java.time.LocalDate;

public class Expense {
    private int expenseId;
    private int groupId;
    private String title;
    private double amount;
    private int paidBy;
    private LocalDate date;

    public Expense(int expenseId, int groupId, String title, double amount, int paidBy, LocalDate date) {
        this.expenseId = expenseId;
        this.groupId = groupId;
        this.title = title;
        this.amount = amount;
        this.paidBy = paidBy;
        this.date = date;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public int getPaidBy() {
        return paidBy;
    }

    public LocalDate getDate() {
        return date;
    }
}