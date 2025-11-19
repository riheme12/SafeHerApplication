package com.example.safeherapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class ReportingToolsActivity extends AppCompatActivity {
    private List<IncidentReport> reports;
    private LinearLayout reportsContainer;
    private EditText titleInput, descriptionInput, locationInput, notesInput;
    private Button createBtn, saveBtn, cancelBtn;
    private RadioGroup categoryGroup, severityGroup;
    private boolean isCreating = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting_tools);
        reportsContainer = findViewById(R.id.reports_container);
        titleInput = findViewById(R.id.edit_title);
        descriptionInput = findViewById(R.id.edit_description);
        locationInput = findViewById(R.id.edit_location);
        notesInput = findViewById(R.id.edit_notes);
        categoryGroup = findViewById(R.id.radio_category);
        severityGroup = findViewById(R.id.radio_severity);

        createBtn = findViewById(R.id.btn_create);
        saveBtn = findViewById(R.id.btn_save);
        cancelBtn = findViewById(R.id.btn_cancel);

        loadReports();
        createBtn.setOnClickListener(v -> showCreateForm(true));
        saveBtn.setOnClickListener(v -> handleCreateReport());
        cancelBtn.setOnClickListener(v -> showCreateForm(false));
    }

    // Affichage des rapports
    private void loadReports() {
        reports = StorageUtils.getIncidentReports(this);
        displayReports();
    }

    private void displayReports() {
        reportsContainer.removeAllViews();
        if (reports.isEmpty() && !isCreating) {
            TextView empty = new TextView(this);
            empty.setText("No reports yet.\nCreate a report to document an incident.");
            reportsContainer.addView(empty);
        } else {
            for (IncidentReport report : reports) {
                View card = LayoutInflater.from(this).inflate(R.layout.report_item, reportsContainer, false);
                ((TextView) card.findViewById(R.id.report_title)).setText(report.title);
                ((TextView) card.findViewById(R.id.report_date)).setText(report.date);
                ((TextView) card.findViewById(R.id.report_category)).setText(report.category);
                ((TextView) card.findViewById(R.id.report_severity)).setText(report.severity);
                ((TextView) card.findViewById(R.id.report_description)).setText(report.description);
                Button deleteBtn = card.findViewById(R.id.btn_delete);
                deleteBtn.setOnClickListener(v -> handleDeleteReport(report.id));
                reportsContainer.addView(card);
            }
        }
    }

    // Gestion du formulaire de création
    private void showCreateForm(boolean show) {
        isCreating = show;
        findViewById(R.id.create_form).setVisibility(show ? View.VISIBLE : View.GONE);
        createBtn.setVisibility(show ? View.GONE : View.VISIBLE);
        displayReports();
    }

    private void handleCreateReport() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();
        String category = ((RadioButton) findViewById(categoryGroup.getCheckedRadioButtonId())).getText().toString();
        String severity = ((RadioButton) findViewById(severityGroup.getCheckedRadioButtonId())).getText().toString();

        if (title.isEmpty() || description.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Missing Information")
                    .setMessage("Please enter a title and description.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        IncidentReport report = new IncidentReport(
                String.valueOf(System.currentTimeMillis()),
                title, description, new Date().toString(),
                category, severity, location, notes
        );
        StorageUtils.addIncidentReport(this, report);
        titleInput.setText(""); descriptionInput.setText(""); locationInput.setText(""); notesInput.setText("");
        showCreateForm(false);
        new AlertDialog.Builder(this)
                .setTitle("Report Saved")
                .setMessage("Your incident report has been saved securely on your device.")
                .setPositiveButton("OK", null).show();
        loadReports();
    }

    private void handleDeleteReport(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Report")
                .setMessage("Are you sure you want to delete this report? This action cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    StorageUtils.deleteIncidentReport(this, id);
                    loadReports();
                }).show();
    }
}
