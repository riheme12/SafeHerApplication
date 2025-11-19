package com.example.safeherapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class AwarenessRightsActivity extends AppCompatActivity {
    private Button back,ressource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awareness_rights);
        back=findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ressource=findViewById(R.id.resources_button);
        ressource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AwarenessRightsActivity.this, "ressource clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AwarenessRightsActivity.this, SupportResourcesActivity.class));
            }
        });

    }
}




























































