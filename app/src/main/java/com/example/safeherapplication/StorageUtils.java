package com.example.safeherapplication;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StorageUtils {
    private static final String PREFS_KEY = "IncidentReports";
    private static final String REPORTS_KEY = "reports";

    public static List<IncidentReport> getIncidentReports(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        String json = prefs.getString(REPORTS_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<IncidentReport>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public static void addIncidentReport(Context context, IncidentReport report) {
        List<IncidentReport> reports = getIncidentReports(context);
        reports.add(report);
        saveReports(context, reports);
    }

    public static void deleteIncidentReport(Context context, String reportId) {
        List<IncidentReport> reports = getIncidentReports(context);
        Iterator<IncidentReport> iterator = reports.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().id.equals(reportId)) iterator.remove();
        }
        saveReports(context, reports);
    }

    private static void saveReports(Context context, List<IncidentReport> reports) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REPORTS_KEY, new Gson().toJson(reports));
        editor.apply();
    }
}

