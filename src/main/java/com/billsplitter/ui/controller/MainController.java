package com.billsplitter.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class MainController {
    @FXML
    private Pane contentPane;

    @FXML
    private void showGroupScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/billsplitter/fxml/group.fxml"));
        Pane groupPane = loader.load();
        contentPane.getChildren().setAll(groupPane);
    }

    @FXML
    private void showExpenseScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/billsplitter/fxml/expense.fxml"));
        Pane expensePane = loader.load();
        contentPane.getChildren().setAll(expensePane);
    }

    @FXML
    private void showSummaryScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/billsplitter/fxml/summary.fxml"));
        Pane summaryPane = loader.load();
        contentPane.getChildren().setAll(summaryPane);
    }
}