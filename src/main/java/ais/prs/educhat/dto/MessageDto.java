package ais.prs.educhat.dto;

import java.time.Instant;

import ais.prs.educhat.models.Message;

public record MessageDto(
    String messageId,
    String senderId,
    String recipientId,
    String content,
    Instant timestamp
) {
    public static MessageDto from(Message message) {
        return new MessageDto(
            message.getId(), 
            message.getSenderId(), 
            message.getRecipientId(), 
            message.getContent(), 
            message.getTimestamp()
        );
    }
}
