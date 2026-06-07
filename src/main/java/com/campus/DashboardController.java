package com.campus;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * DashboardController
 *
 * Faculty view: live today's attendance table, real stat counters,
 * CSV export via LogExporter, and student registration navigation.
 */
public class DashboardController {

    // ── Table ─────────────────────────────────────────────────────────────────
    @FXML private TableView<AttendanceRecord> entryTableView;
    @FXML private TableColumn<AttendanceRecord, String> colStudentNumber;
    @FXML private TableColumn<AttendanceRecord, String> colStudentName;
    @FXML private TableColumn<AttendanceRecord, String> colStatus;
    @FXML private TableColumn<AttendanceRecord, String> colTimeIn;
    @FXML private TableColumn<AttendanceRecord, String> colTimeOut;
    @FXML private TableColumn<AttendanceRecord, String> colDate;

    // ── Stat labels ───────────────────────────────────────────────────────────
    @FXML private Label totalEntriesLabel;
    @FXML private Label timeInLabel;
    @FXML private Label timeOutLabel;

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy");

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final StudentDAO    studentDAO    = new StudentDAO();

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        entryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupColumns();
        loadTodayData();
    }

    // ── Column setup ──────────────────────────────────────────────────────────

    private void setupColumns() {

        colStudentNumber.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStudentId()));

        // Resolve name from students table
        colStudentName.setCellValueFactory(c -> {
            try {
                return studentDAO.findById(c.getValue().getStudentId())
                        .map(s -> new SimpleStringProperty(s.getFullName()))
                        .orElse(new SimpleStringProperty("Unknown"));
            } catch (SQLException e) {
                return new SimpleStringProperty("—");
            }
        });

        // Action → friendly status
        colStatus.setCellValueFactory(c -> {
            String action = c.getValue().getAction();
            return new SimpleStringProperty(
                    "TIME_IN".equals(action) ? "Timed In" : "Timed Out");
        });

        // Time In column — only populate for TIME_IN rows
        colTimeIn.setCellValueFactory(c -> {
            if ("TIME_IN".equals(c.getValue().getAction())) {
                return new SimpleStringProperty(
                        c.getValue().getTimestamp().format(TIME_FMT));
            }
            return new SimpleStringProperty("—");
        });

        // Time Out column — only populate for TIME_OUT rows
        colTimeOut.setCellValueFactory(c -> {
            if ("TIME_OUT".equals(c.getValue().getAction())) {
                return new SimpleStringProperty(
                        c.getValue().getTimestamp().format(TIME_FMT));
            }
            return new SimpleStringProperty("—");
        });

        colDate.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getTimestamp().toLocalDate().format(DATE_FMT)));
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private void loadTodayData() {
        try {
            List<AttendanceRecord> records = attendanceDAO.findAllToday();
            ObservableList<AttendanceRecord> data =
                    FXCollections.observableArrayList(records);
            entryTableView.setItems(data);

            long ins  = records.stream()
                    .filter(r -> "TIME_IN".equals(r.getAction())).count();
            long outs = records.stream()
                    .filter(r -> "TIME_OUT".equals(r.getAction())).count();

            totalEntriesLabel.setText(String.valueOf(records.size()));
            timeInLabel.setText(String.valueOf(ins));
            timeOutLabel.setText(String.valueOf(outs));

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not load today's records: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    /** Navigate to Add Student form. */
    @FXML
    private void addStudent() {
        try {
            MainApp.switchScene("add-student.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Export today's attendance to a CSV file chosen by the user. */
    @FXML
    private void exportCSV() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Export Folder");
        File dir = chooser.showDialog(MainApp.getStage());

        if (dir == null) return; // user cancelled

        try {
            File csv = LogExporter.exportToday(Path.of(dir.toURI()));

            showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                    "CSV saved to:\n" + csv.getAbsolutePath());

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Export Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    /** Refresh the table (useful as a manual refresh button if added). */
    @FXML
    private void refreshTable() {
        loadTodayData();
    }

    /** Logout and return to role selection. */
    @FXML
    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to logout?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    MainApp.switchScene("role-selection.fxml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
