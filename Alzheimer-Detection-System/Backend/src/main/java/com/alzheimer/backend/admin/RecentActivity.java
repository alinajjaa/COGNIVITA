package com.alzheimer.backend.admin;

public class RecentActivity {
    private String type;
    private String description;
    private String timestamp;
    private String icon;

    public RecentActivity(String type, String description, String timestamp, String icon) {
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
        this.icon = icon;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
