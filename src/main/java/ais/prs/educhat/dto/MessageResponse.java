package ais.prs.educhat.dto;

import ais.prs.educhat.models.Message;

public record MessageResponse(
    boolean delivered,
    Message message,
    String flagCategory,
    String llmRephrase
) {}
