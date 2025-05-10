package com.billsplitter.ui.controller;

import com.billsplitter.db.DatabaseHandler;
import com.billsplitter.model.User;
import com.billsplitter.service.BillSplitterService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupController {
    @FXML
    private TextField groupNameField;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField userPhoneField;
    @FXML
    private ListView<User> userList;

    private final BillSplitterService service = new BillSplitterService();
    private final List<User> addedUsers = new ArrayList<>();

    @FXML
    private void addUser() throws SQLException {
        String name = userNameField.getText();
        String phone = userPhoneField.getText();
        if (!name.isEmpty() && !phone.isEmpty()) {
            service.addUser(name, phone);
            User user = new User(getLastInsertedUserId(), name, phone);
            addedUsers.add(user);
            userList.getItems().add(user);
            userNameField.clear();
            userPhoneField.clear();
        }
    }

    private int getLastInsertedUserId() throws SQLException {
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

    @FXML
    private void createGroup() throws SQLException {
        String groupName = groupNameField.getText();
        if (!groupName.isEmpty() && !addedUsers.isEmpty()) {
            service.createGroup(groupName, addedUsers.get(0).getUserId(), addedUsers);
            groupNameField.clear();
            userList.getItems().clear();
            addedUsers.clear();
        }
    }
}