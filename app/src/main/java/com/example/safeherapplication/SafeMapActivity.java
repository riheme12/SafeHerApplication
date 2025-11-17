package com.example.safeherapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class SafeMapActivity extends AppCompatActivity {

    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView dangerZonesRecyclerView;
    private Button reportDangerBtn;
    private Button toggleViewBtn;
    private TextView emptyStateText;
    private DangerZoneAdapter adapter;
    private List<DangerZone> dangerZones;
    private List<Marker> markers;
    private boolean isListView = false;
    private View listContainer;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPORTANT: Configuration OSMDroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_safe_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.mapView);
        dangerZonesRecyclerView = findViewById(R.id.dangerZonesRecyclerView);
        reportDangerBtn = findViewById(R.id.reportDangerBtn);
        toggleViewBtn = findViewById(R.id.toggleViewBtn);
        emptyStateText = findViewById(R.id.emptyStateText);
        listContainer = findViewById(R.id.listContainer);

        markers = new ArrayList<>();
        dangerZones = new ArrayList<>();

        // Sample danger zones (Sfax, Tunisia)
        dangerZones.add(new DangerZone("Avenue Habib Bourguiba", "Harcèlement verbal",
                "Harcèlement fréquent en soirée", "Élevé", "Il y a 2 jours",
                34.7406, 10.7603));
        dangerZones.add(new DangerZone("Rue de la République", "Zone peu éclairée",
                "Éclairage insuffisant la nuit", "Moyen", "Il y a 5 jours",
                34.7398, 10.7589));
        dangerZones.add(new DangerZone("Place Bab Bhar", "Foule dense",
                "Zone très fréquentée", "Faible", "Il y a 1 semaine",
                34.7420, 10.7610));

        setupMap();
        setupRecyclerView();
        addDangerZonesToMap();

        reportDangerBtn.setOnClickListener(v -> showReportDialog());
        toggleViewBtn.setOnClickListener(v -> toggleView());

        requestLocationPermission();
    }

    private void setupMap() {
        // Configure map
        mapView.setTileSource(TileSourceFactory.MAPNIK); // Style OpenStreetMap
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(13.0);

        GeoPoint startPoint = new GeoPoint(34.7406, 10.7603);
        mapController.setCenter(startPoint);

        // Add location overlay
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            centerOnCurrentLocation();
        }
    }

    private void centerOnCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            GeoPoint currentLocation = new GeoPoint(location.getLatitude(),
                                    location.getLongitude());
                            mapView.getController().animateTo(currentLocation);
                            mapView.getController().setZoom(14.0);
                        }
                    });
        }
    }

    private void addDangerZonesToMap() {
        for (DangerZone zone : dangerZones) {
            addMarkerForZone(zone);
        }
    }

    private void addMarkerForZone(DangerZone zone) {
        GeoPoint position = new GeoPoint(zone.getLatitude(), zone.getLongitude());

        // Create marker
        Marker marker = new Marker(mapView);
        marker.setPosition(position);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(zone.getLocation());
        marker.setSnippet(zone.getTitle() + " - " + zone.getSeverity());

        // Set marker icon based on severity
        switch (zone.getSeverity()) {
            case "Élevé":
                marker.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                marker.setTextIcon("🔴");
                break;
            case "Moyen":
                marker.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
                marker.setTextIcon("🟠");
                break;
            default:
                marker.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
                marker.setTextIcon("🟡");
                break;
        }

        mapView.getOverlays().add(marker);
        markers.add(marker);

        // Add circle around danger zone
        addCircleForZone(zone);
    }

    private void addCircleForZone(DangerZone zone) {
        GeoPoint center = new GeoPoint(zone.getLatitude(), zone.getLongitude());

        int radius;
        int fillColor;
        int strokeColor;

        switch (zone.getSeverity()) {
            case "Élevé":
                radius = 200;
                fillColor = Color.argb(50, 255, 59, 48);
                strokeColor = Color.argb(150, 255, 59, 48);
                break;
            case "Moyen":
                radius = 150;
                fillColor = Color.argb(50, 255, 149, 0);
                strokeColor = Color.argb(150, 255, 149, 0);
                break;
            default:
                radius = 100;
                fillColor = Color.argb(50, 255, 204, 0);
                strokeColor = Color.argb(150, 255, 204, 0);
                break;
        }

        // Create circle
        Polygon circle = new Polygon(mapView);
        circle.setPoints(Polygon.pointsAsCircle(center, radius));
        circle.setFillColor(fillColor);
        circle.setStrokeColor(strokeColor);
        circle.setStrokeWidth(2);

        mapView.getOverlays().add(circle);
    }

    private void toggleView() {
        isListView = !isListView;

        if (isListView) {
            mapView.setVisibility(View.GONE);
            listContainer.setVisibility(View.VISIBLE);
            toggleViewBtn.setText("🗺️ Voir la Carte");
        } else {
            mapView.setVisibility(View.VISIBLE);
            listContainer.setVisibility(View.GONE);
            toggleViewBtn.setText("📋 Voir la Liste");
        }
    }

    private void setupRecyclerView() {
        adapter = new DangerZoneAdapter(dangerZones);
        dangerZonesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dangerZonesRecyclerView.setAdapter(adapter);
    }

    private void showReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_report_danger, null);

        EditText locationInput = dialogView.findViewById(R.id.dangerLocationInput);
        EditText titleInput = dialogView.findViewById(R.id.dangerTitleInput);
        EditText descriptionInput = dialogView.findViewById(R.id.dangerDescriptionInput);
        RadioGroup severityGroup = dialogView.findViewById(R.id.severityRadioGroup);
        Button useCurrentLocationBtn = dialogView.findViewById(R.id.useCurrentLocationBtn);

        useCurrentLocationBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                locationInput.setText("Position actuelle (" +
                                        String.format("%.4f", location.getLatitude()) + ", " +
                                        String.format("%.4f", location.getLongitude()) + ")");
                                locationInput.setTag(location);
                            }
                        });
            }
        });

        builder.setView(dialogView)
                .setTitle("Signaler une zone dangereuse")
                .setPositiveButton("Signaler", (dialog, which) -> {
                    String location = locationInput.getText().toString().trim();
                    String title = titleInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();

                    String severity = "Moyen";
                    int selectedId = severityGroup.getCheckedRadioButtonId();
                    if (selectedId == R.id.severityLow) severity = "Faible";
                    else if (selectedId == R.id.severityMedium) severity = "Moyen";
                    else if (selectedId == R.id.severityHigh) severity = "Élevé";

                    if (location.isEmpty() || title.isEmpty()) {
                        Toast.makeText(this, "Veuillez remplir tous les champs obligatoires",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double lat = 34.7406;
                    double lng = 10.7603;

                    Object locationTag = locationInput.getTag();
                    if (locationTag instanceof Location) {
                        Location loc = (Location) locationTag;
                        lat = loc.getLatitude();
                        lng = loc.getLongitude();
                    }

                    DangerZone newZone = new DangerZone(location, title, description,
                            severity, "À l'instant", lat, lng);
                    dangerZones.add(0, newZone);
                    adapter.notifyItemInserted(0);

                    addMarkerForZone(newZone);
                    mapView.invalidate();

                    GeoPoint newPoint = new GeoPoint(lat, lng);
                    mapView.getController().animateTo(newPoint);
                    mapView.getController().setZoom(15.0);

                    Toast.makeText(this, "Zone signalée avec succès ✅", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                centerOnCurrentLocation();
                myLocationOverlay.enableMyLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }


    // RecyclerView Adapter
    private class DangerZoneAdapter extends RecyclerView.Adapter<DangerZoneAdapter.DangerZoneViewHolder> {
        private List<DangerZone> zones;

        public DangerZoneAdapter(List<DangerZone> zones) {
            this.zones = zones;
        }

        @Override
        public DangerZoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_danger_zone, parent, false);
            return new DangerZoneViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DangerZoneViewHolder holder, int position) {
            DangerZone zone = zones.get(position);
            holder.bind(zone);
        }

        @Override
        public int getItemCount() {
            return zones.size();
        }

        class DangerZoneViewHolder extends RecyclerView.ViewHolder {
            private TextView locationText;
            private TextView titleText;
            private TextView descriptionText;
            private TextView severityText;
            private TextView timeText;
            private ImageView warningIcon;

            public DangerZoneViewHolder(View itemView) {
                super(itemView);
                locationText = itemView.findViewById(R.id.dangerLocationText);
                titleText = itemView.findViewById(R.id.dangerTitleText);
                descriptionText = itemView.findViewById(R.id.dangerDescriptionText);
                severityText = itemView.findViewById(R.id.dangerSeverityText);
                timeText = itemView.findViewById(R.id.dangerTimeText);
                warningIcon = itemView.findViewById(R.id.warningIcon);
            }

            public void bind(DangerZone zone) {
                locationText.setText(zone.getLocation());
                titleText.setText(zone.getTitle());
                descriptionText.setText(zone.getDescription().isEmpty() ?
                        "Aucune description" : zone.getDescription());
                severityText.setText(zone.getSeverity());
                timeText.setText("Signalé " + zone.getReportedTime());

                int severityColor;
                switch (zone.getSeverity()) {
                    case "Élevé":
                        severityColor = 0xFFFF3B30;
                        break;
                    case "Moyen":
                        severityColor = 0xFFFF9500;
                        break;
                    default:
                        severityColor = 0xFFFFCC00;
                        break;
                }
                severityText.setTextColor(severityColor);
                warningIcon.setColorFilter(severityColor);

                itemView.setOnClickListener(v -> {
                    GeoPoint position = new GeoPoint(zone.getLatitude(), zone.getLongitude());
                    mapView.getController().animateTo(position);
                    mapView.getController().setZoom(16.0);
                    toggleView();
                });
            }
        }
    }
}