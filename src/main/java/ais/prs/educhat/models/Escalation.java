package ais.prs.educhat.models;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import ais.prs.educhat.models.enums.ReviewStatus;
import ais.prs.educhat.models.enums.TriggerType;

public class Escalation {
    
    private Long id;
    private String studentId;
    private String classCohort;
    private TriggerType triggerType;
    private String triggerDetail;   // ANOMALY_DETECTED: comma-separated spike hours, e.g. "16:00,19:00"; THRESHOLD_EXCEEDED: the flag count, e.g. "10"
    private ReviewStatus reviewStatus;
    private Instant createdAt;
    // Null until the escalation is reviewed
    private String reviewedBy;
    private String reviewComments;
    private Instant reviewedAt;

    // Default constructor
    public Escalation() {}

    // Escalation creation constructor
    public Escalation(String studentId, String classCohort, TriggerType triggerType, String triggerDetail) {
        this.studentId = studentId;
        this.classCohort = classCohort;
        this.triggerType = triggerType;
        this.triggerDetail = triggerDetail;
        this.reviewStatus = ReviewStatus.TO_BE_REVIEWED; // Default to TO_BE_REVIEWED when creating a new escalation
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getClassCohort() {
        return classCohort;
    }

    public void setClassCohort(String classCohort) {
        this.classCohort = classCohort;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggerDetail() {
        return triggerDetail;
    }

    public void setTriggerDetail(String triggerDetail) {
        this.triggerDetail = triggerDetail;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(String reviewComments) {
        this.reviewComments = reviewComments;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    @Override 
    public String toString() {
        return "Escalation{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", classCohort='" + classCohort + '\'' +
                ", triggerType=" + triggerType +
                ", triggerDetail='" + triggerDetail + '\'' +
                ", reviewStatus=" + reviewStatus +
                ", createdAt=" + createdAt +
                ", reviewedBy='" + reviewedBy + '\'' +
                ", reviewComments='" + reviewComments + '\'' +
                ", reviewedAt=" + reviewedAt +
                '}';
    }

    public static Escalation createEscalationFromRowSet(SqlRowSet srs) {
        Escalation escalation = new Escalation();
        escalation.setId(srs.getLong("id"));
        escalation.setStudentId(srs.getString("student_id"));
        escalation.setClassCohort(srs.getString("class_cohort"));
        escalation.setTriggerType(TriggerType.valueOf(srs.getString("trigger_type")));
        escalation.setTriggerDetail(srs.getString("trigger_detail"));
        escalation.setReviewStatus(ReviewStatus.valueOf(srs.getString("review_status")));
        escalation.setCreatedAt(srs.getTimestamp("created_at").toInstant());
        escalation.setReviewedBy(srs.getString("reviewed_by"));
        escalation.setReviewComments(srs.getString("review_comments"));
        // SQL NULL (not yet reviewed) -> null
        Timestamp reviewedAt = srs.getTimestamp("reviewed_at");
        escalation.setReviewedAt(reviewedAt != null ? reviewedAt.toInstant() : null);
        return escalation;
    }

    public Escalation createEscalation(Profile profile, TriggerType triggerType, String triggerDetail) {
        Escalation escalation = new Escalation(
            profile.getId(),
            profile.getClassCohort(),
            triggerType,
            triggerDetail
        );
        return escalation;
    }
}
