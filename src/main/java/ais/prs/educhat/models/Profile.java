package ais.prs.educhat.models;

import java.time.Instant;
import java.util.UUID;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import ais.prs.educhat.models.enums.AccountStatus;
import ais.prs.educhat.models.enums.DisciplinaryStatus;
import ais.prs.educhat.models.enums.Role;
import jakarta.json.Json;
import jakarta.json.JsonObject;

// Single entry per person (to be stored in MySQL)
public class Profile {
    
    // email, password (created if time permits)
    private String id;
    private String name;
    private String gender;
    private Role role;
    private String school; // In the form of school code (e.g., "BPGH", "DLS")
    private String classCohort; // 6H/26
    private String cca; 
    private AccountStatus status;
    private DisciplinaryStatus disciplinaryStatus;
    private Integer currentSemesterFlagCount;
    private Instant createdAt;
    private Instant updatedAt;

    // Default constructor
    public Profile() {} 

    // Account creation constructor
    public Profile(String name, String gender, Role role, String school, String classCohort, String cca) {
        this.id = UUID.randomUUID().toString().substring(0,8); // Generate a unique ID for the profile
        this.name = name.toUpperCase(); 
        this.gender = gender.toUpperCase(); 
        this.role = role;
        this.school = school.toUpperCase(); 
        this.classCohort = classCohort.toUpperCase(); 
        this.cca = cca.toUpperCase(); 
        this.status = AccountStatus.ACTIVE;
        this.disciplinaryStatus = DisciplinaryStatus.NONE;
        this.currentSemesterFlagCount = 0;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Account constructor for database retrieval (This will be used for general purpose)
    public Profile(String id, String name, String gender, String role, String school, String classCohort, String cca, String status) {
        this.id = id;
        this.name = name; 
        this.gender = gender;
        this.role = Role.valueOf(role);
        this.school = school; 
        this.classCohort = classCohort; 
        this.cca = cca; 
        this.status = AccountStatus.valueOf(status);
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getClassCohort() {
        return classCohort;
    }

    public void setClassCohort(String classCohort) {
        this.classCohort = classCohort;
    }

    public String getCca() {
        return cca;
    }

    public void setCca(String cca) {
        this.cca = cca;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public DisciplinaryStatus getDisciplinaryStatus() {
        return disciplinaryStatus;
    }

    public void setDisciplinaryStatus(DisciplinaryStatus disciplinaryStatus) {
        this.disciplinaryStatus = disciplinaryStatus;
    }

    public Integer getCurrentSemesterFlagCount() {
        return currentSemesterFlagCount;
    }

    public void setCurrentSemesterFlagCount(Integer currentSemesterFlagCount) {
        this.currentSemesterFlagCount = currentSemesterFlagCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", role=" + role +
                ", school='" + school + '\'' +
                ", classCohort='" + classCohort + '\'' +
                ", cca='" + cca + '\'' +
                ", status=" + status +
                ", disciplinaryStatus=" + disciplinaryStatus +
                ", currentSemesterFlagCount=" + currentSemesterFlagCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // Convert Profile object to JSON representation
    public JsonObject toJson() {
        return Json.createObjectBuilder()
            .add("id", id)
            .add("name", name)
            .add("gender", gender)
            .add("role", role.toString())
            .add("school", school)
            .add("classCohort", classCohort)
            .add("cca", cca)
            .add("status", status.toString())
            .build();
    }

    // Create Profile object from JSON representation
    public static Profile createProfile(JsonObject json) {
        return new Profile(
            json.getString("id"),
            json.getString("name"),
            json.getString("gender"),
            json.getString("role"),
            json.getString("school"),
            json.getString("classCohort"),
            json.getString("cca"),
            json.getString("status")
        );
    }

    // Create Profile object from SQL Row Set
    public static Profile createProfile(SqlRowSet srs) {
        return new Profile(
            srs.getString("id"),
            srs.getString("name"),
            srs.getString("gender"),
            srs.getString("role"),
            srs.getString("school"),
            srs.getString("class_cohort"),
            srs.getString("cca"),
            srs.getString("status")
        );
    }

    // Additional Methods to inspect disciplinary status and flag count to be considered for future implementation
}

