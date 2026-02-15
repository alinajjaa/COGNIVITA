package com.alzheimer.backend.admin;

public class MMSETestAnalytics {
    private String patientName;
    private long testCount;
    private double averageScore;
    private int highestScore;
    private int lowestScore;
    private String trend;

    public MMSETestAnalytics(String patientName, long testCount, double averageScore, int highestScore, int lowestScore, String trend) {
        this.patientName = patientName;
        this.testCount = testCount;
        this.averageScore = averageScore;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
        this.trend = trend;
    }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public long getTestCount() { return testCount; }
    public void setTestCount(long testCount) { this.testCount = testCount; }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

    public int getHighestScore() { return highestScore; }
    public void setHighestScore(int highestScore) { this.highestScore = highestScore; }

    public int getLowestScore() { return lowestScore; }
    public void setLowestScore(int lowestScore) { this.lowestScore = lowestScore; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
}
