package com.alzheimer.backend.admin;

public class AdminStats {
    private long usersCount;
    private long mmseTestsCount;
    private long activeUsersCount;

    public AdminStats(long usersCount, long mmseTestsCount, long activeUsersCount) {
        this.usersCount = usersCount;
        this.mmseTestsCount = mmseTestsCount;
        this.activeUsersCount = activeUsersCount;
    }

    public long getUsersCount() { return usersCount; }
    public void setUsersCount(long usersCount) { this.usersCount = usersCount; }

    public long getMmseTestsCount() { return mmseTestsCount; }
    public void setMmseTestsCount(long mmseTestsCount) { this.mmseTestsCount = mmseTestsCount; }

    public long getActiveUsersCount() { return activeUsersCount; }
    public void setActiveUsersCount(long activeUsersCount) { this.activeUsersCount = activeUsersCount; }
}
