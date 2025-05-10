package com.billsplitter.ui.controller;

import com.billsplitter.model.User;
import com.billsplitter.service.BillSplitterService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.sql.SQLException;
import java.util.Map;

public class SummaryController {
    @FXML
    private TableView<Map.Entry<User, Double>> summaryTable;
    @FXML
    private TableColumn<Map.Entry<User, Double>, String> userColumn;
    @FXML
    private TableColumn<Map.Entry<User, Double>, Double> paidColumn;
    @FXML
    private TableColumn<Map.Entry<User, Double>, Double> owedColumn;
    @FXML
    private TableColumn<Map.Entry<User, Double>, Double> balanceColumn;

    private final BillSplitterService service = new BillSplitterService();

    @FXML
    private void initialize() throws SQLException {
        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey().getName()));
        balanceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));

        // Assume groupId = 1 for demo
        Map<User, Double> balances = service.getUserBalances(1);
        summaryTable.getItems().addAll(balances.entrySet());
    }

    @FXML
    private void exportPDF() throws SQLException {
        try (PdfWriter writer = new PdfWriter("summary.pdf");
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            document.add(new Paragraph("Expense Summary"));
            Map<User, Double> balances = service.getUserBalances(1);
            for (Map.Entry<User, Double> entry : balances.entrySet()) {
                document.add(new Paragraph(entry.getKey().getName() + ": " + entry.getValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}