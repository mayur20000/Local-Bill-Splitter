package com.billsplitter.ui.controller;

import com.billsplitter.model.Group;
import com.billsplitter.model.User;
import com.billsplitter.service.BillSplitterService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseController {
    @FXML
    private TextField expenseTitleField;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<User> paidByCombo;
    @FXML
    private ComboBox<Group> groupCombo;
    @FXML
    private CheckBox equalSplitCheck;
    @FXML
    private VBox customSplitPane;
    @FXML
    private TextField customAmountField;
    @FXML
    private ComboBox<User> customUserCombo;

    private final BillSplitterService service = new BillSplitterService();
    private Map<Integer, Double> customAmounts = new HashMap<>();

    @FXML
    private void initialize() throws SQLException {
        List<User> users = service.getAllUsers();
        paidByCombo.getItems().addAll(users);
        customUserCombo.getItems().addAll(users);
        // Populate groupCombo with groups (implement in service)
        equalSplitCheck.setOnAction(event -> customSplitPane.setVisible(!equalSplitCheck.isSelected()));
    }

    @FXML
    private void addCustomShare() {
        User user = customUserCombo.getValue();
        double amount = Double.parseDouble(customAmountField.getText());
        if (user != null && amount > 0) {
            customAmounts.put(user.getUserId(), amount);
            customAmountField.clear();
        }
    }

    @FXML
    private void addExpense() throws SQLException {
        String title = expenseTitleField.getText();
        double amount = Double.parseDouble(amountField.getText());
        User paidBy = paidByCombo.getValue();
        Group group = groupCombo.getValue();
        String splitType = equalSplitCheck.isSelected() ? "equal" : "custom";

        if (title != null && amount > 0 && paidBy != null && group != null) {
            service.addExpense(group.getGroupId(), title, amount, paidBy.getUserId(), group.getMembers(), splitType, customAmounts);
            expenseTitleField.clear();
            amountField.clear();
            customAmounts.clear();
        }
    }
}