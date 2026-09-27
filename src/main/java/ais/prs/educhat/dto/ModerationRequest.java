package ais.prs.educhat.dto;

// Request body for the Python moderation service (POST /score)
public record ModerationRequest(
    String text
) {}
