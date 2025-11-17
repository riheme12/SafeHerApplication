package com.example.safeherapplication;

public class DangerZone {
    private String location;
    private String title;
    private String description;
    private String severity;
    private String reportedTime;
    private double latitude;
    private double longitude;

    public DangerZone(String location, String title, String description,
                      String severity, String reportedTime, double latitude, double longitude) {
        this.location = location;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.reportedTime = reportedTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation() { return location; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSeverity() { return severity; }
    public String getReportedTime() { return reportedTime; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}