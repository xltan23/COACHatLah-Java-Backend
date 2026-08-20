package ais.prs.educhat.dto;

import ais.prs.educhat.models.Message;

public record MessageResponse(
    boolean delivered,
    Message message, // Populated only if delivered
    String flagCategory, // Populated only if blocked
    String llmRephrase // Populated only if blocked
) {}
