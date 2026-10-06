package ais.prs.educhat.dto;

public record ReviewDto(
    Long escalationId,
    String reviewedBy,
    String reviewComments
) {
    
}
