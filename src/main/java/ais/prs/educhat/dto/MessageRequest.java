package ais.prs.educhat.dto;

public record MessageRequest(
    String senderId,
    String recipientId,
    String content
) {}
