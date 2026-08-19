package ais.prs.educhat.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import static ais.prs.educhat.repositories.Queries.*;

import java.sql.Timestamp;

import ais.prs.educhat.models.FlagRecord;

@Repository
public class FlagRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean addFlagRecord(FlagRecord flagRecord) {
        System.out.println("Inserting flag record into database: " + flagRecord);
        return jdbcTemplate.update(SQL_INSERT_FLAGRECORD_IN_FLAGRECORDS,
            // flagRecord.getId(),
            flagRecord.getSenderId(),
            flagRecord.getReceiverId(),
            flagRecord.getMessageId(),
            flagRecord.getOriginalText(),
            flagRecord.getRephraseSuggested(),
            flagRecord.getFlagCategory(),
            Timestamp.from(flagRecord.getFlaggedAt())
        ) > 0;
    }

    // Get FlagRecord
}
