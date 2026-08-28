package ais.prs.educhat.dto;

public record MessageResponse(
    boolean delivered,
    MessageDto message,
    String flagCategory,
    String llmRephrase
) {}
