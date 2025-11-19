package com.example.safeherapplication;

import java.util.List;

public class AwarenessSection {
    public String id;
    public String title;
    public int iconRes;
    public int colorRes;
    public List<String> content;

    public AwarenessSection(String id, String title, int iconRes, int colorRes, List<String> content) {
        this.id = id;
        this.title = title;
        this.iconRes = iconRes;
        this.colorRes = colorRes;
        this.content = content;
    }
}
