package ais.prs.educhat.dto;

import java.time.Instant;

import ais.prs.educhat.models.FlagRecord;
import ais.prs.educhat.models.enums.FlagCategory;

public record FlagRecordDto(
    Long id,
    String senderId,
    String recipientId,
    String originalText,
    String flagCategories,
    String confidenceScores,
    Instant flaggedAt
) {
    public static FlagRecordDto from(FlagRecord flagRecord) {
        String flagCategories = String.join(",", flagRecord.getFlagCategories().stream()
            .map(FlagCategory::name)
            .toList());
        String confidenceScores = flagRecord.getConfidenceScores() != null ? flagRecord.getConfidenceScores().toString() : null;
        
        return new FlagRecordDto(
            flagRecord.getId(),
            flagRecord.getSenderId(),
            flagRecord.getRecipientId(),
            flagRecord.getOriginalText(),
            flagCategories,
            confidenceScores,
            flagRecord.getFlaggedAt()
        );
    }
}