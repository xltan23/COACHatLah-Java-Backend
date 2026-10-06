package ais.prs.educhat.dto;

import java.time.Instant;

import ais.prs.educhat.models.Escalation;

public record EscalationDto(
    Long id,
    String studentId,
    String classCohort,
    String triggerType,
    String triggerDetail,   
    String reviewStatus,
    Instant createdAt,
    String reviewedBy,
    String reviewComments,
    Instant reviewedAt
) {
    public static EscalationDto from(Escalation escalation) {
        return new EscalationDto(
            escalation.getId(),
            escalation.getStudentId(),
            escalation.getClassCohort(),
            escalation.getTriggerType().name(),
            escalation.getTriggerDetail(),
            escalation.getReviewStatus().name(),
            escalation.getCreatedAt(),
            escalation.getReviewedBy(),
            escalation.getReviewComments(),
            escalation.getReviewedAt()
        );

    }
}
