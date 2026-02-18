package com.alzheimer.backend.admin;

public class SystemHealth {
    private String status;
    private long uptime;
    private double cpuUsage;
    private double memoryUsage;
    private String databaseStatus;
    private String lastBackup;

    public SystemHealth(String status, long uptime, double cpuUsage, double memoryUsage, String databaseStatus, String lastBackup) {
        this.status = status;
        this.uptime = uptime;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.databaseStatus = databaseStatus;
        this.lastBackup = lastBackup;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }

    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }

    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }

    public String getDatabaseStatus() { return databaseStatus; }
    public void setDatabaseStatus(String databaseStatus) { this.databaseStatus = databaseStatus; }

    public String getLastBackup() { return lastBackup; }
    public void setLastBackup(String lastBackup) { this.lastBackup = lastBackup; }
}
