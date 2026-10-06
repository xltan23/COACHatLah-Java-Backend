package ais.prs.educhat.models.enums;

public enum ReviewStatus {
    NONE, // No disciplinary action has been taken
    TO_BE_REVIEWED, // Upon hitting a threshold of flags, the account is flagged for review
    UNDER_REVIEW, // The account is under review by the disciplinary committee
    REVIEWED, // The account has been reviewed by the disciplinary committee
    SANCTIONED // Subject to disciplinary action
}
