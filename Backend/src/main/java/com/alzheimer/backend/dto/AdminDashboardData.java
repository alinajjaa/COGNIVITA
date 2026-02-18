package com.alzheimer.backend.dto;

import com.alzheimer.backend.admin.DashboardStats;
import com.alzheimer.backend.admin.ChartData;
import com.alzheimer.backend.admin.RecentActivity;
import com.alzheimer.backend.admin.MMSETestAnalytics;
import com.alzheimer.backend.admin.SystemHealth;
import java.util.List;

public class AdminDashboardData {
    private DashboardStats stats;
    private List<ChartData> mmseScoreDistribution;
    private List<ChartData> userRoleDistribution;
    private List<RecentActivity> recentActivities;
    private List<MMSETestAnalytics> testAnalytics;
    private SystemHealth systemHealth;

    public AdminDashboardData() {}

    public AdminDashboardData(DashboardStats stats, List<ChartData> mmseScoreDistribution, 
                             List<ChartData> userRoleDistribution, List<RecentActivity> recentActivities,
                             List<MMSETestAnalytics> testAnalytics, SystemHealth systemHealth) {
        this.stats = stats;
        this.mmseScoreDistribution = mmseScoreDistribution;
        this.userRoleDistribution = userRoleDistribution;
        this.recentActivities = recentActivities;
        this.testAnalytics = testAnalytics;
        this.systemHealth = systemHealth;
    }

    // Getters and Setters
    public DashboardStats getStats() { return stats; }
    public void setStats(DashboardStats stats) { this.stats = stats; }

    public List<ChartData> getMmseScoreDistribution() { return mmseScoreDistribution; }
    public void setMmseScoreDistribution(List<ChartData> mmseScoreDistribution) { this.mmseScoreDistribution = mmseScoreDistribution; }

    public List<ChartData> getUserRoleDistribution() { return userRoleDistribution; }
    public void setUserRoleDistribution(List<ChartData> userRoleDistribution) { this.userRoleDistribution = userRoleDistribution; }

    public List<RecentActivity> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<RecentActivity> recentActivities) { this.recentActivities = recentActivities; }

    public List<MMSETestAnalytics> getTestAnalytics() { return testAnalytics; }
    public void setTestAnalytics(List<MMSETestAnalytics> testAnalytics) { this.testAnalytics = testAnalytics; }

    public SystemHealth getSystemHealth() { return systemHealth; }
    public void setSystemHealth(SystemHealth systemHealth) { this.systemHealth = systemHealth; }
}
