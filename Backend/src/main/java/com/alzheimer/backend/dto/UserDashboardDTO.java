package com.alzheimer.backend.dto;

import com.alzheimer.backend.mmse.MMSETest;
import com.alzheimer.backend.user.User;
import java.util.List;

public class UserDashboardDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private Boolean active;
    private List<MMSETest> mmseTests;

    public UserDashboardDTO() {}

    public UserDashboardDTO(User user, List<MMSETest> mmseTests) {
        this.userId = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole().toString();
        this.active = user.getActive();
        this.mmseTests = mmseTests;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public List<MMSETest> getMmseTests() { return mmseTests; }
    public void setMmseTests(List<MMSETest> mmseTests) { this.mmseTests = mmseTests; }
}
