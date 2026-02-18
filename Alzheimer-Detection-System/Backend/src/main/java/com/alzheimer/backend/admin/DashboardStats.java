package com.alzheimer.backend.admin;

public class DashboardStats {
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long totalAdmins;
    private long totalDoctors;
    private long totalPatients;
    private long totalMMSETests;
    private double averageMMSEScore;
    private long normalCognitionCount;
    private long mildImpairmentCount;
    private long moderateImpairmentCount;
    private long severeImpairmentCount;
    private long testsThisMonth;

    public DashboardStats(long totalUsers, long activeUsers, long inactiveUsers, long totalAdmins,
                         long totalDoctors, long totalPatients, long totalMMSETests, double averageMMSEScore,
                         long normalCognitionCount, long mildImpairmentCount, long moderateImpairmentCount,
                         long severeImpairmentCount, long testsThisMonth) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
        this.totalAdmins = totalAdmins;
        this.totalDoctors = totalDoctors;
        this.totalPatients = totalPatients;
        this.totalMMSETests = totalMMSETests;
        this.averageMMSEScore = averageMMSEScore;
        this.normalCognitionCount = normalCognitionCount;
        this.mildImpairmentCount = mildImpairmentCount;
        this.moderateImpairmentCount = moderateImpairmentCount;
        this.severeImpairmentCount = severeImpairmentCount;
        this.testsThisMonth = testsThisMonth;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }

    public long getInactiveUsers() { return inactiveUsers; }
    public void setInactiveUsers(long inactiveUsers) { this.inactiveUsers = inactiveUsers; }

    public long getTotalAdmins() { return totalAdmins; }
    public void setTotalAdmins(long totalAdmins) { this.totalAdmins = totalAdmins; }

    public long getTotalDoctors() { return totalDoctors; }
    public void setTotalDoctors(long totalDoctors) { this.totalDoctors = totalDoctors; }

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getTotalMMSETests() { return totalMMSETests; }
    public void setTotalMMSETests(long totalMMSETests) { this.totalMMSETests = totalMMSETests; }

    public double getAverageMMSEScore() { return averageMMSEScore; }
    public void setAverageMMSEScore(double averageMMSEScore) { this.averageMMSEScore = averageMMSEScore; }

    public long getNormalCognitionCount() { return normalCognitionCount; }
    public void setNormalCognitionCount(long normalCognitionCount) { this.normalCognitionCount = normalCognitionCount; }

    public long getMildImpairmentCount() { return mildImpairmentCount; }
    public void setMildImpairmentCount(long mildImpairmentCount) { this.mildImpairmentCount = mildImpairmentCount; }

    public long getModerateImpairmentCount() { return moderateImpairmentCount; }
    public void setModerateImpairmentCount(long moderateImpairmentCount) { this.moderateImpairmentCount = moderateImpairmentCount; }

    public long getSevereImpairmentCount() { return severeImpairmentCount; }
    public void setSevereImpairmentCount(long severeImpairmentCount) { this.severeImpairmentCount = severeImpairmentCount; }

    public long getTestsThisMonth() { return testsThisMonth; }
    public void setTestsThisMonth(long testsThisMonth) { this.testsThisMonth = testsThisMonth; }
}
