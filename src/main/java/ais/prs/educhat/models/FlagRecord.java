package ais.prs.educhat.models;

import java.time.Instant;
import java.time.LocalDate;

public class FlagRecord {
    
    private String id;
    private String profileId;
    private String messageId;
    private String originalText;
    private String rephraseSuggested;
    private FlagCategory flagCategory;
    private Instant flaggedAt;

    public FlagRecord() {}

    
}
