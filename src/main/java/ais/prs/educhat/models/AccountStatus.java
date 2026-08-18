package ais.prs.educhat.models;

public enum AccountStatus {
    ACTIVE, // Activated upon creation or post-suspension
    SUSPENDED, // Temporarily deactivated due to disciplinary action
    INACTIVE // Deactivated post-graduation
}
