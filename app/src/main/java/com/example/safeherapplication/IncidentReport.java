package com.example.safeherapplication;

public class IncidentReport {
    public String id;
    public String title;
    public String description;
    public String date;
    public String category;
    public String severity;
    public String location;
    public String notes;

    public IncidentReport(String id, String title, String description,
                          String date, String category, String severity,
                          String location, String notes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.severity = severity;
        this.location = location;
        this.notes = notes;
}
}
