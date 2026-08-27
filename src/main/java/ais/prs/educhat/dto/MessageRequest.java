package ais.prs.educhat.dto;

public record MessageRequest(
    String sender,
    String recipient,
    String content
) {}
